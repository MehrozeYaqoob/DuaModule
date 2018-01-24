package com.islamicfinder.duamodule.viewholders

import android.view.View
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import com.islamicfinder.duamodule.models.DuaCategory
import com.islamicfinder.dua.R

class DuaCategoryViewHolder(itemView: View) : ParentViewHolder(itemView) {

    private val INITIAL_POSITION = 0.0f
    private val ROTATED_POSITION = 180f

    private val arrowExpandImageView: ImageView = itemView.findViewById(R.id.iv_arrow_expand)
    private val duaCategoryTextView: TextView = itemView.findViewById(R.id.tv_dua_category)

    override var isExpanded: Boolean
        get() = super.isExpanded
        set(expanded) {
            super.isExpanded = expanded

            if (expanded) {
                arrowExpandImageView?.rotation = ROTATED_POSITION
            } else {
                arrowExpandImageView?.rotation = INITIAL_POSITION
            }

        }

    fun bind(duaCategory: DuaCategory) {
        duaCategoryTextView.text = duaCategory.name
    }

    override fun onExpansionToggled(expanded: Boolean) {
        super.onExpansionToggled(expanded)

        val rotateAnimation: RotateAnimation
        if (expanded) { // rotate clockwise
            rotateAnimation = RotateAnimation(ROTATED_POSITION,
                    INITIAL_POSITION,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f)
        } else { // rotate counterclockwise
            rotateAnimation = RotateAnimation(-1 * ROTATED_POSITION,
                    INITIAL_POSITION,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f)
        }

        rotateAnimation.duration = 200
        rotateAnimation.fillAfter = true
        arrowExpandImageView.startAnimation(rotateAnimation)

    }

}
