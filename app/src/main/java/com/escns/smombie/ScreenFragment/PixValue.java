package com.escns.smombie.ScreenFragment;

import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by Administrator on 2016-08-25.
 */

public enum PixValue
{
    dip
            {
                @Override
                public int valueOf(float value)
                {
                    return Math.round(value * m.density);
                }
            };

    public static DisplayMetrics m = Resources.getSystem().getDisplayMetrics();

    public abstract int valueOf(float value);
}