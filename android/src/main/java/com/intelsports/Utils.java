
package com.intelsports;

import android.text.TextUtils;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Utils {

    private static Object readableMapToObject(ReadableMap readableMap, String key) {
        if (readableMap == null || TextUtils.isEmpty(key)) {
            return null;
        }

        Object object;

        ReadableType readableType = readableMap.getType(key);
        switch (readableType) {
            case Null:
                object = key;
                break;
            case Boolean:
                object = readableMap.getBoolean(key);
                break;
            case Number:
                // int or double.
                double tmp = readableMap.getDouble(key);
                if (tmp == (int) tmp) {
                    object = (int) tmp;
                } else {
                    object = tmp;
                }
                break;
            case String:
                object = readableMap.getString(key);
                break;
            case Map:
                object = readableMapToMap(readableMap.getMap(key));
                break;
            case Array:
                object = readableArrayToList(readableMap.getArray(key));
                break;
            default:
                throw new IllegalArgumentException("Failed to convert to object with key: " + key + ".");
        }

        return object;
    }

    static Map<String, Object> readableMapToMap(ReadableMap readableMap) {
        if (readableMap == null) {
            return null;
        }

        ReadableMapKeySetIterator iterator = readableMap.keySetIterator();
        if (!iterator.hasNextKey()) {
            return null;
        }

        Map<String, Object> map = new HashMap<>();
        while (iterator.hasNextKey()) {
            String key = iterator.nextKey();
            map.put(key, readableMapToObject(readableMap, key));
        }

        return map;
    }

    private static List<Object> readableArrayToList(ReadableArray readableArray) {
        if (readableArray == null) {
            return null;
        }

        List<Object> list = new ArrayList<>(readableArray.size());
        for (int index = 0; index < readableArray.size(); index++) {
            ReadableType readableType = readableArray.getType(index);
            switch (readableType) {
                case Null:
                    list.add(String.valueOf(index));
                    break;
                case Boolean:
                    list.add(readableArray.getBoolean(index));
                    break;
                case Number:
                    // int or double.
                    double temp = readableArray.getDouble(index);
                    if (temp == (int) temp) {
                        list.add((int) temp);
                    } else {
                        list.add(temp);
                    }
                    break;
                case String:
                    list.add(readableArray.getString(index));
                    break;
                case Map:
                    list.add(readableMapToMap(readableArray.getMap(index)));
                    break;
                case Array:
                    list = readableArrayToList(readableArray.getArray(index));
                    break;
                default:
                    throw new IllegalArgumentException("Failed to convert object with index: " + index + ".");
            }
        }

        return list;
    }

    static String getStringForKey(ReadableMap map, String key) {
        if (map != null && map.hasKey(key)) {
            return map.getString(key);
        }
        return null;
    }

    static boolean getBooleanForKey(ReadableMap map, String key) {
        if (map != null && map.hasKey(key)) {
            return map.getBoolean(key);
        }
        return false;
    }
}
