package com.example.hackernews.utils;

import android.util.Log;

import com.example.hackernews.realm.models.ArticleModel;
import com.example.hackernews.realm.models.CommentModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by aman.kush on 9/14/2017.
 */
public class ResponseParser {
    public static ArticleModel getAricle(JSONObject response){
        ArticleModel model = new ArticleModel();
        try{
            JSONObject data = response.getJSONObject("data");
                if(data.has(Constants.ARTICLE.ID)){
                    model.setId(data.getInt(Constants.ARTICLE.ID));
                }
                if(data.has(Constants.ARTICLE.BY)){
                    model.setBy(data.getString(Constants.ARTICLE.BY));
                }
                if(data.has(Constants.ARTICLE.DESCENDANTS)){
                    model.setDescendants(data.getInt(Constants.ARTICLE.DESCENDANTS));
                }
                if(data.has(Constants.ARTICLE.KIDS)){
                    model.setKids(data.getString(Constants.ARTICLE.KIDS));
                }
                if(data.has(Constants.ARTICLE.SCORE)){
                    model.setScore(data.getInt(Constants.ARTICLE.SCORE));
                }
                if(data.has(Constants.ARTICLE.TIME)){
                    model.setTime(DateFormatter.formatDate(data.getLong(Constants.ARTICLE.TIME) * 1000));
                }
                if(data.has(Constants.ARTICLE.TITLE)){
                    model.setTitle(data.getString(Constants.ARTICLE.TITLE));
                }
                if(data.has(Constants.ARTICLE.TYPE)){
                    model.setType(data.getString(Constants.ARTICLE.TYPE));
                }
                if(data.has(Constants.ARTICLE.URL)){
                    model.setUrl(data.getString(Constants.ARTICLE.URL));
                }
        } catch (Exception e){
            Log.e("ResponseParser", "Error Parsing article");
        }
        return model;
    }

    public static ArrayList<Long> getIdList(JSONObject response){
        ArrayList<Long> list = new ArrayList<>();
        try{
            JSONArray lst = response.getJSONArray("data");
            for(int i = 0; i < lst.length(); i++){
                list.add(lst.getLong(i));
            }
        } catch (JSONException e){
            Log.e("ResponseParser", "Error Parsing articles list");
        }
        return list;
    }

    public static CommentModel getComment(JSONObject response){
        CommentModel model = new CommentModel();
        try{
            JSONObject data = response.getJSONObject("data");
            if(data.has(Constants.COMMENT.ID)){
                model.setId(data.getInt(Constants.COMMENT.ID));
            }
            if(data.has(Constants.COMMENT.TIME)){
                model.setTime(data.getLong(Constants.COMMENT.TIME) * 1000);
            }
            if(data.has(Constants.COMMENT.TEXT)){
                model.setText(data.getString(Constants.COMMENT.TEXT));
            }
            if(data.has(Constants.COMMENT.BY)){
                model.setBy(data.getString(Constants.COMMENT.BY));
            }
            if(data.has(Constants.COMMENT.PARENT)){
                model.setParent(data.getInt(Constants.COMMENT.PARENT));
            }
        } catch (Exception e){
            Log.e("ResponseParser", "Error Parsing article");
        }
        return model;
    }
}
