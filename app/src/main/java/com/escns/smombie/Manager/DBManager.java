package com.escns.smombie.Manager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.escns.smombie.DAO.Record;
import com.escns.smombie.DAO.User;
import com.escns.smombie.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hyo99 on 2016-08-02.
 */

public class DBManager extends SQLiteOpenHelper {

    private String DB_NAME; // 테이블 이름
    private String USER_TABLE;
    private String RECORD_TABLE;

    /**
     * 생성자
     * @param context MainActivity의 Context
     */
    public DBManager(Context context) {
        super(context, context.getResources().getString(R.string.app_name), null, 1);
        DB_NAME = context.getResources().getString(R.string.app_name);        // app name의 DB table 생성
        USER_TABLE = DB_NAME+"_USER";
        RECORD_TABLE = DB_NAME+"_RECORD";
    }

    /**
     * onCreate
     * @param db SQLite에서 데이터베이스를 쓰기위한 파라미터
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        StringBuffer sb = new StringBuffer();

        sb.append(" CREATE TABLE "+ RECORD_TABLE +" ( ");
        sb.append(" _id INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(" USER_ID TEXT, ");
        sb.append(" YEAR INTEGER, ");
        sb.append(" MONTH INTEGER, ");
        sb.append(" DAY INTEGER, ");
        sb.append(" HOUR INTEGER, ");
        sb.append(" DIST INTEGER, ");
        sb.append(" STEPCNT INTEGER ) ");

        db.execSQL(sb.toString());

        sb = new StringBuffer();

        sb.append(" CREATE TABLE "+ USER_TABLE +" ( ");
        sb.append(" USER_ID TEXT PRIMARY KEY, ");
        sb.append(" POINT INTEGER, ");
        sb.append(" GOAL INTEGER, ");
        sb.append(" REWORD INTEGER, ");
        sb.append(" SUCCESSCNT INTEGER, ");
        sb.append(" TOTAL INTEGER ) ");

        db.execSQL(sb.toString());
    }

    /**
     * 파라미터로 받은 data를 DB에 저장
     * @param data
     */
    public void insertRecord(Record data) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();

            StringBuffer sb = new StringBuffer();
            sb.append(" INSERT INTO "+ RECORD_TABLE +" ( ");
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
     * id 값에 해당하는 Record Data를 DB에서 찾아서 List 형식으로 반환
     * @param id
     * @return
     */
    public List<Record> getRecord(String id) {

        SQLiteDatabase db = null;
        List<Record> list = new ArrayList<>();
        Record record = null;

        try {
            db = getReadableDatabase();

            StringBuffer sb = new StringBuffer();
            sb.append(" SELECT * FROM "+ RECORD_TABLE);
            sb.append(" WHERE USER_ID is ? ");

            Cursor cursor = db.rawQuery(sb.toString(),
                    new String[]{
                            id
                    });

            while(cursor.moveToNext()) {
                record = new Record(cursor.getString(0), cursor.getInt(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4), cursor.getInt(5), cursor.getInt(6));
                list.add(record);
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
     *
     * @param data
     */
    public void updateUser(User data) {
        SQLiteDatabase db = null;
        User user =  null;

        try {
            db = getReadableDatabase();

            StringBuffer sb = new StringBuffer();
            sb.append(" SELECT * FROM "+ USER_TABLE);
            sb.append(" WHERE USER_ID is ? ");

            Cursor cursor = db.rawQuery(sb.toString(),
                    new String[]{
                            data.getmId()
                    });

            while(cursor.moveToNext()) {
                user = new User(cursor.getString(0), cursor.getInt(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4), cursor.getInt(5));
            }
            if(user==null) {
                sb = new StringBuffer();
                sb.append(" INSERT INTO "+ USER_TABLE +" ( ");
                sb.append(" USER_ID, POINT, GOAL, REWORD, SUCCESSCNT, TOTAL ) ");
                sb.append(" VALUES ( ?, ?, ?, ?, ?, ? ) ");

                db.execSQL(sb.toString(),
                        new Object[]{
                                data.getmId(),
                                data.getmPoint(),
                                data.getmGoal(),
                                data.getmReword(),
                                data.getmSuccessCnt(),
                                data.getmTotal()
                        });
            } else {
                sb = new StringBuffer();
                sb.append(" UPDATE "+ USER_TABLE +" SET");
                sb.append(" POINT = ? ,");
                sb.append(" SUCCESSCNT = ? ,");
                sb.append(" TOTAL = ? ");
                sb.append(" WHERE USER_ID = ? ");

                db.execSQL(sb.toString(),
                        new Object[]{
                                user.getmPoint()+data.getmPoint(),
                                user.getmSuccessCnt()+data.getmSuccessCnt(),
                                user.getmTotal()+1,
                                data.getmId()
                        });
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(db!=null) {
                db.close();
            }
        }
    }

    public User getUser(String id) {

        SQLiteDatabase db = null;
        User user = null;

        try {
            db = getReadableDatabase();

            StringBuffer sb = new StringBuffer();
            sb.append(" SELECT * FROM "+ USER_TABLE);
            sb.append(" WHERE USER_ID is ? ");

            Cursor cursor = db.rawQuery(sb.toString(),
                    new String[]{
                            id
                    });

            while(cursor.moveToNext()) {
                user = new User(cursor.getString(0), cursor.getInt(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4), cursor.getInt(5));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(db!=null) {
                db.close();
            }
        }

        return user;
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
        db.execSQL("INSERT INTO "+ RECORD_TABLE +" VALUES"+ query); // 쿼리문 입력
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
        cursor = db.rawQuery("SELECT * from "+ RECORD_TABLE, null); // RECORD_LIST 테이블 전부 콜
        while(cursor.moveToNext()) { // 테이블이 끝 날때까지 동작하는 반복문
            cnt++;
        }
        cursor.close();
        db.close();

        return cnt;
    }
}
