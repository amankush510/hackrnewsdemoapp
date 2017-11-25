package com.example.hackernews.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by aman.kush on 9/9/2017.
 */
public class RecyclerViewItemTouchListener implements RecyclerView.OnItemTouchListener{
    private ClickListener clicklistener;
    private GestureDetector gestureDetector;

    public RecyclerViewItemTouchListener(Context context, ClickListener listener){
        gestureDetector=new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }
        });
        this.clicklistener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent event) {
        View child = view.findChildViewUnder(event.getX(),event.getY());
        if(child!=null && clicklistener!=null && gestureDetector.onTouchEvent(event)){
            clicklistener.clickEvent(view.getChildAdapterPosition(child));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
