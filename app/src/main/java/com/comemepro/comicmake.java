package com.comemepro;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.comemepro.R;
import com.comemepro.upload2;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import top.defaults.colorpicker.ColorPickerPopup;

public class comicmake extends AppCompatActivity {

    Button click;
    String filePath;

    private static final int REQUEST_EXTERNAL_STORAGe = 1;
    private DatabaseReference mDatabase;
    private static String[] permissionstorage = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    int thickness=30;
    int change=10;
    int cnt=-1;
    int now=-1;
    ArrayList<ImageView> arrImg;
    boolean chk=true;
    Paint a;
    int mDefaultColor = 0;

    class Point{
        float x;
        float y;
        boolean check;
        int color;
        int strok_size;

        public Point(float x, float y, boolean check,int color,int strok_size)
        {
            this.x = x;
            this.y = y;
            this.check = check;
            this.color = color;
            this.strok_size = strok_size;
        }
    }


    class MyView extends View
    {
        int stork_width=10;
        public MyView(Context context) { super(context); }

        @Override
        protected void onDraw(Canvas canvas){
            Paint a = new Paint();
            for(int i=1 ; i<points.size() ; i++)
            {
                a.setColor(points.get(i).color);
                if(!points.get(i).check)
                    continue;
                a.setStrokeWidth(points.get(i-1).strok_size);
                canvas.drawLine(points.get(i-1).x,points.get(i-1).y,points.get(i).x,points.get(i).y,a);
            }
        }
        public void setStroke(int width){
            stork_width=width;
            Toast.makeText(comicmake.this, String.valueOf(stork_width), Toast.LENGTH_SHORT).show();
        }
        public int getStroke(){
            return stork_width;
        }
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    points.add(new Point(x,y,false , color,change));
                case MotionEvent.ACTION_MOVE :
                    points.add(new Point(x,y,true , color,change));
                    break;
                case MotionEvent.ACTION_UP :
                    break;
            }
            invalidate();
            return true;
        }
    }

    ArrayList<Point> points = new ArrayList<Point>();
    int color = Color.BLACK;

    ImageView img,img1,img2,eraser,colors,view;
    String msg;
    Button b1;
    ImageView design,paint,draw1,minus,plus,delete;
    RelativeLayout r1,draw;
    private android.widget.RelativeLayout.LayoutParams layoutParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comicmake);
        verifyStoragePermission(this);
        img=(ImageView)findViewById(R.id.iv1);
        r1 = (RelativeLayout) findViewById(R.id.r1);
        eraser = (ImageView)findViewById(R.id.imageView10);
        draw = (RelativeLayout) findViewById(R.id.draw);
        draw1 = (ImageView)findViewById(R.id.imageView9);
        b1 = (Button) findViewById(R.id.button2);
        design = (ImageView) findViewById(R.id.design);
        paint = (ImageView) findViewById(R.id.paint);
        minus = (ImageView) findViewById(R.id.minus);
        plus = (ImageView) findViewById(R.id.plus);
        colors = (ImageView) findViewById(R.id.colors);
        delete = (ImageView) findViewById(R.id.delete);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase=database.getReference("Post");


        verifyStoragePermission(this);
        StrictMode.VmPolicy.Builder builder=new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        final MyView m = new MyView(draw.getContext());
        draw.addView(m);
        draw1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                color = Color.BLACK ;
            }

        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                points.clear();
                m.invalidate();
            }
        });
        colors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerPopup.Builder(getApplicationContext()).initialColor(
                        Color.RED)
                        .enableBrightness(
                                true)
                        .enableAlpha(
                                true)
                        .okTitle(
                                "Choose")
                        .cancelTitle(
                                "Cancel")
                        // closes the
                        .showIndicator(
                                true) // this is the small box
                        // which shows the chosen
                        // color by user at the
                        // bottom of the cancel
                        // button
                        .showValue(
                                true) // this is the value which
                        // shows the selected
                        // color hex code
                        // the above all values can be made
                        // false to disable them on the
                        // color picker dialog.
                        .build()
                        .show(
                                v,
                                new ColorPickerPopup.ColorPickerObserver() {
                                    @Override
                                    public void
                                    onColorPicked(int color1) {
                                        // set the color
                                        // which is returned
                                        // by the color
                                        // picker
                                        mDefaultColor = color1;

                                        // now as soon as
                                        // the dialog closes
                                        // set the preview
                                        // box to returned
                                        color=color1;
                                    }
                                });
            }
        });

        eraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color=Color.WHITE;
                Paint i = new Paint();
                i.setStrokeWidth(thickness);
                Context context = getApplicationContext();
                CharSequence text = "erase";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                change=m.getStroke()-5;
                Context context = getApplicationContext();
                CharSequence text = "Change the thickness of eraser.";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                change=m.getStroke()+30;
                Context context = getApplicationContext();
                CharSequence text = "Change the thickness of eraser.";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

            }
        });
        design.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chk=true;
            }
        });
        paint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chk=false;
            }
        });
        ImageView.OnClickListener btnclick = new ImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ImageView imageView = (ImageView)v;
                now=imageView.getId();
            }
        };
        RelativeLayout rt = (RelativeLayout)findViewById(R.id.draw);
        ImageView.OnTouchListener imgTouch = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //arrImg.get(now).setX(event.getX()-(arrImg.get(now).getWidth()/2));
                        //arrImg.get(now).setY(event.getY()-(arrImg.get(now).getHeight()/2));
                        if(chk==true) {
                            arrImg.get(now).setX(arrImg.get(now).getX() + event.getX() - (arrImg.get(now).getWidth() / 2));
                            arrImg.get(now).setY(arrImg.get(now).getY() + event.getY() - (arrImg.get(now).getHeight() / 2));
                        }
                        else{

                            arrImg.get(now).setFocusableInTouchMode(false);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        break;
                    default:
                        break;
                }
                return false;
            }
        };
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScreenshotButton(v);
                Intent i = new Intent(comicmake.this,upload2.class);
                startActivity(i);

            }
        });



        arrImg=new ArrayList<>();
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m.setFocusable(false);
                m.setFocusableInTouchMode(false);
                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setImageResource(R.drawable.adsfasd);
                imageView.setId(cnt+1);
                imageView.setOnClickListener(btnclick);
                imageView.setOnTouchListener(imgTouch);
                now=cnt+1;
                draw.addView(imageView);
                arrImg.add(imageView);
                cnt++;
            }
        });
    }
    public void ScreenshotButton(View view){
        View rootView=getWindow().getDecorView();

        File screenShot=Screenshot(rootView);
        if(screenShot!=null){
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(screenShot)));
        }
        b1.setVisibility(View.VISIBLE);
        Toast.makeText(getApplicationContext(),"Saved to Gallery", Toast.LENGTH_SHORT).show();
    }
    public File Screenshot(View view) {
        b1.setVisibility(View.INVISIBLE);
        view.setDrawingCacheEnabled(true);
        Bitmap screenBitmap = view.getDrawingCache();

        String filename = "CoMeme" + System.currentTimeMillis() + ".png";
        filePath=Environment.getExternalStorageDirectory() + "/Pictures"+filename;
        File file = new File(Environment.getExternalStorageDirectory() + "/Pictures", filename);
        FileOutputStream os = null;
        try{
            os=new FileOutputStream(file);
            screenBitmap.compress(Bitmap.CompressFormat.PNG,90,os);
            os.close();
        }
        catch(IOException e){
            e.printStackTrace();
            return null;
        }

        view.setDrawingCacheEnabled(false);
        return file;
    }

    private static final int REQUEST_EXTERNAL_STORAGE=1;
    private static String[] PERMISSION_STORAGE={
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    public static void verifyStoragePermission(Activity activity){
        int permission= ActivityCompat.checkSelfPermission(activity,Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(permission!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,
                    PERMISSION_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
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

            StorageReference storageRef = storage.getReferenceFromUrl("gs://comemepro-f48bc.appspot.com/").child(filePath);

            storageRef.putFile(Uri.parse(filePath))
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