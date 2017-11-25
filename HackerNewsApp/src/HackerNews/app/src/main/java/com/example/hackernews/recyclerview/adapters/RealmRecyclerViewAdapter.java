package com.example.hackernews.recyclerview.adapters;

import android.support.v7.widget.RecyclerView;

import io.realm.RealmBaseAdapter;
import io.realm.RealmObject;

/**
 * Created by aman.kush on 9/15/2017.
 */
public abstract class RealmRecyclerViewAdapter<T extends RealmObject> extends RecyclerView.Adapter {
    private RealmBaseAdapter<T> realmBaseAdapter;

    public T getItem(int position){
        return realmBaseAdapter.getItem(position);
    }

    public RealmBaseAdapter<T> getRealmBaseAdapter(){
        return realmBaseAdapter;
    }

    public void setRealmBaseAdapter(RealmBaseAdapter<T> realmAdapter){
        realmBaseAdapter = realmAdapter;
    }
}
