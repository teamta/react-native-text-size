package com.intelsports;

import android.content.Context;
import android.os.Parcel;
import android.util.Log;


import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.intelsports.ismagicviewcontainer.ISDataNotifyListener;
import com.intelsports.ismagicviewcontainer.ISDeepLinkListener;

public class ISMagicViewCallback implements com.intelsports.ismagicviewcontainer.ISMagicViewCallback {

    private static ReactApplicationContext reactContext = null;

    public ISMagicViewCallback(ReactApplicationContext currentContext) {
        reactContext = currentContext;
    }

    @Override
    public void requestRelatedContentData(String s, Context context, ISDataNotifyListener dataNotifyListener) {
       // List<ContentInfo> contents = TestData.getRelatedContent();
       // dataNotifyListener.notifyData(contents);
    }

    @Override
    public void onUpdatePlayedPosition(String var1, long var2, long var4) {

        WritableMap values=new WritableNativeMap();

        values.putString("contentId",var1);
        values.putDouble("totalVideoTime",(double) var4);
        values.putDouble("currentPlayTime",(double) var2);

        try {
            sendEvent(IntelSportsModule.VIDEO_WATCH_DATA,values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean shareEnable() {
        return true;
    }

    @Override
    public void getShareDeepLink(String contentId, ISDeepLinkListener linkListener) {
        String deepLink = "https://www.google.com/";
        linkListener.notifyDeepLink(deepLink);
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    protected ISMagicViewCallback(Parcel var1) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ISMagicViewCallback> CREATOR = new Creator<ISMagicViewCallback>() {
        @Override
        public ISMagicViewCallback createFromParcel(Parcel source) {
            return new ISMagicViewCallback(source);
        }

        @Override
        public ISMagicViewCallback[] newArray(int size) {
            return new ISMagicViewCallback[size];
        }
    };

    private void sendEvent(String eventName,
                           @Nullable WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }
}
