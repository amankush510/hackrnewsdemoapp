package com.example.hackernews.recyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.hackernews.R;


/**
 * Created by aman.kush on 9/9/2017.
 */
public class RecyclerViewDivider extends RecyclerView.ItemDecoration {
    private Context context;

    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };

    private Drawable divider;

    public RecyclerViewDivider(Context context) {
        this.context = context;
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        divider = a.getDrawable(0);
        a.recycle();
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawVertical(c, parent);
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int l = parent.getPaddingLeft();
        final int r = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int t = child.getBottom() + params.bottomMargin;
            final int b = t + divider.getIntrinsicHeight();
            divider.setBounds(l, t, r, b);
            divider.setColorFilter(context.getResources().getColor(R.color.dividerColor), PorterDuff.Mode.LIGHTEN);
            divider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, 0, divider.getIntrinsicHeight());
    }
}
