package com.islamicfinder.duamodule.interfaces

import com.islamicfinder.duamodule.models.Dua

/**
 * Interface for implementing required methods in a parent list item.
 */
interface ParentListItem {

    /**
     * If list is empty, the parent list item has no children.
     */
    val childItemList: List<Dua>

    /**
     * @return true if expanded, false if not
     */
    val isInitiallyExpanded: Boolean
}