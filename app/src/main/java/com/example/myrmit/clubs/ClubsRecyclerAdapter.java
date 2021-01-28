package com.example.myrmit.clubs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myrmit.R;

import java.util.List;

public class ClubsRecyclerAdapter extends RecyclerView.Adapter<ClubsRecyclerAdapter.ClubsViewHolder> {

    List<String> clubNames;
    List<String> clubCategories;
    List<String> clubCreatedDates;

    public ClubsRecyclerAdapter(List<String> clubNames, List<String> clubCategories, List<String> clubCreatedDates) {
        this.clubNames = clubNames;
        this.clubCategories = clubCategories;
        this.clubCreatedDates = clubCreatedDates;
    }

    @NonNull
    @Override
    public ClubsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.club_items, parent, false);
        ClubsViewHolder viewHolder = new ClubsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ClubsViewHolder holder, int position) {
        holder.tvClubTitle.setText(clubNames.get(position));
        holder.tvClubCategory.setText(clubCategories.get(position));
        holder.tvClubCreatedDate.setText(clubCreatedDates.get(position));
    }

    @Override
    public int getItemCount() {
        return 12;
    }

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
