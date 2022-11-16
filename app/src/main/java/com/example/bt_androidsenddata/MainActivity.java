package com.example.bt_androidsenddata;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private TextView dataReceived;
    private EditText dataToSend;
    private AutoCompleteTextView beURL;
    private CheckBox defaultDataCheck;
    private String defaultData;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ArrayAdapter<String> adapterURLs;
    private ArrayList<String> urls;
    private final String URLS_DELIMITER = "§";

    private RequestQueue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queue = Volley.newRequestQueue(this);

        dataReceived = findViewById(R.id.dataReceived);
        dataReceived.setMovementMethod(new ScrollingMovementMethod());

        dataToSend = findViewById(R.id.dataToSend);
        beURL = findViewById(R.id.beURL);

        defaultDataCheck = findViewById(R.id.defaultDataCheck);
        try (InputStream f = getResources().openRawResource(getResources().getIdentifier("default_data", "raw", getPackageName()))) {
            defaultData = IOUtils.toString(f, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }

        sharedPreferences = getPreferences(MODE_PRIVATE);
        editor = sharedPreferences.edit();
        beURL.setText(sharedPreferences.getString("beURL", ""));
        beURL.setThreshold(0);
        urls = new ArrayList<>(Arrays.asList(sharedPreferences.getString("urls", "").split(URLS_DELIMITER)));
        if (urls.size() == 1 && urls.get(0).equals("")) {
            urls.remove(0);
            urls.add("https://httpbin.org/anything");
        }
        adapterURLs = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, urls);
        beURL.setAdapter(adapterURLs);

    }

    public void defaultDataCheck(View view) {
        if (defaultDataCheck.isChecked()) {
            dataToSend.setText(defaultData);

        } else {
            dataToSend.setText("");
            beURL.setText("");
        }
    }

    public void sendData(View view) {
        dataReceived.setText("Sending data...");
        JSONObject data = new JSONObject();
        try {
            data.put("data", dataToSend.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String url = beURL.getText().toString();
        if (!urls.contains(url)) {
            urls.add(url);
            adapterURLs.notifyDataSetChanged();
            editor.putString("urls", String.join(URLS_DELIMITER, urls));
            editor.apply();
        }

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, data, response -> {
            String prettyRes = null;
            try {
                prettyRes = new JSONObject(response.toString()).toString(4);
            } catch (JSONException e) {
                e.printStackTrace();
                prettyRes = response.toString();
            }
            dataReceived.setText(prettyRes);
        }, error -> dataReceived.setText(error.toString()));

        queue.add(req);

    }
}