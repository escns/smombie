package com.escns.smombie.Utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2016-08-25.
 */

public class RandomAd {
    public static final String BASE_URL = "https://raw.githubusercontent.com/HaJaeKwon/GnBangExam/master/app/src/main/res/drawable/";

    public List<String> getRandomAdUrl(int cnt) {
        List<String> list = new ArrayList<>();

        Boolean[] check;
        check = new Boolean[cnt];
        for(int i=0; i<cnt; i++) {
            check[i]=false;
        }
        Random random = new Random();

        for(int i=0; i<cnt; i++) {
            int j;
            do{
                j = random.nextInt(cnt);
            } while(check[j]==true);

            check[j]=true;
            list.add(BASE_URL+"ad_"+(j+1)+".png");
            Log.i("tag", "get URL is " + list.get(i));
        }
        return list;
    }
}
