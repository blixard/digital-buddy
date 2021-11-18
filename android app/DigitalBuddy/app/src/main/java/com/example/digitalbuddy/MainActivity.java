package com.example.digitalbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CustomAdapter.OnRoomListener{
    
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private String userId;
    private final static String TAG = "MainActivity";
    ArrayList<Room> roomArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getUserId();
        setNavBtns();


    }

    private void setNavBtns() {
        findViewById(R.id.iv_create_room_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MenuScreen.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.iv_setting_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext() , SettingPage.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.iv_search_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getUserId() {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(acct !=null){
            userId = acct.getId();
            getRoomsList();
        }
    }

    private void getRoomsList() {
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("users/"+userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Users user = snapshot.getValue(Users.class);
                String rooms= user.rooms.toString();
                setRecyclerViewData(rooms);
//                databaseReference.removeEventListener(this);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setRecyclerViewData(String rooms){
        Log.d(TAG, "setRecyclerViewData: " + rooms);
        ArrayList<Room> al = new ArrayList<Room>();
        FirebaseDatabase databaseRooms = FirebaseDatabase.getInstance();
        DatabaseReference refRooms = databaseRooms.getReference("rooms");
        refRooms.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                al.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: " + postSnapshot.getValue(Room.class).getId().toString());
                    if(rooms.contains(postSnapshot.getValue(Room.class).getId().toString()) ){
                        al.add(postSnapshot.getValue(Room.class));
                    }

                }
                roomArrayList = al;
                setRecyclerView(al);
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

    public void setRecyclerView(ArrayList<Room> al){
        RecyclerView rv = findViewById(R.id.rv_rooms);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new CustomAdapter(al,this));
        SpaceMessagesRv space = new SpaceMessagesRv(20);
        if(rv.getItemDecorationCount() > 0){
            rv.removeItemDecorationAt(0);
        }

        rv.addItemDecoration(space);
    }

    @Override
    public void onRoomClick(int position) {
        Room room = roomArrayList.get(position);
        Intent intent = new Intent(getApplicationContext() , ChatRoom.class);
        Bundle b = new Bundle();
        b.putSerializable("room" , room);
        intent.putExtras(b);
        Toast.makeText(getApplicationContext(), room.getRoomName() , Toast.LENGTH_LONG).show();
        startActivity(intent);
    }
}