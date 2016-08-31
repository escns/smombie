package com.escns.smombie.Utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2016-08-25.
 * 랜덤한 광고 생성을 위한 class, 현재는 웹에 올라가 있는 광고 이미지 중 랜덤하게 선택
 */

public class RandomAd {

    public static final String BASE_URL = "https://raw.githubusercontent.com/HaJaeKwon/GnBangExam/master/app/src/main/res/drawable/";
    public static final int AD_TOTAL_CNT = 5;

    /**
     * 광고의 Url을 List 형식으로 cnt 갯수만큼 리턴한다.
     * @param cnt   필요한 광고의 갯수
     * @return      Url을 List에 담아 리턴
     */
    public List<String> getRandomAdUrl(int cnt) {
        List<String> list = new ArrayList<>();

        Boolean[] check;
        check = new Boolean[AD_TOTAL_CNT];
        for(int i=0; i<AD_TOTAL_CNT; i++) {
            check[i]=false;
        }
        Random random = new Random();

        for(int i=0; i<cnt; i++) {
            int j;
            do{
                j = random.nextInt(AD_TOTAL_CNT);
            } while(check[j]==true);

            check[j]=true;
            list.add(BASE_URL+"ad_"+(j+1)+".png");
            Log.i("tag", "get URL is " + list.get(i));
        }
        return list;
    }
}
