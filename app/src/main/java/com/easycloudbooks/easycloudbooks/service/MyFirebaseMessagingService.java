package com.easycloudbooks.easycloudbooks.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat ;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.easycloudbooks.easycloudbooks.BuildConfig;
import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.activity.MainActivity;
import com.easycloudbooks.easycloudbooks.activity.SplashActivity;
import com.easycloudbooks.easycloudbooks.app.App;
import com.easycloudbooks.easycloudbooks.app.Config;
import com.easycloudbooks.easycloudbooks.common.DataDateTime;
import com.easycloudbooks.easycloudbooks.common.DataGlobal;
import com.easycloudbooks.easycloudbooks.common.DataText;
import com.easycloudbooks.easycloudbooks.model.ACJM;
import com.easycloudbooks.easycloudbooks.util.CustomAuthRequest;
import com.easycloudbooks.easycloudbooks.util.CustomRequest;
import com.easycloudbooks.easycloudbooks.util.GlideApp;
import com.easycloudbooks.easycloudbooks.util.NotificationUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonElement;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.easycloudbooks.easycloudbooks.constants.Constants.GCM_FRIEND_REQUEST_ACCEPTED;
import static com.easycloudbooks.easycloudbooks.constants.Constants.GCM_NOTIFY_COMMENT;
import static com.easycloudbooks.easycloudbooks.constants.Constants.GCM_NOTIFY_COMMENT_REPLY;
import static com.easycloudbooks.easycloudbooks.constants.Constants.GCM_NOTIFY_CUSTOM;
import static com.easycloudbooks.easycloudbooks.constants.Constants.GCM_NOTIFY_FOLLOWER;
import static com.easycloudbooks.easycloudbooks.constants.Constants.GCM_NOTIFY_GIFT;
import static com.easycloudbooks.easycloudbooks.constants.Constants.GCM_NOTIFY_IMAGE_COMMENT;
import static com.easycloudbooks.easycloudbooks.constants.Constants.GCM_NOTIFY_IMAGE_COMMENT_REPLY;
import static com.easycloudbooks.easycloudbooks.constants.Constants.GCM_NOTIFY_IMAGE_LIKE;
import static com.easycloudbooks.easycloudbooks.constants.Constants.GCM_NOTIFY_LIKE;
import static com.easycloudbooks.easycloudbooks.constants.Constants.GCM_NOTIFY_MESSAGE;
import static com.easycloudbooks.easycloudbooks.constants.Constants.GCM_NOTIFY_PERSONAL;
import static com.easycloudbooks.easycloudbooks.constants.Constants.GCM_NOTIFY_SYSTEM;
import static com.easycloudbooks.easycloudbooks.constants.Constants.GCM_NOTIFY_VIDEO_COMMENT;
import static com.easycloudbooks.easycloudbooks.constants.Constants.GCM_NOTIFY_VIDEO_COMMENT_REPLY;
import static com.easycloudbooks.easycloudbooks.constants.Constants.GCM_NOTIFY_VIDEO_LIKE;
import static com.easycloudbooks.easycloudbooks.constants.Constants.METHOD_NOTIFICATIONS_acknowledge;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        //String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        App.getInstance().setGcmToken(token);


        // Saving reg id to shared preferences
        storeRegIdInPref(token);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.getIdToken(false).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    if (task.isSuccessful()) {
                        String refreshedToken1 = user.getIdToken(false).getResult().getToken();
                        storeUserTokenInPref(getApplicationContext(), refreshedToken1);
                    }
                }
            });

        }
        // sending reg id to your server

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        /*Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", token);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);*/
    }




    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("User.FCMAR", token);
        editor.commit();
    }

    public void storeUserTokenInPref(Context mContext, String token) {
        if (mContext != null) {
            SharedPreferences pref = mContext.getSharedPreferences(App.getInstance().SharedPrefName, 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("User.FCMR", token);
            editor.commit();
        }
    }
    @Override
    public void onMessageReceived(RemoteMessage message) {
        String from = message.getFrom();
        Map data = message.getData();

        Log.w("Message", data.toString() );

        generateNotification(getApplicationContext(), data);
    }

//    @Override
//    public void onMessageReceived(String from, Bundle data) {
//
//        generateNotification(getApplicationContext(), data);
//        Log.e("Message", "Could not parse malformed JSON: \"" + data.toString() + "\"");
//    }

    @Override
    public void onDeletedMessages() {
        sendNotification("Deleted messages on server");
    }

    @Override
    public void onMessageSent(String msgId) {

        sendNotification("Upstream message sent. Id=" + msgId);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {

        Log.e("Message", "sendNotification: \"" + msg + "\"");
    }

    /**
     * Create a notification to inform the user that server has sent a message.
     */

    public static void MessageReceivedAknowledge(final String[]  NObjIds, final Long ACId, final String Status) {
        try {
            if(NObjIds != null && NObjIds.length != 0  && ACId > 0)
            {
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("nobjids", new JSONArray(NObjIds));
                jsonParam.put("acid", ACId);
                jsonParam.put("status", Status);

                new CustomAuthRequest(Request.Method.POST, METHOD_NOTIFICATIONS_acknowledge, jsonParam, ACId ,null,null ) {

                    @Override
                    protected void onCreateFinished(CustomAuthRequest request) {
                        int socketTimeout = 0;//0 seconds - change to what you want
                        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                        request.customRequest.setRetryPolicy(policy);
                        App.getInstance().addToRequestQueue(request);
                    }
                };
            }
        }
         catch (JSONException ex) {
             DataGlobal.SaveLog(TAG, ex);
         }

    }

    private static void generateNotification(Context context, Map data) {

        /*long msgId = 0;
        long msgFromUserId = 0;
        int msgFromUserState = 0;
        int msgFromUserVerify = 0;
        String msgFromUserUsername = "";
        String msgFromUserFullname = "";
        String msgFromUserPhotoUrl = "";
        String msgMessage = "";
        String msgImgUrl = "";
        String msgCreateAt = "0";
        String msgDate = "";
        String msgTimeAgo = "";
        String msgRemoveAt = "0";*/

        Object obj1 = data.get("title");
        String title = obj1 != null ? obj1.toString() : "";

         obj1 = data.get("type");
        String type = obj1 != null ? obj1.toString() : "";

         obj1 = data.get("body");
        String body = obj1 != null ? obj1.toString() : "";

        obj1 = data.get("id");
        String id = obj1 != null ? obj1.toString() : "";


        obj1 = data.get("nobjids");
        String nobjidstr = obj1 != null ? obj1.toString() : "";

        String[] nobjids =  nobjidstr.split(",");


        obj1 = data.get("et");
        String et = obj1 != null ? obj1.toString() : "";


        obj1 = data.get("acid");
        String acidstr = obj1 != null ? obj1.toString() : "0";

        long ACId = Long.parseLong(acidstr);
        MessageReceivedAknowledge(nobjids,ACId, "delivered");

        Log.w("Message",type );

        Log.w("Message",title );

        Log.w("Message",body );

        Log.w("Message",id );
        Log.w("acid",acidstr );
        Log.w("Message",et );
        DateTime etd = DataDateTime.getJodaTime(et);

        String CHANNEL_ID = "my_channel_01"; // id for channel.

       int icon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? R.drawable.ic_stat_notification: R.mipmap.ic_launcher;
        int NotificationColor = context.getResources().getColor(R.color.cpb_blue_dark);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = new Intent(context, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("acid",ACId);
        intent.putExtra("nfid",id);
        intent.putExtra("nobjids",nobjids);
        intent.putExtra("mode","notification");
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 , intent,                        PendingIntent.FLAG_ONE_SHOT);

        ACJM AC = App.getInstance().getAC(ACId);
        if(type.equals("SM") && AC  != null)
        {
            Log.w("Message","Start" );
            try {
                String ACImage = App.getInstance().ValidateACImage(AC.I);
                String ACName = AC.N != "" ? AC.N : "EasyCloudBooks";
                Log.w("Message",ACImage);
                FutureTarget<Bitmap> futureTargetac = Glide.with(context)
                        .asBitmap()
                        .load(ACImage)
                        .submit();
                Bitmap ACbitmap = futureTargetac.get();
                // this is a my insertion looking for a solution
                // Log.i("FirebaseService",Integer.toString(icon));
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(icon)
                        .setColor(NotificationColor)
                        .setContentTitle(title)

                .setGroup(String.valueOf( ACId))
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .setWhen(etd.getMillis())
                        .setLargeIcon(ACbitmap);
                Glide .with(context).clear(futureTargetac);

                Notification.Builder summaryNotification =
                        new Notification.Builder(context)
                                .setContentTitle(title)
                                //set content text to support devices running API level < 24
                                .setSmallIcon(icon)
                                //build summary info into InboxStyle template
                                .setStyle(new Notification.InboxStyle()
                                        .addLine(title)
                                        .setSummaryText(AC.N))
                                //specify which group this notification belongs to
                                .setGroup(String.valueOf( ACId))
                                //set this notification as the summary for the group
                                .setGroupSummary(true);

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT  >= Build.VERSION_CODES.O)
                {
                    String channelId = CHANNEL_ID;
                    NotificationChannel channel = new NotificationChannel(  channelId,       "ECBNotification" , NotificationManager.IMPORTANCE_DEFAULT    );
                    notificationManager.createNotificationChannel(channel);
                    notificationBuilder.setChannelId(channelId);
                    summaryNotification.setChannelId(channelId);
                }

                notificationManager.notify(( int ) System. currentTimeMillis () , notificationBuilder.build());
                notificationManager.notify((int)ACId , summaryNotification.build());
            }
            catch (Exception ex)
            {
                Log.w(TAG, "generateNotification: ", ex);
            }
            Log.w("Message","Done" );
        }
        else if(type.equals("DM") && AC  != null) {
            String json =  data.get("json").toString();
            if(json != null) {
                try {
                    JSONObject obj = new JSONObject(json);
                    if(obj.getString("Event").equals(Config.PaymentReceived)) {
                        Intent registrationComplete = new Intent(Config.PaymentReceived);
                        registrationComplete.putExtra("json", json);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(registrationComplete);
                    }
                }
                catch (JSONException ex)
                {
                    Log.w(TAG, "generateNotification: ",ex );
                }
            }
        }
        else if(type.equals("SM"))
        {
            Log.w("Message","Start" );
            try {


                // this is a my insertion looking for a solution
                // Log.i("FirebaseService",Integer.toString(icon));
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(icon)
                        .setColor(NotificationColor)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setWhen(etd.getMillis())
                        .setContentIntent(pendingIntent);

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT  >= Build.VERSION_CODES.O)
                {
                    String channelId = CHANNEL_ID;
                    NotificationChannel channel = new NotificationChannel(  channelId,       "ECBNotification" , NotificationManager.IMPORTANCE_DEFAULT    );
                    notificationManager.createNotificationChannel(channel);
                    notificationBuilder.setChannelId(channelId);
                }
                notificationManager.notify(( int ) System. currentTimeMillis ()  /* ID of notification */, notificationBuilder.build());
            }
            catch (Exception ex)
            {
                Log.w(TAG, "generateNotification: ", ex);
            }
            Log.w("Message","Done" );
        }
        else if(type == "SIM"|| type.equals("SIM"))
        {
            try {

                String image = data.get("image").toString();
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(icon)
                        .setColor(NotificationColor)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setWhen(etd.getMillis())
                        .setContentIntent(pendingIntent);
                if(AC != null){
                    notificationBuilder.setGroup(String.valueOf( ACId));
                }

                FutureTarget<Bitmap> futureTarget = Glide.with(context)
                        .asBitmap()
                        .load(image)
                        .submit();

                Bitmap bitmap = futureTarget.get();
                NotificationCompat.BigPictureStyle notiStyle = new NotificationCompat.BigPictureStyle();
                notiStyle.setSummaryText(body);
                notiStyle.bigPicture(bitmap);
                FutureTarget<Bitmap> futureTargetac = null;
                // this is a my insertion looking for a solution
               // Log.i("FirebaseService", Integer.toString(icon));


                Notification.Builder summaryNotification = null;

                if(AC != null){
                    String ACImage = App.getInstance().ValidateACImage(AC.I);
                     futureTargetac = Glide.with(context)
                            .asBitmap()
                            .load(ACImage)
                            .submit();
                    Bitmap ACbitmap = futureTargetac.get();
                    notiStyle.bigLargeIcon(ACbitmap);

                    summaryNotification = new Notification.Builder(context)
                            .setContentTitle(title)
                            //set content text to support devices running API level < 24
                            .setSmallIcon(icon)
                            //build summary info into InboxStyle template
                            .setStyle(new Notification.InboxStyle()
                                    .addLine(title)
                                    .setSummaryText(AC.N))
                            //specify which group this notification belongs to
                            .setGroup(String.valueOf( ACId))
                            //set this notification as the summary for the group
                            .setGroupSummary(true);
                    notificationBuilder.setChannelId(CHANNEL_ID);
                }
                else{
                    notiStyle.bigLargeIcon(null);}

                //notificationBuilder.setLargeIcon(bitmap);
                notificationBuilder.setStyle(notiStyle);





                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT  >= Build.VERSION_CODES.O)
                {
                    String channelId = CHANNEL_ID;
                    NotificationChannel channel = new NotificationChannel(  channelId,       "ECBNotification" , NotificationManager.IMPORTANCE_DEFAULT    );
                    notificationManager.createNotificationChannel(channel);
                    notificationBuilder.setChannelId(channelId);
                }
                notificationManager.notify(( int ) System. currentTimeMillis ()/* ID of notification */, notificationBuilder.build());
                if(summaryNotification != null)
                {
                    notificationManager.notify((int)ACId , summaryNotification.build());
                }
                Glide.with(context).clear(futureTarget);

                if(futureTargetac != null)
                    Glide.with(context).clear(futureTargetac);
            }
            catch (ExecutionException ex)
            {
                Log.w(TAG, "generateNotification: ", ex);
            }
            catch (InterruptedException ex)
            {
                Log.w(TAG, "generateNotification 1: ", ex);
            }
        }

        /*long accountId = Long.parseLong(data.get("accountId").toString().replace(".0",""));
        long actionId = Long.parseLong(data.get("id").toString().replace(".0",""));

        if (Integer.valueOf(type) == GCM_NOTIFY_MESSAGE) {

            msgId = Long.parseLong(data.get("msgId").toString().replace(".0",""));
            msgFromUserId = Long.parseLong(data.get("msgFromUserId").toString().replace(".0",""));
            msgFromUserState = Integer.parseInt(data.get("msgFromUserState").toString().replace(".0",""));
            msgFromUserVerify = Integer.parseInt(data.get("msgFromUserVerify").toString().replace(".0",""));
            msgFromUserUsername = data.get("msgFromUserUsername").toString();
            msgFromUserFullname = data.get("msgFromUserFullname").toString();
            msgFromUserPhotoUrl = data.get("msgFromUserPhotoUrl").toString();
            msgMessage = data.get("msgMessage").toString();
            msgImgUrl = data.get("msgImgUrl").toString();
            msgCreateAt = data.get("msgCreateAt").toString();
            msgDate = data.get("msgDate").toString();
            msgTimeAgo = data.get("msgTimeAgo").toString();
            msgRemoveAt = data.get("msgRemoveAt") != null ? data.get("msgRemoveAt").toString() : "0";
        }

        int icon = R.drawable.ic_stat_notification;
        long when = System.currentTimeMillis();
        String title = context.getString(R.string.app_name);*/

//        App.getInstance().reload();
        int test = 52365;//Integer.valueOf(type)
        switch (test) {

            case GCM_NOTIFY_SYSTEM: {

              /*  NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_stat_notification)
                                .setContentTitle(title)
                                .setContentText(message);

                Intent resultIntent;

                if (App.getInstance().getId() != 0) {

                    resultIntent = new Intent(context, MainActivity.class);

                } else {

                    resultIntent = new Intent(context, SplashActivity.class);
                }

                resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                mBuilder.setAutoCancel(true);
                mNotificationManager.notify(0, mBuilder.build());
*/
                break;
            }

            case GCM_NOTIFY_CUSTOM: {

                /*if (App.getInstance().getId() != 0) {

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.ic_stat_notification)
                                    .setContentTitle(title)
                                    .setContentText(message);

                    Intent resultIntent = new Intent(context, MainActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(MainActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    mBuilder.setAutoCancel(true);
                    mNotificationManager.notify(0, mBuilder.build());
                }*/

                break;
            }

            case GCM_NOTIFY_PERSONAL: {

               /* if (App.getInstance().getId() != 0 && App.getInstance().getId() == (accountId)) {

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.ic_stat_notification)
                                    .setContentTitle(title)
                                    .setContentText(message);

                    Intent resultIntent = new Intent(context, MainActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(MainActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    mBuilder.setAutoCancel(true);
                    mNotificationManager.notify(0, mBuilder.build());
                }
*/
                break;
            }

            case GCM_NOTIFY_LIKE: {

              /*  if (App.getInstance().getId() != 0 && App.getInstance().getId()==accountId) {

                    App.getInstance().setNotificationsCount(App.getInstance().getNotificationsCount() + 1);

                    message = context.getString(R.string.label_gcm_like);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.ic_stat_notification)
                                    .setContentTitle(title)
                                    .setContentText(message);

                    Intent resultIntent = new Intent(context, NotificationsActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(NotificationsActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    mBuilder.setAutoCancel(true);
                    mNotificationManager.notify(0, mBuilder.build());
                }

                break;*/
            }

            case GCM_NOTIFY_IMAGE_LIKE: {

                /*if (App.getInstance().getId() != 0 && App.getInstance().getId() == (accountId)) {

                    App.getInstance().setNotificationsCount(App.getInstance().getNotificationsCount() + 1);

                    message = context.getString(R.string.label_gcm_like);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.ic_stat_notification)
                                    .setContentTitle(title)
                                    .setContentText(message);

                    Intent resultIntent = new Intent(context, NotificationsActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(NotificationsActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    mBuilder.setAutoCancel(true);
                    mNotificationManager.notify(0, mBuilder.build());
                }
*/
                break;
            }

            case GCM_NOTIFY_VIDEO_LIKE: {

               /* if (App.getInstance().getId() != 0 && App.getInstance().getId() == (accountId)) {

                    App.getInstance().setNotificationsCount(App.getInstance().getNotificationsCount() + 1);

                    message = context.getString(R.string.label_gcm_like);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.ic_stat_notification)
                                    .setContentTitle(title)
                                    .setContentText(message);

                    Intent resultIntent = new Intent(context, NotificationsActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(NotificationsActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    mBuilder.setAutoCancel(true);
                    mNotificationManager.notify(0, mBuilder.build());
                }*/

                break;
            }

            case GCM_NOTIFY_FOLLOWER: {

                /*if (App.getInstance().getId() != 0 && App.getInstance().getId() == (accountId)) {

                    App.getInstance().setNotificationsCount(App.getInstance().getNotificationsCount() + 1);

                    message = context.getString(R.string.label_gcm_friend_request);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.ic_stat_notification)
                                    .setContentTitle(title)
                                    .setContentText(message);

                    Intent resultIntent = new Intent(context, NotificationsActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(NotificationsActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    mBuilder.setAutoCancel(true);
                    mNotificationManager.notify(0, mBuilder.build());
                }*/

                break;
            }

            case GCM_FRIEND_REQUEST_ACCEPTED: {

               /* if (App.getInstance().getId() != 0 && App.getInstance().getId() == (accountId)) {

                    App.getInstance().setNewFriendsCount(App.getInstance().getNewFriendsCount() + 1);

                    message = context.getString(R.string.label_gcm_friend_request_accepted);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.ic_stat_notification)
                                    .setContentTitle(title)
                                    .setContentText(message);

                    Intent resultIntent = new Intent(context, FriendsActivity.class);
                    resultIntent.putExtra("gcm", true);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(FriendsActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    mBuilder.setAutoCancel(true);
                    mNotificationManager.notify(0, mBuilder.build());
                }*/

                break;
            }

            case GCM_NOTIFY_COMMENT: {

                /*if (App.getInstance().getId() != 0 && App.getInstance().getId() == (accountId)) {

                    App.getInstance().setNotificationsCount(App.getInstance().getNotificationsCount() + 1);

                    message = context.getString(R.string.label_gcm_comment);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.ic_stat_notification)
                                    .setContentTitle(title)
                                    .setContentText(message);

                    Intent resultIntent = new Intent(context, NotificationsActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(NotificationsActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    mBuilder.setAutoCancel(true);
                    mNotificationManager.notify(0, mBuilder.build());
                }*/

                break;
            }

            case GCM_NOTIFY_IMAGE_COMMENT: {

                /*if (App.getInstance().getId() != 0 && App.getInstance().getId() == (accountId)) {

                    App.getInstance().setNotificationsCount(App.getInstance().getNotificationsCount() + 1);

                    message = context.getString(R.string.label_gcm_comment);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.ic_stat_notification)
                                    .setContentTitle(title)
                                    .setContentText(message);

                    Intent resultIntent = new Intent(context, NotificationsActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(NotificationsActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    mBuilder.setAutoCancel(true);
                    mNotificationManager.notify(0, mBuilder.build());
                }*/

                break;
            }

            case GCM_NOTIFY_VIDEO_COMMENT: {

                /*if (App.getInstance().getId() != 0 && App.getInstance().getId() == (accountId)) {

                    App.getInstance().setNotificationsCount(App.getInstance().getNotificationsCount() + 1);

                    message = context.getString(R.string.label_gcm_comment);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.ic_stat_notification)
                                    .setContentTitle(title)
                                    .setContentText(message);

                    Intent resultIntent = new Intent(context, NotificationsActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(NotificationsActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    mBuilder.setAutoCancel(true);
                    mNotificationManager.notify(0, mBuilder.build());
                }*/

                break;
            }

            case GCM_NOTIFY_COMMENT_REPLY: {

               /* if (App.getInstance().getId() != 0 && App.getInstance().getId() == (accountId)) {

                    App.getInstance().setNotificationsCount(App.getInstance().getNotificationsCount() + 1);

                    message = context.getString(R.string.label_gcm_comment_reply);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.ic_stat_notification)
                                    .setContentTitle(title)
                                    .setContentText(message);

                    Intent resultIntent = new Intent(context, NotificationsActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(NotificationsActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    mBuilder.setAutoCancel(true);
                    mNotificationManager.notify(0, mBuilder.build());
                }*/

                break;
            }

            case GCM_NOTIFY_IMAGE_COMMENT_REPLY: {

               /* if (App.getInstance().getId() != 0 && App.getInstance().getId() == (accountId)) {

                    App.getInstance().setNotificationsCount(App.getInstance().getNotificationsCount() + 1);

                    message = context.getString(R.string.label_gcm_comment_reply);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.ic_stat_notification)
                                    .setContentTitle(title)
                                    .setContentText(message);

                    Intent resultIntent = new Intent(context, NotificationsActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(NotificationsActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    mBuilder.setAutoCancel(true);
                    mNotificationManager.notify(0, mBuilder.build());
                }*/

                break;
            }

            case GCM_NOTIFY_VIDEO_COMMENT_REPLY: {

               /* if (App.getInstance().getId() != 0 && App.getInstance().getId() == (accountId)) {

                    App.getInstance().setNotificationsCount(App.getInstance().getNotificationsCount() + 1);

                    message = context.getString(R.string.label_gcm_comment_reply);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.ic_stat_notification)
                                    .setContentTitle(title)
                                    .setContentText(message);

                    Intent resultIntent = new Intent(context, NotificationsActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(NotificationsActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    mBuilder.setAutoCancel(true);
                    mNotificationManager.notify(0, mBuilder.build());
                }*/

                break;
            }

            case GCM_NOTIFY_GIFT: {

               /* if (App.getInstance().getId() != 0 && App.getInstance().getId() == (accountId)) {

                    App.getInstance().setNotificationsCount(App.getInstance().getNotificationsCount() + 1);

                    message = context.getString(R.string.label_gcm_gift);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.ic_stat_notification)
                                    .setContentTitle(title)
                                    .setContentText(message);

                    Intent resultIntent = new Intent(context, NotificationsActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(NotificationsActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    mBuilder.setAutoCancel(true);
                    mNotificationManager.notify(0, mBuilder.build());
                }*/

                break;
            }

            case GCM_NOTIFY_MESSAGE: {

                /*if (App.getInstance().getId() != 0 && accountId == App.getInstance().getId()) {

                    if (App.getInstance().getCurrentChatId() == actionId) {

                        Intent i = new Intent(ChatFragment.BROADCAST_ACTION);
                        i.putExtra(ChatFragment.PARAM_TASK, 0);
                        i.putExtra(ChatFragment.PARAM_STATUS, ChatFragment.STATUS_START);

                        i.putExtra("msgId", (msgId));
                        i.putExtra("msgFromUserId", (msgFromUserId));
                        i.putExtra("msgFromUserState", (msgFromUserState));
                        i.putExtra("msgFromUserVerify", (msgFromUserVerify));
                        i.putExtra("msgFromUserUsername", String.valueOf(msgFromUserUsername));
                        i.putExtra("msgFromUserFullname", String.valueOf(msgFromUserFullname));
                        i.putExtra("msgFromUserPhotoUrl", String.valueOf(msgFromUserPhotoUrl));
                        i.putExtra("msgMessage", String.valueOf(msgMessage));
                        i.putExtra("msgImgUrl", String.valueOf(msgImgUrl));
                        i.putExtra("msgCreateAt", Integer.valueOf(msgCreateAt));
                        i.putExtra("msgDate", String.valueOf(msgDate));
                        i.putExtra("msgTimeAgo", String.valueOf(msgTimeAgo));

                        context.sendBroadcast(i);

                    } else {

                        if (App.getInstance().getMessagesCount() == 0) App.getInstance().setMessagesCount(App.getInstance().getMessagesCount() + 1);

                        if (App.getInstance().getAllowMessagesGCM() == ENABLED) {

                            message = context.getString(R.string.label_gcm_message);

                            NotificationCompat.Builder mBuilder =
                                    new NotificationCompat.Builder(context)
                                            .setSmallIcon(R.drawable.ic_stat_notification)
                                            .setContentTitle(title)
                                            .setContentText(message);

                            Intent resultIntent = new Intent(context, DialogsActivity.class);
                            resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                            stackBuilder.addParentStack(DialogsActivity.class);
                            stackBuilder.addNextIntent(resultIntent);
                            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                            mBuilder.setContentIntent(resultPendingIntent);
                            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                            mBuilder.setAutoCancel(true);
                            mNotificationManager.notify(0, mBuilder.build());
                        }
                    }
                }*/

                break;
            }

            default: {

                break;
            }
        }
    }
}
