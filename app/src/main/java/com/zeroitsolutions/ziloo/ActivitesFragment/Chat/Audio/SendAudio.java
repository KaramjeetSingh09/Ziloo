package com.zeroitsolutions.ziloo.ActivitesFragment.Chat.Audio;

import android.app.Activity;
import android.content.Context;
import android.media.MediaRecorder;
import android.net.Uri;
import android.util.Log;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.zeroitsolutions.ziloo.ActivitesFragment.Chat.ChatA;
import com.zeroitsolutions.ziloo.SimpleClasses.Functions;
import com.zeroitsolutions.ziloo.SimpleClasses.Variable;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class SendAudio {

    private static String mFileName = null;
    private final DatabaseReference adduserInbox;
    DatabaseReference rootref;
    String senderId = "";
    String receiverId = "";
    String receiverName = "";
    String receiverPic = "";
    Context context;
    EditText messageField;
    private MediaRecorder mRecorder = null;

    public SendAudio(Context context, EditText messageField,
                     DatabaseReference rootref, DatabaseReference adduserInbox
            , String senderid, String receiverId, String receiverName, String receiverPic) {

        this.context = context;
        this.messageField = messageField;
        this.rootref = rootref;
        this.adduserInbox = adduserInbox;
        this.senderId = senderid;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.receiverPic = receiverPic;
        mFileName = context.getExternalCacheDir().getAbsolutePath();
        mFileName += "/audiorecordtest.mp3";

    }

    public void startRecording() {
        try {
            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
            }

            mRecorder = new MediaRecorder();

            if (mRecorder != null)
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

            if (mRecorder != null)
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

            if (mRecorder != null)
                mRecorder.setOutputFile(mFileName);

            if (mRecorder != null)
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                if (mRecorder != null)
                    mRecorder.prepare();
            } catch (Exception e) {
                Log.e("resp", "prepare() failed");
            }
            if (mRecorder != null)
                mRecorder.start();

        } catch (Exception e) {
            //
        }
    }


    public void stopRecording() {
        try {


            stopTimerWithoutRecoder();
            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
                uploadAudio();
            }

        } catch (Exception e) {
//
        }
    }


    // this method will upload audio  in firebase database
    public void uploadAudio() {

        Date c = Calendar.getInstance().getTime();
        final String formattedDate = Variable.df.format(c);

        StorageReference reference = FirebaseStorage.getInstance().getReference();
        DatabaseReference dref = rootref.child("chat").child(senderId + "-" + receiverId).push();
        final String key = dref.getKey();
        ChatA.uploadingAudioId = key;
        final String current_user_ref = "chat" + "/" + senderId + "-" + receiverId;
        final String chat_user_ref = "chat" + "/" + receiverId + "-" + senderId;

        HashMap<Object, Object> my_dummi_pic_map = new HashMap<>();
        my_dummi_pic_map.put("receiver_id", receiverId);
        my_dummi_pic_map.put("sender_id", senderId);
        my_dummi_pic_map.put("chat_id", key);
        my_dummi_pic_map.put("text", "");
        my_dummi_pic_map.put("type", "audio");
        my_dummi_pic_map.put("pic_url", "none");
        my_dummi_pic_map.put("status", "0");
        my_dummi_pic_map.put("time", "");
        my_dummi_pic_map.put("sender_name", Functions.getSharedPreference(context).getString(Variable.U_NAME, ""));
        my_dummi_pic_map.put("timestamp", formattedDate);

        HashMap dummy_push = new HashMap<>();
        dummy_push.put(current_user_ref + "/" + key, my_dummi_pic_map);
        rootref.updateChildren(dummy_push);

        Uri uri = Uri.fromFile(new File(mFileName));

        final StorageReference filepath = reference.child("Audio").child(key + ".mp3");

        filepath.putFile(uri).addOnSuccessListener(taskSnapshot -> filepath.getDownloadUrl().addOnSuccessListener(uri1 -> {

            ChatA.uploadingAudioId = "none";
            HashMap<Object, Object> message_user_map = new HashMap<>();
            message_user_map.put("receiver_id", receiverId);
            message_user_map.put("sender_id", senderId);
            message_user_map.put("chat_id", key);
            message_user_map.put("text", "");
            message_user_map.put("type", "audio");
            message_user_map.put("pic_url", uri1.toString());
            message_user_map.put("status", "0");
            message_user_map.put("time", "");
            message_user_map.put("sender_name", Functions.getSharedPreference(context).getString(Variable.U_NAME, ""));
            message_user_map.put("timestamp", formattedDate);

            HashMap user_map = new HashMap<>();

            user_map.put(current_user_ref + "/" + key, message_user_map);
            user_map.put(chat_user_ref + "/" + key, message_user_map);

            rootref.updateChildren(user_map, (databaseError, databaseReference) -> {
                String inbox_sender_ref = "Inbox" + "/" + senderId + "/" + receiverId;
                String inbox_receiver_ref = "Inbox" + "/" + receiverId + "/" + senderId;

                HashMap<Object, Object> sendermap = new HashMap<>();
                sendermap.put("rid", senderId);
                sendermap.put("name", Functions.getSharedPreference(context).getString(Variable.U_NAME, ""));
                sendermap.put("pic", Functions.getSharedPreference(context).getString(Variable.U_PIC, ""));
                sendermap.put("msg", "Send an Audio...");
                sendermap.put("status", "0");
                sendermap.put("date", formattedDate);
                sendermap.put("timestamp", -1 * System.currentTimeMillis());

                HashMap<Object, Object> receivermap = new HashMap<>();
                receivermap.put("rid", receiverId);
                receivermap.put("name", receiverName);
                receivermap.put("pic", receiverPic);
                receivermap.put("msg", "Send an Audio...");
                receivermap.put("status", "1");
                receivermap.put("date", formattedDate);
                receivermap.put("timestamp", -1 * System.currentTimeMillis());

                HashMap both_user_map = new HashMap<>();
                both_user_map.put(inbox_sender_ref, receivermap);
                both_user_map.put(inbox_receiver_ref, sendermap);

                adduserInbox.updateChildren(both_user_map).addOnCompleteListener((OnCompleteListener<Void>) task -> ChatA.sendPushNotification((Activity) context, Functions.getSharedPreference(context).getString(Variable.U_NAME, ""), "Send an Audio...",
                        receiverId, senderId));
            });
        }));
    }

    public void stopTimer() {
        try {
            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
            }
            messageField.setText(null);

        } catch (Exception ignored) {
        }
    }


    public void stopTimerWithoutRecoder() {
        try {
            messageField.setText(null);
        } catch (Exception e) {
            Log.e("error", "stopTimerWithoutRecoder :>" + e);
        }
    }
}