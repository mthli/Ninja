package io.github.mthli.Ninja.View;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.*;

public class SwipeToDismissListener implements View.OnTouchListener {
    public interface DismissCallback {
        boolean canDismiss(Object token);
        void onDismiss(View view, Object token);
    }

    private View view;
    private Object token;
    private DismissCallback callback;

    private int viewHeight = 1;
    private int slop;
    private int minFlingVelocity;
    private int maxFlingVelocity;
    private long animTime;

    private float downY;
    private float translationY;
    private boolean swiping;
    private int swipingSlop;
    private VelocityTracker velocityTracker;

    public SwipeToDismissListener(View view, Object token, DismissCallback callback) {
        this.view = view;
        this.token = token;
        this.callback = callback;

        ViewConfiguration configuration = ViewConfiguration.get(view.getContext());
        this.slop = configuration.getScaledTouchSlop();
        this.minFlingVelocity = configuration.getScaledMinimumFlingVelocity();
        this.maxFlingVelocity = configuration.getScaledMaximumFlingVelocity();
        this.animTime = view.getContext().getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        event.offsetLocation(0, translationY);
        if (viewHeight < 2) {
            viewHeight = this.view.getHeight();
        }

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                downY = event.getRawY();
                if (callback.canDismiss(token)) {
                    velocityTracker = VelocityTracker.obtain();
                    velocityTracker.addMovement(event);
                }

                return false;
            } case MotionEvent.ACTION_UP: {
                if (velocityTracker == null) {
                    break;
                }

                velocityTracker.addMovement(event);
                velocityTracker.computeCurrentVelocity(1000);

                float deltaY = event.getRawY() - downY;
                float velocityY = velocityTracker.getYVelocity();
                float absVelocityY = Math.abs(velocityTracker.getYVelocity());
                boolean dismiss = false;
                boolean dismissDown = false;

                if ((Math.abs(deltaY) > viewHeight / 2) && swiping) {
                    dismiss = true;
                    dismissDown = (deltaY > 0);
                } else if (minFlingVelocity <= absVelocityY && absVelocityY <= maxFlingVelocity && swiping) {
                    dismiss = ((velocityY < 0) == (deltaY < 0));
                    dismissDown = velocityTracker.getYVelocity() > 0;
                }

                if (dismiss) {
                    this.view.animate()
                            .translationY(dismissDown ? viewHeight : -viewHeight)
                            .alpha(0)
                            .setDuration(animTime)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    performDismiss();
                                }
                            });
                } else if (swiping) {
                    this.view.animate()
                            .translationY(0f)
                            .alpha(1f)
                            .setDuration(animTime)
                            .setListener(null);
                }

                downY = 0;
                translationY = 0;
                swiping = false;
                velocityTracker.recycle();
                velocityTracker = null;

                break;
            } case MotionEvent.ACTION_CANCEL: {
                if (velocityTracker == null) {
                    break;
                }

                this.view.animate()
                        .translationY(0f)
                        .alpha(1f)
                        .setDuration(animTime)
                        .setListener(null);

                downY = 0;
                translationY = 0;
                swiping = false;
                velocityTracker.recycle();
                velocityTracker = null;

                break;
            } case MotionEvent.ACTION_MOVE: {
                if (velocityTracker == null) {
                    break;
                }
                velocityTracker.addMovement(event);

                float deltaY = event.getRawY() - downY;
                if (Math.abs(deltaY) > slop) {
                    swiping = true;
                    swipingSlop = (deltaY > 0 ? slop : -slop);
                    this.view.getParent().requestDisallowInterceptTouchEvent(true);

                    MotionEvent cancelEvent = MotionEvent.obtainNoHistory(event);
                    cancelEvent.setAction(MotionEvent.ACTION_CANCEL | (event.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
                    this.view.onTouchEvent(cancelEvent);
                    cancelEvent.recycle();
                }

                if (swiping) {
                    translationY = deltaY;
                    this.view.setTranslationY(deltaY - swipingSlop);
                    this.view.setAlpha(Math.max(0f, Math.min(1f, 1f - 2f * Math.abs(deltaY) / viewHeight)));

                    return true;
                }

                break;
            }
        }

        return false;
    }

    private void performDismiss() {
        final ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        final int originalWidth = view.getWidth();

        ValueAnimator animator = ValueAnimator.ofInt(originalWidth, 1).setDuration(animTime);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                callback.onDismiss(view, token);
                view.setAlpha(1f);
                view.setTranslationY(0f);
                layoutParams.width = originalWidth;
                view.setLayoutParams(layoutParams);
            }
        });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                layoutParams.width = (Integer) animation.getAnimatedValue();
                view.setLayoutParams(layoutParams);
            }
        });
        animator.start();
    }
}
