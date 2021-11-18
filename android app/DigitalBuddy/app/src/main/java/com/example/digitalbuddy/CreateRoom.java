package com.example.digitalbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class CreateRoom extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        Button btnCreateRoom = findViewById(R.id.btn_create_room);

        btnCreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText etRoomName = (EditText) findViewById(R.id.et_roomname_cr);
                EditText etRoomId = (EditText) findViewById(R.id.et_roomid_cr);
                EditText etRoomPass = (EditText) findViewById(R.id.et_roompass_cr);
                String roomName = etRoomName.getText().toString();
                String roomId = etRoomId.getText().toString();
                String roomPass = etRoomPass.getText().toString();


                // TODO: 23-10-2021  check if room id is not there already in database

                setRoomFirebase(roomName , roomId , roomPass);

                Intent intent = new Intent(getApplicationContext() , MainActivity.class);
                startActivity(intent);


            }
        });
    }

    private void setRoominUser(String roomId) {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        String userId = "";
        if(acct !=null){
            userId = acct.getId();
        }

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("users/"+userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Users user = snapshot.getValue(Users.class);
                user.rooms = user.rooms.toString() + ";" + roomId;
                databaseReference.setValue(user);
                databaseReference.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // TODO: 22-10-2021 add user's id to users of Room Object while pushing Room class
    private void setRoomFirebase(String roomName , String roomId , String roomPass) {
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("rooms");
        String id = databaseReference.push().getKey();
        Room room = new Room(id ,roomName, "users" , roomId , roomPass);
        databaseReference.child(id).setValue(room);
        setRoominUser(id);
    }
}