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
        setContentView(R.layout.activity_admin); // Đảm bảo layout này là layout bạn đã cung cấp

        // Khởi tạo các view
        tvAdminTitle = findViewById(R.id.tvAdminTitle);
        btnSearchUser  = findViewById(R.id.btnSearchUser);
        btnListUser  = findViewById(R.id.btnListUser);

        // Thiết lập sự kiện cho nút tìm kiếm người dùng
        btnSearchUser .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến Activity tìm kiếm người dùng
                Intent intent = new Intent(AdminActivity.this, SearchUserActivity.class);
                startActivity(intent);
            }
        });

        // Thiết lập sự kiện cho nút danh sách người dùng
        btnListUser .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến Activity danh sách người dùng
                Intent intent = new Intent(AdminActivity.this, ListUserActivity.class);
                startActivity(intent);
            }
        });
    }
}
