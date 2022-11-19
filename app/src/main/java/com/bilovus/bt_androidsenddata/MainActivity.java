package com.bilovus.bt_androidsenddata;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.commons.io.IOUtils;
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

        findViewById(R.id.sendData).setOnLongClickListener(v -> {
            Intent intent = new Intent(this, ListURLActivity.class);
            intent.putExtra("urls", urls);
            startActivityForResult(intent, 1);

            return true;
        });

    }

    @Override
    @Deprecated
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            urls = data.getStringArrayListExtra("urls");
            adapterURLs.clear();
            adapterURLs.addAll(urls);
            adapterURLs.notifyDataSetChanged();
            editor.putString("urls", String.join(URLS_DELIMITER, urls));
            editor.apply();
        }
    }

    public void defaultDataCheck(View view) {
        if (defaultDataCheck.isChecked()) {
            dataToSend.setText(defaultData);

        } else {
            dataToSend.setText("");
        }
    }

    public void sendData(View view) {
        String url = beURL.getText().toString();
        if (!url.isEmpty()) {
            dataReceived.setText("Sending data...");
            JSONObject data = new JSONObject();
            try {
                data.put("data", dataToSend.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            int index = urls.indexOf(url);
            if (index == -1) {
                urls.add(0, url);
                editor.putString("urls", String.join(URLS_DELIMITER, urls));
                editor.apply();
            } else {
                urls.remove(index);
                urls.add(0, url);
            }
            adapterURLs.notifyDataSetChanged();

            StringRequest req = new StringRequest(Request.Method.POST, url, response -> dataReceived.setText(response), error -> dataReceived.setText(error.toString())) {
                @Override
                public byte[] getBody() {
                    return data.toString().getBytes();
                }
            };

            queue.add(req);
        } else {
            Toast.makeText(this, "URL is empty", Toast.LENGTH_SHORT).show();
        }

    }
}