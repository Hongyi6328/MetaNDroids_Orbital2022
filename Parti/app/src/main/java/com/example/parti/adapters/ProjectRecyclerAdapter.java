package com.example.parti.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.parti.databinding.BrowseProjectsRecyclerViewListItemBinding;
import com.example.parti.wrappers.Project;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ProjectRecyclerAdapter extends FirestoreRecyclerAdapter<Project, ProjectHolder> {

    private boolean filterActionable;
    private String uuid;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ProjectRecyclerAdapter(@NonNull FirestoreRecyclerOptions<Project> options, boolean filterActionable, String uuid) {
        super(options);
        this.filterActionable = filterActionable;
        this.uuid = uuid;
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectHolder projectHolder, int position, @NonNull Project project) {
        projectHolder.bind(position, project, filterActionable, uuid);
    }

    @NonNull
    @Override
    public ProjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProjectHolder(BrowseProjectsRecyclerViewListItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }
}
