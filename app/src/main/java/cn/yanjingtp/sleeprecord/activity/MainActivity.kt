package cn.yanjingtp.sleeprecord.activity

import android.content.Context

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import cn.yanjingtp.sleeprecord.R
import cn.yanjingtp.sleeprecord.adapter.MyListAdapter
import cn.yanjingtp.sleeprecord.bean.SleepRecordBean
import cn.yanjingtp.sleeprecord.db.MyDBUtil
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    var dayFormat = SimpleDateFormat("yyyy-MM-dd")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sp = getSharedPreferences("sleepRecord", Context.MODE_PRIVATE)
        var dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        var list: MutableList<SleepRecordBean> = arrayListOf()
        var adater: MyListAdapter? = null

        if (!sp.getBoolean("btnState", false)) {
            btnState.text = "开始睡了"
        } else {
            btnState.text = "已经醒了"
        }

        list = MyDBUtil(this).getData(dayFormat.format(Date(System.currentTimeMillis())))
        adater = MyListAdapter(this, list)
        listView.adapter = adater
        adater.notifyDataSetChanged()

        updateTotalToday()

        btnState.setOnClickListener {
            if (!sp.getBoolean("btnState", false)) {
                btnState.text = "已经醒了"
                sp.edit().putBoolean("btnState", true).apply()
                var bean = SleepRecordBean(dayFormat.format(Date(System.currentTimeMillis())),
                        dateFormat.format(Date(System.currentTimeMillis())),
                        "",
                        "")

                MyDBUtil(this@MainActivity).saveStartTime(bean)
                list.clear()
                list.addAll(MyDBUtil(this).getData(dayFormat.format(Date(System.currentTimeMillis()))))

                adater.notifyDataSetChanged()

            } else {
                btnState.text = "开始睡了"
                sp.edit().putBoolean("btnState", false).apply()
                var bean = SleepRecordBean("",
                        list[0].startTime,
                        dateFormat.format(Date(System.currentTimeMillis())),
                        "")

                MyDBUtil(this@MainActivity).saveEndTime(bean)
                list.clear()
                list.addAll(MyDBUtil(this@MainActivity).getData(dayFormat.format(System.currentTimeMillis())))
                adater.notifyDataSetChanged()

                updateTotalToday()

            }
        }

    }

    //更新当日睡眠总时间
    fun updateTotalToday() {
        tvToday.text = "今天的睡眠总时间:" + MyDBUtil(this).getTotalToday(dayFormat.format(Date(System.currentTimeMillis())))
    }
}
