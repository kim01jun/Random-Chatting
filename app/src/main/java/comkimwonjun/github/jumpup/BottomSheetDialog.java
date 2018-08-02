package comkimwonjun.github.jumpup;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Objects;

/**
 * JumpUp
 * Created by KimWonJun on 2018-07-27.
 */
public class BottomSheetDialog extends BottomSheetDialogFragment {

    public static BottomSheetDialog getInstance() {
        return new BottomSheetDialog();
    }

    private RecordingDialog recordingDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottomsheet_chatting, container, false);
        LinearLayout imageLayout = view.findViewById(R.id.imageLayout);
        LinearLayout audioLayout = view.findViewById(R.id.audioLayout);
        recordingDialog = new RecordingDialog(Objects.requireNonNull(getContext()), true, DialogInterface::dismiss, ((ChattingActivity) Objects.requireNonNull(getActivity())).room, ((ChattingActivity) Objects.requireNonNull(getActivity())).uuid);

        imageLayout.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            Objects.requireNonNull(getActivity()).startActivityForResult(Intent.createChooser(intent, "Select Picture"), Constants.REQUEST_GET_IMAGE);
            dismiss();
        });

        audioLayout.setOnClickListener(v -> {
            dismiss();
            recordingDialog.show();
        });

        return view;
    }

}
