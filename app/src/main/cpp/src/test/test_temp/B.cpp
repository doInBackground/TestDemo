//
// Created by WCL on 2023/6/16.
//

#include "B.h"

//int y = 2;//[#]定义全局变量:为头文件中声明的全局变量,定义并赋值.与"test/test/A.cpp"中的成员变量y产生[定义]冲突.故注释.

//void testA() {//[#]定义方法:与"test/test/A.cpp"中的成员方法testA()产生[定义]冲突.故注释.//定义两个同名的全局方法的需求是不合理的.
//}

void testB() {
    printf("testB");
}

//A::A() {}
//
//A::~A() {}
//
//int A::getAdd(int _a, int _b) {
//    return _a + _b;
//}
//
//int A::getSub(int a, int b) {
//    return a - b;
//}
