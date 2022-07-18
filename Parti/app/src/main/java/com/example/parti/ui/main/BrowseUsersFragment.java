package com.example.parti.ui.main;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.parti.Parti;
import com.example.parti.adapters.UserRecyclerAdapter;
import com.example.parti.databinding.FragmentBrowseUsersBinding;
import com.example.parti.wrappers.User;
import com.example.parti.wrappers.util.LinearLayoutManagerWrapper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.time.LocalDateTime;
import java.util.Date;

public class BrowseUsersFragment extends Fragment {

    public static final int RECYCLER_VIEW_LIST_LIMIT = 50;
    public static final String SEARCH_PLACEHOLDER = "\uf8ff";
    public static final long USER_RECYCLER_VIEW_LIST_REFRESH_INTERVAL = 1000;

    FirebaseFirestore firebaseFirestore;
    Query query;
    UserRecyclerAdapter userRecyclerAdapter;
    FragmentBrowseUsersBinding fragmentBrowseUsersBinding;
    LocalDateTime searchBarActionEventTimeStamp;

    public BrowseUsersFragment() {}

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

        fragmentBrowseUsersBinding.inputBrowseUsersSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                LocalDateTime now = LocalDateTime.now();
                if (now.getNano() - searchBarActionEventTimeStamp.getNano() > USER_RECYCLER_VIEW_LIST_REFRESH_INTERVAL) {
                    String searchInput = fragmentBrowseUsersBinding.inputBrowseUsersSearch.getText().toString();
                    changeQuery(searchInput);
                    searchBarActionEventTimeStamp = now;
                }
                return false;
            }
        });

        fragmentBrowseUsersBinding.buttonBrowseUsersSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchInput = fragmentBrowseUsersBinding.inputBrowseUsersSearch.getText().toString();
                changeQuery(searchInput);
            }
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

















/*
@Deprecated
public class BrowseUsersFragment extends Fragment implements MyProjectsAdapter.OnProjectSelectedListener {

    FirebaseFirestore firebaseFirestore;
    Query query;
    MyProjectsAdapter userRecyclerAdapter;
    FragmentMyProjectsBinding fragmentBrowseUsersBinding;

    static final String TAG = "read-data";
    public static final String PROJECT_COLLECTION_PATH = Parti.PROJECT_COLLECTION_PATH;
    public static final String USER_COLLECTION_PATH = Parti.USER_COLLECTION_PATH;

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

        //setHasOptionsMenu(true);
        fragmentBrowseUsersBinding = FragmentMyProjectsBinding.inflate(inflater, container, false);
        fragmentBrowseUsersBinding.buttonNewProjectDeprecated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BrowseUsersFragment.this.getContext(), EditProjectActivity.class);
                intent.putExtra("purpose", EditProjectActivity.Purpose.CREATE);
                startActivity(intent);
            }
        });

        return fragmentBrowseUsersBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        query = firebaseFirestore.collection(PROJECT_COLLECTION_PATH);
        //.orderBy("avgRating", Query.Direction.DESCENDING)
        //.limit(LIMIT);
        userRecyclerAdapter = new MyProjectsAdapter(query, this);

        //int a = android.R.drawable.ic_dialog_email;
        //Log.d(TAG, "" + a);

        /*
        // The following code is just for testing purposes
        Project[] projects = new Project[] {
                new Project(1, "Email", "This is a short description about the project", android.R.drawable.ic_dialog_email),
                new Project(2, "Info", "This is a short description about the project", android.R.drawable.ic_dialog_info),
                new Project(3, "Delete", "This is a short description about the project", android.R.drawable.ic_delete),
                new Project(3, "Dialer", "This is a short description about the project", android.R.drawable.ic_dialog_dialer),
                new Project(4, "Alert", "This is a short description about the project", android.R.drawable.ic_dialog_alert),
                new Project(5, "Map", "This is a short description about the project", android.R.drawable.ic_dialog_map),
                new Project(6, "Email", "This is a short description about the project", android.R.drawable.ic_dialog_email),
                new Project(7, "Info", "This is a short description about the project", android.R.drawable.ic_dialog_info),
                new Project(8, "Delete", "This is a short description about the project", android.R.drawable.ic_delete),
                new Project(9, "Dialer", "This is a short description about the project", android.R.drawable.ic_dialog_dialer),
                new Project(10, "Alert", "This is a short description about the project", android.R.drawable.ic_dialog_alert),
                new Project(11, "Map", "This is a short description about the project", android.R.drawable.ic_dialog_map),
        };
         */

        /*
        View view = inflater.inflate(R.layout.fragment_browse_projects, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.browse_projects_recycler_view);
        BrowseProjectsRecyclerViewListAdapter adapter = new BrowseProjectsRecyclerViewListAdapter(projects);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        return view;
        //

        fragmentBrowseUsersBinding.myProjectsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentBrowseUsersBinding.myProjectsRecyclerView.setAdapter(userRecyclerAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Start listening for Firestore updates
        if (userRecyclerAdapter != null) {
            userRecyclerAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (userRecyclerAdapter != null) {
            userRecyclerAdapter.stopListening();
        }
    }


    @Override
    public void onProjectSelected(DocumentSnapshot project) {
        //BrowseProjectsFragmentDirections.ActionMainFragmentToRestaurantDetailFragment action = MainFragmentDirections
        //        .actionMainFragmentToRestaurantDetailFragment(restaurant.getId());

        //NavHostFragment.findNavController(this)
        //        .navigate(action);
    }
}
*/