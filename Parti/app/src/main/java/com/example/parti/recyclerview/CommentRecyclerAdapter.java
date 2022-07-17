package com.example.parti.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.parti.databinding.ProjectCommentsRecyclerViewListItemBinding;
import com.example.parti.wrappers.ProjectComment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class CommentRecyclerAdapter extends FirestoreRecyclerAdapter<ProjectComment, CommentHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public CommentRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ProjectComment> options) {
        super(options);
    }

    @Override
    public void onBindViewHolder(CommentHolder commentHolder, int position, ProjectComment comment) {
        commentHolder.bind(position, comment);
    }

    @Override
    public CommentHolder onCreateViewHolder(ViewGroup group, int i) {
        return new CommentHolder(ProjectCommentsRecyclerViewListItemBinding.inflate(
                LayoutInflater.from(group.getContext()), group, false));
    }
}
