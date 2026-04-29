package com.example.logcatapp;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.LayoutRes;

public class Utils {
    public static void layoutDialog(Context context, @LayoutRes int layoutId) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(layoutId);
        dialog.show();
    }
}