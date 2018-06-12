package cn.yanjingtp.sleeprecord.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import cn.yanjingtp.sleeprecord.bean.SleepRecordBean
import java.text.SimpleDateFormat
import java.util.*


class MyDBUtil(context: Context) {

    private val tableName: String = "tb_sleep_record"
    private var mySqlOpenHelper = MySqlOpenHelper(context)

    //保存开始时间
    fun saveStartTime(bean: SleepRecordBean) {
        var db: SQLiteDatabase = mySqlOpenHelper!!.writableDatabase
        var contentValues = ContentValues()
        contentValues.put("date_now", bean.dateNow)
        contentValues.put("start_time", bean.startTime)
        contentValues.put("end_time", bean.endTime)
        contentValues.put("interval", bean.interval)
        db.insert(tableName, null, contentValues)
        db.close()

    }

    //保存睡醒时间及睡觉时长
    fun saveEndTime(bean: SleepRecordBean) {
        var db: SQLiteDatabase = mySqlOpenHelper.writableDatabase
        var contentValues = ContentValues()
        var dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        var startTime: Date = dateFormat.parse(bean.startTime)
        var endTime: Date = dateFormat.parse(bean.endTime)

        val diff = endTime.time - startTime.time

        contentValues.put("end_time", bean.endTime)
        contentValues.put("interval", getTimeDiff(diff))
        contentValues.put("total_time", diff)

        db.update(tableName, contentValues, "start_time = ?", arrayOf(bean.startTime))
        db.close()
    }

    //获取listview中需要数据
    fun getData(dateNow: String): MutableList<SleepRecordBean> {
        var list: MutableList<SleepRecordBean> = ArrayList()
        var db: SQLiteDatabase = mySqlOpenHelper!!.readableDatabase
        val cursor = db.query(tableName, arrayOf("start_time", "end_time", "interval"), "date_now= ?", arrayOf(dateNow), null, null, "start_time desc")
        if (cursor != null) {
            while (cursor.moveToNext()) {
                var bean = SleepRecordBean("",
                        cursor.getString(cursor.getColumnIndex("start_time")),
                        cursor.getString(cursor.getColumnIndex("end_time")),
                        cursor.getString(cursor.getColumnIndex("interval"))
                )
                list.add(bean)
            }
        }

        return list
    }

    fun delData(startTime : String){
        var db:SQLiteDatabase = mySqlOpenHelper.writableDatabase
        db.delete(tableName,"start_time = ?", arrayOf(startTime))
        db.close()
    }

    //获取当日睡眠总时间
    fun getTotalToday(dateNow: String): String {
        var totalToady: Long = 0
        var db: SQLiteDatabase = mySqlOpenHelper.readableDatabase
        val cursor = db.query(tableName, arrayOf("sum(total_time) as total_time"), "date_now = ?", arrayOf(dateNow), dateNow, null, null)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                totalToady = cursor.getLong(cursor.getColumnIndex("total_time"))
            }
        }

        return getTimeDiff(totalToady)
    }

    //计算时间差
    fun getTimeDiff(diff: Long): String {
        var timeDiff: String?

        var days = diff / (1000 * 60 * 60 * 24)
        val hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
        val minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60)
        val second = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60) - minutes * (1000 * 60)) / 1000

        when {
            days > 0 -> timeDiff = "" + days + "天" + hours + "小时" + minutes + "分钟" + second + "秒"
            hours > 0 -> timeDiff = "" + hours + "小时" + minutes + "分钟" + second + "秒"
            minutes > 0 -> timeDiff = "" + minutes + "分钟" + second + "秒"
            else -> timeDiff = "" + second + "秒"
        }

        return timeDiff
    }
}