package com.mp3dl.Helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.mp3dl.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RequestVideoNameTask extends AsyncTask<Object, Void, String> {
    private static final String TAG = "RequestVideoNameTask";

    private JSONObject data;
    private Activity activity;
    private int status;

    public RequestVideoNameTask(Activity activity, JSONObject data) {
        this.data = data;
        this.activity =  activity;
    }

    @Override
    protected String doInBackground(Object... objects) {
        StringBuilder response = new StringBuilder();

        try {
            String yt_metadata_url = activity.getString(R.string.yt_video_metadata_api);
            yt_metadata_url = yt_metadata_url.replace("{%vId}", this.data.getString("vId"));
//            Log.d(TAG, "doInBackground: vId: " + this.data.getString("vId"));
//            Log.d(TAG, "doInBackground: yt_metadata_url: " + yt_metadata_url);
            URL hostUrl = new URL(yt_metadata_url);

            HttpURLConnection connection = (HttpURLConnection) hostUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoInput(true);

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
            inputStream.close();
            bufferedReader.close();
        } catch (IOException | JSONException e) {
//            e.printStackTrace();
            Log.e(TAG, "doInBackground: ", e);
        }
        return response.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(s.equals("")) return;
        Log.d(TAG, "onPostExecute: " + s);
        if(this.status != HttpURLConnection.HTTP_OK) {
            // todo: generate log file
            Toast.makeText(this.activity, "Error: check log file", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            final JSONObject jsonData = new JSONObject(s);
            final String title = jsonData.getString("title");

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("File name:");
            final FrameLayout frameLayout = new FrameLayout(activity);
            frameLayout.setPadding(32, 16, 32, 16);
            final EditText editText = new EditText(activity);
            frameLayout.addView(editText);
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            editText.setText(title);
            builder.setView(frameLayout);
            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveToFile(title);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        } catch (JSONException e) {
//            e.printStackTrace();
            Log.e(TAG, "onPostExecute: ", e);
        }
    }

    private void saveToFile(String title) {
        try {
            String fileUri = this.data.getString("uri");
            URL hostUrl = new URL(this.activity.getString(R.string.cloud_py_ydl_host) + fileUri);

            HttpURLConnection connection = (HttpURLConnection) hostUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            InputStream inputStream;
            this.status = connection.getResponseCode();
            if(this.status == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();
            } else {
                inputStream = connection.getErrorStream();
            }
            Log.d(TAG, "saveToFile STATUS: " + status);

//            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//            BufferedReader bufferedReader = new BufferedReader();
//            String line = null;
//            while ((line = bufferedReader.readLine()) != null) {
//                response.append(line.trim());
//            }
            String downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
//            Log.d(TAG, "saveToFile: location: " + downloadsFolder + "/" + title + ".mp3");
            File file = new File(downloadsFolder + "/" + title + ".mp3");
            OutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[4096];
            int len;
            while((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }

            inputStream.close();
//            bufferedReader.close();
            outputStream.close();
            Toast.makeText(activity, "File saved successfully in Downloads", Toast.LENGTH_SHORT).show();
            activity.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        } catch (ProtocolException | JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
