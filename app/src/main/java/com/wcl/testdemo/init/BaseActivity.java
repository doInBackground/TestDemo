package com.wcl.testdemo.init;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.blankj.utilcode.util.SPStaticUtils;
import com.wcl.testdemo.R;
import com.wcl.testdemo.constant.SPKeys;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initThemes();
        super.onCreate(savedInstanceState);
    }

    //初始化换肤主题.
    private void initThemes() {
        String themesType = SPStaticUtils.getString(SPKeys.THEMES_TYPE);
        switch (themesType) {
            case "BLACK":
                setTheme(R.style.AppStyleBlack);
                break;
            case "WHITE":
            default:
                setTheme(R.style.AppStyleWhite);
                break;
        }
//        //如需在代码中取不同主题的值,则:
//        TypedArray typedArray = obtainStyledAttributes(new int[]{
//                R.attr.colorAttrName,
//                R.attr.integerAttrName,
//                R.attr.booleanAttrName,
//                R.attr.dimensionAttrName,
//                R.attr.floatAttrName,
//                R.attr.stringAttrName,
//                R.attr.referenceAttrName
//        });
//        int colorAttrName = typedArray.getColor(0, Color.BLACK);
//        int integerAttrName = typedArray.getInt(1, Integer.MAX_VALUE);
//        boolean booleanAttrName = typedArray.getBoolean(2, false);
//        int dimensionAttrName = typedArray.getDimensionPixelSize(3, 66);
//        float floatAttrName = typedArray.getFloat(4, 99.99f);
//        String stringAttrName = typedArray.getString(5);
//        Drawable referenceAttrName = typedArray.getDrawable(6);
    }

}
