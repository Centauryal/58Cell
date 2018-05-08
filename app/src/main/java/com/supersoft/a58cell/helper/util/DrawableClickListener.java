package com.supersoft.a58cell.helper.util;

/**
 * Created by itclub21 on 3/7/2017.
 */

public interface DrawableClickListener {
    enum DrawablePosition { TOP, BOTTOM, LEFT, RIGHT }

    void onClick(DrawablePosition target);
}
