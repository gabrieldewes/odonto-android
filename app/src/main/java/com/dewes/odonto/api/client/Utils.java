package com.dewes.odonto.api.client;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import okhttp3.ResponseBody;

/**
 * Created by Dewes on 25/06/2017.
 */

class Utils {

    static String parseErrorBody(ResponseBody responseBody) {
        InputStream i = responseBody.byteStream();
        BufferedReader r = new BufferedReader(new InputStreamReader(i));
        StringBuilder errorResult = new StringBuilder();
        String line;
        try {
            while ((line = r.readLine()) != null) {
                errorResult.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("API", "errorBody "+ errorResult.toString());
        return errorResult.toString();
    }
}
