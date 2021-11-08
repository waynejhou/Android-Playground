package org.waynezhou.libView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.waynezhou.libUtil.LogHelper;

import java.util.ArrayList;
import java.util.List;

public class StarGroupView extends FrameLayout {
    private static final int PADDING = 80;
    private static final float START_ANGLE = 270f;
    private static final double ROTATE_X = Math.PI * 7 / 18;
    private static final float SCALE_PX_ANGLE = 0.2f;
    private static final float AUTO_SWEEP_ANGLE = 0.1f;
    private static final int ANIM_DURATION = 1000;
    // region contructor
    private final int padding;

    public StarGroupView(@NonNull Context context) {
        super(context);
        padding = (int) (context.getResources().getDisplayMetrics().density * PADDING);
    }

    public StarGroupView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        padding = (int) (context.getResources().getDisplayMetrics().density * PADDING);
    }

    public StarGroupView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        padding = (int) (context.getResources().getDisplayMetrics().density * PADDING);
    }

    public StarGroupView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        padding = (int) (context.getResources().getDisplayMetrics().density * PADDING);
    }
    // endregion

    private float radius;
    private int childCount;
    private float averageAngle;
    private int closeIndex;
    private final float startSweepAngle = 0;
    private final ValueAnimator velocityAnimator = new ValueAnimator();
    private final ValueAnimator selectAnimator = new ValueAnimator();
    private final Interpolator interpolator = new DecelerateInterpolator();

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);
        radius = (getMeasuredWidth() / 2f - padding);
        childCount = getChildCount();
        averageAngle = 360f / childCount;
        layoutChildren();
        //initAnim();
        velocityAnimator.setDuration(ANIM_DURATION);
        velocityAnimator.setInterpolator(interpolator);
        velocityAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            sweepAngle += (value * SCALE_PX_ANGLE);
            layoutChildren();
        });
        selectAnimator.setDuration(ANIM_DURATION);
        selectAnimator.setInterpolator(interpolator);
        selectAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            sweepAngle = value;
            layoutChildren();
        });
    }

    private void layoutChildren() {
        if (childCount == 0) {
            return;
        }
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            double rotation = -averageAngle * i + sweepAngle;
            double angle = (START_ANGLE + rotation) * Math.PI / 180;
            if (0 < rotation && rotation < 10) {
                closeIndex = i;
            }
            double sin = Math.sin(angle);
            double cos = Math.cos(angle);
            double coordinateX = getMeasuredWidth() / 2f - radius * cos;
            double coordinateY = (radius / 2f - radius  * sin * Math.cos(ROTATE_X) * 0.5) * 0.45;
            int l = (int) (coordinateX - childWidth / 2);
            int t = (int) (coordinateY - childHeight / 2);
            int r = (int) (coordinateX + childWidth / 2);
            int b = (int) (coordinateY + childHeight / 2);
            child.layout(l,t,r,b);
            float scale = (float) ((1 - 0.3f) / 3 * (1 - Math.sin(angle)) + 0.3f) * 2f ;
            child.setScaleX(scale);
            child.setScaleY(scale);
            child.setZ(scale);
        }

    }

    private float sweepAngle = 0f;
    private boolean isCompensated;
    private final VelocityTracker velocityTracker = VelocityTracker.obtain();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        isCompensated = false;
        float x = event.getX();
        velocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDown(x);
                return true;
            case MotionEvent.ACTION_MOVE:
                swipe(x);
                break;
            case MotionEvent.ACTION_UP:
                moveUp();
                break;
        }
        return super.onTouchEvent(event);
    }

    private float downX = 0f;
    private float downAngle = sweepAngle;

    private void touchDown(float x) {
        downX = x;
        downAngle = sweepAngle;
        velocityAnimator.cancel();
        //stopRotating();
    }

    private void swipe(float x) {
        float dx = downX - x;
        sweepAngle = (dx * SCALE_PX_ANGLE + downAngle);
        layoutChildren();
    }

    private void moveUp() {
        velocityTracker.computeCurrentVelocity(16);
        scrollByVelocity(velocityTracker.getXVelocity());
        //startRotating();
    }

    private void scrollByVelocity(float velocity) {
        float end = 0f;
        if (velocity < 0) {
            end -= AUTO_SWEEP_ANGLE;
        }
        velocityAnimator.setFloatValues(-velocity, end);
        velocityAnimator.start();
    }

    private int selectedIdx = 0;
    public int getSelectedIdx(){
        return selectedIdx;
    }
    public void select(int idx){
        //LogHelper.d(idx);
        int newIdx = idx % getChildCount();
        int diff = (newIdx - selectedIdx);
        LogHelper.d(diff);
        if(Math.abs(diff)>getChildCount()/2){
            if(diff < 0){
                diff = getChildCount() + diff;
            }
            else if(diff > 0){
                diff = diff - getChildCount();
            }
        }
        LogHelper.d(diff);
        float value = averageAngle * diff;
        selectAnimator.setFloatValues(sweepAngle,  sweepAngle+value);
        selectAnimator.start();
        selectedIdx = newIdx;
    }
    /*
    private static final float START_ANGLE = 270f;
    private static final float AUTO_SWEEP_ANGLE = 0.1f;
    private static final float SCALE_PX_ANGLE = 0.2f;
    private static final int PADDING = 80;
    private static final int ROTATE_DELAY = 16;
    private static final int ANIM_DURATION = 10000;
    private static final double ROTATE_X = Math.PI * 7 / 18;

    private final String TAG = StarGroupView.class.getName();

    private VelocityTracker velocityTracker = VelocityTracker.obtain();
    private ValueAnimator velocityAnimator = new ValueAnimator();
    private ValueAnimator scrollAnimator = new ValueAnimator();

    private float sweepAngle = 0f;
    private float downAngle = sweepAngle;
    private float downX = 0f;

    private float radius;
    private float averageAngle;
    private int childCount;
    private int padding;
    private int closeIndex;

    private boolean isCompensated;

    public StarGroupView(@NonNull Context context) {
        super(context);
    }

    public StarGroupView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StarGroupView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        padding = (int) (context.getResources().getDisplayMetrics().density * PADDING);
    }

    public void startRotating() {
        postDelayed(autoScrollRunnable, 100);
    }

    public void stopRotating() {
        removeCallbacks(autoScrollRunnable);
    }

    public void select() {
        if (childCount == 0) {
            return;
        }
        if (!isCompensated) {
            compensate();
        }
        scrollAnimator.setFloatValues(100f);
        scrollAnimator.start();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);
        radius = (getMeasuredWidth() / 2f - padding);
        childCount = getChildCount();
        averageAngle = 360f / childCount;
        layoutChildren();
        initAnim();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        isCompensated = false;
        float x = event.getX();
        velocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDown(x);
                return true;
            case MotionEvent.ACTION_MOVE:
                swipe(x);
                break;
            case MotionEvent.ACTION_UP:
                moveUp();
                break;
        }
        return super.onTouchEvent(event);
    }

    private void layoutChildren() {
        if (childCount == 0) {
            return;
        }
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            double rotation = - averageAngle * i + sweepAngle;
            double angle = (START_ANGLE + rotation) * Math.PI / 180;
            if (0 < rotation && rotation < 10) {
                closeIndex = i;
            }
            double sin = Math.sin(angle);
            double cos = Math.cos(angle);
            double coordinateX = getMeasuredWidth() / 2f - radius * cos;
            double coordinateY = radius / 2f - radius * sin * Math.cos(ROTATE_X);
            child.layout((int)(coordinateX - childWidth / 2),
                    (int)(coordinateY - childHeight / 2),
                    (int)(coordinateX + childWidth / 2),
                    (int)(coordinateY + childHeight / 2));
            float scale = (float) ((1 - 0.3f) / 2 * (1 - Math.sin(angle)) + 0.3f);
            child.setScaleX(scale);
            child.setScaleY(scale);
        }
    }

    private void initAnim() {
        velocityAnimator.setDuration(ANIM_DURATION);
        velocityAnimator.setInterpolator(new DecelerateInterpolator());
        velocityAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                sweepAngle += (value * SCALE_PX_ANGLE);
                layoutChildren();
            }
        });
        scrollAnimator.setDuration(1000);
        scrollAnimator.setInterpolator(new DecelerateInterpolator());
        scrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                long currentTime = animation.getCurrentPlayTime();
                sweepAngle += (currentTime/1000f*(float)animation.getAnimatedValue());
                layoutChildren();
            }
        });
    }

    private void swipe(float x) {
        float dx = downX - x;
        sweepAngle = (dx * SCALE_PX_ANGLE + downAngle);
        layoutChildren();
    }

    private void select(float dx) {
        sweepAngle = (dx * SCALE_PX_ANGLE + downAngle);
        downAngle = sweepAngle;
        layoutChildren();
    }

    private void touchDown(float x) {
        downX = x;
        downAngle = sweepAngle;
        velocityAnimator.cancel();
        stopRotating();
    }

    private void moveUp() {
        velocityTracker.computeCurrentVelocity(16);
        scrollByVelocity(velocityTracker.getXVelocity());
        //startRotating();
    }

    private void scrollByVelocity(float velocity) {
        float end = 0f;
        if (velocity < 0) {
            end -= AUTO_SWEEP_ANGLE;
        }
        velocityAnimator.setFloatValues(-velocity, end);
        velocityAnimator.start();
    }

    private void compensate() {
        stopRotating();
        float closeAngle = START_ANGLE - closeIndex * averageAngle + sweepAngle;
        float fixAngle = closeAngle - START_ANGLE;
        sweepAngle -= fixAngle;
        downAngle = sweepAngle;
        isCompensated = true;
        layoutChildren();
    }

    private Runnable autoScrollRunnable = new Runnable() {
        @Override
        public void run() {
            sweepAngle += AUTO_SWEEP_ANGLE;
            sweepAngle %= 360;
            layoutChildren();
            postDelayed(this, ROTATE_DELAY);
        }
    };
    */
}