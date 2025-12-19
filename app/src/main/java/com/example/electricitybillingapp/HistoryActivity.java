package com.example.electricitybillingapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    DatabaseHelper db;
    ListView listView;
    ArrayList<String> listItems;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        db = new DatabaseHelper(this);
        listView = findViewById(R.id.listView);
        listItems = new ArrayList<>();

        loadData();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Cursor cursor = db.getAllData();
            if (cursor != null) {
                if (cursor.moveToPosition(position)) {
                    Intent intent = new Intent(HistoryActivity.this, DetailActivity.class);

                    intent.putExtra("MONTH", cursor.getString(1));
                    intent.putExtra("UNITS", String.format("%.2f", cursor.getDouble(2)));
                    intent.putExtra("TOTAL", String.format("%.2f", cursor.getDouble(3)));
                    intent.putExtra("REBATE", String.format("%.0f", cursor.getDouble(4)));
                    intent.putExtra("FINAL", String.format("%.2f", cursor.getDouble(5)));

                    startActivity(intent);
                }
                cursor.close();
            }
        });
    }

    private void loadData() {
        Cursor cursor = db.getAllData();
        listItems.clear();

        if (cursor == null || cursor.getCount() == 0) {
            Toast.makeText(this, "No records found", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                String record = "Month: " + cursor.getString(1) +
                        "\nBill Amount: RM " + String.format("%.2f", cursor.getDouble(5));
                listItems.add(record);
            }
            cursor.close();
        }

        // CUSTOM ADAPTER TO FORCE BLACK TEXT
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                // simple_list_item_1 always uses the ID text1 for its TextView
                TextView tv = (TextView) view.findViewById(android.R.id.text1);

                // Set text color to Black and make it Bold
                tv.setTextColor(Color.BLACK);
                tv.setTypeface(null, Typeface.BOLD);
                tv.setTextSize(16); // Optional: makes it slightly easier to read

                return view;
            }
        };

        listView.setAdapter(adapter);
    }
}