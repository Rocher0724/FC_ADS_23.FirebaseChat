package choongyul.android.com.firebasechat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomListActivity extends AppCompatActivity {
    private static final String TAG = "RoomListActivity";
    ListView listView;
    List<Room> datas = new ArrayList<>();
    ListViewAdapter listAdapter;
    // 데이터 베이스 연결
    FirebaseDatabase database;
    DatabaseReference roomRef;
    Button btnAdd;

    String userid;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        Intent intent = getIntent();
        userid = intent.getExtras().getString("userid");
        username = intent.getExtras().getString("username");

        // 파이어베이스 연결
        database = FirebaseDatabase.getInstance();
        roomRef = database.getReference("room");

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(clickListener);

        listView = (ListView) findViewById(R.id.listView);
        listAdapter = new ListViewAdapter(this, datas);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Room room = datas.get(position);
                Intent intent = new Intent(RoomListActivity.this, RoomActivity.class);
                intent.putExtra("key",room.getKey());
                intent.putExtra("title",room.getTitle());
                intent.putExtra("userid",userid);
                intent.putExtra("username",username);
                startActivity(intent);
            }
        });

        roomRef.addValueEventListener(roomListener);
    }

    int i = 1;
    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DatabaseReference addRoomRef = roomRef.push();
            addRoomRef.setValue(i+"번째 방");
            i = i+1;
        }
    };

    // 데이터 베이스 변경시 알아채는 리스너
    ValueEventListener roomListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            datas.clear();
            for( DataSnapshot snapshot : dataSnapshot.getChildren() ) {
                Room room = new Room();
                room.setKey(snapshot.getKey());
                room.setTitle(snapshot.getValue().toString());

                datas.add(room);
            }
            listAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            // ...
        }
    };
}

class ListViewAdapter extends BaseAdapter {
    Context context;
    List<Room> datas;
    LayoutInflater inflater;
    public ListViewAdapter(Context context, List<Room> datas) {
        this.context = context;
        this.datas = datas;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) { //한번 화면에 세팅됐던 행은 convertView에 담겨져서 돌아온다
            convertView = inflater.inflate(R.layout.item_room_list, null);
        }
        Room room = datas.get(position);
        TextView roomTitle = (TextView) convertView.findViewById(R.id.roomTitle);
        roomTitle.setText(room.getTitle());




        return convertView;
    }
}