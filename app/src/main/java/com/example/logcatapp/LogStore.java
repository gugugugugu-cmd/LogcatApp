package com.example.logcatapp;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogStore {

    private static final String LOG_FILE_NAME = "app.log";

    public static File getLogFile(Context context) {
        return new File(context.getFilesDir(), LOG_FILE_NAME);
    }

    public static synchronized void appendLog(Context context, String message) {
        File file = getLogFile(context);
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());
        String line = "[" + time + "] " + message + "\n";

        try (FileWriter writer = new FileWriter(file, true)) {
            writer.write(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized String readLogs(Context context) {
        File file = getLogFile(context);
        if (!file.exists()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static synchronized void clearLogs(Context context) {
        File file = getLogFile(context);
        try (FileWriter writer = new FileWriter(file, false)) {
            writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}