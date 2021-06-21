package com.barryzea.inventariofirebase.common.Utils;

import android.content.Context;
import android.widget.EditText;

import com.barryzea.inventariofirebase.R;

public class CommonUtils {
    public static boolean validateProducts(Context context, EditText edtName, EditText edtUrl, EditText edtQuantity){
        boolean isValid=true;
        if(edtName.getText().toString().trim().isEmpty()){
            edtName.setError(context.getString(R.string.this_field_is_required));
            edtName.requestFocus();
            isValid=false;
        }
        if (edtUrl.getText().toString().trim().isEmpty()){
            edtQuantity.setError(context.getString(R.string.this_field_is_required));
            edtUrl.requestFocus();
            isValid=false;
        }
        if(edtQuantity.getText().toString().trim().isEmpty()){
            edtQuantity.setError(context.getString(R.string.this_field_is_required));
            edtQuantity.requestFocus();
            isValid=false;

        }
        else if(Integer.valueOf(edtQuantity.getText().toString().trim())<=0){
            edtQuantity.setError(context.getString(R.string.this_quantity_must_be_more));
            edtQuantity.requestFocus();
            isValid=true;
        }

        return isValid;

    }
}
