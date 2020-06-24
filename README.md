### JNI编程基础

文章参考：https://blog.csdn.net/liugec/article/details/106944311

#### 字符串操作

```cpp
//c++中需要以c的方式编译
extern "C"
//JNIEnv: 由Jvm传入与线程相关的变量。定义了JNI系统操作、java交互等方法。在这里获取任何java层的东西都需要JNIEnv参与
//jobject: 表示当前调用对象，即 this , 如果是静态的native方法，则获得jclass
//JNIEXPORT 宏定义，相当于public，将方法暴露，才能供别人调用
//JNICALL
JNIEXPORT jstring JNICALL
Java_com_it_androidjnisimple_MainActivity_HelloJNI(
        JNIEnv *env, jobject thiz, jstring str_, jint i, jfloat v) {
    // TODO: implement HelloJNI()

    // 获得c字符串
    // 开辟内存x，拷贝java字符串到x中 返回指向x的指针
    // 参数2 isCopy：提供一个boolean（int）指针，用于接收jvm传给我们的字符串是否是拷贝的。
    // ture:表示是拷贝的一个新字符串(即新申请的内存)；false:表示使用的是java的字符串（地址）
    // 通常，我们不关心这个,一般传个NULL或0就可以。表示一个空地址
    const char* str = env->GetStringUTFChars(str_, 0);


    //格式化字符串
    char returnStr[100];
    sprintf(returnStr,"C++ string:%d,%s,%f",i,str,v);

    LOGE("jni 获取Java的参数:%d,%s,%f",i,str,v);
    //释放掉内存 x
    env->ReleaseStringUTFChars(str_, str);

    // 返回java字符串
    return  env->NewStringUTF(returnStr);
}
```

```java
HelloJNI("string", 10, 10.0f);
```

打印结果：
21493-21493/com.it.androidjnisimple E/TAG_JNI: jni 获取Java的参数:10,string,10.000000







#### 字符串数组、基本数据类型数组

```cpp
extern "C"
JNIEXPORT jstring JNICALL
Java_com_it_androidjnisimple_MainActivity_HelloJNI_1Array(
        JNIEnv *env, jobject thiz, jobjectArray a_,jintArray b_) {
    // TODO: implement HelloJNI_Array()
    //1、 获得字符串数组
    //获得数组长度
    int32_t str_length = env->GetArrayLength(a_);
    LOGE("字符串 数组长度:%d",str_length);
    //获得字符串数组的数据
    for (int i = 0; i < str_length; ++i) {
        jstring str = static_cast<jstring>(env->GetObjectArrayElement(a_, i));
        const char* c_str =  env->GetStringUTFChars(str, 0);
        LOGE("字符串有:%s",c_str);
        //使用完释放
        env->ReleaseStringUTFChars(str,c_str);
    }


    //2、获得基本数据类型数组
    int32_t int_length = env->GetArrayLength(b_);
    LOGE("int 数组长度:%d",int_length);
    //对应的有 GetBoolean 、GetFloat等
    jint *b = env->GetIntArrayElements(b_, 0);
    for (int i = 0; i < int_length; i++) {
        LOGE("int 数据有:%d",b[i]);
    }
    env->ReleaseIntArrayElements(b_, b, 0);
    return env->NewStringUTF("222");

}
```

```java
String[] strings = {"测", "试"};
int[] ints = {1, 2, 3};
HelloJNI_Array(strings, ints);
```

打印结果：

21493-21493/com.it.androidjnisimple E/TAG_JNI: 字符串 数组长度:2
21493-21493/com.it.androidjnisimple E/TAG_JNI: 字符串有:测
21493-21493/com.it.androidjnisimple E/TAG_JNI: 字符串有:试
21493-21493/com.it.androidjnisimple E/TAG_JNI: int 数组长度:3
21493-21493/com.it.androidjnisimple E/TAG_JNI: int 数据有:1
21493-21493/com.it.androidjnisimple E/TAG_JNI: int 数据有:2
21493-21493/com.it.androidjnisimple E/TAG_JNI: int 数据有:3







### C/C++反射Java

```java
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
```



#### C/C++调用Java类中方法 

```cpp
//C/C++反射Java  反射方法
extern "C"
JNIEXPORT void JNICALL
Java_com_it_androidjnisimple_MainActivity_HelloJNI_1InvokeHelper(JNIEnv *env, jobject thiz) {
    // TODO: implement HelloJNI_InvokeHelper()
    jclass  clazz = env->FindClass("com/it/androidjnisimple/Helper");
    //====调用Java静态方法====
    //参数1：要反射的对象   参数2：方法名   参数3：签名。 如果不会填 可以使用javap
    jmethodID staticMethod = env->GetStaticMethodID(clazz, "staticMethod", "(Ljava/lang/String;IZ)V");
    jstring staticStr = env->NewStringUTF("C++调用静态方法");
    env->CallStaticVoidMethod(clazz, staticMethod, staticStr, 1, true);


    //====调用Java非静态方法====
    //获得构造方法 <init>：构造方法写法
    jmethodID  constructMethod = env->GetMethodID(clazz, "<init>", "()V");
    //创建对象
    jobject  helper = env->NewObject(clazz,constructMethod);
    jmethodID instanceMethod = env->GetMethodID(clazz,"instanceMethod","(Ljava/lang/String;IZ)V");
    jstring instanceStr= env->NewStringUTF("C++调用实例方法");
    env->CallVoidMethod(helper,instanceMethod,instanceStr,2, false);

    //释放
    env->DeleteLocalRef(clazz);
    env->DeleteLocalRef(staticStr);
    env->DeleteLocalRef(instanceStr);
    env->DeleteLocalRef(helper);
}
```

打印结果：

21493-21493/com.it.androidjnisimple E/TAG: staticMethod a=C++调用静态方法 b=1 c=true
21493-21493/com.it.androidjnisimple E/TAG: instanceMethod a=C++调用实例方法 b=2 c=false







#### C/C++修改Java类中属性

```cpp
//C/C++反射Java  反射属性
extern "C"
JNIEXPORT void JNICALL
Java_com_it_androidjnisimple_Helper_reflectHelper(JNIEnv *env, jobject thiz) {
    // TODO: implement reflectHelper()

//instance 就是 helper
    jclass clazz = env->GetObjectClass(thiz);
    //获得int a的标示
    jfieldID a = env->GetFieldID(clazz,"a","I");
    int avalue = env->GetIntField(thiz,a);
    LOGE("获得java属性a:%d",avalue);
    //修改属性值
    env->SetIntField(thiz,a,100);

    jfieldID b = env->GetStaticFieldID(clazz,"b","Ljava.lang.String;");
    //获取值
    jstring bstr = static_cast<jstring>(env->GetStaticObjectField(clazz, b));
    const char* bc_str = env->GetStringUTFChars(bstr,0);
    LOGE("获得java属性b:%s",bc_str);

    //修改
    jstring new_str = env->NewStringUTF("C++字符串");
    env->SetStaticObjectField(clazz,b,new_str);

    env->ReleaseStringUTFChars(bstr,bc_str);
    env->DeleteLocalRef(new_str);
    env->DeleteLocalRef(clazz);
}
```

打印结果：

21493-21493/com.it.androidjnisimple E/TAG: 修改前 ： a = 10 b=java字符串
21493-21493/com.it.androidjnisimple E/TAG_JNI: 获得java属性a:10
21493-21493/com.it.androidjnisimple E/TAG_JNI: 获得java属性b:java字符串
21493-21493/com.it.androidjnisimple E/TAG: 修改后 ： a = 100 b=C++字符串





#### Native动态注册

```cpp
void  dynamicNative1(JNIEnv *env, jobject jobj){
    LOGE("dynamicNative1 动态注册");
}
jstring  dynamicNative2(JNIEnv *env, jobject jobj,jint i){
    return env->NewStringUTF("我是动态注册的dynamicNative2方法");
}

//需要动态注册的方法数组
static const JNINativeMethod mMethods[] = {
        {"dynamicNative","()V", (void *)dynamicNative1},
        {"dynamicNative", "(I)Ljava/lang/String;", (jstring *)dynamicNative2}

};
//需要动态注册native方法的类名
static const char* mClassName = "com/it/androidjnisimple/MainActivity";

jint JNI_OnLoad(JavaVM* vm, void* reserved){
    JNIEnv* env = NULL;
    //获得 JniEnv
    int r = vm->GetEnv((void**) &env, JNI_VERSION_1_4);
    if( r != JNI_OK){
        return -1;
    }
    jclass mainActivityCls = env->FindClass( mClassName);
    // 注册 如果小于0则注册失败
    r = env->RegisterNatives(mainActivityCls,mMethods,2);
    if(r  != JNI_OK )
    {
        return -1;
    }
    return JNI_VERSION_1_4;
}
```

打印结果：

21493-21493/com.it.androidjnisimple E/TAG_JNI: dynamicNative1 动态注册