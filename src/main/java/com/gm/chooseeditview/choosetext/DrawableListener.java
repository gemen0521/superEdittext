package com.gm.chooseeditview.choosetext;

import android.view.View;

/**
 * Created by gm on 2017/12/6.
 */

public interface DrawableListener {

  enum DrawableType {

    Onclick_Left,
    Onclick_Top,
    Onclick_Right,
    Onclick_Bottom
  }

  void DrawableOnclick(DrawableType type, View view);
}
