package com.escns.smombie.Utils;

/**
 * Created by Administrator on 2016-08-26.
 */

public class Global {

    private static final int STOP = 0;
    private static final int MOVE = 1;

    private static Global mGlobal;
    private int isWalking = STOP;

    private  Global() {
    }

    public static Global getInstance() {
        if(mGlobal==null) {
            mGlobal = new Global();
        }
        return mGlobal;
    }

    public int getIsWalking() {
        return isWalking;
    }

    public void setIsWalking(int isWalking) {
        this.isWalking = isWalking;
    }
}
