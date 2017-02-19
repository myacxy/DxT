package net.myacxy.squinch.helpers;

import android.databinding.BindingAdapter;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.rengwuxian.materialedittext.MaterialEditText;

import net.myacxy.retrotwitch.utils.StringUtil;

import io.reactivex.functions.Consumer;

public class BindingAdapters {

    private BindingAdapters() {
        throw new IllegalAccessError();
    }

    @BindingAdapter("android:layout_width")
    public static void setLayoutWidth(View view, float width) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = (int) width;
        view.setLayoutParams(layoutParams);
    }

    @BindingAdapter("android:layout_height")
    public static void setLayoutHeight(View view, float height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) height;
        view.setLayoutParams(layoutParams);
    }

    @BindingAdapter("enabled")
    public static void setEnabled(ViewGroup viewGroup, boolean enabled) {
        viewGroup.setAlpha(enabled ? 1f : 0.38f);

        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            child.setEnabled(enabled);
            if (child instanceof ViewGroup) {
                setEnabled((ViewGroup) child, enabled);
            }
        }
    }

    @BindingAdapter("met_error")
    public static void setError(MaterialEditText view, String error) {
        view.setError(error);
    }

    @BindingAdapter("imageUrl")
    public static void loadImage(SimpleDraweeView view, String url) {
        if (!StringUtil.isEmpty(url)) {
            Uri uri = Uri.parse(url);
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setProgressiveRenderingEnabled(true)
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(view.getController())
                    .build();
            view.setController(controller);
        } else {
            view.setImageURI(Uri.EMPTY);
        }
    }

    @BindingAdapter(value = {"onActionDone"}, requireAll = false)
    public static void onEditorAction(EditText view, final Consumer<String> done) {
        view.setOnEditorActionListener((v, i, e) -> {
            try {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    done.accept(v.getText().toString());
                    return true;
                }
            } catch (Exception e1) {
                return false;
            }
            return false;
        });
    }

    @BindingAdapter(value = {"onEnterUp"}, requireAll = false)
    public static void onKey(EditText view, final Consumer<String> enterUp) {
        view.setOnKeyListener((v, i, e) -> {
            try {
                if (e.getAction() == KeyEvent.ACTION_UP && i == KeyEvent.KEYCODE_ENTER) {
                    enterUp.accept(view.getText().toString());
                    return true;
                }
            } catch (Exception e1) {
                return false;
            }
            return false;
        });
    }
}
