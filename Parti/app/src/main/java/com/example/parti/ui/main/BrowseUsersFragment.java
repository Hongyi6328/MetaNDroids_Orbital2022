package com.example.parti.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.parti.Parti;
import com.example.parti.adapters.UserRecyclerAdapter;
import com.example.parti.databinding.FragmentBrowseUsersBinding;
import com.example.parti.wrappers.User;
import com.example.parti.wrappers.util.LinearLayoutManagerWrapper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.time.LocalDateTime;

public class BrowseUsersFragment extends Fragment {

    public static final int RECYCLER_VIEW_LIST_LIMIT = 50;
    public static final String SEARCH_PLACEHOLDER = "\uf8ff";
    public static final long USER_RECYCLER_VIEW_LIST_REFRESH_INTERVAL = 750_000; //in nanosecond

    FirebaseFirestore firebaseFirestore;
    Query query;
    FragmentBrowseUsersBinding fragmentBrowseUsersBinding;
    LocalDateTime searchBarActionEventTimeStamp;

    public BrowseUsersFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        fragmentBrowseUsersBinding = FragmentBrowseUsersBinding.inflate(inflater, container, false);
        return fragmentBrowseUsersBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        firebaseFirestore = FirebaseFirestore.getInstance();

        initialiseAdapter();

        searchBarActionEventTimeStamp = LocalDateTime.now();

        fragmentBrowseUsersBinding.inputBrowseUsersSearch.setOnEditorActionListener((v, actionId, event) -> {
            refreshSearchResult();
            return false;
        });

        fragmentBrowseUsersBinding.inputBrowseUsersSearch.setOnKeyListener((v, keyCode, event) -> {
            refreshSearchResult();
            return false;
        });

        fragmentBrowseUsersBinding.buttonBrowseUsersSearch.setOnClickListener(v -> {
            String searchInput = fragmentBrowseUsersBinding.inputBrowseUsersSearch.getText().toString();
            changeQuery(searchInput);
        });
    }

    private void initialiseAdapter() {
        query = firebaseFirestore.collection(Parti.USER_COLLECTION_PATH).limit(RECYCLER_VIEW_LIST_LIMIT);
        FirestoreRecyclerOptions<User> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .setLifecycleOwner(this)
                .build();
        UserRecyclerAdapter userRecyclerAdapter = new UserRecyclerAdapter(firestoreRecyclerOptions);
        fragmentBrowseUsersBinding.recyclerViewBrowseUsers.setLayoutManager(new LinearLayoutManagerWrapper(getContext()));
        fragmentBrowseUsersBinding.recyclerViewBrowseUsers.setAdapter(userRecyclerAdapter);
    }

    private void refreshSearchResult() {
        LocalDateTime now = LocalDateTime.now();
        long nanoEarlier = searchBarActionEventTimeStamp.toLocalTime().toNanoOfDay();
        long nanoLater = now.toLocalTime().toNanoOfDay();
        long nano = nanoLater - nanoEarlier;
        if (nano > USER_RECYCLER_VIEW_LIST_REFRESH_INTERVAL) {
            String searchInput = fragmentBrowseUsersBinding.inputBrowseUsersSearch.getText().toString();
            changeQuery(searchInput);
            searchBarActionEventTimeStamp = now;
        }
    }

    private void changeQuery(String searchInput) {
        query = firebaseFirestore.collection(Parti.USER_COLLECTION_PATH);
        if (!searchInput.isEmpty()) {
            query = query.orderBy(User.ALIAS_FIELD).startAt(searchInput).endAt(searchInput + SEARCH_PLACEHOLDER);
        }
        query = query.limit(RECYCLER_VIEW_LIST_LIMIT);
        FirestoreRecyclerOptions<User> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .setLifecycleOwner(this)
                .build();
        UserRecyclerAdapter userRecyclerAdapter = new UserRecyclerAdapter(firestoreRecyclerOptions);
        fragmentBrowseUsersBinding.recyclerViewBrowseUsers.setLayoutManager(new LinearLayoutManagerWrapper(getContext()));
        fragmentBrowseUsersBinding.recyclerViewBrowseUsers.setAdapter(userRecyclerAdapter);
    }
}