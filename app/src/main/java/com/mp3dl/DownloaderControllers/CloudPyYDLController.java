package com.mp3dl.DownloaderControllers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.mp3dl.Helpers.RequestShareableLinkTask;
import com.mp3dl.Helpers.VideoUrl;
import com.mp3dl.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CloudPyYDLController implements IDownloaderController {
    private static final String TAG = "CloudPyYDLController: ";

    @Override
    public void download(Activity activity, String videoUrl) {
        String vId = VideoUrl.GetVIdFromURL(activity, videoUrl);
//        Toast.makeText(context, vid, Toast.LENGTH_LONG).show();

        try {
            JSONObject jsonData = new JSONObject();
            jsonData.put("url", videoUrl);
            new RequestShareableLinkTask(activity, jsonData).execute(activity);
        } catch (JSONException e) {
//            e.printStackTrace();
            Log.e(TAG, "download: ", e);
        }

//        String vId = VideoUrl.GetVIdFromURL(context, videoUrl);
////        Toast.makeText(context, vid, Toast.LENGTH_LONG).show();
//        try{
//            URL hostUrl = new URL(context.getString(R.string.cloud_py_ydl_host)); // + "/shareable-link"
//            HttpURLConnection connection = (HttpURLConnection) hostUrl.openConnection();
//            connection.setRequestMethod("POST");
//            connection.setRequestProperty("Content-Type", "application/json");
//            connection.setDoInput(true);
//            connection.setDoOutput(true);
//            JSONObject data = new JSONObject();
//            data.put("url", videoUrl);
//            OutputStream outputStream = connection.getOutputStream();
//            byte[] dataBytes = data.toString().getBytes("utf-8");
//            outputStream.write(dataBytes, 0, dataBytes.length);
//
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
//            StringBuilder response = new StringBuilder();
//            String line = null;
//            while((line = bufferedReader.readLine()) != null) {
//                response.append(line.trim());
//            }
//            Toast.makeText(context, response.toString(), Toast.LENGTH_LONG).show();
//
//        } catch (IOException | JSONException e) {
//            Log.e(TAG, "download: ", e);
//        }
    }
}
