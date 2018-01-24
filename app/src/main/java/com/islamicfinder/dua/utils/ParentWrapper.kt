package com.islamicfinder.duamodule.utils

import com.islamicfinder.duamodule.models.Dua
import com.islamicfinder.duamodule.interfaces.ParentListItem

class ParentWrapper(var parentListItem: ParentListItem?) {

    var isExpanded: Boolean = false

    val isInitiallyExpanded: Boolean
        get() = parentListItem!!.isInitiallyExpanded

    val childItemList: List<Dua>
        get() = parentListItem!!.childItemList

    init {
        isExpanded = false
    }
}
