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

import com.example.mylist.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class RegisterActivity extends AppCompatActivity {

    private EditText edtUsername, edtEmail, edtPassword, edtRePassword;
    private Button btnRegister;
    private DatabaseReference userRef;
    private FirebaseAuth mAuth; // Thêm biến FirebaseAuth
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_register);

        initView();
        initFirebase();
        setListener();
    }

    private void initView() {
        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtRePassword = findViewById(R.id.edtRePassword);
        btnRegister = findViewById(R.id.btnRegister);
    }

    private void initFirebase() {
        userRef = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance(); // Khởi tạo FirebaseAuth
    }

    private void setListener() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performRegister();
            }
        });
    }

    private void performRegister() {
        String username = edtUsername.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String rePassword = edtRePassword.getText().toString().trim();

        // Validation
        if (username.isEmpty()) {
            edtUsername.setError("Vui lòng nhập tên đăng nhập");
            edtUsername.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            edtEmail.setError("Vui lòng nhập email");
            edtEmail.requestFocus();
            return;
        }

        if (!isValidEmail(email)) {
            edtEmail.setError("Email không hợp lệ");
            edtEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            edtPassword.setError("Vui lòng nhập mật khẩu");
            edtPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            edtPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            edtPassword.requestFocus();
            return;
        }

        if (rePassword.isEmpty()) {
            edtRePassword.setError("Vui lòng nhập lại mật khẩu");
            edtRePassword.requestFocus();
            return;
        }

        if (!password.equals(rePassword)) {
            edtRePassword.setError("Mật khẩu nhập lại không khớp");
            edtRePassword.requestFocus();
            return;
        }

        btnRegister.setEnabled(false);
        btnRegister.setText("Đang đăng ký...");

        // **Chỉ kiểm tra tên đăng nhập trong Realtime Database**
        // Email sẽ được Firebase Auth kiểm tra trùng lặp
        checkUsernameExists(username, email, password);
    }

    private void checkUsernameExists(String username, String email, String password) {
        userRef.orderByChild("username").equalTo(username) // Chỉ kiểm tra username
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            ShowMessage("Tên đăng nhập đã tồn tại!");
                            resetRegisterButton();
                        } else {
                            createUserWithFirebaseAuth(username, email, password);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w(TAG, "checkUsernameExists:cancelled", error.toException());
                        ShowMessage("Lỗi kết nối cơ sở dữ liệu: " + error.getMessage());
                        resetRegisterButton();
                    }
                });
    }

    private void createUserWithFirebaseAuth(String username, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            String uid = mAuth.getCurrentUser().getUid();

                            User newUser = new User(uid, username, email, "");
                            userRef.child(uid).setValue(newUser)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> dbTask) {
                                            if (dbTask.isSuccessful()) {
                                                ShowMessage("Đăng ký thành công!");
                                                goToLoginActivity();
                                            } else {
                                                Log.w(TAG, "saveUserToDatabase:failure", dbTask.getException());
                                                ShowMessage("Đăng ký tài khoản thành công nhưng không lưu được thông tin bổ sung.");
                                                resetRegisterButton();
                                            }
                                        }
                                    });
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                ShowMessage("Email này đã được đăng ký với tài khoản khác!");
                            } else {
                                ShowMessage("Đăng ký thất bại: " + task.getException().getMessage());
                            }
                            resetRegisterButton();
                        }
                    }
                });
    }


    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void resetRegisterButton() {
        btnRegister.setEnabled(true);
        btnRegister.setText("ĐĂNG KÝ");
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void ShowMessage(String message) {
        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
    }

}
