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
import com.example.parti.wrappers.ProjectType;
import com.example.parti.wrappers.User;
import com.example.parti.wrappers.util.LinearLayoutManagerWrapper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BrowseProjectsFragment extends Fragment {

    private static final String TAG = "browse-projects-fragment";
    private static final int FILTER_STATUS_DEFAULT = -1;
    private static final int FILTER_STATUS_ALL = 0;
    private static final int FILTER_STATUS_ACTIONABLE = FILTER_STATUS_ALL + 1;
    private static final int FILTER_STATUS_POSTED = FILTER_STATUS_ACTIONABLE + 1;
    private static final int FILTER_STATUS_PARTICIPATED = FILTER_STATUS_POSTED + 1;
    private static final int FILTER_STATUS_COMMENTED = FILTER_STATUS_PARTICIPATED + 1;
    private static final int FILTER_STATUS_ONGOING = FILTER_STATUS_COMMENTED + 1;
    private static final int FILTER_STATUS_ENDED = FILTER_STATUS_ONGOING + 1;
    private static final int FILTER_STATUS_APP = FILTER_STATUS_ENDED + 1;
    private static final int FILTER_STATUS_SURVEY = FILTER_STATUS_APP + 1;
    private static final int FILTER_STATUS_EXPERIMENT = FILTER_STATUS_SURVEY + 1;
    private static final int FILTER_STATUS_OTHER = FILTER_STATUS_EXPERIMENT + 1;

    private FirebaseFirestore firebaseFirestore;
    private Query query;
    private FragmentBrowseProjectsBinding browseProjectsFragmentBinding;
    private int filterStatus = FILTER_STATUS_ALL;
    private ProjectRecyclerAdapter projectRecyclerAdapter;

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
        browseProjectsFragmentBinding = FragmentBrowseProjectsBinding.inflate(
                inflater,
                container,
                false);
        browseProjectsFragmentBinding.buttonBrowseProjectNewProject.setOnClickListener(v -> {
            Intent intent = new Intent(BrowseProjectsFragment.this.getContext(), EditProjectActivity.class);
            intent.putExtra("purpose", EditProjectActivity.Purpose.CREATE);
            startActivity(intent);
        });

        return browseProjectsFragmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        initialiseAdapter();
    }

    private void setQuery(int position) {
        if (filterStatus == position) return;
        User user = ((Parti) getActivity().getApplication()).getLoggedInUser();
        query = firebaseFirestore.collection(Parti.PROJECT_COLLECTION_PATH);

        switch (position) {
            case (FILTER_STATUS_ALL):
                break;

            case (FILTER_STATUS_POSTED):
                query = query.whereEqualTo(Project.ADMIN_FIELD, user.getUuid());
                break;

            case (FILTER_STATUS_ACTIONABLE):
                query = query.whereEqualTo(Project.CONCLUDED_FIELD, false);
                break;

            case (FILTER_STATUS_PARTICIPATED):
                query = query.whereArrayContains(Project.PARTICIPANTS_FIELD, user.getUuid());
                break;

            case (FILTER_STATUS_COMMENTED):
                query = query.whereArrayContains(Project.COMMENTS_FIELD, user.getUuid());
                break;

            case (FILTER_STATUS_ONGOING):
                query = query.whereEqualTo(Project.CONCLUDED_FIELD, false);
                break;

            case (FILTER_STATUS_ENDED):
                query = query.whereEqualTo(Project.CONCLUDED_FIELD, true);
                break;

            case (FILTER_STATUS_APP):
                query = query.whereEqualTo(Project.PROJECT_TYPE_FIELD, ProjectType.APP.name());
                break;

            case (FILTER_STATUS_SURVEY):
                query = query.whereEqualTo(Project.PROJECT_TYPE_FIELD, ProjectType.SURVEY.name());
                break;

            case (FILTER_STATUS_EXPERIMENT):
                query = query.whereEqualTo(Project.PROJECT_TYPE_FIELD, ProjectType.EXPERIMENT.name());
                break;

            case (FILTER_STATUS_OTHER):
                query = query.whereEqualTo(Project.PROJECT_TYPE_FIELD, ProjectType.OTHER.name());
                break;

            default:
                break;
        }

        filterStatus = position;
        query = query.orderBy(Project.RANKING_FIELD, Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Project> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Project>()
                .setQuery(query, Project.class)
                .setLifecycleOwner(this)
                .build();
        projectRecyclerAdapter = new ProjectRecyclerAdapter(
                firestoreRecyclerOptions,
                position == FILTER_STATUS_ACTIONABLE,
                user.getUuid());
        browseProjectsFragmentBinding.recyclerViewBrowseProject.setLayoutManager(new LinearLayoutManagerWrapper(getContext()));
        browseProjectsFragmentBinding.recyclerViewBrowseProject.setAdapter(projectRecyclerAdapter);
    }

    private void initialiseAdapter() {
        query = firebaseFirestore.collection(Parti.PROJECT_COLLECTION_PATH);
        query = query.orderBy(Project.RANKING_FIELD, Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Project> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Project>()
                .setQuery(query, Project.class)
                .setLifecycleOwner(this)
                .build();
        projectRecyclerAdapter = new ProjectRecyclerAdapter(
                firestoreRecyclerOptions,
                false,
                "");
        browseProjectsFragmentBinding.recyclerViewBrowseProject.setLayoutManager(new LinearLayoutManagerWrapper(getContext()));
        browseProjectsFragmentBinding.recyclerViewBrowseProject.setAdapter(projectRecyclerAdapter);

        browseProjectsFragmentBinding.spinnerBrowseProjectFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(
                    AdapterView<?> parent,
                    View view,
                    int position,
                    long id) {
                setQuery(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //this is not gonna possibly happen
            }
        });
    }
}