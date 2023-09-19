package com.example.teafarmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import Database.DatabaseHelper;

public class Register extends AppCompatActivity {

    private EditText etUsername, etId_number;
    private Button btnRegister;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.etUsername);
        etId_number = findViewById(R.id.etId_no);
        btnRegister = findViewById(R.id.btnRegister);

        databaseHelper = new DatabaseHelper(this);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String idNumberStr = etId_number.getText().toString().trim();

                if (username.isEmpty() || idNumberStr.isEmpty()) {
                    if (username.isEmpty()) {
                        etUsername.setError("Username is required to register!!!");
                    }
                    if (idNumberStr.isEmpty()) {
                        etId_number.setError("Id number is required to register!!!");
                    }
                    Toast.makeText(Register.this, "Enter required fields", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        int idNumber = Integer.parseInt(idNumberStr);
                        boolean verifyUser = databaseHelper.checkLogin(username, String.valueOf(idNumber));

                        if (!verifyUser) {
                            double totalWght = 0.0; // Set the appropriate value for total_wght
                            double totAmnt = 0.0; // Set the appropriate value for tot_amnt

                            databaseHelper.insertUser(username.trim(), idNumber, totalWght, totAmnt);
                            Toast.makeText(Register.this, username + " Registered successfully.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Register.this, UserListActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(Register.this, "User already exists", Toast.LENGTH_LONG).show();
                        }
                    } catch (NumberFormatException e) {
                        // Handle the case where the input ID number is not a valid integer
                        etId_number.setError("Invalid ID number");
                    }
                }
            }
        });

    }

    private boolean isValidInput(String username, String idNumberStr) {
        return !username.isEmpty() && !idNumberStr.isEmpty();
    }
}
