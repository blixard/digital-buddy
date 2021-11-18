package com.example.digitalbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class JoinRoom extends AppCompatActivity {

    final static String TAG = "Join Room";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_room);
        joinRoomfun();
    }

    private void joinRoomfun() {
        EditText etRoomid = findViewById(R.id.et_roomid_jr);
        EditText etRoomPass = findViewById(R.id.et_roompass_jr);
        findViewById(R.id.btn_joinroom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinRoomFromDB(etRoomid.getText().toString() , etRoomPass.getText().toString() );
                Intent intent = new Intent(getApplicationContext() , MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void joinRoomFromDB(String roomId, String roomPass) {
        FirebaseDatabase databaseRooms = FirebaseDatabase.getInstance();
        DatabaseReference refRooms = databaseRooms.getReference("rooms");
        refRooms.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: " + postSnapshot.getValue(Room.class).getId().toString());
                    if(postSnapshot.getValue(Room.class).getRoomId().equals(roomId) &&postSnapshot.getValue(Room.class).getPassword().equals(roomPass)){
                        addRoomToUser(postSnapshot.getValue(Room.class).getId());
                        break;
                    }
                }
                refRooms.removeEventListener(this);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });
    }

    private void addRoomToUser(String id) {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        String userId = "";
        if(acct !=null){
            userId = acct.getId();
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference  databaseReference = database.getReference("users/"+userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Users user = snapshot.getValue(Users.class);
                if(  !(user.rooms.contains(id))  ){
                    user.rooms = user.rooms.toString() + ";" + id;
                    databaseReference.setValue(user);
                }
                databaseReference.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}