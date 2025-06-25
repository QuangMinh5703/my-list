package com.example.mylist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {

    private TextView tvAdminTitle;
    private Button btnSearchUser , btnListUser ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        tvAdminTitle = findViewById(R.id.tvAdminTitle);
        btnSearchUser  = findViewById(R.id.btnSearchUser);
        btnListUser  = findViewById(R.id.btnListUser);

        btnSearchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, SearchUserActivity.class);
                startActivity(intent);
            }
        });

        btnListUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, ListUserActivity.class);
                startActivity(intent);
            }
        });
    }
}
