package com.islamicfinder.duamodule.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View

open class ParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    /******************************* Region Properties *******************************/

    var parentListItemExpandCollapseListener: ParentListItemExpandCollapseListener? = null
    open var isExpanded: Boolean = false

    /******************************* Region Interface *******************************/

    interface ParentListItemExpandCollapseListener {
        fun onParentListItemExpanded(position: Int)
        fun onParentListItemCollapsed(position: Int)
    }

    init {
        isExpanded = false
    }

    /******************************* Region Event Handling Methods *******************************/

    fun setMainItemClickToExpand() {
        itemView.setOnClickListener(this)
    }

    open fun onExpansionToggled(expanded: Boolean) {

    }

    override fun onClick(v: View) {
        if (isExpanded) {
            collapseView()
        } else {
            expandView()
        }
    }

    fun shouldItemViewClickToggleExpansion(): Boolean {
        return true
    }

    /******************************* Region View Methods *******************************/

    private fun expandView() {
        isExpanded = true
        onExpansionToggled(false)

        if (parentListItemExpandCollapseListener != null) {
            parentListItemExpandCollapseListener!!.onParentListItemExpanded(adapterPosition)
        }
    }

    private fun collapseView() {
        isExpanded = false
        onExpansionToggled(true)

        if (parentListItemExpandCollapseListener != null) {
            parentListItemExpandCollapseListener!!.onParentListItemCollapsed(adapterPosition)
        }
    }
}
