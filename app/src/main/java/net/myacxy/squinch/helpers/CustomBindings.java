package net.myacxy.squinch.helpers;

import android.databinding.BindingAdapter;
import android.net.Uri;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.rengwuxian.materialedittext.MaterialEditText;

import net.myacxy.retrotwitch.utils.StringUtil;

public class CustomBindings
{
    private CustomBindings() { }

    @BindingAdapter({"error"})
    public static void setError(MaterialEditText view, String error) {
        view.setError(error);
    }

    @BindingAdapter({"imageUrl"})
    public static void loadImage(SimpleDraweeView view, String url) {
        if (!StringUtil.isBlank(url))
        {
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
}
