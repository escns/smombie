package com.escns.smombie.DAO;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hyo99 on 2016-08-18.
 */

/**
 * User의 정보들을 관리하는 class
 */
public class User {

    @SerializedName("USER_ID_INT")
    private int mIdInt;         // 유저 고유 번호
    @SerializedName("USER_ID_TEXT")
    private String mIdStr;      // 페이스북 유저 고유 번호
    @SerializedName("NAME")
    private String mName;       // 이름
    @SerializedName("EMAIL")
    private String mEmail;      // 이메일
    @SerializedName("GENDER")
    private String mGender;     // 성별
    @SerializedName("AGE")
    private int mAge;           // 나이
    @SerializedName("POINT")
    private int mPoint;         // 포인트
    @SerializedName("GOAL")
    private int mGoal;          // 목표점
    @SerializedName("REWORD")
    private int mReword;        // 쿠폰 수
    @SerializedName("SUCCESSCNT")
    private int mSuccessCnt;    // 성공횟수
    @SerializedName("FAILCNT")
    private int mFailCnt;       // 실패횟수
    @SerializedName("AVGDIST")
    private int mAvgDist;       // 하루 평균 걸음 거리

    /**
     * 생성자
     * @param mIdInt 유저 고유 번호
     * @param mIdStr 페이스북 유저 고유 번호
     * @param mName 이름
     * @param mEmail 이메일
     * @param mGender 성별
     * @param mAge 나이
     * @param mPoint 포인트
     * @param mGoal 목표점
     * @param mReword 쿠폰
     * @param mSuccessCnt 성공횟수
     * @param mFailCnt 실패횟수
     * @param mAvgDist 하루 평균 걸은 거리
     */
    public User(int mIdInt, String mIdStr, String mName, String mEmail, String mGender, int mAge, int mPoint, int mGoal, int mReword, int mSuccessCnt, int mFailCnt, int mAvgDist) {
        this.mIdInt = mIdInt;
        this.mIdStr = mIdStr;
        this.mName = mName;
        this.mEmail = mEmail;
        this.mGender = mGender;
        this.mAge = mAge;
        this.mPoint = mPoint;
        this.mGoal = mGoal;
        this.mReword = mReword;
        this.mSuccessCnt = mSuccessCnt;
        this.mFailCnt = mFailCnt;
        this.mAvgDist = mAvgDist;
    }

    public int getmIdInt() {
        return mIdInt;
    }

    public void setmIdInt(int mIdInt) {
        this.mIdInt = mIdInt;
    }

    public String getmIdStr() {
        return mIdStr;
    }

    public void setmIdStr(String mIdStr) {
        this.mIdStr = mIdStr;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmGender() {
        return mGender;
    }

    public void setmGender(String mGender) {
        this.mGender = mGender;
    }

    public int getmAge() {
        return mAge;
    }

    public void setmAge(int mAge) {
        this.mAge = mAge;
    }

    public int getmPoint() {
        return mPoint;
    }

    public void setmPoint(int mPoint) {
        this.mPoint = mPoint;
    }

    public int getmGoal() {
        return mGoal;
    }

    public void setmGoal(int mGoal) {
        this.mGoal = mGoal;
    }

    public int getmReword() {
        return mReword;
    }

    public void setmReword(int mReword) {
        this.mReword = mReword;
    }

    public int getmSuccessCnt() {
        return mSuccessCnt;
    }

    public void setmSuccessCnt(int mSuccessCnt) {
        this.mSuccessCnt = mSuccessCnt;
    }

    public int getmFailCnt() {
        return mFailCnt;
    }

    public void setmFailCnt(int mFailCnt) {
        this.mFailCnt = mFailCnt;
    }

    public int getmAvgDist() {
        return mAvgDist;
    }

    public void setmAvgDist(int mAvgDist) {
        this.mAvgDist = mAvgDist;
    }

    @Override
    public String toString() {
        return "User{" +
                "mIdInt='" + mIdInt + '\'' +
                ", mIdStr='" + mIdStr + '\'' +
                ", mName='" + mName + '\'' +
                ", mEmail='" + mEmail + '\'' +
                ", mGender='" + mGender + '\'' +
                ", mAge=" + mAge +
                ", mPoint=" + mPoint +
                ", mGoal=" + mGoal +
                ", mReword=" + mReword +
                ", mSuccessCnt=" + mSuccessCnt +
                ", mFailCnt=" + mFailCnt +
                ", mAvgDist=" + mAvgDist +
                '}';
    }
}
