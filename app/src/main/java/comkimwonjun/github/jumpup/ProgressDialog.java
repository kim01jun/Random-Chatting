package comkimwonjun.github.jumpup;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.util.Objects;

/**
 * JumpUp
 * Created by KimWonJun on 2018-07-30.
 */
public class ProgressDialog extends Dialog {
    private Button button;
    private View.OnClickListener onClickListener;

    ProgressDialog(@NonNull Context context, View.OnClickListener onClickListener) {
        super(context);
        this.onClickListener = onClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        Objects.requireNonNull(getWindow()).setAttributes(lpWindow);
        setContentView(R.layout.dialog_progress);
        button = findViewById(R.id.progress_cancel);
        button.setOnClickListener(onClickListener);
    }
}
