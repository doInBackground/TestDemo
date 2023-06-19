//
// Created by WCL on 2023/6/19.
// 测试namespace命名空间的使用.
// 经测试引入同名头文件无法正常引入后引入的头文件,在头文件不同名的情况下可正常使用namespace功能.
//

#ifndef TESTDEMO_TEST1_H
#define TESTDEMO_TEST1_H

#include "../../music_player/AndroidLog.h"
//"test/test_temp/A.h"和"test/test_temp/C.h"仅文件名不同,内容一模一样.它们都是"test/include/A.h"添加namespace的拷贝份.
#include "A.h"
//#include "../test_temp/A.h" //[#]引入同名头文件,编译器仅能识别先引入的头文件中的成员,后引入的头文件中的成员在此使用时编译器即会报错.故注释.
#include "../test_temp/C.h" //[#]引入不同名头文件,即可正常使用namespace.

void test1();

#endif //TESTDEMO_TEST1_H
