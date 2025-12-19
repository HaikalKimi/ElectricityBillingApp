package com.example.electricitybillingapp;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    DatabaseHelper myDb; // Database helper instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        myDb = new DatabaseHelper(this);

        // Receive data from HistoryActivity
        String month = getIntent().getStringExtra("MONTH");
        String units = getIntent().getStringExtra("UNITS");
        String total = getIntent().getStringExtra("TOTAL");
        String rebate = getIntent().getStringExtra("REBATE");
        String finalCost = getIntent().getStringExtra("FINAL");

        // Link to XML IDs
        TextView tvMonth = findViewById(R.id.detailMonth);
        TextView tvUnits = findViewById(R.id.detailUnits);
        TextView tvTotal = findViewById(R.id.detailTotal);
        TextView tvRebate = findViewById(R.id.detailRebate);
        TextView tvFinal = findViewById(R.id.detailFinal);
        Button btnDelete = findViewById(R.id.btnDeleteDetail); // Make sure this ID exists in your XML
        Button btnBack = findViewById(R.id.btnBackHistory);

        // --- STYLING ---
        tvMonth.setTextColor(Color.BLACK);
        tvUnits.setTextColor(Color.BLACK);
        tvTotal.setTextColor(Color.BLACK);
        tvRebate.setTextColor(Color.BLACK);
        tvFinal.setTextColor(Color.BLACK);
        tvFinal.setTypeface(null, Typeface.BOLD);

        // --- DISPLAY DATA ---
        tvMonth.setText("Billing Month: " + (month != null ? month : "N/A"));
        tvUnits.setText("Total Usage: " + units + " kWh");
        tvTotal.setText("Total Charges (Before Rebate): RM " + total);
        tvRebate.setText("Rebate Applied: " + rebate + "%");
        tvFinal.setText("Final Bill Amount: RM " + finalCost);

        // --- DELETE LOGIC WITH CONFIRMATION ---
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmation(month, units);
            }
        });

        // Back button logic
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void showDeleteConfirmation(String month, String units) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Record");
        builder.setMessage("Are you sure you want to delete the bill for " + month + "?");

        builder.setPositiveButton("Yes, Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Call the delete method from DatabaseHelper
                Integer deletedRows = myDb.deleteData(month, units);
                if (deletedRows > 0) {
                    Toast.makeText(DetailActivity.this, "Record deleted successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to HistoryActivity
                } else {
                    Toast.makeText(DetailActivity.this, "Error: Record not found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}