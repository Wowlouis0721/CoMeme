package com.comemepro;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.comemepro.listview_item;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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

import java.util.ArrayList;
import java.util.Dictionary;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {
    ArrayList<listview_item> items = new ArrayList<listview_item>();
    FirebaseStorage storage = FirebaseStorage.getInstance("gs://comemepro-f48bc.appspot.com");
    StorageReference storageRef = storage.getReference();

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView comment;
        protected ImageView img_test;
        protected ImageView img_like;
        protected ImageView delete;
        protected View view;


        public CustomViewHolder(View view) {
            super(view);
            this.view = view;
            this.img_test = (ImageView)view.findViewById(R.id.rectangle);
            this.img_like = (ImageView)view.findViewById(R.id.like);
            this.delete = (ImageView)view.findViewById(R.id.delete);
            this.comment = (ImageView)view.findViewById(R.id.comment);
        }
    }

    public CustomAdapter(ArrayList<listview_item> list) {
        this.items = list;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item2, viewGroup, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }




    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {


        viewholder.img_like.setImageResource(R.drawable._8125_ok_512x512);
        viewholder.delete.setImageResource(R.drawable.delete_737_475058);
        listview_item item = items.get(position);
        storageRef.child("images/"+item.getmainId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){
            @Override
            public void onSuccess(Uri uri){
                Glide.with(viewholder.view).load(uri).placeholder(R.drawable.boom).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(viewholder.img_test);
            }
        }).addOnFailureListener(new OnFailureListener(){
            @Override
            public void onFailure(@NonNull Exception exception){
            }
        });
        viewholder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query NoNamesQuery = ref.child("Post").orderByChild("img").equalTo(item.getmainId());

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

        viewholder.img_like.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d("Like Button ","is Pressed.");
            }
        });

        viewholder.comment.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent open=new Intent(v.getContext(), com.comemepro.comicmake_detail.class);
                v.getContext().startActivity(open);
            }
        });
    }

    @Override
    public int getItemCount() {
        return(null != items ? items.size():0);
    }

    public void clear(){
        items.clear();
    }

}