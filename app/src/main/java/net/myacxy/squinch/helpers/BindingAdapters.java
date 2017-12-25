package net.myacxy.squinch.helpers;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.databinding.BindingAdapter;
import android.net.Uri;
import android.support.design.widget.TextInputLayout;
import android.util.Property;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import net.myacxy.retrotwitch.utils.StringUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.functions.Consumer;

public class BindingAdapters {

    private static final TypeEvaluator<String> EVALUATOR_STRING_ANIMATED_NUMBERS = new TypeEvaluator<String>() {

        private Pattern pattern = Pattern.compile("\\d+");

        @Override
        public String evaluate(float fraction, String startValue, String endValue) {

            Matcher matcher = pattern.matcher(startValue);
            List<Integer> startInts = new ArrayList<>();
            while (matcher.find()) {
                startInts.add(Integer.valueOf(matcher.group()));
            }

            matcher = pattern.matcher(endValue);
            List<Integer> endInts = new ArrayList<>();
            while (matcher.find()) {
                endInts.add(Integer.valueOf(matcher.group()));
            }

            if (startInts.size() == endInts.size()) {
                Object[] targetInts = new Integer[startInts.size()];
                for (int i = 0; i < startInts.size(); i++) {
                    Integer start = startInts.get(i);
                    Integer end = endInts.get(i);
                    targetInts[i] = (int) (start + (end - start) * fraction);
                }
                return String.format(startValue.replaceAll("\\d+", "%d"), targetInts);
            } else {
                return endValue;
            }
        }
    };

    private static final TypeEvaluator<String> EVALUATOR_STRING_ANIMATED_CHARS = new TypeEvaluator<String>() {

        @Override
        public String evaluate(float fraction, String startValue, String endValue) {

            char[] startChars = startValue.toCharArray();
            char[] endChars = endValue.toCharArray();
            int length = Math.max(startChars.length, endChars.length);

            char[] data = new char[length];

            for (int i = 0; i < length; i++) {
                int start = startChars.length <= i ? ' ' : startChars[i];
                int end = endChars.length <= i ? ' ' : endChars[i];
                float j = length * fraction;
                if (j > i) {
                    data[i] = (char) end;
                } else {
                    data[i] = (char) (start + fraction * (end - start));
                }
            }

            return new String(data).trim();
        }
    };

    private static final Property<TextView, String> PROPERTY_TEXT = new Property<TextView, String>(String.class, "property_text") {
        @Override
        public String get(TextView object) {
            return object.getText().toString();
        }

        @Override
        public void set(TextView object, String value) {
            object.setText(value);
        }
    };

    private static final ThreadLocal<SimpleDateFormat> FORMAT_DATE_AND_TIME = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("dd.MM.yy HH:mm:ss", Locale.getDefault());
        }
    };

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
        viewGroup.setEnabled(enabled);
        setDeepEnabled(viewGroup, enabled);
    }

    @BindingAdapter("et_error")
    public static void setError(EditText view, String error) {
        view.setError(error);
    }

    @BindingAdapter("til_error")
    public static void setError(TextInputLayout view, String error) {
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

    @BindingAdapter("animatedNumbersText")
    public static void setAnimatedNumbersText(TextView view, String text) {
        ObjectAnimator.ofObject(view, PROPERTY_TEXT, EVALUATOR_STRING_ANIMATED_NUMBERS, text)
                .setDuration(600)
                .start();
    }

    @BindingAdapter(value = {"fromAnimatedText", "toAnimatedText"}, requireAll = false)
    public static void setAnimatedText(TextView view, String from, String to) {
        from = StringUtil.isEmpty(from) ? "" : from;
        to = StringUtil.isEmpty(to) ? "" : to;
        ObjectAnimator.ofObject(view, PROPERTY_TEXT, EVALUATOR_STRING_ANIMATED_CHARS, from, to)
                .setDuration(600)
                .start();
    }

    @BindingAdapter("timeToText")
    public static void setTimeToText(TextView view, long time) {
        view.setText(FORMAT_DATE_AND_TIME.get().format(new Date(time)));
    }

    @BindingAdapter("animatedTimeToText")
    public static void setAnimatedTimeToText(TextView view, long time) {
        ObjectAnimator.ofObject(view, PROPERTY_TEXT, EVALUATOR_STRING_ANIMATED_CHARS, "", FORMAT_DATE_AND_TIME.get().format(new Date(time)))
                .setDuration(600)
                .start();
    }

    private static void setDeepEnabled(ViewGroup viewGroup, boolean enabled) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            child.setEnabled(enabled);
            if (child instanceof ViewGroup) {
                setDeepEnabled((ViewGroup) child, enabled);
            }
        }
    }
}
