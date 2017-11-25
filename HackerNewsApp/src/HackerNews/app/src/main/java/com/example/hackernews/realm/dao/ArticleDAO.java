package com.example.hackernews.realm.dao;

import com.example.hackernews.realm.controller.RealmController;
import com.example.hackernews.realm.models.ArticleModel;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by aman.kush on 9/15/2017.
 */
public class ArticleDAO {
    private Realm realm = RealmController.getInstance().getRealm();

    public void clearAll() {
        realm.beginTransaction();
        realm.clear(ArticleModel.class);
        realm.commitTransaction();
    }

    public RealmResults<ArticleModel> getArticles() {
        return realm.where(ArticleModel.class).findAll();
    }

    public boolean containsArticles() {
        return !realm.allObjects(ArticleModel.class).isEmpty();
    }

    public void insertArticles(ArrayList<ArticleModel> list){
        realm.beginTransaction();
        for(int i = 0; i < list.size(); i++){
            realm.copyToRealm(list.get(i));
        }
        realm.commitTransaction();
    }
}
