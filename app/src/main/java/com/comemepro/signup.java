package com.comemepro;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class signup extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText editid;
    EditText editpw;
    EditText confirmpw;
    ImageView b1;
    String strid;
    String strpw;
    private Object String;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        editid = (EditText) findViewById(R.id.id);
        editpw = (EditText) findViewById(R.id.pw);
        b1 = (ImageView) findViewById(R.id.imageView8);
        confirmpw = (EditText) findViewById(R.id.confirmpw);


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String strconfrimpw = confirmpw.getText().toString();
                strid = editid.getText().toString();
                strpw = editpw.getText().toString();
                if (strpw.equals(strconfrimpw)) {
                    create_user(strid, strpw);
                } else if (strpw != (strconfrimpw)) {
                    Context context = getApplicationContext();
                    CharSequence text = "Different password";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    Toast.makeText(context, text, duration).show();
                }
            }


        });
    }
        public void create_user(String strid, String strpw){
        mAuth.createUserWithEmailAndPassword(strid,strpw).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    FirebaseUser user = mAuth.getCurrentUser();
                    Intent intent=new Intent(signup.this, com.comemepro.login.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(signup.this, "Authentication failed:" +
                            task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}