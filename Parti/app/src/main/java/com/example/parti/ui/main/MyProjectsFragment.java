package com.example.parti.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.parti.Parti;
import com.example.parti.databinding.FragmentBrowseProjectsBinding;
import com.example.parti.recyclerview.BrowseProjectsAdapter;
import com.example.parti.recyclerview.MyProjectsAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MyProjectsFragment extends Fragment implements MyProjectsAdapter.OnProjectSelectedListener {

    FirebaseFirestore firebaseFirestore;
    Query query;
    BrowseProjectsAdapter browseProjectsAdapter;
    FragmentBrowseProjectsBinding browseProjectsFragmentBinding;

    static final String TAG = "read-data";
    public static final String PROJECT_COLLECTION_PATH = Parti.PROJECT_COLLECTION_PATH;

    public BrowseProjectsFragment() {}

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
        browseProjectsFragmentBinding = FragmentBrowseProjectsBinding.inflate(inflater, container, false);
        return browseProjectsFragmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        query = firebaseFirestore.collection(PROJECT_COLLECTION_PATH);
        //.orderBy("avgRating", Query.Direction.DESCENDING)
        //.limit(LIMIT);
        browseProjectsAdapter = new BrowseProjectsAdapter(query, this);

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
         */
        browseProjectsFragmentBinding.browseProjectsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        browseProjectsFragmentBinding.browseProjectsRecyclerView.setAdapter(browseProjectsAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Start listening for Firestore updates
        if (browseProjectsAdapter != null) {
            browseProjectsAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (browseProjectsAdapter != null) {
            browseProjectsAdapter.stopListening();
        }
    }


    @Override
    public void onProjectSelected(DocumentSnapshot project) {
        // Go to the details page for the selected restaurant TODO
        //BrowseProjectsFragmentDirections.ActionMainFragmentToRestaurantDetailFragment action = MainFragmentDirections
        //        .actionMainFragmentToRestaurantDetailFragment(restaurant.getId());

        //NavHostFragment.findNavController(this)
        //        .navigate(action);
    }