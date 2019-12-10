package com.rd.dmmr.tutosearch.notificaciones;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rd.dmmr.tutosearch.ChatPriv;

public class FirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        SharedPreferences sharedPreferences = getSharedPreferences("SP_USER", MODE_PRIVATE);
        String savedCurrentUser = sharedPreferences.getString("current_userID", "None");

        String sent = remoteMessage.getData().get("sent");
        String user = remoteMessage.getData().get("user");
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (fuser != null && sent.equals(fuser.getUid())) {
            if (!savedCurrentUser.equals(user)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sendOAndAboveNotification(remoteMessage);
                } else {
                    sendNormalNotification(remoteMessage);
                }
            }
        }

    }



    private void sendNormalNotification(RemoteMessage remoteMessage) {

        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String tittle = remoteMessage.getData().get("tittle");
        String body = remoteMessage.getData().get("body");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int i = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, ChatPriv.class);
        Bundle bundle = new Bundle();
        bundle.putString("idAmigo", user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentText(body)
                .setContentTitle(tittle)
                .setAutoCancel(true)
                .setSound(defSoundUri)
                .setContentIntent(pIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int j = 0;

        if (i >= 0) {
            j = i;
        }
        notificationManager.notify(j, builder.build());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendOAndAboveNotification(RemoteMessage remoteMessage) {
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String tittle = remoteMessage.getData().get("tittle");
        String body = remoteMessage.getData().get("body");

        String tipo;
        tipo=tipoUser(user);

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int i = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, ChatPriv.class);
        Bundle bundle = new Bundle();
        bundle.putString("idAmigo", user);
        bundle.putString("tipoUser", tipo);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoAndAboveNotification notification1 = new OreoAndAboveNotification(this);
        Notification.Builder builder = notification1.getONotifications(tittle, body, pIntent, defSoundUri, icon);

        int j = 0;

        if (i >= 0) {
            j = i;
        }
        notification1.getManager().notify(j, builder.build());

    }

    private String tipoUser (String idAmigo){
        FirebaseFirestore fdb = FirebaseFirestore.getInstance();
        final String[] tipo = new String[1];
        DocumentReference docRef = fdb.collection("Profesores").document(idAmigo);

                docRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        DocumentSnapshot docS = task.getResult();
                        if (docS.exists()){
                            tipo[0] = "Profesor";
                        }else{
                            tipo[0] = "Estudiante";
                        }

                    }
                });



        return tipo[0];
    }


}


