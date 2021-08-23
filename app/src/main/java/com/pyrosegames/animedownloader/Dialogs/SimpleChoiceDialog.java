package com.pyrosegames.animedownloader.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.pyrosegames.animedownloader.Adapters.ThemeAdapter;
import com.pyrosegames.animedownloader.Options;
import com.pyrosegames.animedownloader.OptionsActivity;
import com.pyrosegames.animedownloader.R;

public class SimpleChoiceDialog extends Dialog {

    private Activity mActivity;
    private String title, message, positiveMessage, negativeMessage;
    private PositiveAction positiveAction;
    private NegativeAction negativeAction;

    public interface PositiveAction{
        void onPositiveClick();
    }

    public interface NegativeAction{
        void onNegativeClick();
    }

    public SimpleChoiceDialog(@NonNull Context context, Activity mActivity, String title, String message,
                              String positiveMessage, String negativeMessage, PositiveAction positiveAction, NegativeAction negativeAction) {
        super(context);
        this.mActivity = mActivity;
        this.title = title;
        this.message = message;
        this.positiveMessage = positiveMessage;
        this.negativeMessage = negativeMessage;
        this.positiveAction = positiveAction;
        this.negativeAction = negativeAction;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fragment_simple_choice_dialog);

        Options options = Options.getInstance(mActivity);
        OptionsActivity.Theme.ButtonsColor buttonsColor = options.getButtonsColors(mActivity);

        TextView titleView = findViewById(R.id.simple_choice_title);
        titleView.setText(title);

        TextView messageView = findViewById(R.id.simple_choice_message);
        messageView.setText(message);

        Button positiveButton = findViewById(R.id.simple_choice_yes);
        positiveButton.setBackgroundTintList(buttonsColor.getPositiveButtonColorList());
        positiveButton.setTextColor(buttonsColor.getPositiveTextColor());
        positiveButton.setVisibility(positiveAction == null ? View.INVISIBLE : View.VISIBLE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positiveAction.onPositiveClick();
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                    }
                });
            }
        });

        Button negativeButton = findViewById(R.id.simple_choice_no);
        negativeButton.setBackgroundTintList(buttonsColor.getNegativeButtonColorList());
        negativeButton.setTextColor(buttonsColor.getNegativeTextColor());
        negativeButton.setVisibility(negativeAction == null ? View.INVISIBLE : View.VISIBLE);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                negativeAction.onNegativeClick();
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                    }
                });
            }
        });

    }
}
