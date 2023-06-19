//
// Created by WCL on 2023/6/16.
//

#include "A.h"

NS_A1::A::A() {}

NS_A1::A::~A() {}

int NS_A1::A::getAdd(int _a, int _b) {
    return _a + _b;
}

int NS_A1::A::getSub(int a, int b) {
    return a - b;
}

void NS_A1::testA() {
    printf("testA %d", x);
    printf("testA %d", NS_A1::x);
}