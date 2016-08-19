package com.escns.smombie.Setting;

/**
 * Created by Administrator on 2016-08-19.
 */

public class Conf {

    private static Conf conf;

    public String mFbId;           // 페이스북 ID
    public String mFbName;         // 페이스북 이름
    public String mFbEmail;        // 페이스북 이메일
    public String mFbGender;       // 페이스북 성별
    public int mFbAge;             // 페이스북 나이

    public static Conf getInstance() {
        if(conf==null) {
            conf = new Conf();
        }
        return conf;
    }

    private Conf() {
    }
}
