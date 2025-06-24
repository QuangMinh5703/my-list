package com.example.mylist;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {

    private Button btnLogout, btnChangePassword;
    LinearLayout btnHome1;
    private TextView tvUserName;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();;
        String userName = intent.getStringExtra("username");

        initView();
        listener();
        tvUserName.setText(userName);
}
    private void initView() {
        View menu = findViewById(R.id.menu_profile);
        btnHome1 = menu.findViewById(R.id.btn_home);
        btnLogout = findViewById(R.id.btn_logout);
        tvUserName = findViewById(R.id.tvUsername);
        btnChangePassword = findViewById(R.id.btn_change_password);
    }

    private void listener() {
        btnHome1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            }
        });

    }
}







