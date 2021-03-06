package com.supersoft.a58cell.helper.exoplayer.widget;

/**
 * Created by itclub21 on 1/19/2018.
 */

import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.v7.widget.RecyclerView;

import com.supersoft.a58cell.helper.exoplayer.ToroPlayer;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author eneim | 6/2/17.
 *
 *         A hub for internal convenient methods.
 */

@SuppressWarnings({ "unused", "WeakerAccess" }) //
final class Common {

    private static final String TAG = "ToroLib:Common";

    static int compare(int x, int y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    static long max(Long... numbers) {
        List<Long> list = Arrays.asList(numbers);
        return Collections.<Long>max(list);
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY) //
    static Comparator<ToroPlayer> ORDER_COMPARATOR = new Comparator<ToroPlayer>() {
        @Override public int compare(ToroPlayer o1, ToroPlayer o2) {
            return Common.compare(o1.getPlayerOrder(), o2.getPlayerOrder());
        }
    };

    @RestrictTo(RestrictTo.Scope.LIBRARY) //
    static Comparator<ToroPlayer> ORDER_COMPARATOR_REVERSE = new Comparator<ToroPlayer>() {
        @Override public int compare(ToroPlayer o1, ToroPlayer o2) {
            return Common.compare(o2.getPlayerOrder(), o1.getPlayerOrder());
        }
    };

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    static boolean allowsToPlay(@NonNull ToroPlayer player) {
        //noinspection ConstantConditions
        boolean valid = player != null && player instanceof RecyclerView.ViewHolder;  // Should be true
        if (valid) valid = ((RecyclerView.ViewHolder) player).itemView.getParent() != null;
        if (valid) valid = player.getPlayerView().getGlobalVisibleRect(new Rect(), new Point());
        return valid;
    }
}