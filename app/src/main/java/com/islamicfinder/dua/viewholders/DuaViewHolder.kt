package com.islamicfinder.duamodule.viewholders

import android.graphics.Color
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.islamicfinder.duamodule.models.Dua
import com.islamicfinder.dua.R

class DuaViewHolder(itemView: View) : ChildViewHolder(itemView), View.OnClickListener {


    private val duaTitleTextView: TextView = itemView.findViewById<View>(R.id.tv_dua_title) as TextView

    init {
        itemView.setOnClickListener(this)
        itemView.setBackgroundColor(Color.argb(255,254,251,241))
    }
    fun bind(dua: Dua) {
        duaTitleTextView.text = dua.title
    }

    /******************************* Region Click Listener Method *******************************/

    override fun onClick(v: View?) {
        Toast.makeText(itemView.context, duaTitleTextView.text,
                Toast.LENGTH_SHORT).show();
    }
}
