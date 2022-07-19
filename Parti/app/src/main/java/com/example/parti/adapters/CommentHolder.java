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

public class CommentHolder extends RecyclerView.ViewHolder {

    private ProjectCommentsRecyclerViewListItemBinding projectCommentsRecyclerViewListItemBinding;
    private ProjectComment comment;

    public CommentHolder(@NonNull View itemView) {
        super(itemView);
    }

    public CommentHolder(ProjectCommentsRecyclerViewListItemBinding binding) {
        super(binding.getRoot());
        this.projectCommentsRecyclerViewListItemBinding = binding;
    }

    public void bind(int position, ProjectComment comment) {

        this.comment = comment;

        downloadAlias();
        downloadImage();

        String commentBody = comment.getComment();
        if (commentBody.isEmpty()) {
            String hint = ProjectComment.DEFAULT_COMMENT_HINT;
            projectCommentsRecyclerViewListItemBinding.inputCommentsRecyclerCommentBody.setHint(hint);
        } else {
            projectCommentsRecyclerViewListItemBinding.inputCommentsRecyclerCommentBody.setText(commentBody);
        }
        projectCommentsRecyclerViewListItemBinding.ratingBarCommentsRecycler.setRating(comment.getRating());

        View.OnClickListener goToViewUserActivity = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ViewUserActivity.class);
                intent.putExtra(User.CLASS_ID, (Bundle) null);
                intent.putExtra(User.UUID_FIELD, comment.getSenderId());
                v.getContext().startActivity(intent);
            }
        };

        // Click listener
        projectCommentsRecyclerViewListItemBinding.inputCommentsRecyclerSenderAlias.setOnClickListener(goToViewUserActivity);
        projectCommentsRecyclerViewListItemBinding.imageCommentsRecycler.setOnClickListener(goToViewUserActivity);
    }

    private void downloadAlias() {
        // Load alias
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firebaseFirestore.collection(Parti.USER_COLLECTION_PATH).document(comment.getSenderId());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String alias;
                if (task.isSuccessful()) {
                    alias = task.getResult().getString(User.ALIAS_FIELD);
                } else {
                    alias = User.DEFAULT_USER_ALIAS;
                }
                projectCommentsRecyclerViewListItemBinding.inputCommentsRecyclerSenderAlias.setText(alias);
            }
        });
    }

    private void downloadImage() {
        // Load image
        String imageId = null;
        if (comment != null) imageId = Parti.PROJECT_IMAGE_COLLECTION_PATH + '/' + comment.getSenderId() + ".jpg";
        if (imageId == null || imageId.equals("")) {
            Glide.with(projectCommentsRecyclerViewListItemBinding.imageCommentsRecycler.getContext())
                    .load(android.R.drawable.sym_def_app_icon)
                    .into(projectCommentsRecyclerViewListItemBinding.imageCommentsRecycler);
        } else {
            StorageReference imageReference = FirebaseStorage.getInstance().getReference().child(imageId);
            final long ONE_MEGABYTE = 1024 * 1024;
            imageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    projectCommentsRecyclerViewListItemBinding.imageCommentsRecycler.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(CommentHolder.this.itemView.getContext(), "Failed to download sender profile image", Toast.LENGTH_LONG).show();
                    //If failed, load the default local image;
                    Glide.with(projectCommentsRecyclerViewListItemBinding.imageCommentsRecycler.getContext())
                            .load(android.R.drawable.sym_def_app_icon)
                            .into(projectCommentsRecyclerViewListItemBinding.imageCommentsRecycler);
                }
            });
        }
    }
}
