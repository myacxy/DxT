/**
 * Copyright (c) 2014, Johannes Hoffmann
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.myacxy.dxt.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.Preference;
import android.text.Html;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.myacxy.dxt.R;

public class AboutDialog extends Preference
{
    public AboutDialog(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public AboutDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AboutDialog(Context context) {
        super(context);
    }

    @Override
    protected void onClick() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getContext().getPackageManager().getPackageInfo(
                    getContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = packageInfo.versionName;
        String title = getContext().getResources().getString(R.string.app_name) + " " + version;

        String body = getContext().getResources().getString(R.string.pref_about_body);
        TextView textView = (TextView) View.inflate(getContext(), R.layout.dialog_about, null);
        textView.setText(Html.fromHtml(body));

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(textView)
                .setIcon(R.drawable.twitch_purple)
                .setTitle(title)
                .setNegativeButton("GitHub", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = "https://github.com/myacxy/DashClockTwitch";
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        getContext().startActivity(intent);
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                Drawable icon = getContext().getResources().getDrawable(R.drawable.github_mark);
                button.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
                centerImageAndTextInButton(button);
            }
        });
        dialog.show();
    }

    /**
     * @see <a href="http://stackoverflow.com/a/22581963">Stack Overflow</a>
     */
    protected void centerImageAndTextInButton(Button button) {
        Rect textBounds = new Rect();
        //Get text bounds
        CharSequence text = button.getText();
        if (text != null && text.length() > 0) {
            TextPaint textPaint = button.getPaint();
            textPaint.getTextBounds(text.toString(), 0, text.length(), textBounds);
        }
        //Set left drawable bounds
        Drawable leftDrawable = button.getCompoundDrawables()[0];
        if (leftDrawable != null) {
            Rect leftBounds = leftDrawable.copyBounds();
            int width = button.getWidth() - (button.getPaddingLeft() + button.getPaddingRight());
            int leftOffset = (width - (textBounds.width() + leftBounds.width()) - button.getCompoundDrawablePadding()) / 2 - button.getCompoundDrawablePadding();
            leftBounds.offset(leftOffset, 0);
            leftDrawable.setBounds(leftBounds);
        }
    }
} // AboutDialog
