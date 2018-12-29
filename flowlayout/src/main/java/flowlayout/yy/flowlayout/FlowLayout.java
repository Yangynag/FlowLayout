package flowlayout.yy.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FlowLayout extends ViewGroup {
    //所有的标签view
    private List<List<View>> mAllViews = new ArrayList<List<View>>();
    //每一行的标签的高度集合
    private List<Integer> mLineHeights = new ArrayList<Integer>();

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //控件的宽高
        int width = 0;
        int height = 0;
        // 获得它的父容器为它设置的测量模式和大小
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        //当前行宽高
        int lineWidth = 0;
        int lineHeight = 0;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            //真正的子view的宽度= ziview测量的宽度加上左右间距
            int realChildWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            //真正的子view的高度= ziview测量的宽度加上左右间距
            int realChildHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            //如果当前行宽加上字view宽度超过最大
            if (lineWidth + realChildWidth > sizeWidth) {
                width = Math.max(lineWidth, realChildWidth);
                lineWidth = realChildWidth;
                height += lineHeight;//控件高度叠加
                lineHeight = realChildHeight;// 记录下一行的高度
            } else {
                lineWidth += realChildWidth; //否则就累加宽度，行高
                lineHeight = Math.max(lineHeight, realChildHeight);//行高取最大高度
            }
            if (i == childCount - 1) {
                width = Math.max(width, lineWidth);
                height += lineHeight;
            }
        }
        setMeasuredDimension(modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width, modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mAllViews.clear();
        mLineHeights.clear();
        int lineWidth = 0;
        int lineHeight = 0;
        int width = getWidth();
        int childCount = getChildCount();
        List<View> lineViews = new ArrayList<View>();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            MarginLayoutParams lp = (MarginLayoutParams) child
                    .getLayoutParams();
            //需要换行
            if (lineWidth + childWidth > width) {
                mAllViews.add(lineViews);
                mLineHeights.add(lineHeight);
                lineWidth = 0;
                lineViews = new ArrayList<>();
            }
            //不需要换行
            lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
            lineHeight = Math.max(lineHeight, childHeight + lp.topMargin + lp.bottomMargin);
            lineViews.add(child);
        }
        mAllViews.add(lineViews);
        mLineHeights.add(lineHeight);
        int left = 0;
        int top = 0;
        int allLineSize = mAllViews.size();
        for (int i = 0; i < allLineSize; i++) {
            lineViews = mAllViews.get(i);
            lineHeight = mLineHeights.get(i);
            int lineViewSize = lineViews.size();
            for (int j = 0; j < lineViewSize; j++) {
                View view = lineViews.get(j);
                MarginLayoutParams marginLayoutParams = (MarginLayoutParams) view.getLayoutParams();
                int realLeft = left + marginLayoutParams.leftMargin;
                int realTop = top + marginLayoutParams.rightMargin;
                int realRight = realLeft + view.getMeasuredWidth();
                int realBottom = realTop + view.getMeasuredHeight();
                view.layout(realLeft, realTop, realRight, realBottom);
                left += view.getMeasuredWidth() + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin;
            }
            left = 0;
            top += lineHeight;
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}
