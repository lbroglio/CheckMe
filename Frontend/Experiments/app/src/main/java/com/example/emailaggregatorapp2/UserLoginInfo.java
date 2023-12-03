package com.example.emailaggregatorapp2;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public abstract class UserLoginInfo {
    /**
     * Username saved for making API requests
     */
    public static String username;
    /**
     * password saved for making API requests
     */
    public static String password;

    public static boolean isAdmin;
}
