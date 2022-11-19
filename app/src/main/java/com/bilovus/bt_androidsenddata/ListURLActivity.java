package com.bilovus.bt_androidsenddata;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ListURLActivity extends AppCompatActivity {

    ClipboardManager clipboard;
    private ArrayList<String> urls;
    private ArrayAdapter<String> adapter;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_url);

        clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        ListView listView = findViewById(R.id.listURLs);
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("What do you want to do with this URL?");
            builder.setMessage(urls.get(position));

            builder.setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss());
            builder.setNegativeButton("Delete", (dialog, which) -> {
                urls.remove(position);
                Toast.makeText(this, "URL deleted", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
            });
            builder.setPositiveButton("Copy", (dialog, which) -> {
                ClipData clip = ClipData.newPlainText("URL", urls.get(position));
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "URL copied", Toast.LENGTH_SHORT).show();
            });
            builder.show();
            return false;
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            intent.putExtra("urlSelected", urls.get(position));
            done();
            finish();
        });

        intent = getIntent();
        urls = intent.getStringArrayListExtra("urls");
        adapter = new URLAdapter(this, urls);
        listView.setAdapter(adapter);
        done();
    }

    public void clearURLs(View view) {
        urls.clear();
        done();
        Toast.makeText(this, "URLs cleared", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void done() {
        intent.putExtra("urls", urls);
        setResult(RESULT_OK, intent);
    }
}
