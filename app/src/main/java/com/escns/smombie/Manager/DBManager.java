package com.escns.smombie.Manager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.escns.smombie.DAO.Record;
import com.escns.smombie.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hyo99 on 2016-08-02.
 */


/**
 * Local DataBase
 */
public class DBManager extends SQLiteOpenHelper {

    private String DB_NAME; // DB 파일 이름
    private String RECORD_TABLE; // 테이블 이름

    /**
     * 생성자
     * @param context MainActivity의 Context
     */
    public DBManager(Context context) {
        super(context, context.getResources().getString(R.string.app_name), null, 1);
        DB_NAME = context.getResources().getString(R.string.app_name);        // app name의 DB table 생성
        RECORD_TABLE = "RECORDS";
    }

    /**
     * onCreate
     * @param db SQLite에서 데이터베이스를 쓰기위한 파라미터
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    /**
     * Record 테이블을 생성한다
     */
    public void CreateRecordTable() {
        SQLiteDatabase db = getWritableDatabase();

        StringBuffer sb = new StringBuffer();

        sb.append(" CREATE TABLE " + RECORD_TABLE + " ( ");
        sb.append(" _id INTEGER PRIMARY KEY AUTOINCREMENT, "); // 의미없음
        sb.append(" USER_ID_INT INTEGER, ");
        sb.append(" YEAR INTEGER, ");
        sb.append(" MONTH INTEGER, ");
        sb.append(" DAY INTEGER, ");
        sb.append(" HOUR INTEGER, ");
        sb.append(" DIST INTEGER ) ");

        db.execSQL(sb.toString());
        db.close();
    }

    /**
     * onUpgrade
     * @param db         SQLite에서 데이터베이스를 쓰기위한 파라미터
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS RECORD_LIST"); // 테이블 삭제 - 초기화
        //onCreate(db); // RECORD_LIST 테이블 다시 생성
    }

    /**
     * 파라미터로 받은 data를 Record테이블에 저장한다
     * @param data
     */
    public void insertRecord(Record data) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            db.execSQL("INSERT INTO " + RECORD_TABLE + " (USER_ID_INT, YEAR, MONTH, DAY, HOUR, DIST) VALUES (" +
                    data.getmIdInt() + "," +
                    data.getmYear() + "," +
                    data.getmMonth() + "," +
                    data.getmDay() + "," +
                    data.getmHour() + "," +
                    data.getmDist() + ")");

            //StringBuffer sb = new StringBuffer();
            //sb.append(" INSERT INTO " + RECORD_TABLE + " ( ");
            //sb.append(" USER_ID_INT, YEAR, MONTH, DAY, HOUR, DIST) ");
            //sb.append(" VALUES ( ?, ?, ?, ?, ?, ?, ? ) ");
            //db.execSQL(sb.toString(),
            //        new Object[]{
            //                data.getmIdInt(),
            //                data.getmYear(),
            //                data.getmMonth(),
            //                data.getmDay(),
            //                data.getmHour(),
            //                data.getmDist()
            //        });

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * Record테이블에서 모든 데이터를 List로 반환한다
     * @return Record테이블의 데이터
     */
    public List<Record> getRecord() {

        SQLiteDatabase db = null;
        List<Record> list = null;
        list = new ArrayList<>();
        Record record = null;

        try {
            db = getReadableDatabase();

            StringBuffer sb = new StringBuffer();
            sb.append(" SELECT * FROM " + RECORD_TABLE);
            Cursor cursor = db.rawQuery(sb.toString(), null);

            while (cursor.moveToNext()) {
                record = new Record(cursor.getInt(1),
                        cursor.getInt(2),
                        cursor.getInt(3),
                        cursor.getInt(4),
                        cursor.getInt(5),
                        cursor.getInt(6));
                list.add(record);
            }
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return list;
    }

    /**
     * Record 테이블 삭제
     */
    public void dropRecordTable() {
        SQLiteDatabase db = getWritableDatabase(); // 데이터베이스 불러오기 - 쓰기전용
        db.execSQL("DROP TABLE IF EXISTS " + RECORD_TABLE); // 쿼리문 입력
        db.close();
    }

    /**
     * Record 테이블의 마지막 행의 mDist를 업데이트한다
     * @param r 업데이트 할 새 mDist 정보
     */
    public void updateLastRecord(Record r) {

        int id = 0;

        SQLiteDatabase db = getWritableDatabase(); // 데이터베이스 불러오기 - 쓰기전용

        Cursor cursor; // 테이블 한줄한줄 읽어오기 위한 Cursor 클래스
        cursor = db.rawQuery("SELECT * from "+ RECORD_TABLE, null); // RECORD_LIST 테이블 전부 콜
        while(cursor.moveToNext()) { // 테이블이 끝 날때까지 동작하는 반복문
            id = cursor.getInt(0); // 정수형 데이터 콜
        }
        cursor.close();

        db.execSQL("UPDATE " + RECORD_TABLE + " SET DIST = " + r.getmDist() +
                " WHERE _id = " + id
        ); // 쿼리문 입력
        db.close();
    }

    /**
     * Record 테이블의 마지막 행을 지운다
     */
    public void deleteLastRecord() {

        int id = 0;

        SQLiteDatabase db = getWritableDatabase(); // 데이터베이스 불러오기 - 쓰기전용

        Cursor cursor; // 테이블 한줄한줄 읽어오기 위한 Cursor 클래스
        cursor = db.rawQuery("SELECT * from "+ RECORD_TABLE, null); // RECORD_LIST 테이블 전부 콜
        while(cursor.moveToNext()) { // 테이블이 끝 날때까지 동작하는 반복문
            id = cursor.getInt(0); // 정수형 데이터 콜
        }
        cursor.close();

        db.execSQL("DELETE FROM " + RECORD_TABLE + " WHERE _id = " + id
        ); // 쿼리문 입력
        db.close();
    }

    /**
     * Record 테이블의 현재 행의 갯수를 반환한다
     * @return 행의 갯수
     */
    public int getRowCount() {
        int cnt = 0;

        SQLiteDatabase db = getWritableDatabase(); // 데이터베이스 불러오기 - 쓰기전용

        Cursor cursor; // 테이블 한줄한줄 읽어오기 위한 Cursor 클래스
        cursor = db.rawQuery("SELECT * from "+ RECORD_TABLE, null); // RECORD_LIST 테이블 전부 콜
        while(cursor.moveToNext()) { // 테이블이 끝 날때까지 동작하는 반복문
            cnt++;
        }
        cursor.close();
        db.close();

        return cnt;
    }
}
