package com.yueding.food.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/11.
 *
 */

public class SpUtil {

    private static SharedPreferences sp;

    /**
     * 使用sp存储list<String>
     * @param context 上下文环境
     * @param name 名称标识
     * @param environmentList 需要存储的list
     */
    public static void putStringList(Context context, String name, List<String> environmentList) {
        if (sp == null) {
            sp = context.getSharedPreferences("spdata", Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(name + "Nums", environmentList.size());
        for (int i = 0; i < environmentList.size(); i++)
        {
            editor.putString(name + "item_" + i, environmentList.get(i));
        }
        editor.apply();
    }

    /**
     * 获取存在sp中的List<String>
     * @param context 上下文环境
     * @param name 名称标识
     * @return 获取到的list
     */
    public static List<String> getStringList(Context context, String name) {
        ArrayList<String> stringList = new ArrayList<>();
        if (sp == null) {
            sp = context.getSharedPreferences("spdata", Context.MODE_PRIVATE);
        }
        int num = sp.getInt(name + "Nums", 0);
        for (int i = 0; i < num; i++) {
            String item = sp.getString(name + "item_" + i, null);
            stringList.add(item);
        }
        return stringList;
    }
}
