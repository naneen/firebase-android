package com.example.naneen.loginfirebase;

import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ViewPosts extends AppCompatActivity {
    RecyclerView recyclerView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;
    private FirebaseRecyclerAdapter<ViewSingleItem, ShowDataViewHolder> mFirebaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_posts);

        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference("User_Details");
        recyclerView = (RecyclerView) findViewById(R.id.viewPostData);
        recyclerView.setLayoutManager(new LinearLayoutManager(ViewPosts.this));
        Toast.makeText(ViewPosts.this, "Please wait, it is loading...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<ViewSingleItem, ShowDataViewHolder>(ViewSingleItem.class,
                R.layout.viewsingleitem, ShowDataViewHolder.class, myRef) {
            @Override
            protected void populateViewHolder(final ShowDataViewHolder viewHolder, ViewSingleItem model, final int position) {
                viewHolder.Image_URL(model.getImage_URL());
                viewHolder.Image_Title(model.getImage_Title());

                // click at any row of the list & we will delete at row out of DB
                viewHolder.itemView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick (final View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ViewPosts.this);
                        builder.setMessage("Delete?").setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        int selectedItem = position;
                                        mFirebaseAdapter.getRef(selectedItem).removeValue();
                                        mFirebaseAdapter.notifyItemRemoved(selectedItem);
                                        recyclerView.invalidate();
                                        onStart();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.setTitle("Are you sure?");
                        dialog.show();
                    }
                });
            }
        };
        recyclerView.setAdapter(mFirebaseAdapter);
        Log.d("debug", "onstart");
    }

    public static class ShowDataViewHolder extends RecyclerView.ViewHolder {
        private final TextView image_title;
        private final ImageView image_URL;

        public ShowDataViewHolder(final View itemView) {
            super(itemView);
            image_URL = (ImageView) itemView.findViewById(R.id.fetch_image);
            image_title = (TextView) itemView.findViewById(R.id.fetch_image_title);
        }

        private void Image_Title(String title) {
            image_title.setText(title);
        }

        private void Image_URL(String title) {
            Glide.with(itemView.getContext())
                    .load(title)
                    .crossFade()
                    .placeholder(R.drawable.loader)
                    .thumbnail(01.f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(image_URL);
        }

    }
}