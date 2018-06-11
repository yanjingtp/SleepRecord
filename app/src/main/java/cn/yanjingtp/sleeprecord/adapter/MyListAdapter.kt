package cn.yanjingtp.sleeprecord.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import cn.yanjingtp.sleeprecord.R
import cn.yanjingtp.sleeprecord.bean.SleepRecordBean

class MyListAdapter(context: Context, list: List<SleepRecordBean>) : BaseAdapter() {
    private var context = context
    private var list = list
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView
        var viewHolder: ViewHolder? = null
        if (viewHolder == null) {
            viewHolder = ViewHolder()
            convertView = View.inflate(context, R.layout.layout_list_adapter_item, null)
            viewHolder.tvStartTime = convertView.findViewById(R.id.tvStartTime)
            viewHolder.tvEndTime = convertView.findViewById(R.id.tvEndTime)
            viewHolder.tvInterval = convertView.findViewById(R.id.tvInterval)
            convertView.tag = viewHolder


        } else {
            viewHolder = convertView!!.tag as ViewHolder
        }

        val startTime = list[position].startTime
        val endTime = list[position].endTime
        val interval = list[position].interval

        viewHolder.tvStartTime!!.text = startTime
        viewHolder.tvEndTime!!.text = endTime
        viewHolder.tvInterval!!.text = interval



        return convertView!!
    }


    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return list.size
    }

    private inner class ViewHolder {
        var tvStartTime: TextView? = null
        var tvEndTime: TextView? = null
        var tvInterval: TextView? = null
    }
}
