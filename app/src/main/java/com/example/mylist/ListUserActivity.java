package com.example.mylist;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mylist.Adapter.ListUserAdapter;
import com.example.mylist.Model.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import java.util.List;

public class ListUserActivity extends AppCompatActivity {

    private TextView tvAdminTitle;
    private Button btnDeleteUser ;
    private RecyclerView recyclerViewUsers;
    private ListUserAdapter userAdapter;
    private List<User> userList;
    private DatabaseReference userRef;
    private String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);
        userList = new ArrayList<>();
        initView();
        initFirebase();
        loadData();
        setupAdapter(userList);

        btnDeleteUser .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUserByUid(id);
            }
        });

    }

    private void deleteUserByUid(String uid) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);

        // Xóa song song cả 2
        Task<Void> deleteFromDB = userRef.removeValue();
        Task<Void> deleteFromAuth = null;

        // Chỉ xóa Authentication nếu là current user
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null && currentUser.getUid().equals(uid)) {
            deleteFromAuth = currentUser.delete();
        }

        // Đợi cả 2 task hoàn thành
        if (deleteFromAuth != null) {
            Tasks.whenAll(deleteFromDB, deleteFromAuth)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("DELETE_USER", "Xóa user hoàn toàn thành công");
                        Toast.makeText(this, "Xóa user thành công", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("DELETE_USER", "Lỗi khi xóa user: " + e.getMessage());
                        Toast.makeText(this, "Lỗi khi xóa user", Toast.LENGTH_SHORT).show();
                    });
        } else {
            deleteFromDB.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Xóa dữ liệu user thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Lỗi khi xóa dữ liệu", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setupAdapter(List<User> userList) {
        userAdapter = new ListUserAdapter(userList, ListUserActivity.this);
        userAdapter.setOnDeleteUserClickListener((user, view) -> {
            String uid = user.getId();
            id = uid;
        });
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUsers.setAdapter(userAdapter);
    }

    private void loadData() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.clear(); // Xóa danh sách cũ trước khi thêm dữ liệu mới
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class); // Giả sử User có constructor phù hợp
                    userList.add(user); // Thêm người dùng vào danh sách
                }
                userAdapter.notifyDataSetChanged(); // Cập nhật adapter
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ListUser Activity", "loadData:onCancelled", databaseError.toException());
            }
        });
    }

    private void initFirebase() {
        userRef = FirebaseDatabase.getInstance().getReference("Users");
    }

    private void initView() {
        tvAdminTitle = findViewById(R.id.tvAdminTitle);
        btnDeleteUser  = findViewById(R.id.btnDeleteUser );
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
    }


}
