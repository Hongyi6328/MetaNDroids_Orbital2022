package com.example.parti.adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.Visibility;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.parti.databinding.BrowseProjectsRecyclerViewListItemBinding;
import com.example.parti.ui.main.UserProfileFragment;
import com.example.parti.ui.main.ViewProjectActivity;
import com.example.parti.wrappers.Project;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Locale;

public class ProjectHolder extends RecyclerView.ViewHolder {

    private BrowseProjectsRecyclerViewListItemBinding browseProjectsRecyclerViewListItemBinding;
    private Project project;

    public ProjectHolder(@NonNull View itemView) {
        super(itemView);
    }

    public ProjectHolder(BrowseProjectsRecyclerViewListItemBinding binding) {
        super(binding.getRoot());
        this.browseProjectsRecyclerViewListItemBinding = binding;
    }

    public void bind(int position, Project project, boolean filterActionable, String uuid) {
        this.project = project;

        if (filterActionable && uuid.equals(project.getAdmin())) { //TODO This is just a workaround
            setViewVisibility(View.GONE);
            return;
        } else if (filterActionable) {
            setViewVisibility(View.VISIBLE);
        }

        downloadImage();
        displayValues();

        // Click listener
        itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ViewProjectActivity.class);
            intent.putExtra(Project.CLASS_ID, ProjectHolder.this.project);
            v.getContext().startActivity(intent);
        });
    }

    private void setViewVisibility(int visibility) {
        browseProjectsRecyclerViewListItemBinding.inputBrowseProjectsRecyclerTitle.setVisibility(visibility);
        browseProjectsRecyclerViewListItemBinding.imageBrowseProjectsRecycler.setVisibility(visibility);
        browseProjectsRecyclerViewListItemBinding.inputBrowseProjectsRecyclerProgress.setVisibility(visibility);
        browseProjectsRecyclerViewListItemBinding.inputBrowseProjectsRecyclerRating.setVisibility(visibility);
        browseProjectsRecyclerViewListItemBinding.progressBarBrowseProjects.setVisibility(visibility);
        browseProjectsRecyclerViewListItemBinding.constraintLayoutBrowseProjectsRecycler.setVisibility(visibility);
        browseProjectsRecyclerViewListItemBinding.dividerBrowseProjectsRecycler.setVisibility(visibility);
        browseProjectsRecyclerViewListItemBinding.inputBrowseProjectsRecyclerShortDescription.setVisibility(visibility);
        browseProjectsRecyclerViewListItemBinding.ratingBarBrowseProjectsRecycler.setVisibility(visibility);
        itemView.setVisibility(visibility);
    }

    private void downloadImage() {
        // Load image
        String imageId = project.getImageId();
        if (imageId == null || imageId.equals(Project.DEFAULT_PROJECT_IMAGE_ID)) {
            Glide.with(browseProjectsRecyclerViewListItemBinding.imageBrowseProjectsRecycler.getContext())
                    .load(android.R.drawable.ic_dialog_info)
                    .into(browseProjectsRecyclerViewListItemBinding.imageBrowseProjectsRecycler);
        } else {
            StorageReference imageReference = FirebaseStorage.getInstance().getReference().child(imageId);
            final long ONE_MEGABYTE = 1024 * 1024;
            imageReference
                    .getBytes(ONE_MEGABYTE)
                    .addOnSuccessListener(bytes -> {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        browseProjectsRecyclerViewListItemBinding.imageBrowseProjectsRecycler.setImageBitmap(bitmap);
                    })
                    .addOnFailureListener(exception -> {
                        Toast.makeText(ProjectHolder.this.itemView.getContext(), "Failed to download project image.", Toast.LENGTH_LONG).show();
                        //If failed, load the default local image;
                        Glide.with(browseProjectsRecyclerViewListItemBinding.imageBrowseProjectsRecycler.getContext())
                                .load(android.R.drawable.ic_dialog_info)
                                .into(browseProjectsRecyclerViewListItemBinding.imageBrowseProjectsRecycler);
                    });
        }
    }

    private void displayValues() {
        browseProjectsRecyclerViewListItemBinding.inputBrowseProjectsRecyclerTitle.setText(project.getName());
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
        String pp = String.format(Locale.ENGLISH, "%.2f PPs!", project.getParticipationPoints().get(0));
        browseProjectsRecyclerViewListItemBinding.inputBrowseProjectsPPs.setText(pp);
    }
}
