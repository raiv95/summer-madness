package com.example.varun.weather;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText info;
    private Button search;
    private String urlStart;
    private String urlEnd;
    private String Url;
    private String jsonData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        info = (EditText) findViewById(R.id.city_zip);
        search = (Button) findViewById(R.id.search_bar);
        urlStart = "api.openweathermap.org/data/2.5/forecast?"; // api call
        urlEnd = "&units=imperial&APPID=03cd22712adf2621367f05cce57f3b0c";
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isNumber = true;
                String location = info.getText().toString(); // save user input
                for (char c: location.toCharArray()) { // checking to see if zip or city name
                    if (!Character.isDigit(c)) {
                        isNumber = false;
                    }
                }
                String query;
                if (isNumber) {
                    query = "zip=";
                } else {
                    query = "q=";
                }
                Url = urlStart + query + location + urlEnd; // full url request
                // check for a network connection
               ConnectivityManager connMgr = (ConnectivityManager)
                       getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    new URLRequest().execute(Url); // download data
                } else { // no connection
                    Toast.makeText(getApplicationContext(), "No network connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private class URLRequest extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            // what do you want to do
            // download the json data from the url
            // need to return the string
            try {
                return downloadData(url[0]);
            } catch (IOException e) {
                return e.toString();
            }
        }
        @Override
        protected void onPostExecute(String result) {
            jsonData = result; // save result
        }
    }
    public String downloadData(String data) throws IOException { // quick fix bracket
        InputStream is = null;
        String content= "";
        try {
            URL url = new URL(data);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000); // ms
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            if (connection.getResponseCode() == 200) {
                connection.connect();
                is = connection.getInputStream();
                content = convertToString(is);
            }
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return content;
    }
    public String convertToString(InputStream inputStream) throws IOException {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }
        return sb.toString(); //
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh, menu); // fill menu with refresh
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_data:
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
