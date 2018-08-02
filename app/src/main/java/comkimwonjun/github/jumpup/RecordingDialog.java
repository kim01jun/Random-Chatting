package comkimwonjun.github.jumpup;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * InProgress)JumpUp-Camp
 * Created by KimWonJun on 2018-08-01.
 */
//TODO: 83번째줄 IllegalStateException 고치기
public class RecordingDialog extends Dialog {

    private Button recordingBtn;
    private MediaRecorder mRecorder;
    private String mFileName;
    private String uuid;
    private String room;
    private int isRecording = 0;

    RecordingDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener, String room, String uuid) {
        super(context, cancelable, cancelListener);
        this.uuid = uuid;
        this.room = room;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        Objects.requireNonNull(getWindow()).setAttributes(lpWindow);
        setContentView(R.layout.dialog_recording);
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/rc.3gp";

        recordingBtn = findViewById(R.id.recordingBtn);
        recordingBtn.setOnClickListener(view -> {
            switch (isRecording)
            {
                case 0:
                    startRecording();
                    isRecording = 1;
                    break;
                case 1:
                    stopRecording();
                    isRecording = 0;
                    break;
            }
        });
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recordingBtn.setText("중지");
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        recordingBtn.setClickable(false);
        recordingBtn.setText("업로드중");

        final String path = room + "/" + uuid + "/" + System.currentTimeMillis() + ".3gp";
        References.getStorageRef().child(path)
                .putFile(Uri.fromFile(new File(mFileName)))
                .addOnSuccessListener(taskSnapshot -> {
                    References.getDbRef().child(room).push().setValue(new MessageData(Constants.TYPE_AUDIO, uuid, path));
                    this.dismiss();
                })
                .addOnFailureListener(Throwable::printStackTrace);
        this.dismiss();
    }
}
