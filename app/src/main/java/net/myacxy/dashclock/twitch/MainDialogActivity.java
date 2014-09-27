package net.myacxy.dashclock.twitch;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;

import net.myacxy.dashclock.twitch.ui.MainDialog;

public class MainDialogActivity extends Activity implements MainDialog.DialogListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fragmentManager = getFragmentManager();
        MainDialog dialog = new MainDialog();
        dialog.show(fragmentManager, "dialog");
    }

    @Override
    public void onDialogDismiss(DialogInterface dialog) {
        finish();
    }
}
