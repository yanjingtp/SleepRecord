package cn.yanjingtp.sleeprecord.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MySqlOpenHelper(context: Context?) : SQLiteOpenHelper(context, "sleepRecord.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {

        var sql = "create table tb_sleep_record (" +
                "_id integer primary key autoincrement ," +
                "date_now varchar," +
                "start_time varchar," +
                "end_time varchar," +
                "interval varchar," +
                "total_time varchar"+
                "); "

        db?.execSQL(sql)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}