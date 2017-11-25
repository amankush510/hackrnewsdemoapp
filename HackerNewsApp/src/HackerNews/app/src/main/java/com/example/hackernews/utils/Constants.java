package com.example.hackernews.utils;

/**
 * Created by aman.kush on 9/14/2017.
 */
public interface Constants {
    String url = "https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty";

    String BASE_URL = "https://hacker-news.firebaseio.com/v0/item/";

    interface ARTICLE {
        String TITLE ="title";
        String URL = "url";
        String BY = "by";
        String DESCENDANTS = "descendants";
        String SCORE = "score";
        String TIME = "time";
        String TYPE = "type";
        String ID = "id";
        String KIDS = "kids";
    }

    interface COMMENT {
        String ID = "id";
        String TIME = "time";
        String TEXT = "text";
        String BY = "by";
        String PARENT = "parent";
    }
}
