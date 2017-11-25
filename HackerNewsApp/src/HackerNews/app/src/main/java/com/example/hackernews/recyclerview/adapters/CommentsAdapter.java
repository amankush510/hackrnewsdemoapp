package com.example.hackernews.recyclerview.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.hackernews.R;
import com.example.hackernews.realm.models.ArticleModel;
import com.example.hackernews.realm.models.CommentModel;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by aman.kush on 9/15/2017.
 */
public class CommentsAdapter extends RealmRecyclerViewAdapter<CommentModel> {
    private Context context;

    public CommentsAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View articleView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_list_item, parent, false);
        return new CommentsViewHolder(articleView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder recyclerHolder, int position) {
        CommentModel model = getItem(position);

        CommentsViewHolder holder = (CommentsViewHolder) recyclerHolder;
        holder.by.setText(model.getBy());

        Date date = new Date(model.getTime());
        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy hh:mm");
        holder.time.setText(df.format(date));
        holder.comment_text.setText(Html.fromHtml(model.getText()));
    }

    @Override
    public int getItemCount() {
        if (getRealmBaseAdapter() != null) {
            return getRealmBaseAdapter().getCount();
        }
        return 0;
    }

    public class CommentsViewHolder extends RecyclerView.ViewHolder {
        public TextView by;
        public TextView comment_text;
        public TextView time;

        public CommentsViewHolder(View view) {
            super(view);
            by = view.findViewById(R.id.tv_comment_user);
            comment_text = view.findViewById(R.id.tv_comment_text);
            time = view.findViewById(R.id.tv_comment_time);

        }
    }
}
