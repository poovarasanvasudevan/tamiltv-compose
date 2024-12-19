package com.poovarasan.tamiltv.widget;

import android.content.Context;

public class JavaUtils {

    public static int dpToPx(float dp, Context context) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
}
