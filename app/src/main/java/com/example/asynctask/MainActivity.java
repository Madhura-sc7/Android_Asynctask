package com.example.asynctask;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView StatusText;   // renamed to PascalCase per naming rule
    Button StartButton;    // renamed to PascalCase per naming rule

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StatusText = findViewById(R.id.statusText);
        StartButton = findViewById(R.id.startButton);

        StartButton.setOnClickListener(v -> {
            new MyBackgroundTask().execute(); // starts the background task
        });
    }

    // inner class - defines what happens in background vs on UI
    // NOTE: AsyncTask is deprecated in modern Android. A safer long-term fix is to
    // replace this with an ExecutorService + Handler, or Kotlin Coroutines, so the
    // background work is cancelled automatically when the Activity is destroyed.
    // Keeping AsyncTask here for now since this is a learning exercise, but flagging
    // it as a known follow-up item.
    private class MyBackgroundTask extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {
            // runs on UI thread, BEFORE background work starts
            StatusText.setText(getString(R.string.status_starting));
        }

        @Override
        protected String doInBackground(Void... voids) {
            // runs on a BACKGROUND thread - simulate slow work
            int totalSteps = 5;

            for (int i = 1; i <= totalSteps; i++) {
                try {
                    Thread.sleep(1000); // pause 1 second, pretending to do heavy work
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // restore interrupt status
                    return getString(R.string.status_interrupted); // stop early instead of continuing silently
                }
                publishProgress(i); // send progress update back to UI thread
            }
            return getString(R.string.status_finished); // now uses string resource
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // runs on UI thread, called each time publishProgress() is used
            if (values != null && values.length > 0) {   // null/empty check before accessing values[0]
                StatusText.setText(getString(R.string.status_progress, values[0]));
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // runs on UI thread, AFTER doInBackground finishes
            StatusText.setText(result != null ? result.toUpperCase(Locale.getDefault()) : getString(R.string.status_unknown));
        }
    }
}
