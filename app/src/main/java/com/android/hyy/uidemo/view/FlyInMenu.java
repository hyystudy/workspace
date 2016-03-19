package com.android.hyy.uidemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import com.android.uidemo.R;

/**
 * @author chen
 * 
 */
public class FlyInMenu extends FrameLayout {

	private static final int SNAP_VELOCITY = 400;
	private static final int FINAL_DP = 50;

	private int menuId, rightLayoutId;

	private View menu;
	private View rightLayout;

	private int touchSlop;

	private float lastMotionX;
	private Context myContext;

	private int testRight = 240;
	private float mStartX;
	private boolean mSpeed = false;

	private boolean mIsOpened = true;
	private VelocityTracker velocityTracker;
	private int velocityX;

	public int duration = 500;
	public boolean linearFlying = true;
	private int finalDis;

	private Handler myHandler;
	private int everyMSpd;

	private enum State {
		ANIMATING, READY, TRACKING,
	};

	private State mState;

	public FlyInMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		myContext = context;

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.FlyInMenu);
		duration = a.getInteger(R.styleable.FlyInMenu_animationDuration, 500);
		RuntimeException e = null;
		menuId = a.getResourceId(R.styleable.FlyInMenu_menu_item, 0);
		if (menuId == 0) {
			e = new IllegalArgumentException(
					a.getPositionDescription()
							+ ": The handle attribute is required and must refer to a valid child.");
		}
		rightLayoutId = a.getResourceId(R.styleable.FlyInMenu_content, 0);
		if (rightLayoutId == 0) {
			e = new IllegalArgumentException(
					a.getPositionDescription()
							+ ": The content attribute is required and must refer to a valid child.");
		}
		a.recycle();

		if (e != null) {
			throw e;
		}

		myHandler = new Handler();
		mState = State.READY;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		menu = findViewById(menuId);
		if (menu == null) {
			throw new RuntimeException();
		}
		rightLayout = findViewById(rightLayoutId);
		if (rightLayout == null) {
			throw new RuntimeException();
		}

		removeView(menu);
		removeView(rightLayout);

		addView(menu);
		addView(rightLayout);

		touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		System.out.println("onLayout");
		super.onLayout(changed, left, top, right, bottom);
		final float scale = myContext.getResources().getDisplayMetrics().density;
		testRight = this.getWidth() - (int) (FINAL_DP * scale + 0.5f);
		everyMSpd = (testRight * 16) / duration;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			Log.d(VIEW_LOG_TAG, "viewgroup intercept down");
			break;
		case MotionEvent.ACTION_MOVE:
			Log.d(VIEW_LOG_TAG, "viewgroup intercept move");
			break;
		case MotionEvent.ACTION_UP:
			Log.d(VIEW_LOG_TAG, "viewgroup intercept up");
			break;
		}

		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		// Log.d(VIEW_LOG_TAG, "mState="+mState);
		if (mState == State.ANIMATING) {
			return false;
		}

		if (velocityTracker == null) {
			velocityTracker = VelocityTracker.obtain();
		}
		velocityTracker.addMovement(event);

		final int action = event.getAction();
		float x = event.getX();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			Log.d(VIEW_LOG_TAG, "down");

			if (!mIsOpened) {
				if (event.getX() < rightLayout.getLeft())
					return false;
			}

			if (mIsOpened) {
				menu.setVisibility(VISIBLE);
			}

			lastMotionX = x;
			mStartX = x;
			break;
		case MotionEvent.ACTION_MOVE:
			// Log.d(VIEW_LOG_TAG, "move");

			mState = State.TRACKING;

			int deltaX = (int) (lastMotionX - x);
			lastMotionX = x;
			// 向右滑，如果已经到最右边就停止滑动
			if (deltaX < 0) {
				if (rightLayout.getLeft() >= testRight)
					break;
				if (deltaX < (rightLayout.getLeft() - testRight))
					deltaX = (int) rightLayout.getLeft() - testRight;

			}
			// 向左滑， 到最左边就停止滑动
			else {
				if (rightLayout.getLeft() < 0)
					break;
				if (deltaX > rightLayout.getLeft())
					deltaX = rightLayout.getLeft();
			}

			// rightLayout.scrollBy(deltaX, 0);
			rightLayout.offsetLeftAndRight(-deltaX);
			// rightLayout.scrollBy(-deltaX, 0);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			Log.d(VIEW_LOG_TAG, "up");

			final VelocityTracker tempVelocityTracker = velocityTracker;
			tempVelocityTracker.computeCurrentVelocity(1000);
			velocityX = (int) tempVelocityTracker.getXVelocity();
			System.out.println("velocityX=" + velocityX);
			if (velocityX > SNAP_VELOCITY || velocityX < -SNAP_VELOCITY) {
				mSpeed = true;
			} else {
				mSpeed = false;
			}

			if (velocityTracker != null) {
				velocityTracker.recycle();
				velocityTracker = null;
			}

			// 当这是一次点击动作
			if (Math.abs(x - mStartX) < touchSlop) {
				if (mIsOpened) {
					setToOpen();
				} else {
					setToClose();
				}
			}
			// 当这是一次滑动事件
			else {
				// 是一次滑动，且之前是打开的状态
				if (mIsOpened) {
					// 是一次向左的滑动不做处理
					if (x - mStartX < 0)
						break;

					// 如果是向右的滑动，如果速度满足值

					if (mSpeed) {
						setToClose();
					}
					// 如果速度不满足值
					else {
						// 判断是否滑过一半距离以上
						if (rightLayout.getLeft() > (testRight / 2)) {
							setToClose();
						} else {
							setToOpen();
						}
					}

				}
				// 如果之前是关闭的状态
				else {
					// 如果是向右的滑动不做处理
					if (x - mStartX > 0)
						break;

					if (mSpeed) {
						setToOpen();
					} else {
						if (rightLayout.getLeft() < (testRight / 2)) {
							setToOpen();
						} else {
							setToClose();
						}
					}
				}
			}

			break;
		case MotionEvent.ACTION_CANCEL:
			mState = State.READY;
			break;
		}
		return true;
	}

	private void setToClose() {
		mIsOpened = false;
		finalDis = rightLayout.getLeft() - testRight;
		if (finalDis == 0) {
			mState = State.READY;
		} else {
			updateConUI(-finalDis, false);
		}
	}

	private void setToOpen() {
		mIsOpened = true;
		finalDis = rightLayout.getLeft();
		if (finalDis == 0) {
			mState = State.READY;
		} else {
			updateConUI(finalDis, true);
		}
	}

	private void updateConUI(int length, final boolean minus) {
		mState = State.ANIMATING;
		Log.d(VIEW_LOG_TAG, "mState=" + mState);
		int i = 0;
		while (length > 0) {
			if (length < everyMSpd) {
				if (minus)
					updatePer(-length, i);
				else
					updatePer(length, i);
				length = 0;
			} else {
				length -= everyMSpd;
				if (minus)
					updatePer(-everyMSpd, i);
				else
					updatePer(everyMSpd, i);
			}
			i++;
		}

	}

	private void updatePer(final int length, final int number) {

		myHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				rightLayout.offsetLeftAndRight(length);
				invalidate();
				if (rightLayout.getLeft() == 0
						|| rightLayout.getLeft() == testRight) {
					mState = State.READY;
					if (rightLayout.getLeft() == 0)
						menu.setVisibility(GONE);
					System.out.println("end update state=" + mState);
				}

			}
		}, 16 * number);
	}

}
