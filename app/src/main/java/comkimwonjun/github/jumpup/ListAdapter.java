package comkimwonjun.github.jumpup;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;
import java.util.ArrayList;

/**
 * JumpUp
 * Created by KimWonJun on 7/24/2018.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private ArrayList<MessageData> messageList;
    private MediaPlayer mediaPlayer;
    private Context context;
    private String uuid;

    ListAdapter(ArrayList<MessageData> messageList, Context context, String uuid) {
        this.messageList = messageList;
        this.context = context;
        this.uuid = uuid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = -1;
        switch(viewType) {
            case Constants.OTHER_MESSAGE:
                layout = R.layout.item_other;
                break;
            case Constants.USER_MESSAGE:
                layout = R.layout.item_user;
                break;
        }
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessageData messageData = messageList.get(position);
        switch (messageData.getType())
        {
            case Constants.TYPE_TEXT:
                holder.message.setVisibility(View.VISIBLE);
                holder.image.setVisibility(View.GONE);
                holder.audioBtn.setVisibility(View.GONE);
                holder.message.setText(messageData.getMessage());
                holder.message.setTextSize(20);
                break;
            case Constants.TYPE_IMAGE:
                holder.message.setVisibility(View.GONE);
                holder.image.setVisibility(View.VISIBLE);
                holder.audioBtn.setVisibility(View.GONE);
                References.getStorageRef().child(messageData.getMessage())
                        .getDownloadUrl().addOnSuccessListener(uri -> Glide.with(context)
                        .load(uri.toString())
                        .apply(new RequestOptions()
                                .override(500, 500)
                                .placeholder(R.drawable.image))
                        .into(holder.image));
                break;
            case Constants.TYPE_AUDIO:
                holder.message.setVisibility(View.GONE);
                holder.image.setVisibility(View.GONE);
                holder.audioBtn.setVisibility(View.VISIBLE);
                holder.audioBtn.setOnClickListener(v -> {
                    switch (holder.audioBtn.getText().toString()) {
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
                break;
            case Constants.TYPE_NOTICE:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int pos) {
        return uuid.equals(messageList.get(pos).getUuid()) ? Constants.USER_MESSAGE : Constants.OTHER_MESSAGE;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView message;
        ImageView image;
        Button audioBtn;

        ViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            image = itemView.findViewById(R.id.image);
            audioBtn = itemView.findViewById(R.id.audioBtn);
        }
    }
}
