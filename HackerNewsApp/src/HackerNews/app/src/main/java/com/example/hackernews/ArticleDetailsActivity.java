package com.example.hackernews;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.hackernews.fragments.CommentsFragment;
import com.example.hackernews.fragments.WebviewFragment;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by aman.kush on 9/15/2017.
 */
public class ArticleDetailsActivity extends AppCompatActivity {
    private TabLayout tbl_article_details;
    private ViewPager vp_article_details;
    private TextView tv_titel, tv_by, tv_time, tv_url;

    private String commentsIds;
    private String url;
    private String title, by;
    private String time;
    private int id;
    private int totalComments;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_details);
        init();
        initUI();
        initUIActions();
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void init() {
        commentsIds = (String)getIntent().getExtras().get("commentsIds");
        url = (String)getIntent().getExtras().get("url");
        title = (String)getIntent().getExtras().get("title");
        time = (String)getIntent().getExtras().get("time");
        by = (String)getIntent().getExtras().get("by");
        id = (int)getIntent().getExtras().get("id");

        try{
            JSONArray array = new JSONArray(commentsIds);
            totalComments = array.length();
        }catch (Exception e){

        }
    }

    private void initUI() {
        vp_article_details = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager();
        tbl_article_details = (TabLayout) findViewById(R.id.tabs);
        tbl_article_details.setupWithViewPager(vp_article_details);
        tv_titel = (TextView) findViewById(R.id.tv_title);
        tv_by = (TextView) findViewById(R.id.tv_user);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_url = (TextView) findViewById(R.id.tv_url);
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        CommentsFragment fragment = new CommentsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("commentsIds", commentsIds);
        bundle.putInt("id", id);
        fragment.setArguments(bundle);

        if(url != null) {
            WebviewFragment webviewFragment = new WebviewFragment();
            bundle = new Bundle();
            bundle.putString("url", url);
            webviewFragment.setArguments(bundle);
            adapter.addFragment(webviewFragment, "Full Article");
        }

        adapter.addFragment(fragment, "" + totalComments + " Comments");

        vp_article_details.setAdapter(adapter);
    }

    private void initUIActions() {
        tv_titel.setText(title);
        tv_by.setText("By - " + by);
        tv_time.setText(time);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> titles = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
}
