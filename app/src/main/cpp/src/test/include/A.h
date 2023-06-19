//
// Created by WCL on 2023/6/16.
//

#ifndef TESTDEMO_A_H
#define TESTDEMO_A_H

#include <stdio.h>

/**
 * [声明]可以出现多次,但[定义]只能出现一次.(注:[定义]要为变量分配内存空间,而[声明]不需要为变量分配内存空间)
 *  extern int a; //声明一个全局变量a.
 *  int a; //定义一个全局变量a.
 *  extern int a = 0; //定义一个全局变量a,并给初值.
 *  int a = 0; //定义一个全局变量a,并给初值.
 */

//int a;//定义公有全局变量.//因为定义不能有多次,故当该头文件被多次引用时报错:multiple definition of 'a'.可通过"static"/"extern"等方式解决.
static int x;//定义私有变量.//static声明成员后,包含该头文件的源文件实际上会各自拥有独立的同名变量(等同于分别在多个cpp文件中定义同名static变量).
extern int y;//声明全局变量.//extern使全局成员由定义变为声明,并需要在包含该头文件的其中一个源文件中定义该成员.这样该头文件被多次引用时也不会报错,其他文件也可以正常使用该全局成员了.

void testA();//声明全局方法.//该方法是全局方法,可以多次声明,但只能有一次定义,故在其他文件定义同名的全局方法的需求是不合理的.

class A {
    int a = 1;//私有成员.
public://访问修饰符号.[public:类外部可访问] [private(默认情况):类外部不可访问,仅类内和友元函数可访问] [protected:与私有成员类似,但子类中可以].
    int b = 1;
    int c;
public:
    A();//声明构造函数.

    ~A();//声明析构函数.

    static int getAdd(int a, int b);

    int getSub(int a, int b);

};

#endif //TESTDEMO_A_H