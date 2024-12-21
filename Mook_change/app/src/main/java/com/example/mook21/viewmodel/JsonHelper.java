package com.example.mook21.viewmodel;

import android.content.Context;
import android.util.Log;

import com.example.mook21.model.Mix;
import com.example.mook21.model.Sound;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for loading JSON data from assets folder.
 */
public class JsonHelper {

    private static final String TAG = "JsonHelper";

    /**
     * Reads mixs.json from assets and parses it into a list of Mix objects.
     *
     * @param context Application context
     * @return List of Mix objects
     */
    public static List<Mix> loadMixes(Context context) {
        return loadJsonFile(context, "mixs.json", new TypeToken<List<Mix>>() {}.getType());
    }

    /**
     * Reads sounds.json from assets and parses it into a list of Sound objects.
     *
     * @param context Application context
     * @return List of Sound objects
     */


    /**
     * Generic method to read a JSON file from assets and parse it into a list of objects.
     *
     * @param context  Application context to access assets.
     * @param fileName Name of the JSON file in assets folder.
     * @param type     TypeToken representing the desired list type.
     * @param <T>      Generic type for the list.
     * @return List of objects parsed from the JSON file.
     */
    private static <T> List<T> loadJsonFile(Context context, String fileName, Type type) {
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            String json = new String(buffer, "UTF-8");

            return new Gson().fromJson(json, type);
        } catch (IOException e) {
            Log.e(TAG, "Error reading " + fileName, e);
            return Collections.emptyList();
        }
    }
    public static List<Sound> loadSounds(Context context) {
        try {
            // Mở file sounds.json từ assets
            InputStream inputStream = context.getAssets().open("sounds.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            // Chuyển dữ liệu từ bytes sang String JSON
            String json = new String(buffer, "UTF-8");

            // Sử dụng Gson để parse JSON thành danh sách Sound
            Type soundListType = new TypeToken<List<Sound>>() {}.getType();
            return new Gson().fromJson(json, soundListType);
        } catch (IOException e) {
            Log.e(TAG, "Error reading sounds.json", e);
            return Collections.emptyList(); // Trả về list rỗng nếu gặp lỗi
        }
    }

}
