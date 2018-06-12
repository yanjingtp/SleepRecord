package cn.yanjingtp.sleeprecord.activity

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
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

        var list: MutableList<SleepRecordBean> = MyDBUtil(this).getData(dayFormat.format(Date(System.currentTimeMillis())))
        var adater: MyListAdapter? = MyListAdapter(this, list)

        var selectDate = ""

        if (!sp.getBoolean("btnState", false)) {
            btnState.text = "开始睡了"
        } else {
            btnState.text = "已经醒了"
        }


        listView.adapter = adater
        adater!!.notifyDataSetChanged()

        updateTotalToday(dayFormat.format(Date(System.currentTimeMillis())))

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
                var bean:SleepRecordBean
                if (list.isEmpty()) {
                    bean = SleepRecordBean(dayFormat.format(Date(System.currentTimeMillis())),
                            dayFormat.format(Date(System.currentTimeMillis()))+" 00:00:00",
                            dateFormat.format(Date(System.currentTimeMillis())),
                            "")
                    MyDBUtil(this@MainActivity).saveStartTime(bean)
                }else{
                    bean = SleepRecordBean("",
                            list[0].startTime,
                            dateFormat.format(Date(System.currentTimeMillis())),
                            "")
                }

                MyDBUtil(this@MainActivity).saveEndTime(bean)
                list.clear()
                list.addAll(MyDBUtil(this@MainActivity).getData(dayFormat.format(System.currentTimeMillis())))
                adater.notifyDataSetChanged()

                updateTotalToday(dayFormat.format(Date(System.currentTimeMillis())))

            }

        }

        //侧滑显示
        val toggle = android.support.v7.app.ActionBarDrawerToggle(this, layout_main, R.string.open, R.string.close)
        layout_main.addDrawerListener(toggle)
        toggle.syncState()

        //当前显示日期
        tvNowShow.text = "当前显示：" + dayFormat.format(Date(System.currentTimeMillis()))

        tvNowShow.setOnClickListener {
            val ca = Calendar.getInstance()
            var showYear: Int
            var showMonth: Int
            var showDay: Int
            when {
                selectDate.isEmpty() -> {
                    showYear = ca.get(Calendar.YEAR)
                    showMonth = ca.get(Calendar.MONTH)
                    showDay = ca.get(Calendar.DAY_OF_MONTH)
                }
                else -> {
                    showYear = selectDate.substring(0, 4).toInt()
                    showMonth = selectDate.substring(5, 7).toInt() - 1
                    showDay = selectDate.substring(8, 10).toInt()
                }
            }

            DatePickerDialog(this@MainActivity,
                    DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->

                        when {
                            month + 1 < 10 && dayOfMonth < 10 -> selectDate = "" + year + "-0" + (month + 1) + "-0" + dayOfMonth
                            month + 1 < 10 -> selectDate = "" + year + "-0" + (month + 1) + "-" + dayOfMonth
                            dayOfMonth < 10 -> selectDate = "" + year + "-" + (month + 1) + "-0" + dayOfMonth

                        }

                        tvNowShow.text = "当前显示：" + selectDate

                        //刷新页面显示
                        list.clear()
                        list.addAll(MyDBUtil(this@MainActivity).getData(selectDate))
                        adater.notifyDataSetChanged()

                        updateTotalToday(selectDate)

                        layout_main.closeDrawer(layout_left)  //关闭侧滑
                    },
                    showYear, showMonth, showDay).show()
        }

        listView.setOnItemLongClickListener { parent, view, position, id ->
            val dialog = AlertDialog.Builder(this@MainActivity).create()
            dialog.setCancelable(false)
            dialog.setTitle("是否删除")
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定", DialogInterface.OnClickListener { dialog, which ->
                MyDBUtil(this@MainActivity).delData(list[position].startTime)
                //刷新页面显示
                list.clear()
                list.addAll(MyDBUtil(this@MainActivity).getData(dayFormat.format(Date(System.currentTimeMillis()))))
                adater.notifyDataSetChanged()

                updateTotalToday(dayFormat.format(Date(System.currentTimeMillis())))
                dialog.dismiss()
            })
            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })
            dialog.show()
            true
        }

    }


    //更新当日睡眠总时间
    fun updateTotalToday(dateNow: String) {
        tvToday.text = "今天的睡眠总时间:" + MyDBUtil(this).getTotalToday(dateNow)
    }
}

