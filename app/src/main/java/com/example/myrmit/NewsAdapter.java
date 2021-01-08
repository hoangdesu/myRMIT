package com.example.myrmit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class NewsAdapter extends PagerAdapter {
    private List<News> newsList;
    private LayoutInflater layoutInflater;
    private Context context;

    public NewsAdapter(List<News> newsList, Context context) {
        this.newsList = newsList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return newsList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.news_item, container, false);

        ImageView newsImage;
        TextView title, description;

        newsImage = (ImageView) view.findViewById(R.id.news_image);
        title = (TextView) view.findViewById(R.id.news_title);
        description = (TextView) view.findViewById(R.id.description);

        newsImage.setImageResource(newsList.get(position).getImage());
        title.setText(newsList.get(position).getTitle());
        description.setText(newsList.get(position).getDescription());

        container.addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
