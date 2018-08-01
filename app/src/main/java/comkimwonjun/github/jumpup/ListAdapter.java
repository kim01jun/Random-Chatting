package comkimwonjun.github.jumpup;

import android.media.MediaPlayer;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;
import java.util.ArrayList;

/**
 * JumpUp
 * Created by KimWonJun on 7/24/2018.
 */

//TODO: 리사이클러뷰로 바꾸기
public class ListAdapter extends BaseAdapter {
    private ArrayList<MessageData> messageList;
    private MediaPlayer mediaPlayer;
    private String uuid;

    ListAdapter(ArrayList<MessageData> messageList, String uuid) {
        this.messageList = messageList;
        this.uuid = uuid;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int i) {
        MessageData messageData = messageList.get(i);
        return messageData.getUuid() + ":" + messageData.getMessage();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        return MessageData.TYPE_MAX;
    }

    @Override
    public int getItemViewType(int pos) {
        return messageList.get(pos).getType();
    }

    //TODO: layoutParams 설정 안되는 현상 수정하기
    @Override
    public View getView(int pos, View view, ViewGroup parent) {
        MessageData messageData = messageList.get(pos);

        LinearLayout linearLayout = new LinearLayout(parent.getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(20, 20, 20, 20);

        if (!uuid.equals(messageData.getUuid()))
            layoutParams.gravity = Gravity.START;
        else
            layoutParams.gravity = Gravity.END;

        switch (getItemViewType(pos)) {
            case 0:
                TextView messageText = new TextView(parent.getContext());
                messageText.setLayoutParams(layoutParams);
                messageText.setTextSize(20);
                if (!uuid.equals(messageData.getUuid()))
                    messageText.setText("낯선 사람:" + messageData.getMessage());
                else
                    messageText.setText("나:" + messageData.getMessage());
                linearLayout.addView(messageText);
                break;
            case 1:
                ImageView imageView = new ImageView(parent.getContext());
                imageView.setLayoutParams(layoutParams);
                linearLayout.addView(imageView);
                References.getStorageRef().child(messageData.getMessage())
                        .getDownloadUrl().addOnSuccessListener(uri -> Glide.with(parent)
                        .load(uri.toString())
                        .apply(new RequestOptions()/*.override(500, 500)*/.placeholder(R.drawable.image))
                        .into(imageView));
                break;
            case 2:
                Button button = new Button(parent.getContext());
                button.setText("재생");
                button.setLayoutParams(layoutParams);

                button.setOnClickListener(v -> {
                    switch (button.getText().toString()) {
                        case "재생":
                            ((TextView) v).setText("중지");
                            References.getStorageRef().child(messageData.getMessage())
                                    .getDownloadUrl().addOnSuccessListener(uri -> {
                                if (mediaPlayer != null)
                                    mediaPlayer.release();
                                mediaPlayer = new MediaPlayer();
                                try {
                                    mediaPlayer.setDataSource(uri.toString());
                                    mediaPlayer.prepare();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                mediaPlayer.start();
                            });
                            break;
                        case "중지":
                            ((TextView) v).setText("재생");
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            mediaPlayer = null;
                            break;
                    }
                });
                linearLayout.addView(button);
                break;
        }

        return linearLayout;
    }
}
