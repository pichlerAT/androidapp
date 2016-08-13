package com.frysoft.notifry.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.frysoft.notifry.adapter.TaskAdapter;
import com.frysoft.notifry.data.Tasklist;

import java.util.ArrayList;

/**
 * Created by Edwin Pichler on 11.06.2016.
 */
public class DragableListView extends ListView {

    private final int SMOOTH_SCROLL_AMOUNT_AT_EDGE = 15;
    private final int MOVE_DURATION = 150;
    private final int LINE_THICKNESS = 5;

    private int mLastEventY = -1;

    private int mDownY = -1;
    private int mDownX = -1;

    private int mTotalOffset = 0;

    private boolean mCellIsMobile = false;
    private boolean mIsMobileScrolling = false;
    private int mSmoothScrollAmountAtEdge = 0;

    private final int INVALID_ID = -1;
    private long mAboveItemId = INVALID_ID;
    private long mMobileItemId = INVALID_ID;
    private long mBelowItemId = INVALID_ID;

    private BitmapDrawable mHoverCell;
    private Rect mHoverCellCurrentBounds;
    private Rect mHoverCellOriginalBounds;

    private final int INVALID_POINTER_ID = -1;
    private int mActivePointerId = INVALID_POINTER_ID;

    private boolean mIsWaitingForScrollFinish = false;
    private int mScrollState = OnScrollListener.SCROLL_STATE_IDLE;

    private Tasklist mTask;
    private int mPositionFrom;
    private int mPositionTo;

    public DragableListView(Context context) {
        super(context);
        init(context);
    }

    public DragableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public DragableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context){
        setOnItemLongClickListener(mOnItemLongClickListener);
        setOnScrollListener(mOnScrollListener);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        mSmoothScrollAmountAtEdge = (int)(SMOOTH_SCROLL_AMOUNT_AT_EDGE / metrics.density);
    }

    private AdapterView.OnItemLongClickListener mOnItemLongClickListener = new OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            mTotalOffset = 0;

            int position = pointToPosition(mDownX, mDownY);
            int index = position - getFirstVisiblePosition();

            View selectedView = getChildAt(index);
            mMobileItemId = getAdapter().getItemId(position);
            mHoverCell = getAndAddHoverView(selectedView);
            selectedView.setVisibility(INVISIBLE);
            mCellIsMobile = true;
            updateNeighborViewsForID(mMobileItemId);

            return false;
        }
    };

    /**
     * Creates a bitmap of the selected listview item and puts it infront of the listview
     * @param view
     * @return
     */
    private BitmapDrawable getAndAddHoverView(View view) {
        //Get item's coordinates
        int width = view.getWidth();
        int height = view.getHeight();
        int top = view.getTop();
        int left = view.getLeft();

        //Draws the bitmap of the selected item
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas (bitmap);
        view.draw(canvas);

        //Draws a black border for the bitmap
        Canvas can = new Canvas(bitmap);
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(LINE_THICKNESS);
        paint.setColor(Color.BLACK);
        can.drawBitmap(bitmap, 0, 0, null);
        can.drawRect(rect, paint);

        BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
        mHoverCellOriginalBounds = new Rect(left, top, left + width, top + height);
        mHoverCellCurrentBounds = new Rect(mHoverCellOriginalBounds);
        drawable.setBounds(mHoverCellCurrentBounds);

        return drawable;
    }

    /**
     * Stores information of the current above and below view of the selected listview item (mHoverCell)
     * @param itemID
     */
    private void updateNeighborViewsForID(long itemID) {
        int position = getPositionForID(itemID);
        TaskAdapter adapter = ((TaskAdapter) getAdapter());
        mAboveItemId = adapter.getItemId(position - 1);
        mBelowItemId = adapter.getItemId(position + 1);
    }

    public int getPositionForID (long itemID) {
        View v = getViewForID(itemID);
        if (v == null) {
            return -1;
        } else {
            return getPositionForView(v);
        }
    }

    public View getViewForID (long itemID) {
        int firstVisiblePosition = getFirstVisiblePosition();
        TaskAdapter adapter = ((TaskAdapter)getAdapter());
        for(int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            int position = firstVisiblePosition + i;
            long id = adapter.getItemId(position);
            if (id == itemID) {
                return v;
            }
        }
        return null;
    }

    /**
     * This method is necessary to draw the Bitmap of the selected item (mHoverCell) above all other listview items.
     * @param canvas
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mHoverCell != null){
            mHoverCell.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int pointerIndex;
        switch(ev.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                mDownX = (int) ev.getX();
                mDownY = (int) ev.getY();
                mActivePointerId = ev.getPointerId(0);
                break;

            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER_ID) {
                    break;
                }
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                mLastEventY = (int) ev.getY(pointerIndex);
                int deltaY = mLastEventY - mDownY;

                if (mCellIsMobile) {
                    mHoverCellCurrentBounds.offsetTo(mHoverCellOriginalBounds.left, mHoverCellOriginalBounds.top + deltaY + mTotalOffset);
                    mHoverCell.setBounds(mHoverCellCurrentBounds);

                    invalidate();
                    handleCellSwitch();
                    mIsMobileScrolling = false;
                    handleMobileCellScroll();

                    return false;

                }
                break;

            case MotionEvent.ACTION_UP:
                touchEventsEnded();
                break;

            case MotionEvent.ACTION_CANCEL:
                touchEventsCancelled();
                break;

            case MotionEvent.ACTION_POINTER_UP:
                pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerID = ev.getPointerId(pointerIndex);
                if (pointerID == mActivePointerId){
                    touchEventsEnded();
                }
                break;

            default:
                break;

        }

        return super.onTouchEvent(ev);
    }

    /**
     * This method actually handles the swap of the mHoverCell
     */
    private void handleCellSwitch() {
        final int deltaY = mLastEventY - mDownY;
        final int deltaYTotal = mHoverCellOriginalBounds.top + mTotalOffset + deltaY;

        View aboveView = getViewForID(mAboveItemId);
        View belowView = getViewForID(mBelowItemId);
        View mobileView = getViewForID(mMobileItemId);

        boolean isAbove = (aboveView != null) && (deltaYTotal < aboveView.getTop());
        boolean isBelow = (belowView != null) && (deltaYTotal > belowView.getTop());

        if (isAbove || isBelow){
            final long switchItemID = isBelow ? mBelowItemId : mAboveItemId;
            View switchView = isBelow ? belowView : aboveView;
            final int originalItem = getPositionForView(mobileView);

            if (switchView == null){
                updateNeighborViewsForID(mMobileItemId);
                return;
            }

            //swapElements(TasklistManager.getTasklists(), originalItem, getPositionForView(switchView));
            mobileView.setVisibility(View.VISIBLE);
            ((TaskAdapter) getAdapter()).notifyDataSetChanged();

            final int switchViewStartTop = switchView.getTop();
            mDownY = mLastEventY;
            updateNeighborViewsForID(mMobileItemId);

            final ViewTreeObserver observer = getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    observer.removeOnPreDrawListener(this);
                    View mobileView = getViewForID(mMobileItemId);
                    if (mobileView != null) {
                        mobileView.setVisibility(View.INVISIBLE);
                    }
                    View switchView = getViewForID(switchItemID);
                    mTotalOffset += deltaY;

                    int switchViewNewTop = switchView.getTop();
                    int delta = switchViewStartTop - switchViewNewTop;
                    switchView.setTranslationY(delta);

                    ObjectAnimator animator = ObjectAnimator.ofFloat(switchView, View.TRANSLATION_Y, 0);
                    animator.setDuration(MOVE_DURATION);
                    animator.start();

                    return true;
                }
            });
        }

    }

    private void swapElements(ArrayList<Tasklist> taskLists, int originalItem, int positionForView) {
        mTask = taskLists.get(originalItem);
        taskLists.set(originalItem, taskLists.get(positionForView));
        taskLists.set(positionForView, mTask);
        mPositionFrom = originalItem;
        mPositionTo = positionForView;

    }

    private void touchEventsEnded() {
        final View mobileView = getViewForID(mMobileItemId);
        if (mCellIsMobile || mIsWaitingForScrollFinish){
            mCellIsMobile = false;
            mIsWaitingForScrollFinish = false;
            mIsMobileScrolling = false;
            mActivePointerId = INVALID_POINTER_ID;

            if (mScrollState != OnScrollListener.SCROLL_STATE_IDLE){
                mIsWaitingForScrollFinish = true;
                return;
            }

            mHoverCellCurrentBounds.offsetTo(mHoverCellOriginalBounds.left, mobileView.getTop());

            ObjectAnimator hoverViewAnimator = ObjectAnimator.ofObject(mHoverCell, "bounds", sBoundEvaluator, mHoverCellCurrentBounds);
            hoverViewAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    invalidate();
                }
            });
            hoverViewAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    setEnabled(false);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mAboveItemId = INVALID_ID;
                    mMobileItemId = INVALID_ID;
                    mBelowItemId = INVALID_ID;
                    mobileView.setVisibility(VISIBLE);
                    mHoverCell = null;
                    setEnabled(true);
                    invalidate();
                }
            });
            hoverViewAnimator.start();
            /*if (mPositionFrom != mPositionTo) {
                mTask.move(mPositionFrom, mPositionTo);
            }*/
        }
        else{
            touchEventsCancelled();
        }
    }

    /**
     * Resets everything to default.
     */
    private void touchEventsCancelled() {
        View mobileView = getViewForID(mMobileItemId);
        if (mCellIsMobile){
            mAboveItemId = INVALID_ID;
            mMobileItemId = INVALID_ID;
            mBelowItemId = INVALID_ID;
            mobileView.setVisibility(VISIBLE);
            mHoverCell = null;
            invalidate();
        }
        mCellIsMobile = false;
        mIsMobileScrolling = false;
        mActivePointerId = INVALID_POINTER_ID;
    }

    /**
     * This TypeEvaluator animates the Bitmap correctly to its final position, when the drag is finished.
     */
    private final static TypeEvaluator<Rect> sBoundEvaluator = new TypeEvaluator<Rect>() {
        @Override
        public Rect evaluate(float fraction, Rect startValue, Rect endValue) {
            return new Rect(interpolate(startValue.left, endValue.left, fraction),
                    interpolate(startValue.top, endValue.top, fraction),
                    interpolate(startValue.right, endValue.right, fraction),
                    interpolate(startValue.bottom, endValue.bottom, fraction));
        }

        public int interpolate(int start, int end, float fraction){
            return (int) (start + fraction * (end-start));
        }
    };

    private void handleMobileCellScroll() {
        mIsMobileScrolling = handleMobileCellScroll(mHoverCellCurrentBounds);
    }

    public boolean handleMobileCellScroll(Rect rect){
        int offset = computeVerticalScrollOffset();
        int height = getHeight();
        int extent = computeVerticalScrollExtent();
        int range = computeVerticalScrollRange();
        int hoverViewTop = rect.top;
        int hoverHeight = rect.height();

        if (hoverViewTop <= 0 && offset > 0){
            smoothScrollBy(-mSmoothScrollAmountAtEdge, 0);
            return true;
        }

        if (hoverViewTop + hoverHeight >= height && (offset + extent) < range){
            smoothScrollBy(mSmoothScrollAmountAtEdge, 0);
            return true;
        }
        return false;
    }

    /**
     * The ScrollListener handles cell swapping and scrolls the list if mHoverCell is dragged to the top or the bottom border of the screen
     */
    private AbsListView.OnScrollListener mOnScrollListener = new OnScrollListener() {

        private int mPreviousFirstVisibleItem = -1;
        private int mPreviousVisibleItemCount = -1;
        private int mCurrentFirstVisibleItem;
        private int mCurrentVisibleItemCount;
        private int mCurrentScrollState;

        @Override
        public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            mCurrentFirstVisibleItem = firstVisibleItem;
            mCurrentVisibleItemCount = visibleItemCount;

            mPreviousFirstVisibleItem = (mPreviousFirstVisibleItem == -1) ? mCurrentFirstVisibleItem : mPreviousFirstVisibleItem;
            mPreviousVisibleItemCount = (mPreviousVisibleItemCount == -1) ? mCurrentVisibleItemCount : mPreviousVisibleItemCount;

            checkAndHandleFirstVisibleCellChange();
            checkAndHandleLastVisibleCellChange();

            mPreviousFirstVisibleItem = mCurrentFirstVisibleItem;
            mPreviousVisibleItemCount = mCurrentVisibleItemCount;
        }

        /**
         * Checks if mHoverCell is dragged to the top of the list. Then the list will be updated correctly.
         */
        private void checkAndHandleFirstVisibleCellChange() {
            if (mCurrentFirstVisibleItem != mPreviousFirstVisibleItem){
                if (mCellIsMobile && mMobileItemId != INVALID_ID) {
                    updateNeighborViewsForID(mMobileItemId);
                    handleCellSwitch();
                }
            }
        }

        /**
         * Checks if mHoverCell is dragged to the bottom of the list. Then the list will be updated correctly.
         */
        private void checkAndHandleLastVisibleCellChange() {
            int currentLastVisibleItem = mCurrentFirstVisibleItem + mCurrentVisibleItemCount;
            int previousLstVisibleItem = mPreviousFirstVisibleItem + mPreviousVisibleItemCount;

            if (currentLastVisibleItem != previousLstVisibleItem ){
                if (mCellIsMobile && mMobileItemId != INVALID_ID) {
                    updateNeighborViewsForID(mMobileItemId);
                    handleCellSwitch();
                }
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView absListView, int scrollState) {
            mCurrentScrollState = scrollState;
            mScrollState = scrollState;
            isScrollCompleted();
        }

        private void isScrollCompleted() {
            if (mCurrentVisibleItemCount > 0 && mCurrentScrollState == SCROLL_STATE_IDLE){
                if (mCellIsMobile && mIsMobileScrolling){
                    handleMobileCellScroll();
                }
                else if (mIsWaitingForScrollFinish){
                    touchEventsEnded();
                }
            }
        }
    };
}
