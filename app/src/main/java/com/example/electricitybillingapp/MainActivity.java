package com.example.electricitybillingapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper myDb;
    EditText etUnits;
    Spinner spinnerMonth;
    RadioGroup rgRebate;
    TextView tvResult;

    Button btnCalculate, btnSave, btnAbout, btnHistory, btnClear;

    private double currentUnits = 0;
    private double currentTotalCharge = 0;
    private double currentRebatePct = 0;
    private double currentFinalCost = 0;
    private boolean isCalculated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Database and UI elements
        myDb = new DatabaseHelper(this);
        etUnits = findViewById(R.id.etUnits);
        spinnerMonth = findViewById(R.id.spinnerMonth);
        rgRebate = findViewById(R.id.rgRebate);
        tvResult = findViewById(R.id.tvResult);

        btnAbout = findViewById(R.id.btnAbout);
        btnHistory = findViewById(R.id.btnHistory);
        btnClear = findViewById(R.id.btnClear);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnSave = findViewById(R.id.btnSave);

        // Reset state when rebate selection changes
        rgRebate.setOnCheckedChangeListener((group, checkedId) -> {
            isCalculated = false;
            tvResult.setText("Rebate changed. Please calculate again.");
            tvResult.setTextColor(Color.RED);
        });

        // Button Listeners
        btnCalculate.setOnClickListener(v -> performCalculation());

        btnSave.setOnClickListener(v -> {
            if (isCalculated) {
                saveToDatabase(currentUnits, currentTotalCharge, currentRebatePct, currentFinalCost);
            } else {
                Toast.makeText(this, "Please calculate the bill first!", Toast.LENGTH_SHORT).show();
            }
        });

        btnAbout.setOnClickListener(v -> startActivity(new Intent(this, AboutActivity.class)));
        btnHistory.setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class)));

        if (btnClear != null) {
            btnClear.setOnClickListener(v -> clearFields());
        }
    }

    private boolean performCalculation() {
        String input = etUnits.getText().toString().trim();

        if (input.isEmpty()) {
            etUnits.setError("Please enter units used");
            isCalculated = false;
            return false;
        }

        try {
            currentUnits = Double.parseDouble(input);

            // TIERED CALCULATION WITH MATH.FLOOR
            // We floor the total to match your sample sheet's RM 32 requirement
            if (currentUnits <= 200) {
                currentTotalCharge = Math.floor(currentUnits * 0.218);
            } else if (currentUnits <= 300) {
                currentTotalCharge = Math.floor((200 * 0.218) + ((currentUnits - 200) * 0.334));
            } else if (currentUnits <= 600) {
                currentTotalCharge = Math.floor((200 * 0.218) + (100 * 0.334) + ((currentUnits - 300) * 0.516));
            } else {
                currentTotalCharge = Math.floor((200 * 0.218) + (100 * 0.334) + (300 * 0.516) + ((currentUnits - 600) * 0.546));
            }

            // REBATE CALCULATION
            currentRebatePct = getSelectedRebate();
            double rebateAmount = currentTotalCharge * currentRebatePct;
            currentFinalCost = currentTotalCharge - rebateAmount;

            // Display breakdown
            displayResults(currentTotalCharge, rebateAmount, currentFinalCost);

            isCalculated = true;
            return true;

        } catch (NumberFormatException e) {
            etUnits.setError("Invalid number format");
            isCalculated = false;
            return false;
        }
    }

    private double getSelectedRebate() {
        int selectedId = rgRebate.getCheckedRadioButtonId();
        if (selectedId == R.id.rb1) return 0.01;
        if (selectedId == R.id.rb2) return 0.02;
        if (selectedId == R.id.rb3) return 0.03;
        if (selectedId == R.id.rb4) return 0.04;
        if (selectedId == R.id.rb5) return 0.05;
        return 0.0;
    }

    private void displayResults(double totalCharge, double rebateAmount, double finalCost) {
        tvResult.setVisibility(View.VISIBLE);
        tvResult.setTextColor(Color.BLACK);
        tvResult.setTypeface(null, Typeface.BOLD);

        // Receipt-style formatting
        String resultText = String.format(
                "BILL SUMMARY\n" +
                        "----------------------------\n" +
                        "Total Charges:  RM %.2f\n" +
                        "Rebate (%d%%):  -RM %.2f\n" +
                        "----------------------------\n" +
                        "Final Cost:     RM %.2f",
                totalCharge, (int)(currentRebatePct * 100), rebateAmount, finalCost);

        tvResult.setText(resultText);
    }

    private void saveToDatabase(double units, double total, double rebate, double finalCost) {
        String month = spinnerMonth.getSelectedItem().toString();
        boolean isInserted = myDb.insertData(month, units, total, rebate * 100, finalCost);

        if (isInserted) {
            Toast.makeText(this, "Record saved for " + month, Toast.LENGTH_SHORT).show();
            isCalculated = false;
        } else {
            Toast.makeText(this, "Save Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        etUnits.setText("");
        rgRebate.clearCheck();
        tvResult.setText("");
        tvResult.setVisibility(View.GONE);
        isCalculated = false;
        currentTotalCharge = 0;
        currentUnits = 0;
        currentRebatePct = 0;
        currentFinalCost = 0;
    }
}