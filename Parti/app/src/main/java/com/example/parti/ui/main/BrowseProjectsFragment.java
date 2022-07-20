package com.example.parti.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

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
    private User user;
    private List<ProjectRecyclerAdapter> adapters;
    private FragmentBrowseProjectsBinding browseProjectsFragmentBinding;
    private FirestoreRecyclerOptions<Project> firestoreRecyclerOptions;
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
        user = ((Parti) getActivity().getApplication()).getLoggedInUser();
        initialiseAdapter();
    }

    private void setQuery(int position) {
        if (filterStatus == position) return;

        browseProjectsFragmentBinding.recyclerViewBrowseProject.setAdapter(adapters.get(position));
        filterStatus = position;
    }

    private ProjectRecyclerAdapter queryToAdapter(Query query, boolean filterActionable) {
        firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Project>()
                .setQuery(query, Project.class)
                .setLifecycleOwner(this)
                .build();
        return new ProjectRecyclerAdapter(
                firestoreRecyclerOptions,
                filterActionable,
                user.getUuid());
    }

    private Query defaultQuery() {
        return firebaseFirestore.collection(Parti.PROJECT_COLLECTION_PATH)
                .orderBy(Project.RANKING_FIELD, Query.Direction.DESCENDING);
    }

    private void initialiseAdapter() {

        adapters = new ArrayList<>();

        adapters.add(queryToAdapter(
                defaultQuery(),
                false));

        adapters.add(queryToAdapter(
                defaultQuery().whereEqualTo(Project.CONCLUDED_FIELD, false),
                true));

        adapters.add(queryToAdapter(
                defaultQuery().whereEqualTo(Project.ADMIN_FIELD, user.getUuid()),
                false));

        adapters.add(queryToAdapter(
                defaultQuery().whereArrayContains(Project.PARTICIPANTS_FIELD, user.getUuid()),
                false));

        adapters.add(queryToAdapter(
                defaultQuery().whereArrayContains(Project.COMMENTS_FIELD, user.getUuid()),
                false));

        adapters.add(queryToAdapter(
                defaultQuery().whereEqualTo(Project.CONCLUDED_FIELD, false),
                false));

        adapters.add(queryToAdapter(
                defaultQuery().whereEqualTo(Project.CONCLUDED_FIELD, true),
                false));

        adapters.add(queryToAdapter(
                defaultQuery().whereEqualTo(Project.PROJECT_TYPE_FIELD, ProjectType.APP.name()),
                false));

        adapters.add(queryToAdapter(
                defaultQuery().whereEqualTo(Project.PROJECT_TYPE_FIELD, ProjectType.SURVEY.name()),
                false));

        adapters.add(queryToAdapter(
                defaultQuery().whereEqualTo(Project.PROJECT_TYPE_FIELD, ProjectType.EXPERIMENT.name()),
                false));

        adapters.add(queryToAdapter(
                defaultQuery().whereEqualTo(Project.PROJECT_TYPE_FIELD, ProjectType.OTHER.name()),
                false));

        browseProjectsFragmentBinding.recyclerViewBrowseProject.setLayoutManager(new LinearLayoutManagerWrapper(getContext()));
        browseProjectsFragmentBinding.recyclerViewBrowseProject.setAdapter(adapters.get(FILTER_STATUS_ALL));

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