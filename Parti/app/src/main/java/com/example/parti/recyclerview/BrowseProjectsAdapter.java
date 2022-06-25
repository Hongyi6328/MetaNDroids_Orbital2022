package com.example.parti.recyclerview;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.parti.Parti;
import com.example.parti.databinding.BrowseProjectsRecyclerViewListItemBinding;
import com.example.parti.wrapper.classes.Project;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private BrowseProjectsRecyclerViewListItemBinding binding;

        public ViewHolder(BrowseProjectsRecyclerViewListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnProjectSelectedListener listener) {

            Project project = snapshot.toObject(Project.class);
            Resources resources = itemView.getResources();

            // Load image
            String imageId = project.getImageId();
            if (imageId.equals(DEFAULT_PROJECT_IMAGE_ID)) imageId = "" + android.R.drawable.ic_dialog_info;
            Glide.with(binding.projectImage.getContext())
                    .load(android.R.drawable.ic_dialog_info) //TODO
                    .into(binding.projectImage);

            binding.projectTitle.setText(project.getName());
            binding.shortDescription.setText(project.getShortDescription());

            Random rnd = new Random(LocalDateTime.now().toLocalTime().toNanoOfDay());
            float next = rnd.nextFloat() * 10;
            String preview = Float.valueOf(next).toString();
            binding.projectRatingBarSmall.setRating(next); //TODO
            binding.projectRatingPreview.setText(preview);

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onProjectSelected(snapshot);
                    }
                }
            });
        }

    }
}
