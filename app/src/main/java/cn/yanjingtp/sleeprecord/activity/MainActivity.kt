package cn.yanjingtp.sleeprecord.activity

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Toast
import cn.yanjingtp.sleeprecord.R
import cn.yanjingtp.sleeprecord.adapter.MyListAdapter
import cn.yanjingtp.sleeprecord.bean.SleepRecordBean
import cn.yanjingtp.sleeprecord.db.MyDBUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_modify.view.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private var dayFormat = SimpleDateFormat("yyyy-MM-dd")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        var list: MutableList<SleepRecordBean> = MyDBUtil(this).getData(dayFormat.format(Date(System.currentTimeMillis())))
        var adapter: MyListAdapter? = MyListAdapter(this, list)


        listView.adapter = adapter
        adapter!!.notifyDataSetChanged()

        updateTotalToday(dayFormat.format(Date(System.currentTimeMillis())))

        //开始睡了
        btnSleep.setOnClickListener {
            var bean = SleepRecordBean(dayFormat.format(Date(System.currentTimeMillis())), dateFormat.format(System.currentTimeMillis()), "", "")
            MyDBUtil(this@MainActivity).saveStartTime(bean)
            list.clear()
            list.addAll(MyDBUtil(this@MainActivity).getData(dayFormat.format(Date(System.currentTimeMillis()))))
            adapter.notifyDataSetChanged()
            listView.setSelection(0)
        }

        //已经醒了
        btnWake.setOnClickListener {
            if (list.isEmpty()) {
                Toast.makeText(this@MainActivity, R.string.alert_sleep, Toast.LENGTH_SHORT).show()
            } else if (!list.isEmpty() && !list[0].endTime.isEmpty()) {
                Toast.makeText(this@MainActivity, R.string.alert_wake, Toast.LENGTH_SHORT).show()
            } else {
                var bean = SleepRecordBean(dayFormat.format(Date(System.currentTimeMillis())), list[0].startTime, dateFormat.format(Date(System.currentTimeMillis())), "")
                MyDBUtil(this@MainActivity).saveEndTime(bean)
                list.clear()
                list.addAll(MyDBUtil(this@MainActivity).getData(dayFormat.format(Date(System.currentTimeMillis()))))
                adapter.notifyDataSetChanged()
            }
        }

        //侧滑显示
        val toggle = android.support.v7.app.ActionBarDrawerToggle(this, layout_main, R.string.open, R.string.close)
        layout_main.addDrawerListener(toggle)
        toggle.syncState()

        //当前显示日期
        tvNowShow.text = "当前显示：" + dayFormat.format(Date(System.currentTimeMillis()))
        var selectDate = ""
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
                        selectDate = String.format("%d-%s-%s"
                                , year
                                , String.format("%02d", month + 1)
                                , String.format("%02d", dayOfMonth)
                        )

                        tvNowShow.text = "当前显示：" + selectDate

                        //刷新页面显示
                        list.clear()
                        list.addAll(MyDBUtil(this@MainActivity).getData(selectDate))
                        adapter.notifyDataSetChanged()

                        updateTotalToday(selectDate)

                        layout_main.closeDrawer(layout_left)  //关闭侧滑
                    },
                    showYear, showMonth, showDay).show()
        }

        //删除
        listView.setOnItemLongClickListener { parent, view, position, id ->
            val dialog = AlertDialog.Builder(this@MainActivity).create()
            dialog.setCancelable(false)
            dialog.setTitle("是否删除")
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定", DialogInterface.OnClickListener { dialog, which ->
                MyDBUtil(this@MainActivity).delData(list[position].startTime)
                //刷新页面显示
                list.clear()
                list.addAll(MyDBUtil(this@MainActivity).getData(dayFormat.format(Date(System.currentTimeMillis()))))
                adapter.notifyDataSetChanged()

                updateTotalToday(dayFormat.format(Date(System.currentTimeMillis())))
                dialog.dismiss()
            })
            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })
            dialog.show()
            true
        }

        listView.setOnItemClickListener { parent, view, position, id ->
            val dialog = AlertDialog.Builder(this@MainActivity)
                    .setNegativeButton("取消", null)
//                    .setPositiveButton("确定", null)
                    .create()
            dialog.setCancelable(false)
            var view = View.inflate(this@MainActivity, R.layout.layout_modify, null)
            view.etStartTime.text = Editable.Factory.getInstance().newEditable(list[position].startTime)
            view.etStartTime.isFocusableInTouchMode = false
            view.etWakeTime.text = Editable.Factory.getInstance().newEditable(list[position].endTime)
            view.etWakeTime.isFocusableInTouchMode = false



            dialog.setView(view)

            dialog.setOnShowListener {

                view.etWakeTime.setOnClickListener {
                    val ca = Calendar.getInstance()
                    DatePickerDialog(this@MainActivity, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->

                        TimePickerDialog(this@MainActivity, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                            var wakeTime = String.format("%d-%s-%s %s:%s:%s"
                                    , year
                                    , String.format("%02d", month + 1)
                                    , String.format("%02d", dayOfMonth)
                                    , String.format("%02d", hourOfDay)
                                    , String.format("%02d", minute)
                                    , String.format("%02d", 0)
                            )

                            Log.e("====", wakeTime)
                            if (dateFormat.parse(wakeTime).before(dateFormat.parse(list[position].startTime))) {
                                Toast.makeText(this@MainActivity, R.string.alert_modify, Toast.LENGTH_LONG).show()
                            } else {
                                var bean = SleepRecordBean(dayFormat.format(Date(System.currentTimeMillis())), list[position].startTime, wakeTime, "")
                                MyDBUtil(this@MainActivity).saveEndTime(bean)
                                var targetDate =dayFormat.format(dayFormat.parse(list[position].startTime))
                                list.clear()
                                list.addAll(MyDBUtil(this@MainActivity).getData(targetDate))
                                adapter.notifyDataSetChanged()
                                updateTotalToday(targetDate)
                                dialog.dismiss()
                            }
                        }, ca.get(Calendar.HOUR_OF_DAY), ca.get(Calendar.MINUTE), true).show()

                    }
                            , list[position].startTime.substring(0, 4).toInt()
                            , list[position].startTime.substring(5, 7).toInt() - 1
                            , list[position].startTime.substring(8, 10).toInt()
                    ).show()
                }

                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                    dialog.dismiss()
                }
            }

            dialog.show()
        }

    }


    //更新当日睡眠总时间
    private fun updateTotalToday(dateNow: String) {
        tvToday.text = "今天的睡眠总时间:" + MyDBUtil(this).getTotalToday(dateNow)
    }
}

