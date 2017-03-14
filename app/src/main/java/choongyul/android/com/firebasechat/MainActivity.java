package choongyul.android.com.firebasechat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    EditText etID;
    EditText etPW;
    Button signBtn;
    Button btnAddId;

    FirebaseDatabase database;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. 파이어베이스 연결 - DB connection
        database = FirebaseDatabase.getInstance();
        // 2. CRUD 작업의 기준이 되는 노드(user)를 레퍼런스로 가져온다
        userRef = database.getReference("user");

        etID = (EditText) findViewById(R.id.etID);
        etPW = (EditText) findViewById(R.id.etPW);
        signBtn = (Button) findViewById(R.id.signBtn);
        btnAddId = (Button) findViewById(R.id.btnAddId);

        signBtn.setOnClickListener(clickListener);
        btnAddId.setOnClickListener(clickListener);

    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnAddId:
                    Intent intent = new Intent(MainActivity.this, JoinActivity.class);
                    startActivity(intent);
                    break;
                case R.id.signBtn:
                    final String id = etID.getText().toString();
                    final String pw = etPW.getText().toString();

                    // DB 1. 파이어베이스로 child(id) 레퍼런스에 대한 쿼리를 날린다.
                    userRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        // DB 2. 파이어베이스는 데이터쿼리가 완료되면 스냅샷에 담아서 onDataChange를 호출해준다.
                        // 현재 데이터베이스로 본다면 datasnapshot는 aaa - name - 홍길동 , aaa- password - 123을 넘겨준다
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() > 0) {
                                String fbPw = dataSnapshot.child("password").getValue().toString();
                                String name = dataSnapshot.child("name").getValue().toString();
                                Log.w("MainActivity", "pw=" + fbPw);
                                if (fbPw.equals(pw)) {
                                    Intent intent = new Intent(MainActivity.this, RoomListActivity.class);
                                    intent.putExtra("userid", id);
                                    intent.putExtra("username", name);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(MainActivity.this, "비밀번호가 틀렸습니다", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "User 가 없습니다", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    break;
            }
        }
    };
}
