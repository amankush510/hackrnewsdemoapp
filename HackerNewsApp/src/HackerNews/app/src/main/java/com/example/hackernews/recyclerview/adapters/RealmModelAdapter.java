package com.example.hackernews.recyclerview.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import io.realm.RealmBaseAdapter;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by aman.kush on 9/15/2017.
 */
public class RealmModelAdapter<T extends RealmObject> extends RealmBaseAdapter<T> {
    public RealmModelAdapter(Context context, RealmResults<T> results, boolean autoupdate) {
        super(context, results, autoupdate);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
