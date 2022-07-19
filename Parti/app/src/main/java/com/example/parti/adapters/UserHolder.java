package com.example.parti.adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.parti.Parti;
import com.example.parti.databinding.ProjectCommentsRecyclerViewListItemBinding;
import com.example.parti.databinding.UsersRecyclerViewListItemBinding;
import com.example.parti.ui.main.ViewUserActivity;
import com.example.parti.wrappers.ProjectComment;
import com.example.parti.wrappers.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UserHolder extends RecyclerView.ViewHolder {

    private UsersRecyclerViewListItemBinding usersRecyclerViewListItemBinding;
    private User user;

    public UserHolder(@NonNull View itemView) {
        super(itemView);
    }

    public UserHolder(UsersRecyclerViewListItemBinding binding) {
        super(binding.getRoot());
        this.usersRecyclerViewListItemBinding = binding;
    }

    public void bind(int position, User user) {

        this.user = user;

        downloadImage();

        usersRecyclerViewListItemBinding.inputUserRecyclerAlias.setText(user.getAlias());
        usersRecyclerViewListItemBinding.inputUsersRecyclerUuid.setText(user.getUuid());
        usersRecyclerViewListItemBinding.inputUsersRecyclerShortDescription.setText(user.shortDescription());

        // Click listener
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ViewUserActivity.class);
                intent.putExtra(User.CLASS_ID, user);
                v.getContext().startActivity(intent);
            }
        });
    }

    private void downloadImage() {
        // Load image
        String imageId = null;
        if (user != null) imageId = user.getProfileImageId();
        if (imageId == null || imageId.equals("")) {
            Glide.with(usersRecyclerViewListItemBinding.imageUsersRecycler.getContext())
                    .load(android.R.drawable.sym_def_app_icon)
                    .into(usersRecyclerViewListItemBinding.imageUsersRecycler);
        } else {
            StorageReference imageReference = FirebaseStorage.getInstance().getReference().child(imageId);
            final long ONE_MEGABYTE = 1024 * 1024;
            imageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    usersRecyclerViewListItemBinding.imageUsersRecycler.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(UserHolder.this.itemView.getContext(), "Failed to download user profile image.", Toast.LENGTH_LONG).show();
                    //If failed, load the default local image;
                    Glide.with(usersRecyclerViewListItemBinding.imageUsersRecycler.getContext())
                            .load(android.R.drawable.sym_def_app_icon)
                            .into(usersRecyclerViewListItemBinding.imageUsersRecycler);
                }
            });
        }
    }
}
