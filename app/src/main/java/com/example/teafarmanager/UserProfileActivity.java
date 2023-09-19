package com.example.teafarmanager;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import Database.DatabaseHelper;

public class UserProfileActivity extends AppCompatActivity {

    private TextView owedAmountTextView;
    private TextView dateTextView;
    private EditText weightEditText;
    private EditText etAmountPaid;
    private Button addReceiptButton;
    private Button confirmPaymentButton;

    private double weightSum;
    private double owedAmount;
    private String pluckingDate;

    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    private TextView weightSumTextView;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        databaseHelper = new DatabaseHelper(this);

        // Retrieve the user's details from the intent
        String username = getIntent().getStringExtra("username");
        double totalWeight = getIntent().getDoubleExtra("weight", 0.0);
        double totalAmountOwed = getIntent().getDoubleExtra("owed amount", 0.0);
        double totalAmountPaid = getIntent().getDoubleExtra("amount paid", 0.0);
        double balance = getIntent().getDoubleExtra("balance", 0.0);

        Log.d("UserProfileActivity", "Received username: " + username);
        Log.d("UserProfileActivity", "Received totalWeight: " + totalWeight);
        Log.d("UserProfileActivity", "Received totalOwedAmount: " + totalAmountOwed);
        Log.d("UserProfileActivity", "Received totalAmountPaid: " + totalAmountPaid);
        Log.d("UserProfileActivity", "Balance: " + balance);

        TextView usernameTextView = findViewById(R.id.textViewUsername);
        TextView totalWeightTextView = findViewById(R.id.textViewTotalWeight);
        TextView totalOwedAmountTextView = findViewById(R.id.textViewTotalOwedAmount);
        TextView totalAmountPaidTextView = findViewById(R.id.textViewTotalAmountPaid);
        TextView balanceTextView = findViewById(R.id.textViewBalance);


        weightSumTextView = findViewById(R.id.textView_weight_sum);
        dateTextView = findViewById(R.id.text_date);
        Button editButton = findViewById(R.id.btn_edit);

        // Find the additional UI elements
        owedAmountTextView = findViewById(R.id.text_owed_amount);
        weightEditText = findViewById(R.id.edit_weight);
        etAmountPaid = findViewById(R.id.et_amount_paid);
        addReceiptButton = findViewById(R.id.btn_add_receipt);
        confirmPaymentButton = findViewById(R.id.btn_confirm_payment);

        // Set the user's details in the TextViews
        usernameTextView.setText(username);
        totalWeightTextView.setText("Total Weight              :  " + String.valueOf(totalWeight) + " Kg");
        totalOwedAmountTextView.setText("Total Amount Owed : Ksh " +String.valueOf(totalAmountOwed));
        totalAmountPaidTextView.setText("Total Amount Paid   : Ksh " + String.valueOf(totalAmountPaid));
        balanceTextView.setText("Balance                      :  Ksh " + String.valueOf(balance));

        // Initialize date-related components
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Date picker button click listener
        Button pickDateButton = findViewById(R.id.btn_pick_date);
        pickDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        // Add receipt button click listener
        addReceiptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReceipt();
            }
        });

        // Confirm payment button click listener
        confirmPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmPayment();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editEmployeeDetails();
            }
        });

        // Initialize the database connection
        databaseHelper.getWritableDatabase();
    }

    private void showDatePicker() {
        // Get the current date
        Calendar currentDate = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                pluckingDate = dateFormat.format(calendar.getTime());
                updateSelectedDate();
            }
        };

        // Create a date picker dialog with a maximum date set to the current date
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMaxDate(currentDate.getTimeInMillis()); // Set maximum date
        datePickerDialog.show();
    }


    private void addReceipt() {
        String weightString = weightEditText.getText().toString().trim();
        if (!weightString.isEmpty()) {
            try {
                double weight = Double.parseDouble(weightString);
                weightSum += weight;
                owedAmount = weightSum * 8; // Assuming 8 shillings per kilogram
                updateOwedAmount();
                updateTotalWeight();
                clearWeightEditText();
            } catch (NumberFormatException e) {
                // Show a toast message indicating invalid input
                Toast.makeText(this, "Invalid input. Please enter a valid number.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Show a toast message indicating empty input
            Toast.makeText(this, "Please enter a weight value.", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmPayment() {
        // Check if the plucking date has been entered
        if (pluckingDate == null || pluckingDate.isEmpty()) {
            // Show a toast message indicating that the plucking date is required
            dateTextView.setError("Date required!!!");
            Toast.makeText(this, "Please select a plucking date.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve data entered by user i.e plucking record
        String username = getIntent().getStringExtra("username");
        double weight = weightSum;
        double totalOwedAmount = owedAmount;
        double amountPaid = Double.parseDouble(etAmountPaid.getText().toString().trim());
        String pluckingDate = this.pluckingDate;

        // Get the employeeId from the username
        int employeeId = databaseHelper.getUserIdByUsername(username);

        // Check if the plucking record already exists for the selected date
        boolean recordExists = databaseHelper.checkPluckingRecordExists(employeeId, pluckingDate);

        if (recordExists) {
            // Retrieve the existing weight and amount for the selected date
            double existingWeight = databaseHelper.getWeightByDate(employeeId, pluckingDate);
            double existingAmountPaid = databaseHelper.getAmountPaidByDate(employeeId, pluckingDate);
            double existingOwedAmount = databaseHelper.getOwedAmountByDate(employeeId, pluckingDate);

            // Add the new weight and amount to the existing values
            weight += existingWeight;
            totalOwedAmount += existingOwedAmount;
            amountPaid += existingAmountPaid;

            // Update the existing plucking record in the database
            databaseHelper.updatePluckingRecord(employeeId, weight, totalOwedAmount, amountPaid, pluckingDate);
        } else {
            // Insert a new plucking record into the database
            databaseHelper.insertPluckingRecord(employeeId, weight, totalOwedAmount, amountPaid, pluckingDate);
        }

        // Update the total weight and amount in the UI
        databaseHelper.updateTotals();

        // Show a toast message indicating successful payment confirmation
        Toast.makeText(this, "Payment confirmed. Plucking record added.", Toast.LENGTH_SHORT).show();

        // Update the UI
        updateOwedAmount();
        updateTotalWeight();
        clearWeightEditText();

        // Find the TextView that displays the total amount paid
        TextView totalWeightTextView = findViewById(R.id.textViewTotalWeight);
        TextView totalOwedAmountTextView = findViewById(R.id.textViewTotalOwedAmount);
        TextView totalAmountPaidTextView = findViewById(R.id.textViewTotalAmountPaid);
        TextView balanceTextView = findViewById(R.id.textViewBalance);

        // Get the text from the TextView
        String totalWeightText = totalWeightTextView.getText().toString();
        String totalAmountPaidText = totalAmountPaidTextView.getText().toString();
        String totalOwedAmountText = totalOwedAmountTextView.getText().toString();
        String balanceText = balanceTextView.getText().toString();

        // Remove any non-numeric characters (e.g., currency symbols, commas, etc.)
        totalWeightText = totalWeightText.replaceAll("[^0-9.]", "");
        totalOwedAmountText = totalOwedAmountText.replaceAll("[^0-9.]", "");
        totalAmountPaidText = totalAmountPaidText.replaceAll("[^0-9.]", "");
        balanceText = balanceText.replaceAll("[^0-9.]", "");

        // Convert the cleaned text to a double
        double existingTotalWeight = Double.parseDouble(totalWeightText);
        double existingTotalOwedAmount = Double.parseDouble(totalOwedAmountText);
        double existingTotalAmountPaid = Double.parseDouble(totalAmountPaidText);
        double existingBalance = Double.parseDouble(balanceText);


        Log.d("UserProfileActivity", "Existing Total Weight: " + existingTotalWeight);
        Log.d("UserProfileActivity", "Existing Total Amount Owed: " + existingTotalOwedAmount);
        Log.d("UserProfileActivity", "Existing Total Amount Paid: " + existingTotalAmountPaid);
        Log.d("UserProfileActivity", "Existing Balance: " + existingBalance);

        existingTotalWeight = existingTotalWeight + weight;
        existingTotalOwedAmount = existingTotalOwedAmount + totalOwedAmount;
        existingTotalAmountPaid = existingTotalAmountPaid + amountPaid;
        existingBalance = existingBalance - amountPaid;

        // Reset the total weight and owed amount variables
        weightSum = 0.0;
        owedAmount = 0.0;
        etAmountPaid.setText("");

    }

    public void editEmployeeDetails() {
        // Check if the plucking date has been entered
        if (pluckingDate == null || pluckingDate.isEmpty()) {
            // Show a toast message indicating that the plucking date is required
            Toast.makeText(this, "Please select a plucking date.", Toast.LENGTH_SHORT).show();
            return; // Exit the method
        }

        // Get the employeeId from the username
        String username = getIntent().getStringExtra("username");
        int employeeId = databaseHelper.getUserIdByUsername(username);

        // Check if the employee exists in the database
        if (employeeId != -1) {
            // Retrieve the employee details based on the selected date
            double weight = databaseHelper.getWeightByDate(employeeId, pluckingDate);
            double owedAmount = databaseHelper.getOwedAmountByDate(employeeId, pluckingDate);
            double amountPaid = databaseHelper.getOwedAmountByDate(employeeId, pluckingDate);

            // Find the TextViews in the XML layout
            TextView weightSumTextView = findViewById(R.id.textView_weight_sum);
            TextView owedAmountTextView = findViewById(R.id.text_owed_amount);

            // Update the UI with the retrieved details
            weightSumTextView.setText("Total Weight: " + weight);
            owedAmountTextView.setText("Owed Amount: " + owedAmount);

            // Show a toast message indicating that the employee details have been updated
            Toast.makeText(this, "Employee details updated.", Toast.LENGTH_SHORT).show();
        } else {
            // Handle the case when the username does not exist in the Employees table
            Toast.makeText(this, "Invalid username. Employee details not updated.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateSelectedDate() {
        TextView dateTextView = findViewById(R.id.text_date);
        dateTextView.setText("Selected Date: " + pluckingDate);
    }

    private void updateOwedAmount() {
        owedAmountTextView.setText("Owed Amount: " + owedAmount);
    }

    private void clearWeightEditText() {
        weightEditText.setText("");
    }

    private void updateTotalWeight() {
        weightSumTextView.setText("Total Weight: " + weightSum);
    }
}