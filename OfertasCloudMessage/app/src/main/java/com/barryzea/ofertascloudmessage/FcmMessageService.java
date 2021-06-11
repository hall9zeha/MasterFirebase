package com.barryzea.ofertascloudmessage;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FcmMessageService extends FirebaseMessagingService {
    private static final String DESCUENTO = "descuento";

    public FcmMessageService() {
    }

    @Override
    public void onNewToken(@NonNull String newToken) {
        super.onNewToken(newToken);
        //imprimiendo el token en la consola, deberiamos guardar el token recibido en un servicio aparte pero
        //el curso no trata de ello
        registerTokenToService(newToken);
    }

    private void registerTokenToService(String newToken) {
        Log.d("Token", newToken);
    }


    @Override
    public void onMessageReceived(@NonNull  RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getData().size() >0 && remoteMessage.getNotification() !=null){
            sendNotification(remoteMessage);
        }
    }

    private void sendNotification(RemoteMessage remoteMessage) {

        float desc=Float.valueOf(remoteMessage.getData().get(DESCUENTO));
        Intent intent= new Intent(this, MainActivity.class);
        intent.putExtra(DESCUENTO,desc);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationManager notificationManager= (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);


        Uri defaultSound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification.Builder notificationBuilder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_shop)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);

        String channelId= desc <.10 ? getString(R.string.low_channel_id) : getString(R.string.low_channel_id);
        String name= desc > .10? getString(R.string.normal_channel_name) : getString(R.string.normal_channel_name);

        //cambio de color de la notificacion
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            notificationBuilder.setColor(desc > .4 ? ContextCompat.getColor(getApplicationContext(), R.color.teal_700):
                    ContextCompat.getColor(getApplicationContext(), R.color.purple_500));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel= new NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableVibration(true);
            channel.enableLights(true);
            channel.setVibrationPattern(new long[]{100, 200, 200, 50});
            if(notificationManager !=null){
                notificationManager.createNotificationChannel(channel);
            }
            notificationBuilder.setChannelId(channelId);

            if(notificationManager !=null){
                notificationManager.notify("",0, notificationBuilder.build());

            }
        }
    }
}