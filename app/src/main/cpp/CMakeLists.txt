
#指定cmake最小支持版本
cmake_minimum_required(VERSION 3.4.1)

#设置本地动态库，编译生成动态库  #根据native-lib.cpp源文件编译一个native-lib的动态库
#native-lib: 模块名，变量，即最终生成的so包的名字
#SHARED：动态库     STATIC：静态库
#*.cpp: 源文件
add_library( 
        native-lib
        SHARED
        native-lib.cpp)

#查找系统库(可以简写到下面)，这里查找的是系统日志库，并赋值给变量log-1ib
find_library(
        log-lib
        log)

#需要链接或者编译的库  即编译native-lib动态库需要链接 log模块
#设置依赖的库(第一个参数必须为目标模块,顺序不能换)
target_link_libraries(
        native-lib
        ${log-lib})

#target_link_libraries(
#        native-lib
#        #直接将log库添加，可以省去 find_library(xxx) 配置
#        log)

