package net.myacxy.dashclock.twitch.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;

import net.myacxy.dashclock.twitch.R;

public class UserVoiceDialog extends Preference {

    public UserVoiceDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UserVoiceDialog(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public UserVoiceDialog(Context context) {
        super(context);
    }

    public interface DialogItemClickedListener {
        public void itemClicked(int position);
    }

    protected DialogItemClickedListener mListener;

    public void setListener(DialogItemClickedListener listener) {
        mListener = listener;
    }

    @Override
    protected void onClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1,
                getContext().getResources().getStringArray(R.array.pref_feedback_entries));
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.itemClicked(which);
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.setTitle(getContext().getString(R.string.pref_user_voice_title));
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE,
                getContext().getString(android.R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setVisibility(View.GONE);
            }
        });

        dialog.show();
    }
}
