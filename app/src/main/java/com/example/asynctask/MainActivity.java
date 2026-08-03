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

    private ExecutorService BackgroundExecutor;   // runs background work off the UI thread
    private Handler MainHandler;                  // posts results back to the UI thread
    private volatile boolean IsCancelled = false;  // lets us stop the loop safely on destroy

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StatusText = findViewById(R.id.statusText);
        StartButton = findViewById(R.id.startButton);

        BackgroundExecutor = Executors.newSingleThreadExecutor();
        MainHandler = new Handler(Looper.getMainLooper());

        StartButton.setOnClickListener(v -> runBackgroundTask());
    }

    private void runBackgroundTask() {
        StatusText.setText(getString(R.string.status_starting));

        BackgroundExecutor.execute(() -> {
            int totalSteps = 5;
            String finalResult = getString(R.string.status_finished);

            for (int i = 1; i <= totalSteps; i++) {
                if (IsCancelled) {
                    return; // Activity is gone, stop work immediately
                }
                try {
                    Thread.sleep(1000); // pause 1 second, pretending to do heavy work
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    finalResult = getString(R.string.status_interrupted);
                    break;
                }

                final int progress = i;
                MainHandler.post(() -> {
                    if (!IsCancelled) {
                        StatusText.setText(getString(R.string.status_progress, progress));
                    }
                });
            }

            final String resultToShow = finalResult;
            MainHandler.post(() -> {
                if (!IsCancelled) {
                    StatusText.setText(resultToShow != null
                            ? resultToShow.toUpperCase(Locale.getDefault())
                            : getString(R.string.status_unknown));
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IsCancelled = true;          // stop posting UI updates once Activity is destroyed
        BackgroundExecutor.shutdownNow(); // stop the background thread
    }
}
