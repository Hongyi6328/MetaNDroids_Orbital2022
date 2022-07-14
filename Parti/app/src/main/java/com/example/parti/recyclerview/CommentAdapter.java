package com.example.parti.recyclerview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.parti.Parti;
import com.example.parti.databinding.ProjectCommentsRecyclerViewListItemBinding;
import com.example.parti.wrappers.ProjectComment;
import com.example.parti.wrappers.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * RecyclerView adapter for a list of Restaurants.
 */
public class CommentAdapter extends FirestoreAdapter<CommentAdapter.ViewHolder> {

    public interface OnCommentSelectedListener {
        void onCommentSelected(DocumentSnapshot comment);
    }

    private OnCommentSelectedListener onCommentSelectedListener;

    public static final String DEFAULT_PROFILE_IMAGE_ID = Parti.DEFAULT_PROFILE_IMAGE_ID;

    public CommentAdapter(Query query, OnCommentSelectedListener listener) {
        super(query);
        onCommentSelectedListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ProjectCommentsRecyclerViewListItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), onCommentSelectedListener);
        //holder.itemView.setOnClickListener();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ProjectCommentsRecyclerViewListItemBinding projectCommentsRecyclerViewListItemBinding;

        public ViewHolder(ProjectCommentsRecyclerViewListItemBinding binding) {
            super(binding.getRoot());
            this.projectCommentsRecyclerViewListItemBinding = binding;
        }

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnCommentSelectedListener listener) {

            ProjectComment comment = snapshot.toObject(ProjectComment.class);

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
                        alias = Parti.DEFAULT_USER_ALIAS;
                    }
                    projectCommentsRecyclerViewListItemBinding.senderAlias.setText(alias);
                }
            });

            // Load image
            String imageId = null;
            if (comment != null) imageId = Parti.PROJECT_IMAGE_COLLECTION_PATH + '/' + comment.getSenderId() + ".jpg";
            if (imageId == null || imageId.equals("")) {
                Glide.with(projectCommentsRecyclerViewListItemBinding.senderProfileImage.getContext())
                        .load(android.R.drawable.sym_def_app_icon)
                        .into(projectCommentsRecyclerViewListItemBinding.senderProfileImage);
            } else {
                StorageReference imageReference = FirebaseStorage.getInstance().getReference().child(imageId);
                final long ONE_MEGABYTE = 1024 * 1024;
                imageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        projectCommentsRecyclerViewListItemBinding.senderProfileImage.setImageBitmap(bmp);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(ViewHolder.this.itemView.getContext(), "Failed to download sender profile image", Toast.LENGTH_LONG).show();
                        //If failed, load the default local image;
                        Glide.with(projectCommentsRecyclerViewListItemBinding.senderProfileImage.getContext())
                                .load(android.R.drawable.sym_def_app_icon)
                                .into(projectCommentsRecyclerViewListItemBinding.senderProfileImage);
                    }
                });
            }

            projectCommentsRecyclerViewListItemBinding.commentBody.setText(comment.getComment());
            projectCommentsRecyclerViewListItemBinding.commentRatingBarSmall.setRating(comment.getRating());

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onCommentSelected(snapshot);
                    }
                    //TODO Go to user profile
                }
            });
        }
    }
}