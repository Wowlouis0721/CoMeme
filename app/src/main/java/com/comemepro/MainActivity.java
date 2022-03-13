package com.comemepro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private static final String TAG = "MainActivity";
    private Uri filePath;
    String filename;
    FirebaseStorage storage = FirebaseStorage.getInstance("gs://comemepro-f48bc.appspot.com");
    StorageReference storageRef = storage.getReference();

    ImageView img_test,img_like,img_unlike,delete;
    Button btn_report,btn_share;
    LayoutInflater inflater = null;
    View view = null;

    private ArrayList<listview_item> mArrayList;
    private CustomAdapter mAdapter;
    private int count = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Post");
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.listview);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mArrayList = new ArrayList<>();
        mAdapter = new CustomAdapter(mArrayList);
        mRecyclerView.setAdapter(mAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mDatabase=database.getReference("Post");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mAdapter.clear();
                for(DataSnapshot message:dataSnapshot.getChildren()){
                    String value = message.child("img").getValue().toString();
                    mArrayList.add(new listview_item(value));
                }
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
        final TextView textView4=(TextView)findViewById(R.id.textView4);
        textView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                Intent launchActivity1= new Intent(MainActivity.this,comicmake.class);
                startActivity(launchActivity1);
            }
        });
        Intent intent1 = new Intent();
        intent1.setType("image/*");
        intent1.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent1, "select an image"), 0);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == RESULT_OK){
            filePath = data.getData();
            Log.d(TAG, "uri:" + String.valueOf(filePath));
            try {
                uploadFile();
                writeNewPost(filename,"xxx","0","0","Noname", FirebaseAuth.getInstance().getCurrentUser().getUid());
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public Map<String, Object> toMap(String img, String key, String like, String unlike,String title, String uid) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("img", img);
        result.put("key", key);
        result.put("like", like);
        result.put("title", title);
        result.put("unlike", unlike);
        result.put("uid", uid);

        return result;
    }
    private void writeNewPost(String img, String key2, String like, String unlike,String title, String uid) {
        String key = mDatabase.push().getKey();
        Map<String, Object> postValues = toMap(img, key, like, unlike, title, uid);
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, postValues);
        mDatabase.updateChildren(childUpdates);
    }
    //upload the file
    private void uploadFile() {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("uploading");
            progressDialog.show();

            //storage
            FirebaseStorage storage = FirebaseStorage.getInstance();

            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH_mmss");
            Date now = new Date();
            filename = FirebaseAuth.getInstance().getCurrentUser().getUid()+'_'+formatter.format(now) + ".png";
            StorageReference storageRef = storage.getReferenceFromUrl("gs://comemepro-f48bc.appspot.com/").child("images/" + filename);
            storageRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Upload Success!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Upload failed", Toast.LENGTH_SHORT).show();
                        }
                    })

                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests")
                            double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "upload fail", Toast.LENGTH_SHORT).show();
        }
    }
}

