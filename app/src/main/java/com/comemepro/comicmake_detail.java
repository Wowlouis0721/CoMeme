package com.comemepro;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firestore.v1.Value;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class comicmake_detail extends AppCompatActivity{
    private Button sendbt;
    ArrayAdapter<String> adapter;
    private Uri filePath;
    private FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference=firebaseDatabase.getReference();
    DatabaseReference mDatabase;
    @Override

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_comicmake_detail);
        sendbt=(Button)findViewById(R.id.reg_button);
        ListView listView=(ListView)findViewById(R.id.listView);
        EditText comment_et=(EditText)findViewById(R.id.comment_et);
        mDatabase=FirebaseDatabase.getInstance().getReference();
        List<String> list = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    adapter.add(snapshot.getValue().toString());
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databseError){

            }
        });
        sendbt.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                String comment = comment_et.getText().toString();
                databaseReference.child("message").push().setValue(comment);
                adapter.add(comment);
                adapter.notifyDataSetChanged();
            }
        });
    }
    @IgnoreExtraProperties
    public class info{
        public String comments;
        public long nowTime;
        public info(String comments, long nowTime){
            this.comments=comments;
            this.nowTime=nowTime;
        }
    }

}