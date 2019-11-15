
package com.intelsports;

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
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.intelsports.iscmsdataservice.CMSDataService;
import com.intelsports.iscmsdataservice.ISCallback;
import com.intelsports.iscmsdataservice.ISError;
import com.intelsports.iscommonbase.analytics.Telemetry;
import com.intelsports.ismagicviewcontainer.ISMagicViewActivity;
import com.intelsports.ismagicviewcontainer.models.ConsumptionUrlInfo;
import com.intelsports.ismagicviewcontainer.models.ContentInfo;
import com.intelsports.ismagicviewcontainer.models.AdContentInfo;
import com.intelsports.ismagicviewcontainer.models.MediaInfo;
import com.intelsports.ismagicviewcontainer.models.UrlInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntelSportsModule extends ReactContextBaseJavaModule {
    public static final String REACT_CLASS = "IntelSports";

    public static final String TELEMETRY_EVENT = "TelemetryEvent";
    public static final String VIDEO_WATCH_DATA = "VideoWatchData";

    private static ReactApplicationContext reactContext = null;

    IntentFilter inf = new IntentFilter();

    public IntelSportsModule(ReactApplicationContext context) {
        super(context);

        reactContext = context;
        inf.addAction(Telemetry.BROADCAST_ACTION_InteractiveViewDuration);
        LocalBroadcastManager.getInstance(reactContext).registerReceiver(message, inf);
        CMSDataService.initConfig(context);
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public Map<String, Object> getConstants() {

        final Map<String, Object> constants = new HashMap<>();
        constants.put("TELEMETRY_EVENT", IntelSportsModule.TELEMETRY_EVENT);

        return constants;
    }

    private static ContentInfo.StreamType getStreamTypeForKey(ReadableMap map, String key) {
        if (map != null && map.hasKey(key)) {
            if (map.getString(key).equals("live")){
                return ContentInfo.StreamType.LIVE;
            }
        }
        return ContentInfo.StreamType.VOD;
    }

    private static ContentInfo.ContentType getContentTypeForKey(ReadableMap map, String key) {
        if (map != null && map.hasKey(key)) {
            if (map.getString(key).equals("360 image")) {
                return ContentInfo.ContentType.IMAGE;
            }
        }
        return ContentInfo.ContentType.VIDEO;
    }

    private static MediaInfo.MediaContentType getMediaContentTypeForKey(ReadableMap map, String key) {
        if (map != null && map.hasKey(key)) {
            switch (map.getString(key)) {
                case "180 video":
                    return MediaInfo.MediaContentType.VIDEO180;
                case "2D video":
                    return MediaInfo.MediaContentType.PLANAR;
                case "360 video":
                    return MediaInfo.MediaContentType.VIDEO360;
                case "360 image":
                    return MediaInfo.MediaContentType.IMAGE360;
            }
        }
        return MediaInfo.MediaContentType.PLANAR;
    }

    @ReactMethod
    public void fetch(String url, ReadableMap params, final Promise promise) {
        Map<String, Object> parameters = Utils.readableMapToMap(params);
        CMSDataService.getInstance().getCMSDataJson(url, parameters, new ISCallback<String>() {
            @Override
            public void onResponse(String s) {
                if (promise != null) {
                    promise.resolve(s);
                }
            }

            @Override
            public void onFailure(ISError isError) {
                if (promise != null && isError != null) {
                    promise.reject(String.valueOf(isError.getCode()), isError.getMessage());
                }
            }
        });
    }

    @ReactMethod
    public void playContent(ReadableMap eventMap, Boolean isWifiOnly, Integer startPosition) {

        ContentInfo contentInfo = getContentInfo(eventMap,startPosition);
        Intent intent = new Intent(getCurrentActivity(), ISMagicViewActivity.class);
        intent.putExtra(ISMagicViewActivity.DATA_MAGIC_VIEW_CONTENT, contentInfo);
        intent.putExtra(ISMagicViewActivity.DATA_MAGIC_VIEW_CALLBACK, new ISMagicViewCallback(reactContext));
        intent.putExtra(ISMagicViewActivity.WIFI_ONLY, isWifiOnly);
        //intent.putExtra(ISMagicViewActivity.DATA_DEVICE_CAPABILITY, TestConfig.initISDeviceCapability());
        Activity currentActivity = getCurrentActivity();
        if (currentActivity != null) {
            currentActivity.startActivity(intent);
        }
    }

    @ReactMethod
    public void playAdvtVideo(String url, int skipDuration) {
        AdContentInfo adContentInfo = getAdContentInfo(url,skipDuration);
        Intent intent = new Intent(getCurrentActivity(), ISMagicViewActivity.class);
        intent.putExtra(ISMagicViewActivity.DATA_MAGIC_VIEW_AD_CONTENT, adContentInfo);
        Activity currentActivity = getCurrentActivity();
        if (currentActivity != null) {
            currentActivity.startActivity(intent);
        }
    }

    private static AdContentInfo getAdContentInfo(String url, int skipDuration) {
        AdContentInfo adContentInfo = new AdContentInfo();
        adContentInfo.setUrl(url);
        adContentInfo.setSkipDuration(skipDuration);
        return adContentInfo;
    }

    private static ContentInfo getContentInfo(ReadableMap eventMap,Integer startPosition) {
        ContentInfo contentInfo = new ContentInfo();
        contentInfo.setId(Utils.getStringForKey(eventMap, "id"));

        contentInfo.setStreamType(getStreamTypeForKey(eventMap, "eventType"));
        contentInfo.setContentType(getContentTypeForKey(eventMap, "mediaType"));
        contentInfo.setCaptionLine1(Utils.getStringForKey(eventMap, "title"));
        contentInfo.setCaptionLine2(Utils.getStringForKey(eventMap, "subTitle"));
        contentInfo.setContentThumbnailUrl(Utils.getStringForKey(eventMap, "image"));
        contentInfo.setPremiumContent(Utils.getBooleanForKey(eventMap, "isPremiumContent"));
        //contentInfo.setTotalVideoDuration(getStringForKey(eventMap, "totalVideoDuration")); //TODO: Need type change
        contentInfo.setStartPositionMs(startPosition);

        List<MediaInfo> mediaInfos = new ArrayList<>();
        if (eventMap.hasKey("mediaContent")) {
            ReadableArray mediaArray = eventMap.getArray("mediaContent");

            for (int index = 0; mediaArray != null && index < mediaArray.size(); index++) {
                MediaInfo mediaInfo = new MediaInfo();
                ReadableMap mediaInfoMap = mediaArray.getMap(index);

                mediaInfo.setId(Utils.getStringForKey(mediaInfoMap, "id"));
                mediaInfo.setCameraName(Utils.getStringForKey(mediaInfoMap, "cameraName"));
                mediaInfo.setProduced(Utils.getBooleanForKey(mediaInfoMap, "isProduced"));
                mediaInfo.setPreferredCamera(Utils.getBooleanForKey(mediaInfoMap, "preferredCamera"));
                mediaInfo.setContentType(getMediaContentTypeForKey(mediaInfoMap, "contentType"));
                mediaInfo.setEnabled(Utils.getBooleanForKey(mediaInfoMap, "isEnabled"));

                ConsumptionUrlInfo consumptionUrls = new ConsumptionUrlInfo();
                if (mediaInfoMap.hasKey("consumptionUrls")) {
                    ReadableMap consumptionUrlsMap = mediaInfoMap.getMap("consumptionUrls");
                    if (consumptionUrlsMap != null && consumptionUrlsMap.hasKey("monoscopic")) {
                        ReadableMap monoscopicMap = consumptionUrlsMap.getMap("monoscopic");
                        UrlInfo monoscopicUrlInfo = new UrlInfo();
                        monoscopicUrlInfo.setHigh(Utils.getStringForKey(monoscopicMap, "high"));
                        monoscopicUrlInfo.setMid(Utils.getStringForKey(monoscopicMap, "mid"));
                        monoscopicUrlInfo.setLow(Utils.getStringForKey(monoscopicMap, "low"));
                        consumptionUrls.setMonoscopic(monoscopicUrlInfo);
                    }

                    if (consumptionUrlsMap != null && consumptionUrlsMap.hasKey("stereoscopic")) {
                        ReadableMap stereoscopicMap = consumptionUrlsMap.getMap("stereoscopic");
                        UrlInfo stereoscopicUrlInfo = new UrlInfo();
                        stereoscopicUrlInfo.setHigh(Utils.getStringForKey(stereoscopicMap, "high"));
                        stereoscopicUrlInfo.setMid(Utils.getStringForKey(stereoscopicMap, "mid"));
                        stereoscopicUrlInfo.setLow(Utils.getStringForKey(stereoscopicMap, "low"));
                        consumptionUrls.setStereoscopic(stereoscopicUrlInfo);
                    }
                }

                mediaInfo.setConsumptionUrls(consumptionUrls);
                mediaInfos.add(mediaInfo);
            }
            contentInfo.setMediaInfos(mediaInfos);
        }

        return contentInfo;
    }

    private void sendEvent(ReactApplicationContext currentContext,
                           String eventName,
                           @Nullable WritableMap params) {
        currentContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    private BroadcastReceiver message = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {


        }
    };
}