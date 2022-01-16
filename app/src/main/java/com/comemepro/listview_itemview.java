package com.comemepro;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.comemepro.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class listview_itemview extends LinearLayout {
    TextView num;
    int cnt;
    ImageView img_test,img_like,img_unlike,delete;
    Button btn_report,btn_share;
    FirebaseStorage storage = FirebaseStorage.getInstance("gs://comemepro-f48bc.appspot.com");
    StorageReference storageRef = storage.getReference();
    LayoutInflater inflater = null;

    public listview_itemview(Context context) {
        super(context);
        init(context);//
    }

    public listview_itemview(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private void init(Context context){
        inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.item2,null);
        num = (TextView)view.findViewById(R.id.textView8);
        img_test = (ImageView)view.findViewById(R.id.rectangle);
        delete = (ImageView)view.findViewById(R.id.delete);
        img_like.setImageResource(R.drawable.like);
        delete.setImageResource(R.drawable.ddonggodelete);
        cnt=0;
        img_like.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cnt++;
                num.setText(cnt);
            }
        });

    }

    public void setImg_main(String name){

        Glide.with(img_test).clear(img_test);
        storageRef.child("images/"+name).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){
            @Override
            public void onSuccess(Uri uri){
                Glide.with(inflater.getContext()).load(uri).placeholder(R.drawable.boom).into(img_test);
            }
        }).addOnFailureListener(new OnFailureListener(){
            @Override
            public void onFailure(@NonNull Exception exception){
                Toast.makeText(getContext(),"uploading", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void strImg(String txt){
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query NoNamesQuery = ref.child("Post").orderByChild("img").equalTo(txt);

                NoNamesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot NoNameSnapshot: dataSnapshot.getChildren()) {
                            NoNameSnapshot.getRef().removeValue();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });
    }
}