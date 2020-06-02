package com.mp3dl.DownloaderControllers;

import android.app.Activity;
import android.content.Context;

public interface IDownloaderController {
    void download(Activity activity, String url);
}
