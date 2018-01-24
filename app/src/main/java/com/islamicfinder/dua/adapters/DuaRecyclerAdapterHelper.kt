package com.islamicfinder.duamodule.adapters

import com.islamicfinder.duamodule.interfaces.ParentListItem
import com.islamicfinder.duamodule.utils.ParentWrapper
import java.util.*

object DuaRecyclerAdapterHelper {

    /**
     * Generates a full list of all [ParentListItem] objects and their
     * children, in order.
     */
    fun generateParentChildItemList(parentItemList: List<ParentListItem>): List<Any> {
        val parentWrapperList = ArrayList<Any>()
        var parentListItem: ParentListItem
        var parentWrapper: ParentWrapper

        val parentListItemCount = parentItemList.size
        for (i in 0 until parentListItemCount) {
            parentListItem = parentItemList[i]
            parentWrapper = ParentWrapper(parentListItem)
            parentWrapperList.add(parentWrapper)

            if (parentWrapper.isInitiallyExpanded) {
                parentWrapper.isExpanded = true

                val childListItemCount = parentWrapper.childItemList.size
                for (j in 0 until childListItemCount) {
                    parentWrapperList.add(parentWrapper.childItemList[j])
                }
            }
        }

        return parentWrapperList
    }
}
