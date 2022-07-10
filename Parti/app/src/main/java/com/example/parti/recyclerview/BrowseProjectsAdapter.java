package com.example.parti.recyclerview;

import android.content.Intent;
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
import com.example.parti.databinding.BrowseProjectsRecyclerViewListItemBinding;
import com.example.parti.ui.main.MyProfileFragment;
import com.example.parti.ui.main.ViewProjectActivity;
import com.example.parti.wrappers.Project;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Random;

/**
 * RecyclerView adapter for a list of Restaurants.
 */
public class BrowseProjectsAdapter extends FirestoreAdapter<BrowseProjectsAdapter.ViewHolder> {

    public interface OnProjectSelectedListener {
        void onProjectSelected(DocumentSnapshot project);
    }

    private OnProjectSelectedListener mListener;

    public static final String DEFAULT_PROJECT_IMAGE_ID = Parti.DEFAULT_PROJECT_IMAGE_ID;

    public BrowseProjectsAdapter(Query query, OnProjectSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(BrowseProjectsRecyclerViewListItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
        //holder.itemView.setOnClickListener();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private BrowseProjectsRecyclerViewListItemBinding browseProjectsRecyclerViewListItemBinding;

        public ViewHolder(BrowseProjectsRecyclerViewListItemBinding binding) {
            super(binding.getRoot());
            this.browseProjectsRecyclerViewListItemBinding = binding;
        }

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnProjectSelectedListener listener) {

            Project project = snapshot.toObject(Project.class);

            // Load image
            String imageId = project.getImageId();
            if (imageId == null || imageId.equals(DEFAULT_PROJECT_IMAGE_ID)) {
                Glide.with(browseProjectsRecyclerViewListItemBinding.projectImage.getContext())
                        .load(android.R.drawable.ic_dialog_info)
                        .into(browseProjectsRecyclerViewListItemBinding.projectImage);
            } else {
                StorageReference imageReference = FirebaseStorage.getInstance().getReference().child(project.getImageId());
                final long ONE_MEGABYTE = 1024 * 1024;
                imageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        browseProjectsRecyclerViewListItemBinding.projectImage.setImageBitmap(bmp);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(ViewHolder.this.itemView.getContext(), "Failed to download project image", Toast.LENGTH_LONG).show();
                        //If failed, load the default local image;
                        Glide.with(browseProjectsRecyclerViewListItemBinding.projectImage.getContext())
                                .load(android.R.drawable.ic_dialog_info)
                                .into(browseProjectsRecyclerViewListItemBinding.projectImage);
                    }
                });
            }

            browseProjectsRecyclerViewListItemBinding.projectTitle.setText(project.getName());
            browseProjectsRecyclerViewListItemBinding.shortDescription.setText(project.getShortDescription());
            int numParticipants = project.getNumParticipants();
            int numParticipantsNeeded = project.getNumParticipantsNeeded();
            browseProjectsRecyclerViewListItemBinding.projectProgressBarSmall.setMax(numParticipantsNeeded);
            browseProjectsRecyclerViewListItemBinding.projectProgressBarSmall.setProgress(numParticipants);
            String progress = String.format(Locale.ENGLISH, "%d/%d Participated", numParticipants, numParticipantsNeeded);
            browseProjectsRecyclerViewListItemBinding.projectProgressTextSmall.setText(progress);
            float rating = 0;
            if (numParticipants != 0) rating = ((float) project.getTotalRating()) / numParticipants;
            browseProjectsRecyclerViewListItemBinding.projectRatingBarSmall.setRating(rating);
            String preview = String.format(Locale.ENGLISH, "%.1f/5", rating);
            browseProjectsRecyclerViewListItemBinding.projectRatingPreview.setText(preview);

            /*
            Random rnd = new Random(LocalDateTime.now().toLocalTime().toNanoOfDay());
            float next = rnd.nextFloat() * 5;
            String preview = String.format(Locale.ENGLISH, "%.1f", next);
            browseProjectsRecyclerViewListItemBinding.projectRatingBarSmall.setRating(next); //TODO
            browseProjectsRecyclerViewListItemBinding.projectRatingPreview.setText(preview);
             */


            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onProjectSelected(snapshot);
                    }
                    //TODO
                    Intent intent = new Intent(v.getContext(), ViewProjectActivity.class);
                    intent.putExtra("project", project);
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
}
