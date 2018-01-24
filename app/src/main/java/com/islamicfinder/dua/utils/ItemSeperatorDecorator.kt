package com.islamicfinder.dua.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import com.islamicfinder.dua.R
import android.opengl.ETC1.getWidth



/**
 * Created by Mehroze on 1/24/2018.
 */
class ItemSeperatorDecorator(private val context: Context) : RecyclerView.ItemDecoration() {

    private var itemDivider : Drawable? = null

    init {
        itemDivider = ContextCompat.getDrawable(context, R.drawable.line_divider)
    }

    override fun onDrawOver(c: Canvas?, parent: RecyclerView?, state: RecyclerView.State?) {
        super.onDrawOver(c, parent, state)

        val left = parent?.paddingLeft!!
        val right = (parent?.width)?.minus (parent?.paddingRight!!)
        val childCount = parent!!.childCount

        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)

            val params = child.layoutParams as RecyclerView.LayoutParams

            val top = child.bottom + params.bottomMargin
            val bottom = top + itemDivider?.intrinsicHeight!!

            itemDivider?.setBounds(left, top, right, bottom)
            itemDivider?.draw(c)
        }
    }
}