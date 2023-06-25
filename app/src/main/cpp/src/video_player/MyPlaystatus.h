#ifndef MYMUSIC_WLPLAYSTATUS_H
#define MYMUSIC_WLPLAYSTATUS_H


class MyPlaystatus {

public:
    bool exit;
    bool seek = false;
    bool pause = false;
    bool load = true;//正在努力加载
public:
    MyPlaystatus();

};


#endif //MYMUSIC_WLPLAYSTATUS_H
