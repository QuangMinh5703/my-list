package com.example.mylist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mylist.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private TextView tvForgotPassword, tvRegister;
    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private DatabaseReference userRef;


    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_auth_login);

        initView();
        initFirebase();
        setListener();
    }


    private void initView() {
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvRegister = findViewById(R.id.tvRegister);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
    }

    private void initFirebase() {
        userRef = FirebaseDatabase.getInstance().getReference("Users");
    }

    private void setListener() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToResetPasswordActivity();
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegisterActivity();
            }
        });
    }

    private void performLogin() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (username.isEmpty()) {
            edtUsername.setError("Vui lòng nhập tên đăng nhập");
            edtUsername.requestFocus();
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


        btnLogin.setEnabled(false);
        btnLogin.setText("Đang đăng nhập...");


        loginWithEmailAndPassword(username, password);
    }


    private void loginWithEmailAndPassword(String username, String password) {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean loginSuccess = false;
                User currentUser = null;
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    User user = userSnap.getValue(User.class);

                    if (user != null && user.getUsername() != null && user.getPassword() != null) {
                        if (username.equalsIgnoreCase(user.getUsername()) &&
                                password.equals(user.getPassword())) {
                            loginSuccess = true;
                            currentUser = user;
                            break;
                        }
                    }
                }

                if (loginSuccess && currentUser != null) {
                    ShowMessage("Đăng nhập thành công!");
                    goToMainActivity(currentUser.getId());
                } else {
                    ShowMessage("Tài khoản hoặc mật khẩu không đúng!");
                }

                resetLoginButton();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                ShowMessage("Lỗi kết nối cơ sở dữ liệu: " + error.getMessage());
                resetLoginButton();
            }
        });
    }

    private void resetLoginButton() {
        btnLogin.setEnabled(true);
        btnLogin.setText("ĐĂNG NHẬP");
    }

    private void goToMainActivity(Integer id) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
        finish();
    }

    private void goToRegisterActivity() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void goToResetPasswordActivity() {
        Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
        startActivity(intent);
    }

    private void ShowMessage(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
