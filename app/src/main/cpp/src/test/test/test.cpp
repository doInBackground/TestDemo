//
// Created by WCL on 2023/6/16.
//

#include "../include/test.h"

void test() {
    printf("测试项目被调用...");
    testA();
    testB();
    LOGE("LOG测试项目被调用...  %d %d", x, y);
}