package com.example.myapplication.view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

@SuppressLint("DrawAllocation")
public class ScrollVerifyView extends View {

	public interface OnVerifyListener {
		public void success();

		public void fail();
	}

	private Bitmap bitmap, bgBitmap, verifyBitmap;// 原图，背景图，拖动的图片
	private Point verifyPoint, movePoint, startPoint;// 验证的点，拖动中的点，拖动的起点
	private boolean isMoving;// 是否在移动中
	private int moveX;// 移动中的x坐标
	private OnVerifyListener onVerifyListener;

	public void setOnVerifyListener(OnVerifyListener onVerifyListener) {
		this.onVerifyListener = onVerifyListener;
	}

	public ScrollVerifyView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ScrollVerifyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ScrollVerifyView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	@SuppressLint("NewApi")
	public ScrollVerifyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		// TODO Auto-generated constructor stub
	}

	private int getMeasure(int measure, int defaultSize) {
		int result = 200;
		int mode = MeasureSpec.getMode(measure);
		int size = MeasureSpec.getSize(measure);
		if (mode == MeasureSpec.EXACTLY) {// 指定大小
			result = size;
		} else if (mode == MeasureSpec.AT_MOST) {// wrap_content
			result = Math.min(defaultSize, size);
		}
		return result;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(getMeasure(widthMeasureSpec, 200), getMeasure(heightMeasureSpec, 200));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		Paint paint = new Paint();
		// 根据原图进行裁剪出适合当前屏幕的背景图
		if (bgBitmap == null) {
			if (bitmap == null) {
				return;
			}
			bgBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
			Canvas bgCanvas = new Canvas(bgBitmap);
			Rect bgRect;
			if (bitmap.getWidth() / getWidth() < bitmap.getHeight() / getHeight()) {
				bgRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getWidth() * bitmap.getHeight() / getWidth());
			} else {
				bgRect = new Rect(0, 0, bitmap.getWidth() * bitmap.getHeight() / getHeight(), bitmap.getHeight());
			}
			bgCanvas.drawBitmap(bitmap, bgRect, new Rect(0, 0, getWidth(), getHeight()), paint);
			bitmap.recycle();
			bitmap = null;
		}
		// 绘制背景图
		canvas.drawBitmap(bgBitmap, null, new Rect(0, 0, getWidth(), getHeight()), paint);
		// 计算验证的点和拖动起点
		if (verifyPoint == null) {
			int width = getWidth();
			int height = getHeight();
			int randomY = (int) (Math.random() * height);
			int verifyX = width * 3 / 4 - 10;
			int verifyY = randomY + height / 4 + 10 > height ? height * 3 / 4 - 10 : randomY;
			verifyPoint = new Point(verifyX, verifyY);
			startPoint = new Point(10, verifyY);
		}
		paint.setColor(Color.GRAY);
		paint.setStyle(Paint.Style.FILL);
		paint.setAntiAlias(true);
		// 绘制验证的位置
		Rect verifyRect = new Rect(verifyPoint.x, verifyPoint.y, verifyPoint.x + getWidth() / 4,
				verifyPoint.y + getHeight() / 4);
		canvas.drawRect(verifyRect, paint);
		// 裁剪拖动的图片
		if (verifyBitmap == null) {
			verifyBitmap = Bitmap.createBitmap(getWidth() / 4, getHeight() / 4, Config.ARGB_8888);
			Canvas verifyCanvas = new Canvas(verifyBitmap);
			verifyCanvas.drawBitmap(bgBitmap, verifyRect,
					new Rect(0, 0, verifyBitmap.getWidth(), verifyBitmap.getHeight()), paint);
		}
		paint.setColor(Color.BLACK);
		paint.setShadowLayer(20, 0, 0, Color.BLACK);
		// 拖动图片的绘制
		if (isMoving) {// 拖动中
			canvas.drawRect(new Rect(movePoint.x - 2, movePoint.y - 2, movePoint.x + getWidth() / 4 + 2,
					movePoint.y + getHeight() / 4 + 2), paint);
			canvas.drawBitmap(verifyBitmap, null,
					new Rect(movePoint.x, movePoint.y, movePoint.x + getWidth() / 4, movePoint.y + getHeight() / 4),
					paint);
		} else {
			canvas.drawRect(new Rect(startPoint.x - 2, startPoint.y - 2, startPoint.x + getWidth() / 4 + 2,
					startPoint.y + getHeight() / 4 + 2), paint);
			canvas.drawBitmap(verifyBitmap, null,
					new Rect(startPoint.x, startPoint.y, startPoint.x + getWidth() / 4, startPoint.y + getHeight() / 4),
					paint);
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (event.getX() > startPoint.x && event.getX() < startPoint.x + getWidth() / 4
						&& event.getY() > startPoint.y && event.getY() < startPoint.y + getHeight() / 4) {
					movePoint = new Point(startPoint);
					moveX = (int) event.getX();
					isMoving = true;
					invalidate();
					return true;
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (isMoving) {
					if (movePoint.x + getWidth() / 4 < getWidth() && movePoint.x > 0) {
						invalidate();
					}
					movePoint = new Point((int) (movePoint.x + event.getX() - moveX), movePoint.y);
					moveX = (int) event.getX();
					return true;
				}
				break;
			case MotionEvent.ACTION_UP:
				if (isMoving) {
					if (onVerifyListener != null) {
						if (Math.abs(movePoint.x - verifyPoint.x) < 10) {
							onVerifyListener.success();
						} else {
							onVerifyListener.fail();
						}
					}
					isMoving = false;
					movePoint = null;
					moveX = 0;
					invalidate();
					return true;
				}
				break;
			default:
				break;
		}
		return super.onTouchEvent(event);
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
		if (verifyBitmap != null) {
			verifyBitmap.recycle();
			verifyBitmap = null;
		}
		if (bgBitmap != null) {
			bgBitmap.recycle();
			bgBitmap = null;
		}
		verifyPoint = null;
		movePoint = null;
		startPoint = null;
		isMoving = false;
		moveX = 0;
		invalidate();
	}

}
