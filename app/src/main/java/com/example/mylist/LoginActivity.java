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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
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
    private FirebaseAuth mAuth;

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
        mAuth = FirebaseAuth.getInstance(); // Khởi tạo FirebaseAuth
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

        if (username.equals("admin"))
        {
            loginAdmin(password);
            return;
        }

        findEmailByUsername(username, password);
    }

    private void loginAdmin( String password)
    {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean found = false;

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String username = userSnapshot.child("username").getValue(String.class);
                    String passwordInDb = userSnapshot.child("password").getValue(String.class);

                    if ("admin".equals(username)) {
                        found = true;
                        if (password != null && password.equals(passwordInDb)) {
                            Toast.makeText(getApplicationContext(), "Đăng nhập admin thành công", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Mật khẩu admin sai", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                }

                if (!found) {
                    Toast.makeText(getApplicationContext(), "Không tìm thấy tài khoản admin", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Lỗi Firebase: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void findEmailByUsername(String username, String password) {
        userRef.orderByChild("username").equalTo(username)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                            User user = userSnapshot.getValue(User.class);

                            if (user != null && user.getEmail() != null) {
                                loginWithFirebaseAuth(user.getEmail(), password);
                            } else {
                                ShowMessage("Không thể lấy thông tin email từ username.");
                                resetLoginButton();
                            }
                        } else {
                            ShowMessage("Tên đăng nhập không tồn tại.");
                            resetLoginButton();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        ShowMessage("Lỗi kết nối cơ sở dữ liệu: " + error.getMessage());
                        resetLoginButton();
                    }
                });
    }


    private void loginWithFirebaseAuth(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String uid = mAuth.getCurrentUser().getUid();

                            userRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    User currentUser = snapshot.getValue(User.class);
                                    if (currentUser != null) {
                                        ShowMessage("Đăng nhập thành công!");
                                        goToMainActivity(currentUser.getId(), currentUser.getUsername());
                                    } else {
                                        ShowMessage("Đăng nhập thành công tài khoản hoặc mật khẩu không đúng.");
                                        mAuth.signOut();
                                    }
                                    resetLoginButton();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    ShowMessage("Lỗi khi tải thông tin người dùng: " + error.getMessage());
                                    mAuth.signOut();
                                    resetLoginButton();
                                }
                            });

                        } else {
                            resetLoginButton();
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                ShowMessage("Tài khoản không tồn tại. Vui lòng đăng ký.");
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                ShowMessage("Mật khẩu không đúng. Vui lòng kiểm tra lại.");
                            } catch (Exception e) {
                                ShowMessage("Đăng nhập thất bại: " + e.getMessage());
                            }
                        }
                    }
                });
    }

    private void resetLoginButton() {
        btnLogin.setEnabled(true);
        btnLogin.setText("ĐĂNG NHẬP");
    }

    private void goToMainActivity(String uid, String userName) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("uid", uid);
        intent.putExtra("username", userName);
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