package com.example.parti.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.parti.databinding.UsersRecyclerViewListItemBinding;
import com.example.parti.wrappers.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class UserRecyclerAdapter extends FirestoreRecyclerAdapter<User, UserHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public UserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<User> options) {
        super(options);
    }

    @Override
    public void onBindViewHolder(UserHolder userHolder, int position, User user) {
        userHolder.bind(position, user);
    }

    @Override
    public UserHolder onCreateViewHolder(ViewGroup group, int i) {
        return new UserHolder(UsersRecyclerViewListItemBinding.inflate(
                LayoutInflater.from(group.getContext()), group, false));
    }
}
