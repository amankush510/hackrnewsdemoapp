package com.example.hackernews.recyclerview.adapters;

import android.content.Context;

import com.example.hackernews.realm.models.ArticleModel;

import io.realm.RealmResults;

/**
 * Created by aman.kush on 9/15/2017.
 */
public class RealmArticleAdapter extends RealmModelAdapter<ArticleModel> {
    public RealmArticleAdapter(Context context, RealmResults<ArticleModel> results, boolean autoupdate) {
        super(context, results, autoupdate);
    }
}
