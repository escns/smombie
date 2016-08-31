package com.escns.smombie.DAO;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016-08-25.
 * 통계자료 구성을 위해 User 와 Record를 join한 객체
 */

public class UserJoinRecord {

    @SerializedName("_id")
    private int _id;
    @SerializedName("USER_ID_INT")
    private int USER_ID_INT;
    @SerializedName("YEAR")
    private int YEAR;
    @SerializedName("MONTH")
    private int DAY;
    @SerializedName("HOUR")
    private int HOUR;
    @SerializedName("DIST")
    private int DIST;
    @SerializedName("USER_ID_TEXT")
    private String USER_ID_TEXT;
    @SerializedName("NAME")
    private String NAME;
    @SerializedName("EMAIL")
    private String EMAIL;
    @SerializedName("GENDER")
    private String GENDER;
    @SerializedName("AGE")
    private int AGE;
    @SerializedName("POINT")
    private int POINT;
    @SerializedName("GOAL")
    private int REWORD;
    @SerializedName("SUCCESSCNT")
    private int SUCCESSCNT;
    @SerializedName("FAILCNT")
    private int FAILCNT;
    @SerializedName("AVGDIST")
    private int AVGDIST;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getUSER_ID_INT() {
        return USER_ID_INT;
    }

    public void setUSER_ID_INT(int USER_ID_INT) {
        this.USER_ID_INT = USER_ID_INT;
    }

    public int getYEAR() {
        return YEAR;
    }

    public void setYEAR(int YEAR) {
        this.YEAR = YEAR;
    }

    public int getDAY() {
        return DAY;
    }

    public void setDAY(int DAY) {
        this.DAY = DAY;
    }

    public int getHOUR() {
        return HOUR;
    }

    public void setHOUR(int HOUR) {
        this.HOUR = HOUR;
    }

    public int getDIST() {
        return DIST;
    }

    public void setDIST(int DIST) {
        this.DIST = DIST;
    }

    public String getUSER_ID_TEXT() {
        return USER_ID_TEXT;
    }

    public void setUSER_ID_TEXT(String USER_ID_TEXT) {
        this.USER_ID_TEXT = USER_ID_TEXT;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public String getGENDER() {
        return GENDER;
    }

    public void setGENDER(String GENDER) {
        this.GENDER = GENDER;
    }

    public int getAGE() {
        return AGE;
    }

    public void setAGE(int AGE) {
        this.AGE = AGE;
    }

    public int getPOINT() {
        return POINT;
    }

    public void setPOINT(int POINT) {
        this.POINT = POINT;
    }

    public int getREWORD() {
        return REWORD;
    }

    public void setREWORD(int REWORD) {
        this.REWORD = REWORD;
    }

    public int getSUCCESSCNT() {
        return SUCCESSCNT;
    }

    public void setSUCCESSCNT(int SUCCESSCNT) {
        this.SUCCESSCNT = SUCCESSCNT;
    }

    public int getFAILCNT() {
        return FAILCNT;
    }

    public void setFAILCNT(int FAILCNT) {
        this.FAILCNT = FAILCNT;
    }

    public int getAVGDIST() {
        return AVGDIST;
    }

    public void setAVGDIST(int AVGDIST) {
        this.AVGDIST = AVGDIST;
    }

    @Override
    public String toString() {
        return "UserJoinRecord{" +
                "_id=" + _id +
                ", USER_ID_INT=" + USER_ID_INT +
                ", YEAR=" + YEAR +
                ", DAY=" + DAY +
                ", HOUR=" + HOUR +
                ", DIST=" + DIST +
                ", USER_ID_TEXT='" + USER_ID_TEXT + '\'' +
                ", NAME='" + NAME + '\'' +
                ", EMAIL='" + EMAIL + '\'' +
                ", GENDER='" + GENDER + '\'' +
                ", AGE=" + AGE +
                ", POINT=" + POINT +
                ", REWORD=" + REWORD +
                ", SUCCESSCNT=" + SUCCESSCNT +
                ", FAILCNT=" + FAILCNT +
                ", AVGDIST=" + AVGDIST +
                '}';
    }
}
