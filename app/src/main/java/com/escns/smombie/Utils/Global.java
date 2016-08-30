package com.escns.smombie.Utils;

/**
 * Created by Administrator on 2016-08-26.
 */

/**
 * 공유하는 데이터들을 보관하는클래스
 */
public class Global {

    private static final int STOP = 0; // 멈쳤을 때
    private static final int MOVE = 1; // 걷을 때

    private static Global mGlobal;
    private int isWalking = MOVE; // 걷는지 멈쳤는지 판단
    private boolean isNetworking = true; // 인터넷에 연결되어 있는지 아닌지 판단

    /**
     * 생성자
     */
    private  Global() {
    }

    /**
     * 값을 받아온다
     * @return
     */
    public static Global getInstance() {
        if(mGlobal==null) {
            mGlobal = new Global();
        }
        return mGlobal;
    }

    /**
     * 현재 isWalking 값을 가져온다
     * @return isWalking
     */
    public int getIsWalking() {
        return isWalking;
    }

    /**
     * isWalking 값을 입력한다
     * @param isWalking 0 : 멈쳤을 때 / 1 : 걸을 때
     */
    public void setIsWalking(int isWalking) {
        this.isWalking = isWalking;
    }

    /**
     * 현재 isNetworking 값을 가져온다
     * @return isNetworking
     */
    public boolean getIsNetworking() {
        return isNetworking;
    }

    /**
     * isNetworking 값을 입력한다
     * @param isNetworking true : 인터넷이 될 때 / false : 인터넷이 안될 때
     */
    public void setIsNetworking(boolean isNetworking) {
        this.isNetworking = isNetworking;
    }
}
