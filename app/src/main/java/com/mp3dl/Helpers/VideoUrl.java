package com.mp3dl.Helpers;

import android.content.Context;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VideoUrl {
    static private Pattern mobileUrlPattern;
    static private Pattern desktopUrlPattern;

    static public String GetVIdFromURL(Context context, String url) {
        if(mobileUrlPattern == null) {
            mobileUrlPattern = Pattern.compile("youtu.be/(.*?(?=/|$|&))", Pattern.CASE_INSENSITIVE);
        }
        if(desktopUrlPattern == null) {
            desktopUrlPattern = Pattern.compile("youtube.com/watch\\?v=(.*?(?=/|$|&))", Pattern.CASE_INSENSITIVE);
        }
        String vId = null;
        Matcher matcher = null;
        if(url.contains("youtu.be")) {
            matcher = mobileUrlPattern.matcher(url);
        } else if(url.contains("youtube.com")) {
            matcher = desktopUrlPattern.matcher(url);
        }
        if(matcher != null && matcher.find()) {
            vId = matcher.group(1);
        }
        return vId;
    }
}
