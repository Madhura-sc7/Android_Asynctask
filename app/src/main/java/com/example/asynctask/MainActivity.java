package com.example.asynctask;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;   // added for explicit locale handling

public class MainActivity extends AppCompatActivity {

    TextView statusText;
    Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusText = findViewById(R.id.statusText);
        startButton = findViewById(R.id.startButton);

        startButton.setOnClickListener(v -> {
            new MyBackgroundTask().execute(); // starts the background task
        });
    }

    // inner class - defines what happens in background vs on UI
    private class MyBackgroundTask extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {
            // runs on UI thread, BEFORE background work starts
            statusText.setText(getString(R.string.status_starting));
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
            return "Task Finished!";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // runs on UI thread, called each time publishProgress() is used
            statusText.setText("Working... step " + values[0] + "/5");
        }

        @Override
        protected void onPostExecute(String result) {
            // runs on UI thread, AFTER doInBackground finishes
            statusText.setText(result != null ? result.toUpperCase(Locale.getDefault()) : getString(R.string.status_unknown));
        }
    }
}
