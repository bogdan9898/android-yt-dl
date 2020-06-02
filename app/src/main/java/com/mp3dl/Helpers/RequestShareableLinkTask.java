package com.mp3dl.Helpers;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.mp3dl.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RequestShareableLinkTask extends AsyncTask<Object, Void, String> {
    private static final String TAG = "ReqSLTask: ";

    private JSONObject data;
    private Activity activity;
    private String vId;
    private int status;

    public RequestShareableLinkTask(Activity activity, JSONObject data) throws JSONException {
        this.data = data;
        this.activity = activity;
        vId = VideoUrl.GetVIdFromURL(activity, data.getString("url"));
    }

    @Override
    protected String doInBackground(Object... objects) {
        StringBuilder response = new StringBuilder();

        try {
            URL hostUrl = new URL(activity.getString(R.string.cloud_py_ydl_host) + "/shareable-link");

            HttpURLConnection connection = (HttpURLConnection) hostUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            OutputStream outputStream = connection.getOutputStream();
            byte[] dataBytes = this.data.toString().getBytes(StandardCharsets.UTF_8);
            outputStream.write(dataBytes, 0, dataBytes.length);

            InputStream inputStream;
            this.status = connection.getResponseCode();
            if(this.status == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();
            } else {
                inputStream = connection.getErrorStream();
            }
            Log.d(TAG, "doInBackground STATUS: " + status);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                response.append(line.trim());
            }
//            Toast.makeText(context, response.toString(), Toast.LENGTH_LONG).show();
            outputStream.close();
            inputStream.close();
            bufferedReader.close();
        } catch (IOException e) {
//            e.printStackTrace();
            Log.e(TAG, "doInBackground: ", e);
        }
        return response.toString();
    }

    @Override
    protected void onPostExecute(String data) {
        super.onPostExecute(data);
        Log.d(TAG, "onPostExecute: " + data);
        if(data.equals("")) return;
        if(this.status != HttpURLConnection.HTTP_OK) {
            // todo: generate log file
            Toast.makeText(this.activity, "Error: check log file", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            JSONObject jsonData = new JSONObject(data);
            jsonData.put("vId", this.vId);
            jsonData.remove("status");
            new RequestVideoNameTask(activity, jsonData).execute();
        } catch (JSONException e) {
//            e.printStackTrace();
            Log.e(TAG, "onPostExecute: ", e);
        }
    }
}
