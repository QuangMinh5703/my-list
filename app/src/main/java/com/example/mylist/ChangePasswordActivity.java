package com.example.mylist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {


    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_change_password);

        EditText editTextCurrentPassword = findViewById(R.id.editTextCurrentPassword);
        EditText editTextNewPassword = findViewById(R.id.editTextNewPassword);
        Button buttonChangePassword = findViewById(R.id.buttonChangePassword1);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        buttonChangePassword.setOnClickListener(v -> {
            String currentPassword = editTextCurrentPassword.getText().toString().trim();
            String newPassword = editTextNewPassword.getText().toString().trim();

            if (currentPassword.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Vui lòng nhập đầy đủ mật khẩu.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPassword.length() < 6) {
                Toast.makeText(getApplicationContext(), "Mật khẩu mới phải có ít nhất 6 ký tự.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (user != null && user.getEmail() != null) {
                // Bước 1: Xác thực lại người dùng với mật khẩu hiện tại
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

                user.reauthenticate(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Bước 2: Nếu xác thực thành công, đổi mật khẩu
                        user.updatePassword(newPassword).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Đổi mật khẩu thành công! Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();

                                // Đăng xuất và chuyển sang LoginActivity
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                String error = task1.getException() != null ? task1.getException().getMessage() : "Lỗi không xác định";
                                Toast.makeText(getApplicationContext(), "Lỗi khi đổi mật khẩu: " + error, Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "Mật khẩu hiện tại không đúng.", Toast.LENGTH_SHORT).show();
                    }
                });


            }

        });


    }
}
