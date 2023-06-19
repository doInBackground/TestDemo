//
// Created by WCL on 2023/6/16.
//

#ifndef TESTDEMO_B_H
#define TESTDEMO_B_H

#include <stdio.h>

extern int y;//[#]声明全局变量:与"test/include/A.h"中的成员变量y重复[声明],但不产生[定义]冲突.(声明可以多次,但定义只能一次)//故此处代码不删编译不会报错.

void testA();//[#]声明全局方法:与"test/include/A.h"中的成员方法testA()重复[声明],但不产生[定义]冲突.(声明可以多次,但定义只能一次)//故此处代码不删编译不会报错,但若外部引用该头文件并调用该成员会报错.

void testB();//声明全局方法.

//class A {//[#]声明类:与"test/include/A.h"中的成员类A产生[定义]冲突.故注释.
//    int a = 1;
//public:
//    int b = 1;
//    int c;
//public:
//    A();
//
//    ~A();
//
//    static int getAdd(int a, int b);
//
//    int getSub(int a, int b);
//};

#endif //TESTDEMO_B_H
