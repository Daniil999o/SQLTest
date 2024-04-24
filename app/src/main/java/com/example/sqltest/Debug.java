package com.example.sqltest;

import android.util.Log;

public class Debug {
    public static void log(String message) {
        Log.println(Log.DEBUG, "DEBUG", message);
    }
}
