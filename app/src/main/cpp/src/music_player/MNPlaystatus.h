#ifndef MYMUSIC_WLPLAYSTATUS_H
#define MYMUSIC_WLPLAYSTATUS_H

class MNPlaystatus {

public:
    bool exit;
    bool seek = false; //seek是耗时操作,通过记录seek状态来使得seek过程中不进行解码操作.

public:
    MNPlaystatus();

};

#endif //MYMUSIC_WLPLAYSTATUS_H
