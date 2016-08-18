package com.escns.smombie.Manager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.escns.smombie.DAO.Step;
import com.escns.smombie.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hyo99 on 2016-08-02.
 */

public class DBManager extends SQLiteOpenHelper {

    String TableName; // 테이블 이름
    String TableProperty; // 테이블 속성

    /**
     * 생성자
     * @param context MainActivity의 Context
     */
    public DBManager(Context context) {
        super(context, context.getResources().getString(R.string.app_name), null, 1);
        TableName = context.getResources().getString(R.string.app_name);        // app name의 DB table 생성
    }

    /**
     * onCreate
     * @param db SQLite에서 데이터베이스를 쓰기위한 파라미터
     */
    @Override
    public void onCreate(SQLiteDatabase db) {



        StringBuffer sb = new StringBuffer();

        sb.append(" CREATE TABLE "+TableName+" ( ");
        sb.append(" _id INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(" USER_ID TEXT, ");
        sb.append(" YEAR INTEGER, ");
        sb.append(" MONTH INTEGER, ");
        sb.append(" DAY INTEGER, ");
        sb.append(" HOUR INTEGER, ");
        sb.append(" DIST INTEGER, ");
        sb.append(" STEPCNT INTEGER ) ");

        db.execSQL(sb.toString());
    }

    /**
     * 파라미터로 받은 data를 DB에 저장
     * @param data
     */
    public void insertStepData(Step data) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();

            StringBuffer sb = new StringBuffer();
            sb.append(" INSERT INTO "+TableName+" ( ");
            sb.append(" USER_ID, YEAR, MONTH, DAY, HOUR, DIST, STEPCNT ) ");
            sb.append(" VALUES ( ?, ?, ?, ?, ?, ?, ? ) ");

            db.execSQL(sb.toString(),
                    new Object[]{
                            data.getmId(),
                            data.getmYear(),
                            data.getmMonth(),
                            data.getmDay(),
                            data.getmHour(),
                            data.getmDist(),
                            data.getmStepCnt()
                    });

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(db!=null) {
                db.close();
            }
        }
    }

    /**
     * id 값에 해당하는 Step Data를 DB에서 찾아서 List 형식으로 반환
     * @param id
     * @return
     */
    public List<Step> getStepData(String id) {

        SQLiteDatabase db = null;
        List<Step> list = new ArrayList<>();
        Step step = null;

        try {
            db = getReadableDatabase();

            StringBuffer sb = new StringBuffer();
            sb.append(" SELECT * FROM "+TableName);
            sb.append(" WHERE USER_ID is ? ");

            Cursor cursor = db.rawQuery(sb.toString(),
                    new String[]{
                            id
                    });

            while(cursor.moveToNext()) {
                step = new Step(cursor.getString(0), cursor.getInt(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4), cursor.getInt(5), cursor.getInt(6));
                list.add(step);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(db!=null) {
                db.close();
            }
        }

        return list;
    }

    /**
     * onUpgrade
     * @param db SQLite에서 데이터베이스를 쓰기위한 파라미터
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS RECORD_LIST"); // 테이블 삭제 - 초기화
        //onCreate(db); // RECORD_LIST 테이블 다시 생성
    }

    /**
     * 빈데이터의 행 하나를 추가하는 함수
     * @param query
     */
    public void insertRow(String query) {
        SQLiteDatabase db = getWritableDatabase(); // 데이터베이스 불러오기 - 쓰기전용
        db.execSQL("INSERT INTO "+ TableName +" VALUES"+ query); // 쿼리문 입력
        db.close();
    }

    /**
     * 테이블의 행 갯수 출력
     * @return 행 갯수 반환
     */
    public int getRowCount() {
        SQLiteDatabase db = getReadableDatabase(); // 데이터베이스 불러오기 - 읽기전용
        int cnt = 0; // 걸음 수

        Cursor cursor; // 테이블 한줄한줄 읽어오기 위한 Cursor 클래스
        cursor = db.rawQuery("SELECT * from "+ TableName, null); // RECORD_LIST 테이블 전부 콜
        while(cursor.moveToNext()) { // 테이블이 끝 날때까지 동작하는 반복문
            cnt++;
        }
        cursor.close();
        db.close();

        return cnt;
    }
}
