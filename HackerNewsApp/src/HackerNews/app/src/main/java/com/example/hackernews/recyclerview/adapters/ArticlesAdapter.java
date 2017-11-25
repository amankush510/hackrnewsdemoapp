package com.example.hackernews.recyclerview.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.hackernews.R;
import com.example.hackernews.realm.controller.RealmController;
import com.example.hackernews.realm.models.ArticleModel;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;

/**
 * Created by aman.kush on 9/15/2017.
 */
public class ArticlesAdapter extends RealmRecyclerViewAdapter<ArticleModel> {
    final Context context;

    public ArticlesAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View articleView = LayoutInflater.from(parent.getContext()).inflate(R.layout.articles_list_item, parent, false);
        return new ArticleViewHolder(articleView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {
        ArticleModel model = getItem(position);

        ArticleViewHolder holder = (ArticleViewHolder)holder1;
        holder.title.setText(model.getTitle());
        holder.url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        holder.by.setText("by " +model.getBy());
        holder.score.setText(Integer.toString(model.getScore()));

        holder.time.setText(model.getTime());

        if(model.getDescendants() != 0) {
            holder.total_comments.setText(Integer.toString(model.getDescendants()));
        } else {
            holder.total_comments.setVisibility(View.GONE);
            holder.icon_comments.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (getRealmBaseAdapter() != null) {
            return getRealmBaseAdapter().getCount();
        }
        return 0;
    }

    public class ArticleViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView url;
        public TextView by;
        public TextView total_comments;
        public TextView score;
        public TextView time;
        public ImageButton icon_comments;

        public ArticleViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.tv_title);
            score = view.findViewById(R.id.tv_score);
            url = view.findViewById(R.id.tv_url);
            by = view.findViewById(R.id.tv_user);
            total_comments = view.findViewById(R.id.tv_total_comments);
            time = view.findViewById(R.id.tv_time);
            icon_comments = view.findViewById(R.id.ib_icon_comment);

        }
    }
}
