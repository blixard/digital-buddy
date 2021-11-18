package com.example.digitalbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicMarkableReference;

public class ChatRoom extends AppCompatActivity implements MessageAdapter.OnRoomListener {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 100;
    private static final int EXTERNAL_STORAGE_PERMISSION_CODE = 110;
    private Room currentRoom;
    private String roomId;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    final static String TAG = "chatRoom";
    private ArrayList<Message> chatArrayList;
    private boolean permissionToRecordAccepted;
    private boolean permissionToStore;
    private MediaRecorder recorder;
    private String fileName = null;
    private static final String LOG_TAG = "audioRecord logs cr";
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};;
    private StorageReference storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setLayoutSize();
        Bundle bundle = getIntent().getExtras();
        currentRoom = (Room)bundle.get("room");
        roomId = currentRoom.getId();
        TextView chatRoomName =(TextView) findViewById(R.id.tv_chatroom_name);
        chatRoomName.setText(currentRoom.getRoomName());

        if(!(ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CODE);
        }


        setRecyclerViewData();
        setMessageBtn();
        setRecordBtn();
    }

    private void setRecordBtn() {
        ImageView recordBtn = (ImageView) findViewById(R.id.iv_mic_record_btn);
        recordBtn.setColorFilter(Color.GREEN);
        recordBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                TextView chatRoomName =(TextView) findViewById(R.id.tv_chatroom_name);
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    Toast.makeText(getApplicationContext(), "holding" , Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onTouch: holding rupel");
                    recordBtn.setColorFilter(Color.RED);
                    chatRoomName.setText("RECORDING...");
                    onHoldRecord();
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Toast.makeText(getApplicationContext(), "relased" , Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onTouch: released rupel");
                    recordBtn.setColorFilter(Color.GREEN);
                    chatRoomName.setText(currentRoom.getRoomName());
                    onReleaseRecord();

                }
                return true;
            }
        });
    }

    private void onHoldRecord() {
        if(!(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        }
        DateTimeFormatter dtf = null;
        String datetime = "lowversionp";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
            LocalDateTime now = LocalDateTime.now();
            System.out.println(dtf.format(now));
            datetime = "audio-" + dtf.format(now);
        }

        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/" + datetime+ ".3gp";

        onRecord(true);
    }

    private void onReleaseRecord() {
        onRecord(false);
    }

//    audio recording

//    permission to record audio
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
            case EXTERNAL_STORAGE_PERMISSION_CODE:
                permissionToStore  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();
        if (!permissionToStore ) finish();

    }


    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        Log.d(TAG, "startRecording: " + fileName);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;

        uploadAudio();

    }

    private void uploadAudio() {
        String[] nameFile = fileName.split("/");
        String name = nameFile[nameFile.length-1];
        storage = FirebaseStorage.getInstance().getReference();
        StorageReference storageRef = storage.child("Audio/" + name);
        Log.d(TAG, "uploadAudio: name file : " + name);
        Uri uri = Uri.fromFile(new File(fileName));

        storageRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(), "succefully uploaded in firebase storage" , Toast.LENGTH_SHORT).show();
                sendMessageFb(name);
            }
        });
    }

    //    setting the layout size
    private void setLayoutSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        RecyclerView rv = findViewById(R.id.rv_chat);
        ViewGroup.LayoutParams rv_lp = rv.getLayoutParams();
        rv_lp.height = height - 380;
        rv.setLayoutParams(rv_lp);
    }

// setting the message Button
    private void setMessageBtn() {
        EditText et_msg =  findViewById(R.id.et_msg_ml);
        ImageView btn = findViewById(R.id.iv_btn_send_msg);
        btn.setEnabled(false);
        btn.setImageResource(R.mipmap.send_msg_arrow_deactivated_round);
        et_msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(et_msg.getText().toString().equals("") ){
                    btn.setEnabled(false);
                    btn.setImageResource(R.mipmap.send_msg_arrow_deactivated_round);

                }
                else{
                    btn.setEnabled(true);
                    btn.setImageResource(R.mipmap.send_msg_arrow_activated_round);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et_msg =  findViewById(R.id.et_msg_ml);
                String message = et_msg.getText().toString();
                sendMessageFb(message);
                et_msg.setText("");
            }
        });
    }

    private void sendMessageFb(String message) {

//        date time
        DateTimeFormatter dtf = null;
        String datetime = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            System.out.println(dtf.format(now));
            datetime = dtf.format(now);
        }

        String date = datetime.split("\\s")[0];
        String time = datetime.split("\\s")[1];

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        String userId = "";
        String userName = "";
        if(acct !=null){
            userId = acct.getId();
            userName = acct.getDisplayName();
        }
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("rooms/"+roomId + "/messages");
        String messageId = databaseReference.push().getKey();
        Message msg = new Message(messageId, roomId, userId, userName, message,date,time);
        databaseReference.child(messageId).setValue(msg);
    }

    @Override
    public void onRoomClick(int position) {

    }

    public void setRecyclerViewData(){
        ArrayList<Message> al = new ArrayList<Message>();
        FirebaseDatabase databaseChat = FirebaseDatabase.getInstance();
        DatabaseReference refChat = databaseChat.getReference("rooms/" + roomId + "/messages");
        refChat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                al.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
//                    Log.d(TAG, "onDataChange: " +postSnapshot.getValue(Message.class).getMessageId().toString());
                    al.add(postSnapshot.getValue(Message.class));
                }
                chatArrayList = al;
                setRecyclerView(al);
//                refChat.removeEventListener(this);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });


    }

    public void setRecyclerView(ArrayList<Message> al){
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        String userId = "";
        if(acct !=null){
            userId = acct.getId();
        }
        RecyclerView rv = findViewById(R.id.rv_chat);
        LinearLayoutManager l = new LinearLayoutManager(this);
        l.setStackFromEnd(true);
        rv.setLayoutManager(l);
        rv.setAdapter(new MessageAdapter(al,this , userId));
        SpaceMessagesRv space = new SpaceMessagesRv(20);
        if(rv.getItemDecorationCount() > 0){
            rv.removeItemDecorationAt(0);
        }

        rv.addItemDecoration(space);
    }
}