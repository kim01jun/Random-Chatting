package comkimwonjun.github.jumpup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Objects;

//TODO: 생명주기 고려해서 Listener 삭제 및 다시 붙이기 / Image, Audio 파일 전송 추가 (Type 을 정해서 0=텍스트, 1=사진, 2=음성) / 나가기 시 MainActivity 로 다시 되돌아감 / Storage Room 폴더 삭제
public class ChattingActivity extends AppCompatActivity {
    RecyclerView listView;
    ListAdapter adapter;
    ArrayList<MessageData> messageList;
    ChildEventListener childEventListener;
    ImageButton sendBtn;
    ImageButton fileBtn;
    EditText sendEdit;
    String room;
    String uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(Constants.TAG, "Creating!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        Intent intent = getIntent();
        room = intent.getStringExtra("room");
        uuid = intent.getStringExtra("uuid");

        final DatabaseReference dbRef = References.getDbRef();
        messageList = new ArrayList<>();

        listView = findViewById(R.id.messageList);
        sendBtn = findViewById(R.id.sendBtn);
        fileBtn = findViewById(R.id.fileBtn);
        sendEdit = findViewById(R.id.sendEdit);

        adapter = new ListAdapter(messageList, getApplicationContext(), uuid);
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setItemAnimator(new DefaultItemAnimator());


        childEventListener = dbRef.child(room).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                MessageData messageData = dataSnapshot.getValue(MessageData.class);
                messageList.add(messageData);
                listView.post(() -> {
                    adapter.notifyItemInserted(messageList.size() - 1);
                    adapter.notifyDataSetChanged();
                });
                runOnUiThread(() -> listView.scrollToPosition(adapter.getItemCount() - 1));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                startActivity(new Intent(ChattingActivity.this, MainActivity.class));
                Toast.makeText(ChattingActivity.this, "상대방이 나갔습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        sendBtn.setOnClickListener(v -> {
            if(sendEdit.getText().toString().isEmpty()) {
                Toast.makeText(this, "보낼 내용을 입력해 주세요", Toast.LENGTH_SHORT).show();
                return;
            }
            dbRef.child(room).push().setValue(new MessageData(Constants.TYPE_TEXT, uuid, sendEdit.getText().toString()));
            sendEdit.setText(null);
        });

        fileBtn.setOnClickListener(v -> BottomSheetDialog.getInstance().show(getSupportFragmentManager(), "bottomSheet"));

        sendBtn.setOnLongClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return false;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(Constants.TAG, "Starting!");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(Constants.TAG, "Resuming");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(Constants.TAG, "Restarting!");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(Constants.TAG, "Pause!");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(Constants.TAG, "Destroyed");
        References.getDbRef().child(room).removeEventListener(childEventListener);
        References.getDbRef().child(room).removeValue();
        References.getStorageRef().child(room).delete();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            switch (requestCode) {
                case Constants.REQUEST_GET_IMAGE:
                    final String path = room + "/" + uuid + "/" + System.currentTimeMillis();
                    References.getStorageRef().child(path)
                            .putFile(data.getData())
                            .addOnSuccessListener(taskSnapshot -> References.getDbRef()
                                    .child(room)
                                    .push()
                                    .setValue(new MessageData(Constants.TYPE_IMAGE, uuid, path)))
                            .addOnFailureListener(Throwable::printStackTrace);
                    break;
            }
        }
    }
}
