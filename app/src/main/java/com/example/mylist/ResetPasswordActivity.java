package com.example.mylist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ResetPasswordActivity extends AppCompatActivity {

    private EditText edtEmail;
    private Button btnSendEmail;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_reset_password);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance();

        initView();
        setListener();
    }

    private void initView() {
        edtEmail = findViewById(R.id.edtEmail);
        btnSendEmail = findViewById(R.id.btnSendEmail);
    }

    private void setListener() {
        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = edtEmail.getText().toString().trim();

                if (isValidEmail(userEmail)) {
                    checkEmailInDatabaseAndSendResetEmail(userEmail);
                } else {
                    Toast.makeText(ResetPasswordActivity.this,
                            "Vui lòng nhập email hợp lệ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isValidEmail(String email) {
        return email != null && !email.isEmpty() &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void checkEmailInDatabaseAndSendResetEmail(String email) {
        btnSendEmail.setEnabled(false);
        Toast.makeText(this, "Đang kiểm tra email và gửi yêu cầu...", Toast.LENGTH_SHORT).show();

        databaseReference.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            sendPasswordResetEmailWithFirebaseAuth(email);
                        } else {
                            ShowMessage("Email không tồn tại trong hệ thống. Vui lòng kiểm tra lại.");
                            btnSendEmail.setEnabled(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        ShowMessage("Lỗi kết nối cơ sở dữ liệu: " + error.getMessage());
                        btnSendEmail.setEnabled(true);
                    }
                });
    }

    private void sendPasswordResetEmailWithFirebaseAuth(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        btnSendEmail.setEnabled(true);

                        if (task.isSuccessful()) {
                            ShowMessage("Email đặt lại mật khẩu đã được gửi! Vui lòng kiểm tra hộp thư của bạn.");
                            new android.os.Handler().postDelayed(new Runnable() {
                                public void run() {
                                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            }, 2000);
                        } else {
                            String errorMessage = "Không thể gửi email đặt lại mật khẩu. Vui lòng thử lại.";
                            if (task.getException() != null) {
                                if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                    errorMessage = "Tài khoản không tồn tại. Vui lòng kiểm tra email hoặc đăng ký.";
                                } else {
                                    errorMessage = "Lỗi: " + task.getException().getMessage();
                                }
                                Log.e("ResetPassword", "Error sending reset email: " + task.getException().getMessage());
                            }
                            ShowMessage(errorMessage);
                        }
                    }
                });
    }

    private void ShowMessage(String message) {
        Toast.makeText(ResetPasswordActivity.this, message, Toast.LENGTH_LONG).show();
    }
}