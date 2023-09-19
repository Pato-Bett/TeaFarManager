//package com.example.teafarmanager;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import Database.DatabaseHelper;
//
//public class Login extends AppCompatActivity {
//
//    private EditText etUsername, etIdNumber;
//    private Button btnLogin;
//
//    private DatabaseHelper databaseHelper;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.login);
//
//        etUsername = findViewById(R.id.etUsername);
//        etIdNumber = findViewById(R.id.etId_no);
//        btnLogin = findViewById(R.id.btnLogin);
//
//        databaseHelper = new DatabaseHelper(this);
//
//        btnLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String username = etUsername.getText().toString().trim();
//                String idNumberStr = etIdNumber.getText().toString().trim();
//
//                if (username.isEmpty() || idNumberStr.isEmpty()) {
//                    if (username.isEmpty()) {
//                        etUsername.setError("Username is required");
//                    }
//                    if (idNumberStr.isEmpty()) {
//                        etIdNumber.setError("ID Number is required");
//                    }
//                    Toast.makeText(Login.this, "Enter required fields", Toast.LENGTH_LONG).show();
//                } else {
//                    try {
//                        int idNumber = Integer.parseInt(idNumberStr);
//                        boolean isValidUser = databaseHelper.checkLogin(username, String.valueOf(idNumber));
//
//                        if (isValidUser) {
//                            Toast.makeText(Login.this, "Login successful", Toast.LENGTH_LONG).show();
//                            Intent intent = new Intent(Login.this, UserProfileActivity.class);
//                            startActivity(intent);
//                        } else {
//                            Toast.makeText(Login.this, "Invalid username or ID number", Toast.LENGTH_LONG).show();
//                        }
//                    } catch (NumberFormatException e) {
//                        etIdNumber.setError("Invalid ID number");
//                    }
//                }
//            }
//        });
//    }
//}
