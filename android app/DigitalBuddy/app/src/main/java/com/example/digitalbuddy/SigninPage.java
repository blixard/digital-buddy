package com.example.digitalbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SigninPage extends AppCompatActivity {

    static final int RC_SIGN_IN = 100;
    private GoogleSignInClient mGoogleSignInClient;
    static final String TAG = "signin page";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_page);


        creatSigninRequest();
//        checkLastSignin();


        // Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);
//        signInButton.setSize(SignInButton.SIZE_STANDARD);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

    }

    private void creatSigninRequest() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void checkLastSignin() {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            if (acct != null) {

                String personId = acct.getId();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference userRef = database.getReference("users");

                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean inDatabase = false;
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            if (postSnapshot.getValue(Users.class).getPersonId().equals(personId)) {
                                inDatabase = true;
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }

                        Log.d(TAG, "onDataChange: yololo" + inDatabase);

                        if (inDatabase == false) {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference userRef = database.getReference("users").child(personId);
                            GoogleSignInAccount acct = null;
                            try {
                                acct = completedTask.getResult(ApiException.class);
                            } catch (ApiException e) {
                                e.printStackTrace();
                            }
                            // Signed in successfully, show authenticated UI.
                            if (acct != null) {

                                String personName = acct.getDisplayName();
                                String personGivenName = acct.getGivenName();
                                String personFamilyName = acct.getFamilyName();
                                String personEmail = acct.getEmail();
                                String personId = acct.getId();
                                Uri personPhoto = acct.getPhotoUrl();
                                String photo;

                                if (personPhoto == null) {
                                    photo = "";
                                } else {
                                    photo = personPhoto.toString();
                                }
                                Users u = new Users(personName, personGivenName, personFamilyName, personEmail, personId, photo);
                                u.addPersonalRoom(personId);
//                                add personal room in database
                                addRoom(personId);
                                userRef.setValue(u);
                            }

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();

                        }
                        userRef.removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });


            }
        } catch (ApiException e) {
            e.printStackTrace();
        }

    }

    private void addRoom(String id) {
        FirebaseDatabase dbRoom = FirebaseDatabase.getInstance();
        DatabaseReference refRoom = dbRoom.getReference("rooms");
        Room room = new Room(id ,"Chat bot", id , id, "adminPass@123");
        refRoom.child(id).setValue(room);
    }
}