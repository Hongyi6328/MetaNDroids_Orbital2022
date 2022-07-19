package com.example.parti.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.parti.Parti;
import com.example.parti.adapters.ProjectRecyclerAdapter;
import com.example.parti.databinding.FragmentBrowseProjectsBinding;
import com.example.parti.wrappers.Project;
import com.example.parti.wrappers.User;
import com.example.parti.wrappers.util.LinearLayoutManagerWrapper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class BrowseProjectsFragment extends Fragment /*implements BrowseProjectsAdapter.OnProjectSelectedListener*/ {

    private static final String TAG = "browse-projects-fragment";
    private static final int FILTER_STATUS_DEFAULT = -1;
    private static final int FILTER_STATUS_ALL = 0;
    private static final int FILTER_STATUS_POSTED = 1;
    private static final int FILTER_STATUS_PARTICIPATED = 2;
    private static final int FILTER_STATUS_COMMENTED = 3;
    private static final int FILTER_STATUS_ONGOING = 4;
    private static final int FILTER_STATUS_ENDED = 5;
    // public static final String PROJECT_COLLECTION_PATH = Parti.PROJECT_COLLECTION_PATH;

    private FirebaseFirestore firebaseFirestore;
    private Query query;
    private FragmentBrowseProjectsBinding browseProjectsFragmentBinding;
    private int filterStatus = FILTER_STATUS_ALL;
    private ProjectRecyclerAdapter projectRecyclerAdapter;
    //private BrowseProjectsAdapter browseProjectsAdapter;

    public BrowseProjectsFragment() {
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
        browseProjectsFragmentBinding = FragmentBrowseProjectsBinding.inflate(inflater, container, false);
        browseProjectsFragmentBinding.buttonBrowseProjectNewProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                if (!firebaseAuth.getCurrentUser().isEmailVerified()) {
                    Toast.makeText(BrowseProjectsFragment.this.getContext(), "Please verify your email before adding a new project", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(BrowseProjectsFragment.this.getContext(), EditProjectActivity.class);
                    intent.putExtra("purpose", EditProjectActivity.Purpose.CREATE);
                    startActivity(intent);
                }
            }
        });

        return browseProjectsFragmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        initialiseAdapter();

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
    }

    private void setQuery(int position) {
        if (filterStatus == position) return;
        User user = ((Parti) getActivity().getApplication()).getLoggedInUser();
        query = firebaseFirestore.collection(Parti.PROJECT_COLLECTION_PATH);
        List<String> projectsPostedList = user.getProjectsPosted();
        List<String> projectsParticipatedList = user.getProjectsParticipated();
        List<String> projectsCommentedList = user.getCommentsPosted();

        switch (position) {
            case (FILTER_STATUS_ALL):
                break;

            case (FILTER_STATUS_POSTED):
                if (projectsPostedList == null || projectsPostedList.isEmpty()) {
                    /*
                    Toast.makeText(BrowseProjectsFragment.this.getContext(), "You posted no projects", Toast.LENGTH_LONG).show();
                    browseProjectsFragmentBinding.projectFilter.setSelection(filterStatus);
                    return;
                    */
                    query = query.whereEqualTo(Project.PROJECT_ID_FIELD, Parti.PROJECT_MASK);
                } else
                    query = query.whereIn(Project.PROJECT_ID_FIELD, projectsPostedList); //TODO list size cannot be greater than 10
                break;

            case (FILTER_STATUS_PARTICIPATED):
                if (projectsParticipatedList == null || projectsParticipatedList.isEmpty()) {
                    /*
                    Toast.makeText(BrowseProjectsFragment.this.getContext(), "You participated in no projects", Toast.LENGTH_LONG).show();
                    browseProjectsFragmentBinding.projectFilter.setSelection(filterStatus);
                    return;
                    */
                    query = query.whereEqualTo(Project.PROJECT_ID_FIELD, Parti.PROJECT_MASK);
                } else
                    query = query.whereIn(Project.PROJECT_ID_FIELD, projectsParticipatedList); //TODO list size cannot be greater than 10
                break;

            case (FILTER_STATUS_COMMENTED):
                if (projectsCommentedList == null || projectsCommentedList.isEmpty()) {
                    /*
                    Toast.makeText(BrowseProjectsFragment.this.getContext(), "You commented no projects", Toast.LENGTH_LONG).show();
                    browseProjectsFragmentBinding.projectFilter.setSelection(filterStatus);
                    return;
                    */
                    query = query.whereEqualTo(Project.PROJECT_ID_FIELD, Parti.PROJECT_MASK);
                } else
                    query = query.whereIn(Project.PROJECT_ID_FIELD, projectsCommentedList); //TODO list size cannot be greater than 10
                break;

            case (FILTER_STATUS_ONGOING):
                query = query.whereEqualTo(Project.CONCLUDED_FIELD, false);
                break;

            case (FILTER_STATUS_ENDED):
                query = query.whereEqualTo(Project.CONCLUDED_FIELD, true);
                break;

            default:
                break;
        }

        filterStatus = position;
        FirestoreRecyclerOptions<Project> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Project>()
                .setQuery(query, Project.class)
                .setLifecycleOwner(this)
                .build();
        projectRecyclerAdapter = new ProjectRecyclerAdapter(firestoreRecyclerOptions);
        browseProjectsFragmentBinding.recyclerViewBrowseProject.setLayoutManager(new LinearLayoutManagerWrapper(getContext()));
        browseProjectsFragmentBinding.recyclerViewBrowseProject.setAdapter(projectRecyclerAdapter);
    }

    private void initialiseAdapter() {
        query = firebaseFirestore.collection(Parti.PROJECT_COLLECTION_PATH);
        FirestoreRecyclerOptions<Project> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Project>()
                .setQuery(query, Project.class)
                .setLifecycleOwner(this)
                .build();
        projectRecyclerAdapter = new ProjectRecyclerAdapter(firestoreRecyclerOptions);
        browseProjectsFragmentBinding.recyclerViewBrowseProject.setLayoutManager(new LinearLayoutManagerWrapper(getContext()));
        browseProjectsFragmentBinding.recyclerViewBrowseProject.setAdapter(projectRecyclerAdapter);

        browseProjectsFragmentBinding.spinnerBrowseProjectFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setQuery(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //this is not gonna possibly happen
            }
        });

        /*
        query = firebaseFirestore.collection(Parti.PROJECT_COLLECTION_PATH);
        //query.orderBy("avgRating", Query.Direction.DESCENDING)
        //.limit(LIMIT);
        browseProjectsAdapter = new BrowseProjectsAdapter(query, this);
        browseProjectsFragmentBinding.recyclerViewBrowseProject.setLayoutManager(new LinearLayoutManager(getContext()));
        browseProjectsFragmentBinding.recyclerViewBrowseProject.setAdapter(browseProjectsAdapter);
        */
    }

    /*
    @Override
    public void onStart() {
        super.onStart();
        // Start listening for Firestore updates
        if (browseProjectsAdapter != null) {
            browseProjectsAdapter.startListening();
        }
    }
    */

    /*
    @Override
    public void onStop() {
        super.onStop();
        if (browseProjectsAdapter != null) {
            browseProjectsAdapter.stopListening();
        }
    }
    */

    /*
    @Override
    public void onProjectSelected(DocumentSnapshot project) {
        // Go to the details page for the selected project
        //BrowseProjectsFragmentDirections.ActionMainFragmentToRestaurantDetailFragment action = MainFragmentDirections
        //        .actionMainFragmentToRestaurantDetailFragment(restaurant.getId());

        //NavHostFragment.findNavController(this)
        //        .navigate(action);
    }
    */
}