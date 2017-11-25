package com.example.hackernews.realm.dao;

import com.example.hackernews.realm.controller.RealmController;
import com.example.hackernews.realm.models.ArticleModel;
import com.example.hackernews.realm.models.CommentModel;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by aman.kush on 9/15/2017.
 */
public class CommentDAO {
    private Realm realm = RealmController.getInstance().getRealm();

    public void clearAll() {
        realm.beginTransaction();
        realm.clear(CommentModel.class);
        realm.commitTransaction();
    }

    public RealmResults<CommentModel> getCommentsByParent(int id) {
        return realm.where(CommentModel.class).equalTo("parent" , id).findAll();
    }



    public boolean containsArticles() {
        return !realm.allObjects(CommentModel.class).isEmpty();
    }

    public void insertArticles(ArrayList<CommentModel> list){
        realm.beginTransaction();
        for(int i = 0; i < list.size(); i++){
            realm.copyToRealm(list.get(i));
        }
        realm.commitTransaction();
    }
}
