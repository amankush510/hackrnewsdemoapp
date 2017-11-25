package com.example.hackernews.recyclerview.adapters;

import android.content.Context;

import com.example.hackernews.realm.models.ArticleModel;
import com.example.hackernews.realm.models.CommentModel;

import io.realm.RealmResults;

/**
 * Created by aman.kush on 9/16/2017.
 */
public class RealmCommentAdapter extends RealmModelAdapter<CommentModel> {
    public RealmCommentAdapter(Context context, RealmResults<CommentModel> results, boolean autoupdate) {
        super(context, results, autoupdate);
    }
}
