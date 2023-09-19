package com.example.teafarmanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;;
import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;

import Database.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    Button register_new;

    private EditText etUsername, etPassword;

    private Button btnRegister;
    private Button btnEmployees;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        register_new= findViewById(R.id.reg_new);
        btnEmployees=findViewById(R.id.emp_list);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etId_no);
        btnRegister = findViewById(R.id.btnRegister);


        register_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, Register.class);
                startActivity(intent);
            }
        });

        btnEmployees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,UserListActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int itemId = item.getItemId();
        if (itemId==R.id.income_id){
            // Handle item 1 click
            Toast.makeText(this, "Item 1 selected", Toast.LENGTH_SHORT).show();
            return true;
        }
        return  super.onOptionsItemSelected(item);
    }
}