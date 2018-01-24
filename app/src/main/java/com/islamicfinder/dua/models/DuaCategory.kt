package com.islamicfinder.duamodule.models

import com.islamicfinder.duamodule.interfaces.ParentListItem

class DuaCategory(val name: String, private val duaList: List<Dua>) : ParentListItem {

    override val childItemList: List<Dua>
        get() = duaList

    override val isInitiallyExpanded: Boolean
        get() = false
}
