Ninja
===

A web browser that open links in background without ever living your favorite apps.

Open source copy of [Link Bubble](https://play.google.com/store/apps/details?id=com.linkbubble.playstore "Link Bubble") and [Flynx](https://play.google.com/store/apps/details?id=com.flynx "Flynx").

[Download latest Ninja.apk]( "")

__LONG TERM MAINTENANCE.__

## Screenshot:

![all_in_one.png](/Art/screenshot/en/all_in_one.png "all_in_one.png")

## How to use Ninja?

Basically Ninja is a simple web browser like any others, but you can open links in background follow the steps below:

 1. Set Ninja as your __default browser__ when click links.

 2. __Single tap__ will open links in background, and show a __clickable__ notification in statusbar.

 3. __Double taps__ will show a dialog that allows you to open links in foreground, etc.

## QA:

### Why not Google Play?

Ninja is still under development, we need to add some features and fix some bugs, when we think it is right time we will deploy Ninja to Google Play(maybe tomorrow :P).

### Why no incognito mode?

Incognito mode is a necessary feature for a web browser, but since `WebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing)` was __deprecated__ in API level 17 and no longer supported, the incognito mode is __conflict__ with our UI design, so we stop it(but maybe restart to develop it someday). If you want to add incognito mode you can fork our sourse code and do it by yourself :)

### What can I do for Ninja?

 - New design launcher icon(must be 512px * 512px).

 - Translate `strings_general.xml` at [this link](https://github.com/mthli/Ninja/blob/master/Ninja/res/values/strings_general.xml "strings_general.xml").

 - Fork and pull request is welcome all time :)

## How to use the source code?

Just import the `Ninja` folder with your __IntelliJ IDEA__.

## Thanks:

[Lightning-Browser](https://github.com/anthonycr/Lightning-Browser "Lightning-Browser")/[Thunder-Browser](https://github.com/anthonycr/Thunder-Browser "Thunder-Browser") created by [Anthony Restaino](https://github.com/anthonycr "Anthony Restaino").

## License:

_[Apache License, Version 2.0](https://github.com/mthli/Ninja/blob/master/LICENSE "Apache License, Version 2.0")_
