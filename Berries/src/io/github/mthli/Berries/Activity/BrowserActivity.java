package io.github.mthli.Berries.Activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.*;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import io.github.mthli.Berries.R;
import io.github.mthli.Berries.Unit.ViewUnit;

public class BrowserActivity extends Activity {
    private LinearLayout controlPanel;
    private LinearLayout ecPanel;
    private ImageButton listButton;
    private ImageButton historyButton;
    private ImageButton shareButton;
    private ImageButton copyButton;
    private ImageButton moreButton;
    private boolean expand = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browser);

        // TODO
        initUI();
    }

    private void initUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager.TaskDescription description = new ActivityManager.TaskDescription(
                    getString(R.string.app_name),
                    BitmapFactory.decodeResource(getResources(), R.drawable.ic_task_description),
                    getResources().getColor(R.color.blue_500)
            );
            setTaskDescription(description);
        }

        controlPanel = (LinearLayout) findViewById(R.id.browser_control_panel);
        ecPanel = (LinearLayout) findViewById(R.id.browser_control_ec);
        listButton = (ImageButton) findViewById(R.id.browser_control_list);
        historyButton = (ImageButton) findViewById(R.id.browser_control_history);
        shareButton = (ImageButton) findViewById(R.id.browser_control_share);
        copyButton = (ImageButton) findViewById(R.id.browser_control_copy);
        moreButton = (ImageButton) findViewById(R.id.browser_control_more);

        listButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    listButton.getBackground().setAlpha(155);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    listButton.getBackground().setAlpha(255);
                }

                return false;
            }
        });
        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
            }
        });

        historyButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    historyButton.getBackground().setAlpha(155);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    historyButton.getBackground().setAlpha(255);
                }

                return false;
            }
        });
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
            }
        });

        shareButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    shareButton.getBackground().setAlpha(155);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    shareButton.getBackground().setAlpha(255);
                }

                return false;
            }
        });
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
            }
        });

        copyButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    copyButton.getBackground().setAlpha(155);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    copyButton.getBackground().setAlpha(255);
                }

                return false;
            }
        });
        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
            }
        });

        moreButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    moreButton.getBackground().setAlpha(155);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    moreButton.getBackground().setAlpha(255);
                }

                return false;
            }
        });
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (expand) {
                    ViewUnit.collapse(ecPanel);
                    moreButton.setImageDrawable(getResources().getDrawable(R.drawable.browser_control_expand));
                    moreButton.setBackgroundColor(getResources().getColor(R.color.black));
                    expand = false;
                } else {
                    ViewUnit.expand(ecPanel);
                    moreButton.setImageDrawable(getResources().getDrawable(R.drawable.browser_control_collapse));
                    moreButton.setBackgroundColor(getResources().getColor(R.color.black));
                    expand = true;
                }
            }
        });
    }

    // TODO
}
