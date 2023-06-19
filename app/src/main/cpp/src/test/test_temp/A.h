//
// Created by WCL on 2023/6/16.
//

#ifndef TESTDEMO_A_H
#define TESTDEMO_A_H

#include <stdio.h> //引入标准库,否则printf()等无法使用.

namespace NS_A1 {//[#]命名冲突的成员(变量;方法;类;类成员)都需要用"namespace"包裹起来,否则报错:multiple definition of 'xxx'.

    static int x = 2;//定义私有变量.如果不声明成私有,当该头文件多次被引入时,会报重定义错误,因为声明可以多次,但定义只能一次.

    void testA();//方法.

    class A {//类.
        int a = 1;
    public:
        int b = 1;
        int c;
    public:
        A();

        ~A();

        static int getAdd(int a, int b);

        int getSub(int a, int b);

    };

}

#endif //TESTDEMO_A_H
