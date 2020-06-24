package com.it.androidjnisimple;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = findViewById(R.id.sample_text);

        tv.setText(stringFromJNI());

        HelloJNI("string", 10, 10.0f);


        String[] strings = {"测", "试"};
        int[] ints = {1, 2, 3};
        HelloJNI_Array(strings, ints);

        //Native层反射调用 Java代码
        HelloJNI_InvokeHelper();

        new Helper().testReflect();

        //========动态注册========
        dynamicNative();
        dynamicNative(100);





    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    public native String HelloJNI(String string, int i, float v);

    public native String HelloJNI_Array(String[] j,int[] i);

    public native void HelloJNI_InvokeHelper();

    public native void dynamicNative();

    public native String dynamicNative(int i);




}

