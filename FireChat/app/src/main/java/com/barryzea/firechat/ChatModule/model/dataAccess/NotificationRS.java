package com.barryzea.firechat.ChatModule.model.dataAccess;

import android.net.Uri;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.barryzea.firechat.ChatModule.events.ChatEvent;
import com.barryzea.firechat.FireChatApplication;
import com.barryzea.firechat.R;
import com.barryzea.firechat.common.Constants;
import com.barryzea.firechat.common.model.EventErrorTypeListener;
import com.barryzea.firechat.common.pojo.User;
import com.barryzea.firechat.common.utils.UtilsCommon;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NotificationRS {
    private static final String FIRE_CHAT_RS="https://androidfirechat.000webhostapp.com/fireChat/dataAccess/TextingRS.php";
    private static final String SEND_NOTIFICATION="sendNotification";

    public void setSendNotification(String title, String message, String email, String uid,
                                    String myEmail, Uri photoUrl, EventErrorTypeListener listener){
        JSONObject params = new JSONObject();
        try{
            params.put(Constants.METHOD, SEND_NOTIFICATION);
            params.put(Constants.TITLE, title);
            params.put(Constants.MESSAGE, message);
            params.put(Constants.TOPIC, UtilsCommon.getEmailToTopic(email));
            params.put(User.UID, uid);
            params.put(User.EMAIL, myEmail);
            params.put(User.PHOTO_URL, photoUrl);
            params.put(User.USERNAME, title);

        }
        catch(Exception e)
        {
            e.printStackTrace();
            listener.onError(ChatEvent.ERROR_PROCESS_DATA, R.string.error_process_data);
        }
        JsonObjectRequest objectRequest= new JsonObjectRequest(Request.Method.POST, FIRE_CHAT_RS,
                params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    int success= response.getInt(Constants.SUCCESS);
                    switch(success){
                        case ChatEvent.SEND_NOTIFICATION_SUCCESS:
                            break;
                        case ChatEvent.ERROR_METHOD_NOT_EXIST:
                            listener.onError(ChatEvent.ERROR_METHOD_NOT_EXIST, R.string.error_method_not_exist);
                            break;
                        default:
                            listener.onError(ChatEvent.ERROR_SERVER, R.string.common_error_server);
                            break;
                    }
                }
                catch(JSONException e){
                    e.printStackTrace();
                    listener.onError(ChatEvent.ERROR_PROCESS_DATA, R.string.error_process_data);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("volley error", error.getLocalizedMessage());
                listener.onError(ChatEvent.ERROR_VOLLEY, R.string.common_error_volley);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String>params=new HashMap<>();
                params.put("Content-Type","application/json; charset=utf-8");
                return params;
            }
        };
        FireChatApplication.getInstance().addToRequest(objectRequest);
    }
}
