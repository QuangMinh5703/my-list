package com.example.mylist;

import static android.content.ContentValues.TAG;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class RegisterActivity extends AppCompatActivity {

    private EditText edtUsername, edtEmail, edtPassword, edtRePassword;
    private Button btnRegister;
    private DatabaseReference userRef;
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

        checkUserExists(username, email, password);
    }

    private void checkUserExists(String username, String email, String password) {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean usernameExists = false;
                boolean emailExists = false;

                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    User user = userSnap.getValue(User.class);
                    if (user != null) {
                        if (user.getUsername() != null && user.getUsername().equalsIgnoreCase(username)) {
                            usernameExists = true;
                        }
                        if (user.getEmail() != null && user.getEmail().equalsIgnoreCase(email)) {
                            emailExists = true;
                        }
                    }
                }

                if (usernameExists) {
                    ShowMessage("Tên đăng nhập đã tồn tại!");
                    resetRegisterButton();
                    return;
                }

                if (emailExists) {
                    ShowMessage("Email đã được sử dụng!");
                    resetRegisterButton();
                    return;
                }

                createNewUser(username, email, password);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "checkUserExists:cancelled", error.toException());
                ShowMessage("Lỗi kết nối cơ sở dữ liệu: " + error.getMessage());
                resetRegisterButton();
            }
        });
    }

    private void createNewUser(String username, String email, String password) {
        Integer newUserId = (int) System.currentTimeMillis();

        User newUser = new User(newUserId, username, email, password);

        userRef.child(String.valueOf(newUserId)).setValue(newUser)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            ShowMessage("Đăng ký thành công!");

                            goToLoginActivity();
                        } else {
                            Log.w(TAG, "createUser:failure", task.getException());
                            ShowMessage("Đăng ký thất bại. Vui lòng thử lại!");
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
