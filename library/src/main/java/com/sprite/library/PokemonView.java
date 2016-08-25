package com.sprite.library;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;

/**
 * Created by corvo on 16-8-25.
 *
 * @author corvo
 */

public class PokemonView extends View {
    private static final float C = 0.551915024494f;
    private static final int DEFAULT_RADIUS = 100;
    private static final long DEFAULT_DURATION = 800;

    private Paint mTopCirclePaint;
    private Paint mBottomCirclePaint;
    private Paint mCenterPaint;
    private Paint mHeartPaint;
    private int mRadius = 0;
    private float separateDistance;
    private ValueAnimator separateAnimator;
    private ValueAnimator sweepAnimator;
    private ValueAnimator combineAnimator;
    private long duration = DEFAULT_DURATION;
    private float sweepDegree;
    private float scaleHeart = 0.8f;
    private boolean isEnding;

    public PokemonView(Context context) {
        this(context, null);
    }

    public PokemonView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PokemonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.PokemonView);

        mRadius = (int) typedArray.getDimension(R.styleable.PokemonView_radius,DEFAULT_RADIUS);

        typedArray.recycle();

        init();

    }

    private void init() {

        setBackgroundColor(Color.LTGRAY);
        // top circle
        mTopCirclePaint = new Paint();
        mTopCirclePaint.setAntiAlias(true);
        mTopCirclePaint.setColor(Color.rgb(203,27,69));
        mTopCirclePaint.setStyle(Paint.Style.FILL);

        //bottom circle
        mBottomCirclePaint = new Paint();
        mBottomCirclePaint.setAntiAlias(true);
        mBottomCirclePaint.setColor(Color.rgb(252,250,242));
        mBottomCirclePaint.setStyle(Paint.Style.FILL);

        // center
        mCenterPaint = new Paint();
        mCenterPaint.setAntiAlias(true);
        mCenterPaint.setColor(Color.rgb(21,21,21));
        mCenterPaint.setStyle(Paint.Style.FILL);

        // heart paint
        mHeartPaint = new Paint();
        mHeartPaint.setAntiAlias(true);
        mHeartPaint.setColor(Color.rgb(203,27,69));
        mHeartPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(widthSize, heightSize);
        } else {
            width = mRadius * 4 +getPaddingLeft() + getPaddingRight();
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(widthSize, heightSize);
        } else {
            height = mRadius * 4+ getPaddingTop() + getPaddingBottom();
        }

        setMeasuredDimension(width, height);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRadius = DEFAULT_RADIUS;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startAnimator();
                break;
        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        drawCenterHeart(canvas);
        drawHeart(canvas);
        drawTopCircle(canvas);
        drawBottomCircle(canvas);
        drawCenterRectangle(canvas);
        drawCenterBlackCircle(canvas);
        drawCenterWhiteCircle(canvas);
    }

    public void startAnimator() {
        startSeparateAnimator();
        separateAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                startSweepDegreeAnimator();
                startHeartAnimator();
                sweepAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        startCombineAnimator();
                        combineAnimator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                startReverseSweepAnimator();
                            }
                        });
                    }
                });
            }
        });

//        if (sweepAnimator!=null) {
//            sweepAnimator.addListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    super.onAnimationEnd(animation);
//                    startCombineAnimator();
//                }
//            });
//        }
//
//        combineAnimator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
//                startReverseSweepAnimator();
//            }
//        });
    }

    private void drawTopCircle(Canvas canvas) {
        float left = 0 - mRadius - getPaddingLeft();
        float top = 0 - mRadius - getPaddingTop() - separateDistance;
        float right = mRadius + getPaddingRight();
        float bottom = mRadius + getPaddingBottom() - separateDistance;
//        canvas.translate(getWidth()/2,getHeight()/2);
        canvas.rotate(sweepDegree,0,0);
        canvas.drawArc(left, top, right, bottom, 180, 180, false, mTopCirclePaint);
    }

    private void drawBottomCircle(Canvas canvas) {
        float left = 0 - mRadius - getPaddingLeft();
        float top = 0 - mRadius - getPaddingTop() + separateDistance;
        float right = mRadius + getPaddingRight();
        float bottom = mRadius + getPaddingBottom() + separateDistance;
        canvas.drawArc(left, top, right, bottom, 0, 180, false, mBottomCirclePaint);
    }

    private void drawCenterRectangle(Canvas canvas) {
        float left = 0 - mRadius - getPaddingLeft();
        float top = 0 - mRadius / 15 - getPaddingTop() - separateDistance;
        float right = mRadius + getPaddingRight();
        float bottom = mRadius / 15 + getPaddingBottom() - separateDistance;
        canvas.drawRect(left, top, right, bottom, mCenterPaint);
    }

    private void drawCenterBlackCircle(Canvas canvas) {
        canvas.drawCircle(0, -separateDistance, mRadius / 3.5f, mCenterPaint);
    }

    private void drawCenterWhiteCircle(Canvas canvas) {
        canvas.drawCircle(0, -separateDistance, mRadius / 5.5f, mBottomCirclePaint);
    }

    private void drawHeart(Canvas canvas) {
        canvas.translate(getWidth()/2,getHeight()/2);
        Bitmap heart = BitmapFactory.decodeResource(getResources(),R.drawable.ic_favorite_red_300_48dp);
//        canvas.drawBitmap(heart,-mRadius/1.35f,-mRadius/2,null);
        Matrix matrix = new Matrix();
        matrix.postScale(scaleHeart,scaleHeart,-40,-50);
        Bitmap bitmapScale = Bitmap.createBitmap(heart,0,0,heart.getWidth(),heart.getHeight(),matrix,true);
        canvas.drawBitmap(bitmapScale,-mRadius/2f,-mRadius/2.0f,null);

    }

    private void startHeartAnimator(){
        ValueAnimator animator = ValueAnimator.ofFloat(0.6f,0.8f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                scaleHeart = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });

        animator.setDuration(1000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.start();
    }


    private void drawCenterHeart(Canvas canvas) {
        canvas.translate(getWidth()/2,getHeight()/2);
        Path heartPath = new Path();
        heartPath.moveTo(0,-50);
        heartPath.cubicTo(mRadius*3/2,0,mRadius/2,-mRadius*2/5,mRadius/2,-mRadius*3/2);
        canvas.drawPath(heartPath,mHeartPaint);

        Path heartPathR = new Path();
        heartPathR.moveTo(0,-50);
        heartPathR.cubicTo(-mRadius*3/2,0,-mRadius/2,-mRadius*2/5, mRadius/2,-mRadius*3/2);
        canvas.drawPath(heartPathR,mHeartPaint);
    }

    private void startSeparateAnimator() {
        separateAnimator = ValueAnimator.ofFloat(0, 50);
        separateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                separateDistance = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
//        animator.setRepeatCount(ValueAnimator.INFINITE);
//        animator.setRepeatMode(ValueAnimator.RESTART);
        separateAnimator.setDuration(duration);
        separateAnimator.setInterpolator(new BounceInterpolator());
        separateAnimator.start();
    }

    private void startSweepDegreeAnimator() {
        sweepAnimator = ValueAnimator.ofFloat(0, 180);
        sweepAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                sweepDegree = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        sweepAnimator.setDuration(duration);
        sweepAnimator.setInterpolator(new BounceInterpolator());
        sweepAnimator.start();
    }

    private void startCombineAnimator() {
        combineAnimator = ValueAnimator.ofFloat(separateDistance,0);
        combineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                separateDistance = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        combineAnimator.setDuration(duration);
        combineAnimator.setInterpolator(new AccelerateInterpolator());
        combineAnimator.start();
    }

    private void startReverseSweepAnimator() {
        ValueAnimator animator = ValueAnimator.ofFloat(180,360);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                sweepDegree = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        animator.setDuration(duration);
        animator.setInterpolator(new BounceInterpolator());
        animator.start();
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

}
