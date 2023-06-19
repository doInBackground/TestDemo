//
// Created by WCL on 2023/6/19.
//

#ifndef TESTDEMO_C_H
#define TESTDEMO_C_H

#include <stdio.h>

namespace NS_A3 {

    static int x = 3;

    void testA();

    class A {
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


#endif //TESTDEMO_C_H
