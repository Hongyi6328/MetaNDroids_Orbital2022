package com.example.parti.recyclerview;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.parti.wrappers.Project;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ProjectRecyclerAdapter extends FirestoreRecyclerAdapter<Project, ProjectHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ProjectRecyclerAdapter(@NonNull FirestoreRecyclerOptions<Project> options) {
        super(options);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectHolder holder, int position, @NonNull Project model) {

    }

    @NonNull
    @Override
    public ProjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }
}
