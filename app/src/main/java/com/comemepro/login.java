package com.comemepro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity  {
    private FirebaseAuth mAuth;
    EditText loginid;
    EditText loginpw;
    ImageView b1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final String TAG = getClass().getSimpleName();

        mAuth = FirebaseAuth.getInstance();
        loginid = (EditText) findViewById(R.id.loginid);
        loginpw = (EditText) findViewById(R.id.loginpw);
        b1 = (ImageView) findViewById(R.id.imageView11);
        TextView aaa=(TextView)findViewById(R.id.textView2);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strid=loginid.getText().toString();
                String strpw=loginpw.getText().toString();
                signuser(strid,strpw);
            }
        });
        aaa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(login.this,signup.class);
                startActivity(intent);
            }
        });


        }
        public void signuser(String id,String pw){
            mAuth.signInWithEmailAndPassword(id, pw)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful())
                                Toast.makeText(login.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                            else {
                                Intent intent=new Intent(login.this, com.comemepro.MainActivity.class);
                                startActivity(intent);
                            }

                            // ...
                        }
                    });
        }
    }

