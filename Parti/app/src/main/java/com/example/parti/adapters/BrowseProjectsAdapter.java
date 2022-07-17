package com.example.parti.adapters;

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
import com.example.parti.ui.main.ViewProjectActivity;
import com.example.parti.wrappers.Project;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Locale;

/**
 * RecyclerView adapter for a list of Restaurants.
 */
@Deprecated
public class BrowseProjectsAdapter extends FirestoreAdapter<BrowseProjectsAdapter.ViewHolder> {

    public interface OnProjectSelectedListener {
        void onProjectSelected(DocumentSnapshot project);
    }

    private OnProjectSelectedListener onProjectSelectedListener;

    public BrowseProjectsAdapter(Query query, OnProjectSelectedListener listener) {
        super(query);
        onProjectSelectedListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(BrowseProjectsRecyclerViewListItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), onProjectSelectedListener);
        //holder.itemView.setOnClickListener();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private BrowseProjectsRecyclerViewListItemBinding browseProjectsRecyclerViewListItemBinding;
        private Project project;

        public ViewHolder(BrowseProjectsRecyclerViewListItemBinding binding) {
            super(binding.getRoot());
            this.browseProjectsRecyclerViewListItemBinding = binding;
        }

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnProjectSelectedListener listener) {

            project = snapshot.toObject(Project.class);

            downloadImage();
            displayValues();

            /*
            Random rnd = new Random(LocalDateTime.now().toLocalTime().toNanoOfDay());
            float next = rnd.nextFloat() * 5;
            String preview = String.format(Locale.ENGLISH, "%.1f", next);
            browseProjectsRecyclerViewListItemBinding.projectRatingBarSmall.setRating(next);
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

        private void downloadImage() {
            // Load image
            String imageId = project.getImageId();
            if (imageId == null || imageId.equals(Parti.DEFAULT_PROJECT_IMAGE_ID)) {
                Glide.with(browseProjectsRecyclerViewListItemBinding.imageBrowseProjectsRecycler.getContext())
                        .load(android.R.drawable.ic_dialog_info)
                        .into(browseProjectsRecyclerViewListItemBinding.imageBrowseProjectsRecycler);
            } else {
                StorageReference imageReference = FirebaseStorage.getInstance().getReference().child(imageId);
                final long ONE_MEGABYTE = 1024 * 1024;
                imageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        browseProjectsRecyclerViewListItemBinding.imageBrowseProjectsRecycler.setImageBitmap(bmp);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(ViewHolder.this.itemView.getContext(), "Failed to download project image", Toast.LENGTH_LONG).show();
                        //If failed, load the default local image;
                        Glide.with(browseProjectsRecyclerViewListItemBinding.imageBrowseProjectsRecycler.getContext())
                                .load(android.R.drawable.ic_dialog_info)
                                .into(browseProjectsRecyclerViewListItemBinding.imageBrowseProjectsRecycler);
                    }
                });
            }
        }

        private void displayValues() {
            browseProjectsRecyclerViewListItemBinding.inputBrowseProjectsRecyclerProjectTitle.setText(project.getName());
            browseProjectsRecyclerViewListItemBinding.inputBrowseProjectsRecyclerShortDescription.setText(project.shortDescription());
            int numActions = project.getNumActions();
            int numActionsNeeded = project.getNumActionsNeeded();
            browseProjectsRecyclerViewListItemBinding.progressBarBrowseProjects.setMax(numActionsNeeded);
            browseProjectsRecyclerViewListItemBinding.progressBarBrowseProjects.setProgress(numActions);
            String progress = String.format(Locale.ENGLISH, "%d/%d Actions Done", numActions, numActionsNeeded);
            browseProjectsRecyclerViewListItemBinding.inputBrowseProjectsRecyclerProgress.setText(progress);
            float rating = 0;
            int numPeopleRated = project.getNumComments();
            if (numPeopleRated != 0) rating = ((float) project.getTotalRating()) / numPeopleRated;
            browseProjectsRecyclerViewListItemBinding.ratingBarBrowseProjectsRecycler.setRating(rating);
            String preview = String.format(Locale.ENGLISH, "%.1f/5", rating);
            browseProjectsRecyclerViewListItemBinding.inputBrowseProjectsRecyclerRating.setText(preview);
        }
    }
}
