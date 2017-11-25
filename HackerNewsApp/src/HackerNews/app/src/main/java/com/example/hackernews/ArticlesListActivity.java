package com.example.hackernews;

import android.support.v7.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.hackernews.realm.controller.RealmController;
import com.example.hackernews.realm.dao.ArticleDAO;
import com.example.hackernews.realm.models.ArticleModel;
import com.example.hackernews.recyclerview.ClickListener;
import com.example.hackernews.recyclerview.RecyclerViewDivider;
import com.example.hackernews.recyclerview.RecyclerViewItemTouchListener;
import com.example.hackernews.recyclerview.adapters.ArticlesAdapter;
import com.example.hackernews.recyclerview.adapters.RealmArticleAdapter;
import com.example.hackernews.utils.Constants;
import com.example.hackernews.utils.DateFormatter;
import com.example.hackernews.utils.ResponseParser;
import com.example.hackernews.utils.SharedPreferencesManager;
import com.example.hackernews.utils.Utils;
import com.example.hackernews.volley.HNJSONRequest;
import com.example.hackernews.volley.HNVolleySingleton;

import org.json.JSONObject;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class ArticlesListActivity extends AppCompatActivity implements ClickListener {
    private HNJSONRequest request;

    private RealmResults<ArticleModel> articlesList;
    private ArrayList<Long> idList;
    private ArrayList<ArticleModel> newItemsList;
    private ArticlesAdapter adapter;

    private RecyclerView rv_articles;
    private SwipeRefreshLayout swp_refresh_articles;
    private LinearLayout ll_empty_view;

    private int start;
    private int fetched;
    private Integer visibleItemCount, pastVisibleItems, totalItemCount;
    private Integer fetchedAtATime = 10;
    private String title;

    private ProgressDialog progressDialog;
    private Boolean loading = false;

    private ArticleDAO articleDAO;

    private Realm realm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles_list);


        updateTitle();
        init();
        initUI();
        initUIActions();
    }

    private void init() {
        realm = RealmController.with(this).getRealm();
        progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        articleDAO = new ArticleDAO();
        idList = new ArrayList<>();
        newItemsList = new ArrayList<>();
        adapter = new ArticlesAdapter(this);
    }

    private void initUI() {
        rv_articles = (RecyclerView) findViewById(R.id.rv_articles);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
        rv_articles.setLayoutManager(manager);
        rv_articles.setItemAnimator(new DefaultItemAnimator());
        rv_articles.setAdapter(adapter);
        rv_articles.addItemDecoration(new RecyclerViewDivider(this));
        rv_articles.setAdapter(adapter);
        swp_refresh_articles = (SwipeRefreshLayout) findViewById(R.id.swp_refresh_articles);
        checkNetworkConnectivity();
    }

    private void initUIActions() {
        swp_refresh_articles.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utils.isDataConnectivityOn(ArticlesListActivity.this)) {
                    swp_refresh_articles.setRefreshing(false);
                    progressDialog.show();
                    clearUpdates();
                    requestArticles();
                } else {
                    swp_refresh_articles.setRefreshing(false);
                    Toast.makeText(ArticlesListActivity.this, "Please check your internet connection and try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        rv_articles.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!loading) {
                    if (dy > 0) {
                        visibleItemCount = recyclerView.getLayoutManager().getChildCount();
                        totalItemCount = recyclerView.getLayoutManager().getItemCount();
                        pastVisibleItems = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                        if ((visibleItemCount + pastVisibleItems) > totalItemCount - 1) {
                            progressDialog.show();
                            fetchArticles();
                            loading = true;
                        }
                    }
                }
            }
        });

        rv_articles.addOnItemTouchListener(new RecyclerViewItemTouchListener(this, this));
    }

    private void checkNetworkConnectivity() {
        if (Utils.isDataConnectivityOn(this) || articleDAO.containsArticles()) {
            setUpRealmAdapter();
            listArticles();
        } else {
            ll_empty_view = (LinearLayout) findViewById(R.id.ll_no_internet_view);
            ll_empty_view.setVisibility(View.VISIBLE);
            swp_refresh_articles.setVisibility(View.GONE);
            rv_articles.setVisibility(View.GONE);
        }
    }

    private void setUpRealmAdapter() {
        articlesList = articleDAO.getArticles();
        RealmArticleAdapter realmAdapter = new RealmArticleAdapter(this.getApplicationContext(), articlesList, true);
        adapter.setRealmBaseAdapter(realmAdapter);
        adapter.notifyDataSetChanged();
    }

    private void listArticles() {
        ArticleDAO articleDao = new ArticleDAO();
        if (!articleDao.containsArticles()) {
            requestArticles();
        } else {
            if (idList.size() == 0) {
                try {
                    JSONObject resposneData = new JSONObject(SharedPreferencesManager.getString(this, "id_list"));
                    idList = ResponseParser.getIdList(resposneData);
                } catch (Exception e) {
                    Log.e("ArticleListActivity", e.getMessage());
                }
            }
            progressDialog.cancel();
        }
    }

    private void requestArticles() {

        request = new HNJSONRequest(this, Request.Method.GET, Constants.url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                putArticlesIdListInSharedPrefs(response.toString());
                idList.clear();
                idList = ResponseParser.getIdList(response);
                start = 0;
                fetchArticles();
                SharedPreferencesManager.put(ArticlesListActivity.this, "last_updated", System.currentTimeMillis());
                updateTitle();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                updateUI();
                progressDialog.cancel();
            }
        });

        HNVolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void getArticle(Long id) {
        String url = Constants.BASE_URL + id + ".json?print=pretty";
        request = new HNJSONRequest(this, Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                newItemsList.add(ResponseParser.getAricle(response));
                fetched++;
                if (fetched == fetchedAtATime || fetched == idList.size()) {
                    pushArticlesDataToDB();
                    progressDialog.cancel();
                    loading = false;
                    swp_refresh_articles.setRefreshing(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                updateUI();
                progressDialog.cancel();
            }
        });
        HNVolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void fetchArticles() {
        if (Utils.isDataConnectivityOn(this)) {
            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }
            newItemsList.clear();
            fetched = 0;
            start = SharedPreferencesManager.getInt(this, "articles_start_index");
            for (int i = start; i < start + 10 && i < idList.size(); i++) {
                getArticle(idList.get(i));
            }
            start = start + 10;
            putStartInSharedPrefs();
        } else {
            progressDialog.cancel();
            Toast.makeText(this, "Please check your internet connection and try again!", Toast.LENGTH_SHORT).show();
        }
    }

    private void pushArticlesDataToDB() {
        ArticleDAO articleDAO = new ArticleDAO();
        articleDAO.insertArticles(newItemsList);

        articlesList = articleDAO.getArticles();
        adapter.notifyDataSetChanged();
    }

    private void putStartInSharedPrefs() {
        SharedPreferencesManager.put(this, "articles_start_index", start);
    }

    private void putArticlesIdListInSharedPrefs(String idList) {
        SharedPreferencesManager.put(this, "id_list", idList);
    }

    private void updateUI() {
        adapter.notifyDataSetChanged();
    }

    private void clearUpdates() {
        SharedPreferencesManager.put(this, "articles_start_index", 0);
        SharedPreferencesManager.put(this, "id_list", null);
        ArticleDAO dao = new ArticleDAO();
        dao.clearAll();
    }

    @Override
    public void clickEvent(int position) {
        String commentId = articlesList.get(position).getKids();
        String url = articlesList.get(position).getUrl();
        String title = articlesList.get(position).getTitle();
        String by = articlesList.get(position).getBy();
        String time = articlesList.get(position).getTime();
        int id = articlesList.get(position).getId();

        Intent in = new Intent(this, ArticleDetailsActivity.class);
        in.putExtra("commentsIds", commentId);
        in.putExtra("url", url);
        in.putExtra("title", title);
        in.putExtra("by", by);
        in.putExtra("time", time);
        in.putExtra("id", id);
        startActivity(in);
    }

    private void updateTitle() {
        title = "Top Stories";
        getSupportActionBar().setTitle(title);
    }
}
