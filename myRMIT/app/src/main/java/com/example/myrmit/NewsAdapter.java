package com.example.myrmit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyHolder> {
    private Context context;
    private List<News> data;

    public NewsAdapter(Context context, List<News> data) {
        this.context = context;
        this.data = data;
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.news_item, viewGroup, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //Set title, thumnail for the main activity
        holder.newsTitle.setText(data.get(position).getTitle());
        holder.newsThumbnail.setImageResource(data.get(position).getThumbnail());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Send data to NewsActivity
                /*Intent intent = new Intent(context,NewsActivity.class);
                intent.putExtra("Title",data.get(position).getTitle());
                intent.putExtra("Author",data.get(position).getAuthor());
                intent.putExtra("Thumbnail", data.get(position).getThumbnail());
                context.startActivity(intent);*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView newsTitle;
        CardView cardView;
        ImageView newsThumbnail;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            newsTitle = (TextView) itemView.findViewById(R.id.news_title);
            newsThumbnail = (ImageView) itemView.findViewById(R.id.news_image);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
        }
    }
}
