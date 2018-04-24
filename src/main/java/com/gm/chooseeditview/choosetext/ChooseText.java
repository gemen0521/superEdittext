package com.gm.chooseeditview.choosetext;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.gm.chooseeditview.R;


/**
 * Created by gm on 2017/12/6.
 */

public class ChooseText extends android.support.v7.widget.AppCompatEditText {


  private DrawableListener drawableListener;

  RoundsDrawables[] roundsDrawables = new RoundsDrawables[4];
  int downX, downY;

  public ChooseText(Context context, AttributeSet attrs) {
    super(context, attrs);
    initDrawable();
    initTypedArray(context, attrs);
  }

  public ChooseText(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    //初始化先执行，后续操作基于该对象
    initDrawable();
    initTypedArray(context, attrs);
  }


  public void setOnDrawableListener(DrawableListener onDrawableListener) {
    drawableListener = onDrawableListener;
  }

  /***
   * 初始化四周的四个drawable
   */
  private void initDrawable() {

    Drawable[] drawables = getCompoundDrawables();
    for (int i = 0; i < roundsDrawables.length; i++) {
      RoundsDrawables roundsDrawable = new RoundsDrawables();
      roundsDrawable.downStatue = false;
      roundsDrawable.usuallyDrawable = drawables[i];
      roundsDrawables[i] = roundsDrawable;
    }
  }

  private void initTypedArray(Context context, AttributeSet attrs) {
    TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.ChooseText);
    Drawable drawableLeft = mTypedArray.getDrawable(R.styleable.ChooseText_drawable_click_left);
    Drawable drawableTop = mTypedArray.getDrawable(R.styleable.ChooseText_drawable_click_top);
    Drawable drawableRight = mTypedArray.getDrawable(R.styleable.ChooseText_drawable_click_right);
    Drawable drawableBottom = mTypedArray.getDrawable(R.styleable.ChooseText_drawable_click_bottom);
    mTypedArray.recycle();

    roundsDrawables[0].downDrawable = drawableLeft;
    roundsDrawables[1].downDrawable = drawableTop;
    roundsDrawables[2].downDrawable = drawableRight;
    roundsDrawables[3].downDrawable = drawableBottom;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
  }

  private Rect[] getRects() {
    Rect rect = new Rect();
    getDrawingRect(rect);
    Rect[] rects = new Rect[4];
    if (roundsDrawables[0].usuallyDrawable != null) {
      int drawableWidth = roundsDrawables[0].usuallyDrawable.getBounds().width();
      Rect drawableLeftRect = new Rect(rect.left, rect.top,
          (rect.left + getPaddingLeft() + drawableWidth), rect.bottom);
      rects[0] = drawableLeftRect;
    }

    if (roundsDrawables[1].usuallyDrawable != null) {
      int drawableHeight = roundsDrawables[1].usuallyDrawable.getBounds().height();
      Rect drawableTopRect = new Rect(rect.left, rect.top,
          rect.right, rect.top + getPaddingTop() + drawableHeight);
      rects[1] = drawableTopRect;
    }

    if (roundsDrawables[2].usuallyDrawable != null) {
      int drawableWidth = roundsDrawables[2].usuallyDrawable.getBounds().width();
      Rect drawableRightRect = new Rect((rect.right - getPaddingRight() - drawableWidth),
          rect.top, rect.right, rect.bottom);
      rects[2] = drawableRightRect;
    }

    if (roundsDrawables[3].usuallyDrawable != null) {
      int drawableHeight = roundsDrawables[3].usuallyDrawable.getBounds().height();
      Rect drawableBottomRect = new Rect(rect.left,
          rect.bottom - getPaddingBottom() - drawableHeight, rect.right, rect.bottom);
      rects[3] = drawableBottomRect;
    }

    return rects;
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    switch (event.getAction()) {

      case MotionEvent.ACTION_DOWN:
        downX = (int) event.getX();
        downY = (int) event.getY();
        break;

      case MotionEvent.ACTION_UP:
        int upX = (int) event.getX();
        int upY = (int) event.getY();
        if (isSame(downX, downY, upX, upY)) {
          int type = inDrawable(upX, upY);
          if (type >= 0) {
            DrawableListener.DrawableType DrawableType = DrawableListener.DrawableType.values()[type];
            upDrawable(DrawableType);
            if (drawableListener != null) {
              drawableListener.DrawableOnclick(DrawableType, this);
            }
            return false;
          }
        }
        break;
    }
    return super.onTouchEvent(event);

  }



  /***
   * 刷新指定Drawable
   * @param type  location
   */
  public void upDrawable(DrawableListener.DrawableType type) {
    Drawable left = roundsDrawables[0].downStatue
        ? roundsDrawables[0].downDrawable : roundsDrawables[0].usuallyDrawable;
    Drawable top = roundsDrawables[1].downStatue
        ? roundsDrawables[1].downDrawable : roundsDrawables[1].usuallyDrawable;
    Drawable right = roundsDrawables[2].downStatue
        ? roundsDrawables[2].downDrawable : roundsDrawables[2].usuallyDrawable;
    Drawable bottom = roundsDrawables[3].downStatue
        ? roundsDrawables[3].downDrawable : roundsDrawables[3].usuallyDrawable;
    if (type == DrawableListener.DrawableType.Onclick_Left) {
      setCompoundDrawables(getUpDrawable(roundsDrawables[0]), top, right, bottom);
    } else if (type == DrawableListener.DrawableType.Onclick_Top) {
      setCompoundDrawables(left, getUpDrawable(roundsDrawables[1]), right, bottom);
    } else if (type == DrawableListener.DrawableType.Onclick_Right) {
      setCompoundDrawables(left, top, getUpDrawable(roundsDrawables[2]), bottom);
    } else if (type == DrawableListener.DrawableType.Onclick_Right) {
      setCompoundDrawables(left, top, right, getUpDrawable(roundsDrawables[3]));
    }
  }

  private Drawable getUpDrawable(RoundsDrawables roundsDrawable){
    boolean usually = roundsDrawable.downStatue;
    Drawable drawable = roundsDrawable.usuallyDrawable;
    if (!usually && roundsDrawable.downDrawable != null) {
      drawable =  roundsDrawable.downDrawable;
    }
    roundsDrawable.downStatue = !usually;
    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    return drawable;
  }


  private boolean isSame(int downx, int downy, int upx, int upy) {
    float distanceX = Math.abs(downx - upx);
    float distanceY = Math.abs(downy - upy);
    return distanceX < 10 && distanceY < 10;
  }

  private int inDrawable(int x, int y) {
    Rect[] rects = getRects();
    for (int i = 0; i < rects.length; i++) {
      Rect rect = rects[i];
      if (rect != null) {
        if (rect.contains(x, y)) {
          return i;
        }
      } else {
        continue;
      }
    }
    return -1;
  }

  /***
   * drawable 对象
   */
  private class RoundsDrawables {
    //是否点击状态，初始是false
    boolean downStatue;
    //未点击显示
    Drawable usuallyDrawable;
    //点击后显示
    Drawable downDrawable;
  }

}
