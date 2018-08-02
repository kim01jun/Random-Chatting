package comkimwonjun.github.jumpup;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    Button startBtn;
    String roomId;
    String uuid;
    Intent intent;
    ProgressDialog progressDialog;
    ValueEventListener valueEventListener;

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString("uuid", uuid);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startBtn = findViewById(R.id.startBtn);
        uuid = savedInstanceState != null ? (String) savedInstanceState.get("uuid") : UUID.randomUUID().toString();
        progressDialog = new ProgressDialog(MainActivity.this, view -> {
            if(valueEventListener != null)
                References.getDbRef().child("emptyRoom").removeEventListener(valueEventListener);
            References.getDbRef().child("emptyRoom").setValue(null);
            progressDialog.dismiss();
        });

        startBtn.setOnClickListener(view -> {
            progressDialog.show();

            intent = new Intent(getApplicationContext(), ChattingActivity.class);
            intent.putExtra("uuid", uuid);

            References.getDbRef().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    roomId = (String) dataSnapshot.child("emptyRoom").getValue();
                    if(roomId == null) {
                        roomId = System.currentTimeMillis() + uuid;
                        References.getDbRef().child("emptyRoom").setValue(roomId);
                        valueEventListener = References.getDbRef().child("emptyRoom").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getValue() == null) {
                                    intent.putExtra("room", roomId);
                                    startActivity(intent);
                                    progressDialog.dismiss();
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                        });
                    } else {
                        References.getDbRef().child("emptyRoom").setValue(null);
                        intent.putExtra("room", roomId);
                        startActivity(intent);
                        progressDialog.dismiss();
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.REQUEST_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case Constants.REQUEST_PERMISSION:
                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                    Log.d(Constants.TAG, "GRANTED");
                else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle("알림");
                    alert.setNegativeButton("종료", (dialog, which) -> finish());
                    alert.setPositiveButton("설정", (dialog, which) -> {
                        dialog.dismiss();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
                        startActivityForResult(intent, 1);
                    });
                    alert.setMessage("랜덤채팅을 이용하려면 권한이 필요합니다");
                    alert.show();
                }
                break;
        }
    }
}
