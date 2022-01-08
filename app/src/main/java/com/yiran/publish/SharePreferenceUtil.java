package com.yiran.publish;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceUtil {
    private static SharedPreferences sp = null;
    public static SharedPreferences getSp(Context context) {
        if (null == sp) {
            sp = context.getSharedPreferences("publish_info", Context.MODE_PRIVATE);
        }
        return sp;
    }
}
