package com.example.naneen.loginfirebase;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class NewPost extends AppCompatActivity {
    Button selectImage, postBtn, viewAllPosts;
    TextView titleText;
    ImageView imageView;
    public static final int READ_EXTERNAL_STORAGE = 0;
    private static final int GALLERY_INTENT = 2;
    private ProgressDialog mProgressDialog;
    private Firebase mRootRef;
    private Uri mImageUri = null;
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        Firebase.setAndroidContext(NewPost.this);

        selectImage = (Button) findViewById(R.id.selectImg);
        postBtn = (Button) findViewById(R.id.postBtn);
        titleText = (TextView) findViewById(R.id.titleText);
        imageView = (ImageView) findViewById(R.id.imageView);
        viewAllPosts = (Button) findViewById(R.id.viewAllPosts);

        //initialise progress bar
        mProgressDialog = new ProgressDialog(NewPost.this);

        //select image from starage
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check the permission
                if (ContextCompat.checkSelfPermission(NewPost.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplication(), "Call for permission", Toast.LENGTH_SHORT).show();
                    Log.d("Debug", Build.VERSION.SDK_INT + " >= " + Build.VERSION_CODES.N);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Log.d("Debug", "Build.VERSION.SDK_INT >= Build.VERSION_CODES.N");
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE);
                    }
                }
                else {
                    Log.d("Debug", "else --> call gal()");
                    callGallery();
                }
            }
        });

        //initialise firebase
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mRootRef = new Firebase("https://loginfirebase-ec2c2.firebaseio.com/").child("User_Details").push();
        mStorage = FirebaseStorage.getInstance().getReference();

        //click new post
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mName = titleText.getText().toString().trim();
                if (mName.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter title", Toast.LENGTH_SHORT).show();
                    return;
                }
                Firebase childRef_name = mRootRef.child("Image_Title");
                childRef_name.setValue(mName);
                Toast.makeText(getApplicationContext(), "Updated Info", Toast.LENGTH_SHORT).show();
            }
        });

        viewAllPosts.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(getApplicationContext(),ViewPosts.class));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("Debug", (grantResults.length > 0)+"");
        Log.d("Debug", (grantResults[0] == PackageManager.PERMISSION_GRANTED)+"");
        switch (requestCode){
            case READ_EXTERNAL_STORAGE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    callGallery();
                }
                return;
        }
        Toast.makeText(getApplicationContext(), "...", Toast.LENGTH_SHORT).show();
    }

    //if access is grant gallery will be opened
    private void callGallery(){
        Log.d("debug", "call gall");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_INTENT);
    }

    //after selecting image, it will be opened to firebase
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            imageView.setImageURI(mImageUri);
            StorageReference filePath = mStorage.child("User_Images").child(mImageUri.getLastPathSegment());

            mProgressDialog.setMessage("Uploading...");
            mProgressDialog.show();

            filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl(); //ignore this error
                    mRootRef.child("Image_URL").setValue(downloadUri.toString());
                    Glide.with(getApplicationContext())
                            .load(downloadUri)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .into(imageView);
                    Toast.makeText(getApplicationContext(), "Updated...", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            });
        }

    }
}
