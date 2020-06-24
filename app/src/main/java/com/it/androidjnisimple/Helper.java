package com.it.androidjnisimple;

import android.util.Log;

/**
 * Created by lgc on 2020-04-27.
 */
public class Helper {
    int a = 10;
    static String b = "java字符串";

    private static final String TAG = "TAG";
    //private和public 对jni开发来说没任何区别 都能反射调用
    public void instanceMethod(String a,int b,boolean c){
        Log.e(TAG,"instanceMethod a=" +a +" b="+b+" c="+c );
    }

    public static void staticMethod(String a,int b,boolean c){
        Log.e(TAG,"staticMethod a=" +a +" b="+b+" c="+c);
    }


    public void testReflect() {
        Log.e(TAG,"修改前 ： a = " +a +" b="+b);
        reflectHelper();
        Log.e(TAG,"修改后 ： a = " +a +" b="+b);
    }
    public  native void  reflectHelper();

}
