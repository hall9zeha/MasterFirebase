package com.barryzea.firechat.common.utils;

import android.content.Context;
import android.os.Build;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentActivity;

import com.barryzea.firechat.MainModule.view.MainActivity;
import com.barryzea.firechat.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import de.hdodenhof.circleimageview.CircleImageView;

public class UtilsCommon {

    public static String getEmailEncoded(String email){
        String preKey=email.replace("_", "__");
        return preKey.replace(".","_");
    }

    public static String getEmailToTopic(String email){
        String topic = getEmailEncoded(email);
        topic=topic.replace("@", "_64");
        return topic;
    }
    public static void loadImage(Context ctx, String url, ImageView target) {
        RequestOptions options= new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop();
        Glide.with(ctx)
                .load(url)
                .into(target);

    }

    public static boolean validateEmail(Context ctx, TextInputEditText etEmail) {
        boolean isValid=true;
        String email=etEmail.getText().toString().trim();
        if(email.isEmpty()){
            etEmail.setError(ctx.getString(R.string.common_validate_field_required));
            etEmail.requestFocus();
            isValid=false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError(ctx.getString(R.string.common_validate_email_invalid));
            etEmail.requestFocus();
            isValid=false;
        }

        return isValid;
    }

    public static boolean hasMaterialDesign() {
        return Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP;
    }

    public static void showSnackBar(View contentMain, int resMsg) {
        showSnackBar(contentMain, resMsg, Snackbar.LENGTH_SHORT);
    }

    public  static void showSnackBar(View contentMain, int resMsg, int duration) {
        Snackbar.make(contentMain, resMsg, duration).show();
    }

    public static boolean validateMessage(EditText etMessage) {
        return etMessage !=null && !etMessage.getText().toString().trim().isEmpty();
    }
}
