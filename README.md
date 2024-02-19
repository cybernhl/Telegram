## Telegram messenger for Android

[Telegram](https://telegram.org) is a messaging app with a focus on speed and security. It’s superfast, simple and free.
This repo contains the official source code for [Telegram App for Android](https://play.google.com/store/apps/details?id=org.telegram.messenger).

## Creating your Telegram Application

We welcome all developers to use our API and source code to create applications on our platform.
There are several things we require from **all developers** for the moment.

1. [**Obtain your own api_id**](https://core.telegram.org/api/obtaining_api_id) for your application.
2. Please **do not** use the name Telegram for your app — or make sure your users understand that it is unofficial.
3. Kindly **do not** use our standard logo (white paper plane in a blue circle) as your app's logo.
3. Please study our [**security guidelines**](https://core.telegram.org/mtproto/security_guidelines) and take good care of your users' data and privacy.
4. Please remember to publish **your** code too in order to comply with the licences.

### API, Protocol documentation

Telegram API manuals: https://core.telegram.org/api

MTproto protocol manuals: https://core.telegram.org/mtproto

### Compilation Guide

**Note**: In order to support [reproducible builds](https://core.telegram.org/reproducible-builds), this repo contains dummy release.keystore,  google-services.json and filled variables inside BuildVars.java. Before publishing your own APKs please make sure to replace all these files with your own.

You will require Android Studio 3.4, Android NDK rev. 20 and Android SDK 8.1

1. Download the Telegram source code from https://github.com/DrKLO/Telegram ( git clone https://github.com/DrKLO/Telegram.git )
2. Copy your release.keystore into TMessagesProj/config
3. Fill out RELEASE_KEY_PASSWORD, RELEASE_KEY_ALIAS, RELEASE_STORE_PASSWORD in gradle.properties to access your  release.keystore
4.  Go to https://console.firebase.google.com/, create two android apps with application IDs org.telegram.messenger and org.telegram.messenger.beta, turn on firebase messaging and download google-services.json, which should be copied to the same folder as TMessagesProj.
5. Open the project in the Studio (note that it should be opened, NOT imported).
6. Fill out values in TMessagesProj/src/main/java/org/telegram/messenger/BuildVars.java – there’s a link for each of the variables showing where and which data to obtain.
7. You are ready to compile Telegram.

### Localization

We moved all translations to https://translations.telegram.org/en/android/. Please use it.

## Modify to yourself
#### 1.網路環境配置
1. 找到ConnectionsManager.cpp 
> TMessagesProj/jni/tgnet/ConnectionsManager.cpp

```c
void ConnectionsManager::initDatacenters() {
// dev
datacenter->addAddressAndPort("192.168.1.1", 443, 0, "");

// stag/test
datacenter->addAddressAndPort("192.168.2.1", 443, 0, "");

// prod
datacenter->addAddressAndPort("192.168.3.1", 443, 0, "");
}
```
目前改成可以在build.gradle內配置 
搭配cmake的arguments在編譯間段傳入 fd264efbe03169549945b827711b8a9d62193757

#### 2.介面相關
##### LaunchActivity
> TMessagesProj/src/main/java/org/telegram/ui/LaunchActivity.java


TG 採用了單 Activity + 自訂 Fragment 的實作方式，LaunchActivity 就是這個單 Activity 容器；

LaunchActivity 的 contentView 是一個 FrameLayout，它會預設加入一個 DrawerLayoutContainer 用來管理自訂 Fragment；
> TMessagesProj/src/main/java/org/telegram/ui/ActionBar/DrawerLayoutContainer.java

##### IntroActivity (預設啟動頁)
> TMessagesProj/src/main/java/org/telegram/ui/IntroActivity.java


在 IntroActivity 中，中間的圖示是使用 OpenGL 的 EGL 在 c 層 IntroRender 繪製的；
> TMessagesProj/jni/intro/IntroRenderer.c


##### LoginActivity
> TMessagesProj/src/main/java/org/telegram/ui/LoginActivity.java

登陸介面，目前只實現了手機號登陸，
#### ViewTree

- SizeNotifierFrameLayout
    - ScrollView
       - keyboardLinearLayout(Blue)
          - space (填滿頂部statusbar高度)
          - slideViewsContainer(Green)
             - VIEW_SWITCH_LOGIN_TYPE(Red)
             - VIEW_PHONE_INPUT
             - ....
          - keyboardView
    - floatingButtonContainer
    - backButtonView
    - radialProgressView

 
最外層是個SizeNotifierFrameLayout，主要作用是監聽軟鍵盤的高度，從而對內部的view 做些位置上的調整；在其內部目前有4個view，backButtonView 左上角的返回鍵，radialProgressView 暫不清楚是哪裡的 進度條，floatingButtonContainer 是原本用於下一步的圓形floatButton的容器，會根據軟鍵盤的高度自適應底部間距，現在ui 調整把原來的floatButton 改成了“繼續”/“註冊”/“創建帳號” 這些底部按鈕；最後一個view是ScrollVIew；

ScrollView 預設佔滿整個螢幕，內部只有一個 keyboardLinearLayout，在 TG 中它在幾個 code 頁會把底部間距增加 40dp，原因不太清楚，對於我們 ui 來說只起到 ScrollView 本身的滑動作用；

keyboardLinearLayout 內部有三個view(space、slideViewsContainer、keyboardView)，space 用來填滿頂部狀態列的高度，起適配作用，keyboardView 用來顯示app內部自訂的軟鍵盤，中間的slideViewsContainer 則是顯示真正login ui 的 容器；

login ui 中所有的頁面都SlideView，父類為LinearLayout，擴展了一些onShow/onHide/onBackPressed, 所有SlideView 維護在一個views 數組中，並且TG 在LoginAcitivty 中提供了一個setPage 函數來實現SlideView 的切換，效果看 上去和Activity 的切換一樣。
