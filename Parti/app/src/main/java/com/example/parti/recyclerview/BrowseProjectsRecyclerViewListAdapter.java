package com.example.parti.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parti.R;
import com.example.parti.wrappers.Project;


/**
 * Deprecated, replaced by BrowseProjectsAdapter
 *
 */
@Deprecated
public class BrowseProjectsRecyclerViewListAdapter extends RecyclerView.Adapter<BrowseProjectsRecyclerViewListAdapter.ViewHolder>{
    private Project[] projects;

    // RecyclerView recyclerView;
    public BrowseProjectsRecyclerViewListAdapter(Project[] projects) {
        this.projects = projects;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.browse_projects_recycler_view_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Project myListData = projects[position];
        holder.projectTitle.setText(projects[position].getName());
        holder.shortDescription.setText(projects[position].getShortDescription());


        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"click on item: "+ myListData.getShortDescription(), Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return projects.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView projectTitle;
        public TextView shortDescription;
        public ConstraintLayout constraintLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.image_browse_projects_recycler);
            this.projectTitle = (TextView) itemView.findViewById(R.id.input_browse_projects_recycler_project_title);
            this.shortDescription = (TextView) itemView.findViewById(R.id.input_browse_projects_recycler_short_description);
            constraintLayout = (ConstraintLayout) itemView.findViewById(R.id.my_projects_recycler_view_list_item_contraint_layout);
        }
    }
}