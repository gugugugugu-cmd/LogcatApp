package com.example.logcatapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class LogcatFragment extends Fragment {

    private TextView logView;
    private View forBottom;
    private FloatingActionButton fabAdd;
    private FloatingActionButton fabClear;
    private Toolbar toolbar;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable refreshTask = new Runnable() {
        @Override
        public void run() {
            refreshLogs();
            handler.postDelayed(this, 3000);
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logcat, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        logView = view.findViewById(R.id.logView);
        forBottom = view.findViewById(R.id.forBottom);
        fabAdd = view.findViewById(R.id.fabCheck);
        fabClear = view.findViewById(R.id.fabClear);

        toolbar.setTitle(R.string.log_title);
        toolbar.inflateMenu(R.menu.logcat_menu);
        toolbar.setOnMenuItemClickListener(this::onMenuItemSelected);

        logView.setMovementMethod(ScrollingMovementMethod.getInstance());

        fabAdd.setOnClickListener(v -> {
            LogStore.appendLog(requireContext(), "手动添加一条测试日志");
            Toast.makeText(requireContext(), R.string.log_added, Toast.LENGTH_SHORT).show();
            refreshLogs();
        });

        fabClear.setOnClickListener(v -> {
            LogStore.clearLogs(requireContext());
            LogStore.appendLog(requireContext(), "日志已清空");
            Toast.makeText(requireContext(), R.string.log_cleared, Toast.LENGTH_SHORT).show();
            refreshLogs();
        });

        refreshLogs();
        return view;
    }

    private boolean onMenuItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.help_log) {
            Utils.layoutDialog(requireContext(), R.layout.help_dialog_logcat);
            return true;
        } else if (id == R.id.add_test_log) {
            LogStore.appendLog(requireContext(), "菜单添加了一条测试日志");
            Toast.makeText(requireContext(), R.string.log_added, Toast.LENGTH_SHORT).show();
            refreshLogs();
            return true;
        }
        return true;
    }

    private void refreshLogs() {
        String logs = LogStore.readLogs(requireContext());
        logView.setText(logs);
        forBottom.requestFocus();
        forBottom.clearFocus();
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.post(refreshTask);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(refreshTask);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(refreshTask);
    }
}