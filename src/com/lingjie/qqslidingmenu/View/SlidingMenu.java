package com.lingjie.qqslidingmenu.View;

import android.animation.FloatEvaluator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.lingjie.qqslidingmenu.ColorUtil;
import com.nineoldandroids.animation.IntEvaluator;
import com.nineoldandroids.view.ViewHelper;

public class SlidingMenu extends FrameLayout {

	private View menuView;
	private View mainView;
	private ViewDragHelper viewDragHelper;
	private int width;
	private FloatEvaluator floatEvaluator;
	private IntEvaluator intEvaluator;
	public SlidingMenu(Context context) {
		super(context);
		init();
	}

	public SlidingMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SlidingMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		viewDragHelper = ViewDragHelper.create(this, callback);
		floatEvaluator = new FloatEvaluator();
		intEvaluator = new IntEvaluator();
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		if (getChildCount() != 2) {
			try {
				throw new IllegalAccessException(
						"SlidingMenu only have 2 children");
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		menuView = getChildAt(0);
		mainView = getChildAt(1);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		viewDragHelper.processTouchEvent(event);
		return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return viewDragHelper.shouldInterceptTouchEvent(ev);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = getMeasuredWidth();
		dragRange = width * 0.6f;
	}

	private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {

		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			return child == menuView || child == mainView;
		}

		@Override
		public int getViewHorizontalDragRange(View child) {
			return (int) dragRange;
		}

		public int clampViewPositionHorizontal(View child, int left, int dx) {
			if (child == mainView) {
				if (left < 0)
					left = 0;// 限制mainview的右边
				if (left > dragRange)
					left = (int) dragRange;// 限制mainview的左边
			}
			return left;
		};

		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			if (changedView == menuView) {
				// 固定住menuview
				menuView.layout(0, 0, menuView.getMeasuredWidth(),
						menuView.getMeasuredHeight());
				// 让mainview移动起来
				int newLeft = mainView.getLeft() + dx;
				if (newLeft < 0)
					newLeft = 0;
				if (newLeft > dragRange)
					newLeft = (int) dragRange;
				mainView.layout(newLeft, mainView.getTop() + dy, newLeft
						+ mainView.getMeasuredWidth(), mainView.getBottom()
						+ dy);
			}
			//1.计算滑动 的百分比
			float fraction = mainView.getLeft()/dragRange;
			System.out.println(fraction + "百分比为");
			//2.执行伴随动画
			executeAnim(fraction);
			//3.更改状态,回调listener的方法
			if (fraction==0 && currentState != DragState.Close) {
				currentState = DragState.Close;
				if (listener!=null) listener.onClose();
			}else if(fraction == 1f && currentState != DragState.Open){
				currentState = DragState.Open;
				if (listener!=null) listener.onOpen();
			}
			if (listener != null) {
				listener.onDrag(fraction);
			}
		};

		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			if (mainView.getLeft() < dragRange / 2) {
				// 在左边
				viewDragHelper
						.smoothSlideViewTo(mainView, 0, mainView.getTop());
				ViewCompat.postInvalidateOnAnimation(SlidingMenu.this);
			} else {
				// 在右边
				viewDragHelper.smoothSlideViewTo(mainView, (int) dragRange,
						mainView.getTop());
				ViewCompat.postInvalidateOnAnimation(SlidingMenu.this);
			}
		};
	};

	public void computeScroll() {
		if (viewDragHelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(SlidingMenu.this);
		}
	};

	private float dragRange;
	private void executeAnim(float fraction){
		//缩小mainview
		//float scaleValue = 0.8f + (1 - fraction)*0.2f;
		ViewHelper.setScaleX(mainView,floatEvaluator.evaluate(fraction, 1f, 0.8f));
		ViewHelper.setScaleY(mainView,floatEvaluator.evaluate(fraction, 1f, 0.8f));
		
		//ViewHelper.setTranslationX(menuView, intEvaluator.evaluate(fraction, -menuView.getMeasuredWidth()/2, 0));
		
		ViewHelper.setScaleX(menuView,floatEvaluator.evaluate(fraction, 0.5f, 1f));
		ViewHelper.setScaleY(menuView,floatEvaluator.evaluate(fraction, 0.5f, 1f));
		
		ViewHelper.setAlpha(menuView,floatEvaluator.evaluate(fraction, 0.0f, 1f));
		
		getBackground().setColorFilter((Integer) ColorUtil.evaluateColor(fraction, Color.BLACK, Color.TRANSPARENT),Mode.SRC_OVER);
	}
	
	private OnDragStateChangeListener listener;
	
	public void setOnDragStateChangeListener(OnDragStateChangeListener listener){
		this.listener = listener;
	}
	enum DragState{
		Open,Close;
	}
	private DragState currentState = DragState.Close;
	public interface OnDragStateChangeListener{
		
		/**
		 * 打开的回调
		 */
		void onOpen();
		
		
		/**
		 * 关闭的回调
		 */
		void onClose();
		/**
		 * 正在拖拽的回调
		 * @param fraction 
		 */
		void onDrag(float fraction);
	}
}
