package com.example.asynctask;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;                 // unused import — code quality issue
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView statusText;
    Button startButton;
    String last_result;                  // naming violation — Field must be PascalCase, and snake_case used

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
            statusText.setText("Starting..."); // hardcoded string — code quality issue (should use string resource)
        }

        @Override
        protected String doInBackground(Void... Voids) {   // naming violation — parameter must be camelCase (voids)
            // runs on a BACKGROUND thread - simulate slow work
            int TotalSteps = 5; // naming violation — local variable must be camelCase (totalSteps)

            for (int i = 1; i <= TotalSteps; i++) {
                try {
                    Thread.sleep(1000); // pause 1 second, pretending to do heavy work
                } catch (InterruptedException e) {
                    // empty catch block — code quality issue, swallows interrupt silently
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
            statusText.setText(result.toUpperCase()); // no null check — code quality issue (possible NPE)
        }
    }
}
