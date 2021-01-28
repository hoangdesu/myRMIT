package com.example.myrmit.clubs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myrmit.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClubsRecyclerAdapter extends RecyclerView.Adapter<ClubsRecyclerAdapter.ClubsViewHolder> implements Filterable {

    List<String> clubLogos;
    List<String> clubNames;
    List<String> clubCategories;
    List<String> clubCreatedDates;
    List<String> clubListAll;

    View view;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    //StorageReference storageReference = FirebaseStorage.getInstance().getReference("gs://myrmit-c2020.appspot.com/Basketball.png");

    public ClubsRecyclerAdapter(List<String> clubLogos, List<String> clubNames, List<String> clubCategories, List<String> clubCreatedDates) {
        this.clubLogos = clubLogos;
        this.clubNames = clubNames;
        this.clubCategories = clubCategories;
        this.clubCreatedDates = clubCreatedDates;
        this.clubListAll = new ArrayList<>(clubNames);
    }

    @NonNull
    @Override
    public ClubsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        view = layoutInflater.inflate(R.layout.club_items, parent, false);
        ClubsViewHolder viewHolder = new ClubsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ClubsViewHolder holder, int position) {
        String folderPath = "gs://myrmit-c2020.appspot.com/";
        String logo = clubLogos.get(position);

        Glide.with(view.getContext()).load(logo).into(holder.ivClubLogo);

        holder.tvClubTitle.setText(clubNames.get(position));
        holder.tvClubCategory.setText(clubCategories.get(position));
        holder.tvClubCreatedDate.setText(clubCreatedDates.get(position));

    }

    @Override
    public int getItemCount() {
        return clubNames.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        // run no background thread
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<String> filteredList = new ArrayList<>();

            if (charSequence.toString().isEmpty()) {
                filteredList.addAll(clubListAll);
            } else {
                for (String name : clubListAll) {
                    if (name.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        filteredList.add(name);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;

            return filterResults;
        }
        // run on UI thread
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            clubNames.clear();
            clubNames.addAll((Collection<? extends String>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    class ClubsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivClubLogo;
        TextView tvClubTitle, tvClubCategory, tvClubCreatedDate;

        public ClubsViewHolder(@NonNull View itemView) {
            super(itemView);

            ivClubLogo = itemView.findViewById(R.id.ivClubLogo);
            tvClubTitle = itemView.findViewById(R.id.tvClubTitle);
            tvClubCategory = itemView.findViewById(R.id.tvClubCategory);
            tvClubCreatedDate = itemView.findViewById(R.id.tvClubCreatedDate);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(), clubNames.get(getAdapterPosition()), Toast.LENGTH_SHORT).show();
        }
    }
}
