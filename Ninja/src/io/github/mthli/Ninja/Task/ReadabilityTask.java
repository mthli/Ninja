package io.github.mthli.Ninja.Task;

import android.os.AsyncTask;
import io.github.mthli.Ninja.Activity.ReadabilityActivity;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReadabilityTask extends AsyncTask<Void, Void, Boolean> {
    private String query = null;
    private JSONObject result = null;

    private ReadabilityActivity activity;

    public ReadabilityTask(ReadabilityActivity activity, String query) {
        this.activity = activity;
        this.query = query;
    }

    @Override
    protected void onPreExecute() {
        activity.setStatus(ReadabilityActivity.Status.RUNNING);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            URL url = new URL(query);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDefaultUseCaches(true);
            connection.setUseCaches(true);
            connection.connect();

            if (connection.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                reader.close();
                connection.disconnect();

                result = new JSONObject(builder.toString());
            } else {
                result = null;
            }
        } catch (Exception e) {
            result = null;
        }

        if (isCancelled()) {
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean b) {
        activity.setStatus(ReadabilityActivity.Status.IDLE);
        if (b) {
            activity.setResult(result);
            activity.showLoadSuccessful();
        } else {
            activity.setResult(null);
            activity.showLoadError();
        }
    }
}
