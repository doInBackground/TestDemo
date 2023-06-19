//
// Created by WCL on 2023/6/19.
//

#include "../include/test1.h"

//using namespace NS_A3; //声明使用某个命名空间,相当于直接引入该命名空间代码.[可能与其他引入产生冲突(如此处两个引入的头文件都有x变量会产生歧义),故注释]
//using NS_A3::x; //声明使用某个命名空间的某个成员,后续使用时直接用成员名即可.[可能与其他引入产生冲突(如此处两个引入的头文件都有x变量会产生歧义),故注释]

void test1() {
    int a = x;//"test/include/A.h"中的私有变量.
    int b = y;//"test/include/A.h"中的全局变量.

//    int a = NS_A1::x;//"test/test_temp/A.h"中的私有变量.(引入同名头文件时,无法正常使用,故注释)
//    NS_A1::testA();//"test/test_temp/A.h"中的全局方法.(引入同名头文件时,无法正常使用,故注释)
//    NS_A1::A aClass;//"test/test_temp/A.h"中的类.(引入同名头文件时,无法正常使用,故注释)

    int c = NS_A3::x;//"test/test_temp/C.h"中的私有变量.
    NS_A3::testA();//"test/test_temp/C.h"中的全局方法.
    NS_A3::A aClass;//"test/test_temp/C.h"中的类.
}