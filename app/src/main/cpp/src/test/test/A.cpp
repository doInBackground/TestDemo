//
// Created by WCL on 2023/6/16.
//

//#include "A.h" //通过include_directories(src/test/include)指定头文件路径(父路径)时,用这种引入方式.
//#include "include/A.h" //通过include_directories(src/test)指定头文件路径(爷路径)时,用这种引入方式.
#include "../include/A.h" //如果不提供给外界使用,而未在CMakeLists.txt中用include_directories指定头文件路径时,用相对路径方式引入.

int y = 1;//为头文件中声明的全局变量,定义并赋值.

void testA() {
    printf("testA %d %d", x, y);
}

A::A() {}

A::~A() {}

int A::getAdd(int _a, int _b) {
    return _a + _b;
}

int A::getSub(int a, int b) {
    return a - b;
}