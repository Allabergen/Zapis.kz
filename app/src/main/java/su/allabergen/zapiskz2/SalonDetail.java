package su.allabergen.zapiskz2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SalonDetail extends AppCompatActivity {

    private TextView detailId;
    private TextView detailJsonText;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salon_detail);

        detailId = (TextView) findViewById(R.id.detail_id);
        detailJsonText = (TextView) findViewById(R.id.detail_json);

        id = getIntent().getIntExtra("id", -1);

        new DetailAsyncTask().execute("http://zp.jgroup.kz/rest/v1/salon/page?id=" + id);
    }

    private class DetailAsyncTask extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog = new ProgressDialog(SalonDetail.this);

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Загружаются данные...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(in);
                    int data = reader.read();
                    while (data != -1) {
                        char current = (char) data;
                        result += current;
                        data = reader.read();
                    }


                } else {
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                assert urlConnection != null;
                urlConnection.disconnect();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("DETAIL JSON DATA", "onPostExecute: " + s);

            detailId.setText(String.valueOf(id));
            detailJsonText.setText(s);

            progressDialog.dismiss();
        }
    }
}
