package com.example.teafarmanager;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import Database.DatabaseHelper;

public class UserListActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        databaseHelper = new DatabaseHelper(this);

        // Retrieve the list of users from the database
        List<com.example.teafarmanager.User> users = databaseHelper.getUsers();

        // Create a custom adapter to display the users
        UserAdapter adapter = new UserAdapter(this, users);

        // Find the ListView in the XML layout
        ListView listView = findViewById(R.id.listView);

        // Set the adapter for the ListView
        listView.setAdapter(adapter);

        // Set an item click listener for the ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                com.example.teafarmanager.User selectedUser = users.get(position);

                // Create an intent to start the UserProfileActivity
                Intent intent = new Intent(UserListActivity.this, UserProfileActivity.class);
                intent.putExtra("username", selectedUser.getUsername().trim());
                intent.putExtra("weight", selectedUser.getTotalWeight());
                intent.putExtra("owed amount", selectedUser.getTotalOwedAmount());
                intent.putExtra("amount paid", selectedUser.getTotalAmountPaid());
                intent.putExtra("balance", selectedUser.getBalance());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseHelper.close();
    }
}
