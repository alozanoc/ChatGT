package com.alilozano.chatgt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int RC_SIGN_IN = 123;
    ChatGTSharedPreferences prefs = null;
    FirebaseDatabase database;
    int i = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new ChatGTSharedPreferences(this);
        database = FirebaseDatabase.getInstance();

        setContentView(R.layout.activity_main);
        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        Button btnPrueba = findViewById(R.id.btnPrueba);
        btnPrueba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference myRef = database.getReference("message");
                
                myRef.setValue("Hello, World!" +  (i++));
            }
        });

        DatabaseReference messageRef = database.getReference("message");
        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Toast.makeText(MainActivity.this,
                        String.valueOf(dataSnapshot.getValue()),
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        String uid = prefs.getString(ChatGTSharedPreferences.KEY_USER_AUTHENTICATED);
        if(uid==null){
            // Choose authentication providers
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.PhoneBuilder().build());

// Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
        }else{
            // esta logueado
        }
    }

    @Override
    public void onClick(View v) {
        prefs.set(ChatGTSharedPreferences.KEY_USER_AUTHENTICATED, null);
        this.onResume();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                prefs.set(ChatGTSharedPreferences.KEY_USER_AUTHENTICATED, user.getUid());
                DatabaseReference refUser = database.getReference("/usuarios/" + user.getUid());
                refUser.child("nombre").setValue(user.getDisplayName());
                refUser.child("email").setValue(user.getEmail());
                /* DatabaseReference ref1 = database.getReference("/usuarios/" + user.getUid() + "/nombre");
                ref1.setValue(user.getDisplayName());
                DatabaseReference ref2 = database.getReference("/usuarios/" + user.getUid() + "/email");
                ref2.setValue(user.getEmail()); */
                Toast.makeText(this, "Logueado", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "No Logueado", Toast.LENGTH_LONG).show();
            }
        }
    }
}
