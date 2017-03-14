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

import java.util.HashMap;
import java.util.Map;

public class JoinActivity extends AppCompatActivity {

    Button addBtn;
    EditText editTextID;
    EditText editTextPW;
    EditText editTextNick;

    FirebaseDatabase database;
    DatabaseReference userRef;

    boolean createFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        createFlag = false;

        editTextID = (EditText) findViewById(R.id.editTextID);
        editTextPW = (EditText) findViewById(R.id.editTextPW);
        editTextNick = (EditText) findViewById(R.id.editTextNick);
        addBtn = (Button) findViewById(R.id.addBtn);
        addBtn.setOnClickListener(clickListener);

        // 1. 파이어베이스 연결 - DB connection
        database = FirebaseDatabase.getInstance();
        // 2. CRUD 작업의 기준이 되는 노드(user)를 레퍼런스로 가져온다
        userRef = database.getReference("user");
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String id = editTextID.getText().toString();

            userRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                // DB 2. 파이어베이스는 데이터쿼리가 완료되면 스냅샷에 담아서 onDataChange를 호출해준다.
                // 현재 데이터베이스로 본다면 datasnapshot는 aaa - name - 홍길동 , aaa- password - 123을 넘겨준다
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() > 0) {
                        Toast.makeText(JoinActivity.this, "아이디가 이미 있습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        createFlag = true;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            // 생성 플래그가 참인경우 생성.
            if( createFlag ) {

                //id에 담긴 이름을 통해 userRef 의 차일드를 생성후 idRef에 담아주었다.
                DatabaseReference idRef = userRef.child(id);

                String pw = editTextPW.getText().toString();
                String nick = editTextNick.getText().toString();

                Map<String, String> idMap = new HashMap<>();
                idMap.put("name", nick);
                idMap.put("password", pw);

                idRef.setValue(idMap);
                editTextID.setText("");
                editTextPW.setText("");
                editTextNick.setText("");

                Intent intent = new Intent(JoinActivity.this, MainActivity.class);
                Toast.makeText(JoinActivity.this, "등록되었습니다", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        }
    };
}
