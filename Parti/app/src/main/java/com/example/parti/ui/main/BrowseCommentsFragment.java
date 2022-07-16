package com.example.parti.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.parti.Parti;
import com.example.parti.R;
import com.example.parti.databinding.FragmentBrowseCommentsBinding;
import com.example.parti.recyclerview.BrowseProjectsAdapter;
import com.example.parti.recyclerview.CommentAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class BrowseCommentsFragment extends Fragment implements CommentAdapter.OnCommentSelectedListener {

    private FragmentBrowseCommentsBinding fragmentBrowseCommentsBinding;
    private FirebaseFirestore firebaseFirestore;
    private Query query;
    CommentAdapter commentAdapter;

    public BrowseCommentsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentBrowseCommentsBinding = FragmentBrowseCommentsBinding.inflate(inflater, container, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        initialiseAdapter();
        return fragmentBrowseCommentsBinding.getRoot();
    }

    @Override
    public void onCommentSelected(DocumentSnapshot comment) {

    }

    private void initialiseAdapter() {
        query = firebaseFirestore.collection(Parti.PROJECT_COLLECTION_PATH).document("bCPdxTzGAzfPN7GuWhqI6m4ISV62").collection(Parti.COMMENT_SUBCOLLECTION_PATH);
        CommentAdapter adapter = new CommentAdapter(query, this);
        fragmentBrowseCommentsBinding.recyclerViewBrowseComments.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentBrowseCommentsBinding.recyclerViewBrowseComments.setAdapter(adapter);
    }
}