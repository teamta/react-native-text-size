
package com.teamta.reactnative.textsize;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.WritableArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.widget.TextView;
import android.graphics.Typeface;
import android.content.res.AssetManager;
import android.content.res.Resources;

public class TextSizeModule extends ReactContextBaseJavaModule {
    public static final String REACT_CLASS = "TextSize";


    private static ReactApplicationContext reactContext = null;

    IntentFilter inf = new IntentFilter();

    public TextSizeModule(ReactApplicationContext context) {
        super(context);
        reactContext = context;
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public Map<String, Object> getConstants() {

        final Map<String, Object> constants = new HashMap<>();
        return constants;
    }

    private static float pxToDp(int px) {
        return ((px / Resources.getSystem().getDisplayMetrics().density));
    }

    @ReactMethod
    public void calculateSize(String text, String fontName,float fontSize, final Promise promise) {
     
         WritableMap map = new WritableNativeMap();

        Activity currentActivity = getCurrentActivity();
        if (currentActivity != null) {
            TextView textView = new TextView(currentActivity);
            Typeface typeface = Typeface.createFromAsset(currentActivity.getAssets(), "fonts/"+fontName+".ttf");
            textView.setTypeface(typeface);
            textView.setText(text);
            textView.setTextSize(fontSize);
            textView.measure(0, 0);
            map.putDouble("width",  pxToDp(textView.getMeasuredWidth()));
            map.putDouble("height", pxToDp(textView.getMeasuredHeight()));
        }

        if (promise != null) {
            promise.resolve(map);
        }
    }
}