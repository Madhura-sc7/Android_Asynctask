package com.example.asynctask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    TextView StatusText;
    Button StartButton;

    private ExecutorService BackgroundExecutor;
    private Handler MainHandler;
    private volatile boolean IsCancelled = false;
    private volatile boolean IsTaskRunning = false;   // prevents duplicate task submissions

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StatusText = findViewById(R.id.statusText);
        StartButton = findViewById(R.id.startButton);

        BackgroundExecutor = Executors.newSingleThreadExecutor();
        MainHandler = new Handler(Looper.getMainLooper());

        StartButton.setOnClickListener(v -> RunBackgroundTask());
    }

    private void RunBackgroundTask() {
        if (IsTaskRunning) {
            return; // ignore repeated presses while a task is already in progress
        }
        IsTaskRunning = true;
        StartButton.setEnabled(false); // disable button while work is running

        StatusText.setText(getString(R.string.status_starting));

        BackgroundExecutor.execute(() -> {
            int totalSteps = 5;
            String finalResult = getString(R.string.status_finished);

            for (int i = 1; i <= totalSteps; i++) {
                if (IsCancelled) {
                    return;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    finalResult = getString(R.string.status_interrupted);
                    break;
                }

                final int progress = i;
                final int total = totalSteps;
                MainHandler.post(() -> {
                    if (!IsCancelled) {
                        StatusText.setText(getString(R.string.status_progress, progress, total));
                    }
                });
            }

            final String resultToShow = finalResult;
            MainHandler.post(() -> {
                if (!IsCancelled) {
                    StatusText.setText(resultToShow.toUpperCase(Locale.getDefault()));
                    StartButton.setEnabled(true);  // re-enable button once task finishes
                    IsTaskRunning = false;
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IsCancelled = true;
        BackgroundExecutor.shutdownNow();
    }
}
