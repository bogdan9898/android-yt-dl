package com.mp3dl;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mp3dl.DownloaderControllers.CloudPyYDLController;

public class MainActivity extends AppCompatActivity {

    private static final int INTERNET_PERMISSION_CODE = 0;
    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION_CODE = 1;

    private static int permissionsGranted = 0;

    /**
     * todo:
     *      - settings menu:
     *          - auto download: pressing the download button is not required anymore
     *          - set downloads location
     *          - set host URL
     */

    static private String PREFS_KEY_URL = "url";


//    private String[] testURLs = new String[]{
//            "youtu.be/1234/123",
//            "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
//            "https://www.youtu.be/N98IvXZluZ8",
//            "www.youtu.be/N98IvXZluZ8",
//            "youtu.be/N98IvXZluZ8",
//            "youtu.be/N98IvXZluZ8/0000/1111/222",
//            "https://www.youtube.com/watch?v=N98IvXZluZ8/0000/time=?123",
//            "https://www.youtube.com/watch?v=DfLqy83UHk8&list=RDMMDfLqy83UHk8&start_radio=1"
//    };
//    private int testURLsIndex = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // allow HTTP requests
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final CloudPyYDLController downloadController = new CloudPyYDLController();

        Button downloadButton = findViewById(R.id.downloadButton);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = findViewById(R.id.urlTextBox);
                String url = editText.getText().toString();
//                String url = testURLs[testURLsIndex]; // TODO: delete me!!
                if(url.length() <= 0) {
                    Toast.makeText(MainActivity.this, "Invalid url", Toast.LENGTH_LONG).show();
                    return;
                }
//                editText.setText(url); // TODO: delete me!!
                RelativeLayout layout = findViewById(R.id.loadingPanel);
                if(layout.getVisibility() != View.VISIBLE) {
                    layout.setVisibility(View.VISIBLE);
                    downloadController.download(MainActivity.this, url);
                }
            }
        });

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if(Intent.ACTION_SEND.equals(action) && type != null) {
            handleActionSendText(intent);
        }

        if(savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        checkPermissions();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        EditText editText = findViewById(R.id.urlTextBox);

        outState.putString(PREFS_KEY_URL, editText.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        EditText editText = findViewById(R.id.urlTextBox);

        editText.setText(savedInstanceState.getString(PREFS_KEY_URL));
    }

    private void handleActionSendText(Intent intent) {
        EditText editText = findViewById(R.id.urlTextBox);

        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if(sharedText != null) {
            editText.setText(sharedText);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissions() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            Button downloadButton = findViewById(R.id.downloadButton);
            downloadButton.setEnabled(false);
            requestPermissions(
                    new String[] {
                            Manifest.permission.INTERNET
                    },
                    INTERNET_PERMISSION_CODE);
        } else {
            permissionsGranted |= 0b1;
        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Button downloadButton = findViewById(R.id.downloadButton);
            downloadButton.setEnabled(false);
            requestPermissions(
                    new String[] {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    WRITE_EXTERNAL_STORAGE_PERMISSION_CODE);
        } else {
            permissionsGranted |= 0b10;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case INTERNET_PERMISSION_CODE:
                if(grantResults.length >= 1) {
                    if(grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Internet permission denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case WRITE_EXTERNAL_STORAGE_PERMISSION_CODE:
                if(grantResults.length >= 1) {
                    if(grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Write external storage permission denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
        if(permissionsGranted == 0b10) {
            Button downloadButton = findViewById(R.id.downloadButton);
            downloadButton.setEnabled(true);
        }
    }
}
