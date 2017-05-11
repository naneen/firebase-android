package com.example.naneen.loginfirebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignIn extends AppCompatActivity {
    private Button signout, newPost, viewPost, location;
    private FirebaseAuth mAuth;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        signout = (Button) findViewById(R.id.signout);
        mAuth = FirebaseAuth.getInstance();
        textView = (TextView) findViewById(R.id.textView);
        viewPost = (Button) findViewById(R.id.viewPost);
        newPost = (Button) findViewById(R.id.newPost);
        location = (Button) findViewById(R.id.location);

        if(mAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
            textView.setText(user.getEmail());
        }

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            mAuth.signOut();
            Intent i = new Intent(SignIn.this, MainActivity.class);
            finish();
            startActivity(i);
            }
        });

        newPost.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(getApplicationContext(), NewPost.class));
            }
        });

        viewPost.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(getApplicationContext(),ViewPosts.class));
            }
        });

        location.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(getApplicationContext(),ViewLocation.class));
            }
        });
    }
}
