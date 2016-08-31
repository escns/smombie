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
    private boolean isScreen = true;
    private boolean isNetworking = true; // 인터넷에 연결되어 있는지 아닌지 판단

    /**
     * SingleTone 구현을 위해 밖에서는 생성자를 호출할 수 없게 private 설정한 생성자
     */
    private  Global() {
    }

    /**
     * SingleTone 구현을 위한 생성자, 객체가 있다면 만들지 않고 이미 만들어진 객체를 리턴
     * @return  이전에 쓰던 객체를 리턴
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
     * 현재 isScreen 값을 가져온다
     * @return isScreen
     */
    public boolean getIsScreen() {
        return isScreen;
    }

    /**
     * isScreen 값을 입력한다
     * @param isScreen true : 화면이 켜져있을 때 / false : 화면이 꺼져있을 때
     */
    public void setIsScreen(boolean isScreen) {
        this.isScreen = isScreen;
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
