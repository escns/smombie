package com.escns.smombie;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by hyo99 on 2016-08-02.
 */

public class DBManager extends SQLiteOpenHelper {

    String TableName; // 테이블 이름
    String TableProperty; // 테이블 속성

    /**
     * 생성자
     * @param context MainActivity의 Context
     * @param name 테이블 이름
     * @param property 테이블 속성
     */
    public DBManager(Context context, String name, String property) {
        super(context, "Record", null, 1); // Record.db 이란 이름의 데이터베이스파일 생성
        TableName = name;
        TableProperty = property;
    }

    /**
     * onCreate
     * @param db SQLite에서 데이터베이스를 쓰기위한 파라미터
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ TableName +" "+ TableProperty); // 테이블 생성
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
     * 쿼리문을 입력받는 함수
     * @param query 데이터베이스에 적용할 쿼리문
     */
    public void inputQuery(String query) {
        SQLiteDatabase db = getWritableDatabase(); // 데이터베이스 불러오기 - 쓰기전용
        db.execSQL(query); // 쿼리문 입력
        db.close();
        // 새 데이터 입력
        // ex. _query = "INSERT INTO RECORD_LIST VALUES(0)"
        // 기본 데이터 갱신
        // ex. _query = "UPDATE RECORD_LIST SET number=3000 WHERE number<3000"
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
     * 테이블에 있는 데이터들 출력
     * @return 내부 데이터 반환
     */
    public int printData() {
        SQLiteDatabase db = getReadableDatabase(); // 데이터베이스 불러오기 - 읽기전용
        int num = 0; // 걸음 수

        Cursor cursor; // 테이블 한줄한줄 읽어오기 위한 Cursor 클래스
        cursor = db.rawQuery("SELECT * from "+ TableName, null); // RECORD_LIST 테이블 전부 콜
        while(cursor.moveToNext()) { // 테이블이 끝 날때까지 동작하는 반복문
            num = cursor.getInt(0); // 정수형 데이터 콜
        }
        cursor.close();
        db.close();

        return num;
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
