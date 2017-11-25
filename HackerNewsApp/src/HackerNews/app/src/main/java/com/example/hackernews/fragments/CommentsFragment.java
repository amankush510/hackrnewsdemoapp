package com.example.hackernews.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.hackernews.R;
import com.example.hackernews.realm.controller.RealmController;
import com.example.hackernews.realm.dao.ArticleDAO;
import com.example.hackernews.realm.dao.CommentDAO;
import com.example.hackernews.realm.models.CommentModel;
import com.example.hackernews.recyclerview.RecyclerViewDivider;
import com.example.hackernews.recyclerview.adapters.CommentsAdapter;
import com.example.hackernews.recyclerview.adapters.RealmCommentAdapter;
import com.example.hackernews.utils.Constants;
import com.example.hackernews.utils.ResponseParser;
import com.example.hackernews.utils.SharedPreferencesManager;
import com.example.hackernews.volley.HNJSONRequest;
import com.example.hackernews.volley.HNVolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class CommentsFragment extends Fragment {
    private RecyclerView rv_comments;
    private SwipeRefreshLayout swp_refresh;

    private RealmResults<CommentModel> commentsList;
    private ArrayList<CommentModel> newItemsList;
    private ArrayList<Integer> commentIdsList;
    private CommentsAdapter adapter;

    private int start;
    private int fetched;
    private Integer visibleItemCount, pastVisibleItems, totalItemCount;
    private Integer fetchedAtATime = 10;
    private String commentsIds;
    private Boolean loading = false;
    private int id;

    private HNJSONRequest request;

    private CommentDAO commentDAO;

    private ProgressDialog progressDialog;

    private Realm realm;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comments, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        initUI(view);
        initUIActions();
    }

    private void init(){
        commentDAO = new CommentDAO();
        realm = RealmController.with(this).getRealm();
        progressDialog = new ProgressDialog(context, R.style.AppCompatAlertDialogStyle);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        id = getArguments().getInt("id");
        commentsIds = getArguments().getString("commentsIds");
        commentsList = commentDAO.getCommentsByParent(id);
        commentIdsList = new ArrayList<>();
        newItemsList = new ArrayList<>();
        adapter = new CommentsAdapter(context);

        initCommentIdsList();
    }

    private void initCommentIdsList(){
        try{
            JSONArray array = new JSONArray(commentsIds);
            Log.e("CommentsFragment", array.toString());
            for(int i = 0; i < array.length(); i++){
                commentIdsList.add(array.getInt(i));
            }
        }catch (Exception e){
            Log.e("CommentsFragment", e.getMessage());
        }

    }

    private void initUI(View view){
        rv_comments = view.findViewById(R.id.rv_comments);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(context);
        rv_comments.setLayoutManager(manager);
        rv_comments.setItemAnimator(new DefaultItemAnimator());
        rv_comments.setAdapter(adapter);
        rv_comments.addItemDecoration(new RecyclerViewDivider(context));
        rv_comments.setAdapter(adapter);
        swp_refresh = view.findViewById(R.id.swp_refresh_comments);
        setUpRealmAdapter();
    }

    private void initUIActions(){
        rv_comments.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(!loading){
                    if(dy > 0){
                        visibleItemCount = recyclerView.getLayoutManager().getChildCount();
                        totalItemCount = recyclerView.getLayoutManager().getItemCount();
                        pastVisibleItems = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                        if((visibleItemCount + pastVisibleItems) > totalItemCount - 1 && commentIdsList.size() != 0 && commentIdsList.size() != SharedPreferencesManager.getInt(context, "comments_start_index_" + id)){
                            progressDialog.show();
                            fetchComments();
                            loading = true;
                        }
                    }
                }
            }
        });

        swp_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swp_refresh.setRefreshing(false);
                clearUpdates();
                fetchComments();
            }
        });
    }

    private void setUpRealmAdapter(){
        RealmCommentAdapter realmAdapter = new RealmCommentAdapter(getContext(), commentsList , true);
        adapter.setRealmBaseAdapter(realmAdapter);
        adapter.notifyDataSetChanged();
        if(commentsList.size() == 0){
            fetchComments();
        } else {
            progressDialog.cancel();
        }
    }

    private void getComment(int id) {
        String url = Constants.BASE_URL + id + ".json?print=pretty";
        request = new HNJSONRequest(context, Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                newItemsList.add(ResponseParser.getComment(response));
                fetched++;
                if(fetched == fetchedAtATime || fetched == commentIdsList.size()){
                    pushArticlesDataToDB();
                    progressDialog.cancel();
                    loading = false;
                    swp_refresh.setRefreshing(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
            }
        });
        HNVolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    private void fetchComments(){
        if(commentIdsList.size() != 0) {
            newItemsList.clear();
            fetched = 0;
            int i;
            start = SharedPreferencesManager.getInt(context, "comments_start_index_" + 0);
            for (i = start; i < start + 10 && i < commentIdsList.size(); i++) {
                getComment(commentIdsList.get(i));
            }
            start = i;
            putStartInSharedPrefs();
        } else {
            progressDialog.cancel();
        }
    }

    private void putStartInSharedPrefs(){
        SharedPreferencesManager.put(context, "comments_start_index_" + id, start);
    }

    private void pushArticlesDataToDB(){
        commentDAO.insertArticles(newItemsList);

        commentsList = commentDAO.getCommentsByParent(id);
        adapter.notifyDataSetChanged();
    }

    private void clearUpdates(){
        SharedPreferencesManager.put(context, "comments_start_index_" + id, 0);
        commentDAO.clearAll();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
