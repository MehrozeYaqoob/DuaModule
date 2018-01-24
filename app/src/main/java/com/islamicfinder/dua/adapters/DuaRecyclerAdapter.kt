package com.islamicfinder.duamodule.adapters

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.islamicfinder.duamodule.interfaces.ParentListItem
import com.islamicfinder.duamodule.utils.ParentWrapper
import com.islamicfinder.duamodule.viewholders.ChildViewHolder
import com.islamicfinder.duamodule.viewholders.ParentViewHolder
import java.util.ArrayList
import java.util.HashMap

abstract class DuaRecyclerAdapter<PVH : ParentViewHolder, CVH : ChildViewHolder>(private val parentItemList: List<ParentListItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ParentViewHolder.ParentListItemExpandCollapseListener {

    /******************************************* Region  Properties ************************************************************/

    private var mutableItemList: MutableList<Any>
    private var expandCollapseListener: ExpandCollapseListener? = null
    private val attachedRecyclerViewPool: MutableList<RecyclerView>

    /******************************************* Region  Inner Interface ************************************************************/

    interface ExpandCollapseListener {
        fun onListItemExpanded(position: Int)
        fun onListItemCollapsed(position: Int)
    }

    init {
        mutableItemList = DuaRecyclerAdapterHelper.generateParentChildItemList(parentItemList) as MutableList<Any>
        attachedRecyclerViewPool = ArrayList()
    }

    /******************************************* Region  Abstract Methods ************************************************************/

    /**
     * Callback called from [.onCreateViewHolder] when
     * the list item created is a parent.
     * @return A `PVH` corresponding to the [ParentListItem] with
     * the `ViewGroup` parentViewGroup
     */
    abstract fun onCreateParentViewHolder(parentViewGroup: ViewGroup): PVH

    /**
     * Callback called from [.onCreateViewHolder] when
     * the list item created is a child.
     * @return A `CVH` corresponding to the child list item with the
     * `ViewGroup` childViewGroup
     */
    abstract fun onCreateChildViewHolder(childViewGroup: ViewGroup): CVH

    /**
     * Callback called from onBindViewHolder(RecyclerView.ViewHolder, int)
     * when the list item bound to is a parent.
     */
    abstract fun onBindParentViewHolder(parentViewHolder: PVH, position: Int, parentListItem: ParentListItem)

    /**
     * Callback called from onBindViewHolder(RecyclerView.ViewHolder, int)
     * when the list item bound to is a child.
     */
    abstract fun onBindChildViewHolder(childViewHolder: CVH, position: Int, childListItem: Any)

    /******************************************* Region Recycler View Methods ************************************************************/

    /**
     * Implementation of Adapter.onCreateViewHolder(ViewGroup, int)
     * that determines if the list item is a parent or a child and calls through
     * to the appropriate implementation of either [.onCreateParentViewHolder]
     * or [.onCreateChildViewHolder].
     */
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_PARENT) {
            val pvh = onCreateParentViewHolder(viewGroup)
            pvh.parentListItemExpandCollapseListener = this
            return pvh
        } else return if (viewType == TYPE_CHILD) {
            onCreateChildViewHolder(viewGroup)
        } else {
            throw IllegalStateException("Incorrect ViewType found")
        }
    }

    /**
     * Implementation of Adapter.onBindViewHolder(RecyclerView.ViewHolder, int)
     * that determines if the list item is a parent or a child and calls through
     * to the appropriate implementation of either [.onBindParentViewHolder]
     * or [.onBindChildViewHolder].
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val listItem = getListItem(position)
        if (listItem is ParentWrapper) {
            val parentViewHolder = holder as PVH

            if (parentViewHolder.shouldItemViewClickToggleExpansion()) {
                parentViewHolder.setMainItemClickToExpand()
            }

            val parentWrapper = listItem as ParentWrapper?
            parentViewHolder.isExpanded = parentWrapper!!.isExpanded
            onBindParentViewHolder(parentViewHolder, position, parentWrapper.parentListItem!!)
        } else if (listItem == null) {
            throw IllegalStateException("Incorrect ViewHolder found")
        } else {
            onBindChildViewHolder(holder as CVH, position, listItem)
        }
    }

    override fun getItemCount(): Int {
        return mutableItemList.size
    }

    override fun getItemViewType(position: Int): Int {
        val listItem = getListItem(position)
        return if (listItem is ParentWrapper) {
            TYPE_PARENT
        } else if (listItem == null) {
            throw IllegalStateException("Null object added")
        } else {
            TYPE_CHILD
        }
    }

    override fun onParentListItemExpanded(position: Int) {
        val listItem = getListItem(position)
        if (listItem is ParentWrapper) {
            expandParentListItem(listItem as ParentWrapper?, position, true)
        }
    }

    override fun onParentListItemCollapsed(position: Int) {
        val listItem = getListItem(position)
        if (listItem is ParentWrapper) {
            collapseParentListItem(listItem as ParentWrapper?, position, true)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        if (recyclerView != null) {
            attachedRecyclerViewPool.add(recyclerView)
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView?) {
        super.onDetachedFromRecyclerView(recyclerView)
        attachedRecyclerViewPool.remove(recyclerView)
    }

    fun setExpandCollapseListener(expandCollapseListener: ExpandCollapseListener) {
        this.expandCollapseListener = expandCollapseListener
    }

    /******************************************* Region Programmatic Expansion/Collapsing************************************************************/

    /**
     * Expands the parent with the specified index in the list of parents.
     */
    private fun expandParent(parentIndex: Int) {
        val parentWrapperIndex = getParentWrapperIndex(parentIndex)

        val listItem = getListItem(parentWrapperIndex)
        val parentWrapper: ParentWrapper
        if (listItem is ParentWrapper) {
            parentWrapper = listItem
        } else {
            return
        }

        expandViews(parentWrapper, parentWrapperIndex)
    }

    /**
     * Expands the parent associated with a specified [ParentListItem] in
     * the list of parents.
     */
    private fun expandParent(parentListItem: ParentListItem) {
        val parentWrapper = getParentWrapper(parentListItem)
        val parentWrapperIndex = mutableItemList.indexOf(parentWrapper!!)
        if (parentWrapperIndex == -1) {
            return
        }

        expandViews(parentWrapper, parentWrapperIndex)
    }

    fun expandParentRange(startParentIndex: Int, parentCount: Int) {
        val endParentIndex = startParentIndex + parentCount
        for (i in startParentIndex until endParentIndex) {
            expandParent(i)
        }
    }

    fun expandAllParents() {
        for (parentListItem in parentItemList) {
            expandParent(parentListItem)
        }
    }

    private fun collapseParent(parentIndex: Int) {
        val parentWrapperIndex = getParentWrapperIndex(parentIndex)

        val listItem = getListItem(parentWrapperIndex)
        val parentWrapper: ParentWrapper
        if (listItem is ParentWrapper) {
            parentWrapper = listItem
        } else {
            return
        }

        collapseViews(parentWrapper, parentWrapperIndex)
    }

    private fun collapseParent(parentListItem: ParentListItem) {
        val parentWrapper = getParentWrapper(parentListItem)
        val parentWrapperIndex = mutableItemList.indexOf(parentWrapper!!)
        if (parentWrapperIndex == -1) {
            return
        }

        collapseViews(parentWrapper, parentWrapperIndex)
    }

    fun collapseParentRange(startParentIndex: Int, parentCount: Int) {
        val endParentIndex = startParentIndex + parentCount
        for (i in startParentIndex until endParentIndex) {
            collapseParent(i)
        }
    }

    fun collapseAllParents() {
        for (parentListItem in parentItemList) {
            collapseParent(parentListItem)
        }
    }

    fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putSerializable(EXPANDED_STATE_MAP, generateExpandedStateMap())
    }

    fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState == null || !savedInstanceState.containsKey(EXPANDED_STATE_MAP)) {
            return
        }

        val expandedStateMap = savedInstanceState.getSerializable(EXPANDED_STATE_MAP) as HashMap<Int, Boolean> ?: return

        val parentWrapperList = ArrayList<Any>()
        var parentListItem: ParentListItem
        var parentWrapper: ParentWrapper

        val parentListItemCount = parentItemList.size
        for (i in 0 until parentListItemCount) {
            parentListItem = parentItemList[i]
            parentWrapper = ParentWrapper(parentListItem)
            parentWrapperList.add(parentWrapper)

            if (expandedStateMap.containsKey(i)) {
                val expanded = expandedStateMap[i]
                if (expanded!!) {
                    parentWrapper.isExpanded = true

                    val childListItemCount = parentWrapper.childItemList.size
                    for (j in 0 until childListItemCount) {
                        parentWrapper.childItemList[j]?.let { parentWrapperList.add(it) }
                    }
                }
            }
        }

        mutableItemList = parentWrapperList

        notifyDataSetChanged()
    }

    private fun getListItem(position: Int): Any? {
        val indexInRange = position >= 0 && position < mutableItemList.size
        return if (indexInRange) {
            mutableItemList[position]
        } else {
            null
        }
    }

    private fun expandViews(parentWrapper: ParentWrapper?, parentIndex: Int) {
        var viewHolder: PVH?
        for (recyclerView in attachedRecyclerViewPool) {
            viewHolder = recyclerView.findViewHolderForAdapterPosition(parentIndex) as PVH
            if (viewHolder != null && !viewHolder.isExpanded) {
                viewHolder.isExpanded = true
                viewHolder.onExpansionToggled(false)
            }

            expandParentListItem(parentWrapper, parentIndex, false)
        }
    }

    private fun collapseViews(parentWrapper: ParentWrapper?, parentIndex: Int) {
        var viewHolder: PVH?
        for (recyclerView in attachedRecyclerViewPool) {
            viewHolder = recyclerView.findViewHolderForAdapterPosition(parentIndex) as PVH
            if (viewHolder != null && viewHolder.isExpanded) {
                viewHolder.isExpanded = false
                viewHolder.onExpansionToggled(true)
            }

            collapseParentListItem(parentWrapper, parentIndex, false)
        }
    }

    private fun expandParentListItem(parentWrapper: ParentWrapper?, parentIndex: Int, expansionTriggeredByListItemClick: Boolean) {
        if (!parentWrapper!!.isExpanded) {
            parentWrapper.isExpanded = true

            val childItemList = parentWrapper.childItemList
            if (childItemList != null) {
                val childListItemCount = childItemList.size
                for (i in 0 until childListItemCount) {
                    childItemList[i]?.let { mutableItemList.add(parentIndex + i + 1, it) }
                }

                notifyItemRangeInserted(parentIndex + 1, childListItemCount)
            }

            if (expansionTriggeredByListItemClick && expandCollapseListener != null) {
                val expandedCountBeforePosition = getExpandedItemCount(parentIndex)
                expandCollapseListener!!.onListItemExpanded(parentIndex - expandedCountBeforePosition)
            }
        }
    }

    private fun collapseParentListItem(parentWrapper: ParentWrapper?, parentIndex: Int, collapseTriggeredByListItemClick: Boolean) {
        if (parentWrapper!!.isExpanded) {
            parentWrapper.isExpanded = false

            val childItemList = parentWrapper.childItemList
            if (childItemList != null) {
                val childListItemCount = childItemList.size
                for (i in childListItemCount - 1 downTo 0) {
                    mutableItemList.removeAt(parentIndex + i + 1)
                }

                notifyItemRangeRemoved(parentIndex + 1, childListItemCount)
            }

            if (collapseTriggeredByListItemClick && expandCollapseListener != null) {
                val expandedCountBeforePosition = getExpandedItemCount(parentIndex)
                expandCollapseListener!!.onListItemCollapsed(parentIndex - expandedCountBeforePosition)
            }
        }
    }

    private fun getExpandedItemCount(position: Int): Int {
        if (position == 0) {
            return 0
        }

        var expandedCount = 0
        for (i in 0 until position) {
            val listItem = getListItem(i)
            if (listItem !is ParentWrapper) {
                expandedCount++
            }
        }
        return expandedCount
    }

    /********************************************************* Region Data Manipulation *******************************************************/

    fun notifyParentItemInserted(parentPosition: Int) {
        val parentListItem = parentItemList[parentPosition]

        val wrapperIndex: Int
        if (parentPosition < parentItemList.size - 1) {
            wrapperIndex = getParentWrapperIndex(parentPosition)
        } else {
            wrapperIndex = mutableItemList.size
        }

        val sizeChanged = addParentWrapper(wrapperIndex, parentListItem)
        notifyItemRangeInserted(wrapperIndex, sizeChanged)
    }

    /**
     * Notify any registered observers that the currently reflected `itemCount`
     * ParentListItems starting at `parentPositionStart` have been newly inserted.
     * The ParentListItems previously located at `parentPositionStart` and beyond
     * can now be found starting at position `parentPositionStart + itemCount`.
     */
    fun notifyParentItemRangeInserted(parentPositionStart: Int, itemCount: Int) {
        val initialWrapperIndex: Int
        if (parentPositionStart < parentItemList.size - itemCount) {
            initialWrapperIndex = getParentWrapperIndex(parentPositionStart)
        } else {
            initialWrapperIndex = mutableItemList.size
        }

        var sizeChanged = 0
        var wrapperIndex = initialWrapperIndex
        var changed: Int
        val parentPositionEnd = parentPositionStart + itemCount
        for (i in parentPositionStart until parentPositionEnd) {
            val parentListItem = parentItemList[i]
            changed = addParentWrapper(wrapperIndex, parentListItem)
            wrapperIndex += changed
            sizeChanged += changed
        }

        notifyItemRangeInserted(initialWrapperIndex, sizeChanged)
    }

    private fun addParentWrapper(wrapperIndex: Int, parentListItem: ParentListItem): Int {
        var sizeChanged = 1
        val parentWrapper = ParentWrapper(parentListItem)
        mutableItemList.add(wrapperIndex, parentWrapper)
        if (parentWrapper.isInitiallyExpanded) {
            parentWrapper.isExpanded = true
            val childItemList = parentWrapper.childItemList
            mutableItemList.addAll(wrapperIndex + sizeChanged, childItemList)
            sizeChanged += childItemList.size
        }
        return sizeChanged
    }

    /**
     * Notify any registered observers that the ParentListItem previously located at `parentPosition`
     * has been removed from the data set. The ParentListItems previously located at and after
     * `parentPosition` may now be found at `oldPosition - 1.
     */
    fun notifyParentItemRemoved(parentPosition: Int) {
        val wrapperIndex = getParentWrapperIndex(parentPosition)
        val sizeChanged = removeParentWrapper(wrapperIndex)

        notifyItemRangeRemoved(wrapperIndex, sizeChanged)
    }

    /**
     * Notify any registered observers that the `itemCount` ParentListItems previously located
     * at `parentPositionStart` have been removed from the data set. The ParentListItems
     * previously located at and after `parentPositionStart + itemCount` may now be found at
     * `oldPosition - itemCount`.
     */
    fun notifyParentItemRangeRemoved(parentPositionStart: Int, itemCount: Int) {
        var sizeChanged = 0
        val wrapperIndex = getParentWrapperIndex(parentPositionStart)
        for (i in 0 until itemCount) {
            sizeChanged += removeParentWrapper(wrapperIndex)
        }

        notifyItemRangeRemoved(wrapperIndex, sizeChanged)
    }

    private fun removeParentWrapper(parentWrapperIndex: Int): Int {
        var sizeChanged = 1
        val parentWrapper = mutableItemList.removeAt(parentWrapperIndex) as ParentWrapper
        if (parentWrapper.isExpanded) {
            val childListSize = parentWrapper.childItemList.size
            for (i in 0 until childListSize) {
                mutableItemList.removeAt(parentWrapperIndex)
                sizeChanged++
            }
        }
        return sizeChanged
    }

    /**
     * Notify any registered observers that the ParentListItem at `parentPosition` has changed.
     * This will also trigger an item changed for children of the ParentList specified.
     */
    fun notifyParentItemChanged(parentPosition: Int) {
        val parentListItem = parentItemList[parentPosition]
        val wrapperIndex = getParentWrapperIndex(parentPosition)
        val sizeChanged = changeParentWrapper(wrapperIndex, parentListItem)

        notifyItemRangeChanged(wrapperIndex, sizeChanged)
    }

    /**
     * Notify any registered observers that the `itemCount` ParentListItems starting
     * at `parentPositionStart` have changed. This will also trigger an item changed
     * for children of the ParentList specified.
     */
    fun notifyParentItemRangeChanged(parentPositionStart: Int, itemCount: Int) {
        var parentPositionStart = parentPositionStart
        val initialWrapperIndex = getParentWrapperIndex(parentPositionStart)

        var wrapperIndex = initialWrapperIndex
        var sizeChanged = 0
        var changed: Int
        var parentListItem: ParentListItem
        for (j in 0 until itemCount) {
            parentListItem = parentItemList[parentPositionStart]
            changed = changeParentWrapper(wrapperIndex, parentListItem)
            sizeChanged += changed
            wrapperIndex += changed
            parentPositionStart++
        }
        notifyItemRangeChanged(initialWrapperIndex, sizeChanged)
    }

    private fun changeParentWrapper(wrapperIndex: Int, parentListItem: ParentListItem): Int {
        val parentWrapper = mutableItemList[wrapperIndex] as ParentWrapper
        parentWrapper.parentListItem = parentListItem
        var sizeChanged = 1
        if (parentWrapper.isExpanded) {
            val childItems = parentWrapper.childItemList
            val childListSize = childItems.size
            var child: Any?
            for (i in 0 until childListSize) {
                child = childItems[i]
                mutableItemList[wrapperIndex + i + 1] = child!!
                sizeChanged++
            }
        }

        return sizeChanged

    }

    /**
     * Notify any registered observers that the ParentListItem and it's child list items reflected at
     * `fromParentPosition` has been moved to `toParentPosition`.
     */
    fun notifyParentItemMoved(fromParentPosition: Int, toParentPosition: Int) {

        val fromWrapperIndex = getParentWrapperIndex(fromParentPosition)
        val fromParentWrapper = mutableItemList[fromWrapperIndex] as ParentWrapper

        // If the parent is collapsed we can take advantage of notifyItemMoved otherwise
        // we are forced to do a "manual" move by removing and then adding the parent + children
        // (no notifyItemRangeMovedAvailable)
        val isCollapsed = !fromParentWrapper.isExpanded
        val isExpandedNoChildren = !isCollapsed && fromParentWrapper.childItemList.size == 0
        if (isCollapsed || isExpandedNoChildren) {
            val toWrapperIndex = getParentWrapperIndex(toParentPosition)
            val toParentWrapper = mutableItemList[toWrapperIndex] as ParentWrapper
            mutableItemList.removeAt(fromWrapperIndex)
            var childOffset = 0
            if (toParentWrapper.isExpanded) {
                childOffset = toParentWrapper.childItemList.size
            }
            mutableItemList.add(toWrapperIndex + childOffset, fromParentWrapper)

            notifyItemMoved(fromWrapperIndex, toWrapperIndex + childOffset)
        } else {
            // Remove the parent and children
            var sizeChanged = 0
            val childListSize = fromParentWrapper.childItemList.size
            for (i in 0 until childListSize + 1) {
                mutableItemList.removeAt(fromWrapperIndex)
                sizeChanged++
            }
            notifyItemRangeRemoved(fromWrapperIndex, sizeChanged)


            // Add the parent and children at new position
            var toWrapperIndex = getParentWrapperIndex(toParentPosition)
            var childOffset = 0
            if (toWrapperIndex != -1) {
                val toParentWrapper = mutableItemList[toWrapperIndex] as ParentWrapper
                if (toParentWrapper.isExpanded) {
                    childOffset = toParentWrapper.childItemList.size
                }
            } else {
                toWrapperIndex = mutableItemList.size
            }
            mutableItemList.add(toWrapperIndex + childOffset, fromParentWrapper)
            val childItemList = fromParentWrapper.childItemList
            sizeChanged = childItemList.size + 1
            mutableItemList.addAll(toWrapperIndex + childOffset + 1, childItemList)
            notifyItemRangeInserted(toWrapperIndex + childOffset, sizeChanged)
        }
    }

    /**
     * Notify any registered observers that the ParentListItem reflected at `parentPosition`
     * has a child list item that has been newly inserted at `childPosition`.
     * The child list item previously at childPosition is now at
     * position `childPosition + 1.
     */
    fun notifyChildItemInserted(parentPosition: Int, childPosition: Int) {
        val parentWrapperIndex = getParentWrapperIndex(parentPosition)
        val parentWrapper = mutableItemList[parentWrapperIndex] as ParentWrapper

        if (parentWrapper.isExpanded) {
            val parentListItem = parentItemList[parentPosition]
            val child = parentListItem.childItemList[childPosition]
            mutableItemList.add(parentWrapperIndex + childPosition + 1, child)
            notifyItemInserted(parentWrapperIndex + childPosition + 1)
        }
    }

    /**
     * Notify any registered observers that the ParentListItem reflected at `parentPosition`
     * has `itemCount` child list items that have been newly inserted at `childPositionStart`.
     * The child list item previously at `childPositionStart` and beyond are now at
     * position `childPositionStart + itemCount`.
     */
    fun notifyChildItemRangeInserted(parentPosition: Int, childPositionStart: Int, itemCount: Int) {
        val parentWrapperIndex = getParentWrapperIndex(parentPosition)
        val parentWrapper = mutableItemList[parentWrapperIndex] as ParentWrapper

        if (parentWrapper.isExpanded) {
            val parentListItem = parentItemList[parentPosition]
            val childList = parentListItem.childItemList
            var child: Any?
            for (i in 0 until itemCount) {
                child = childList[childPositionStart + i]
                mutableItemList.add(parentWrapperIndex + childPositionStart + i + 1, child!!)
            }
            notifyItemRangeInserted(parentWrapperIndex + childPositionStart + 1, itemCount)
        }
    }

    /**
     * Notify any registered observers that the ParentListItem located at `parentPosition`
     * has a child list item that has been removed from the data set, previously located at `childPosition`.
     * The child list item previously located at and after `childPosition` may
     * now be found at `childPosition - 1`.
     */
    fun notifyChildItemRemoved(parentPosition: Int, childPosition: Int) {
        val parentWrapperIndex = getParentWrapperIndex(parentPosition)
        val parentWrapper = mutableItemList[parentWrapperIndex] as ParentWrapper

        if (parentWrapper.isExpanded) {
            mutableItemList.removeAt(parentWrapperIndex + childPosition + 1)
            notifyItemRemoved(parentWrapperIndex + childPosition + 1)
        }
    }

    /**
     * Notify any registered observers that the ParentListItem located at `parentPosition`
     * has `itemCount` child list items that have been removed from the data set, previously
     * located at `childPositionStart` onwards. The child list item previously located at and
     * after `childPositionStart` may now be found at `childPositionStart - itemCount`.
     */
    fun notifyChildItemRangeRemoved(parentPosition: Int, childPositionStart: Int, itemCount: Int) {
        val parentWrapperIndex = getParentWrapperIndex(parentPosition)
        val parentWrapper = mutableItemList[parentWrapperIndex] as ParentWrapper

        if (parentWrapper.isExpanded) {
            for (i in 0 until itemCount) {
                mutableItemList.removeAt(parentWrapperIndex + childPositionStart + 1)
            }
            notifyItemRangeRemoved(parentWrapperIndex + childPositionStart + 1, itemCount)
        }
    }

    /**
     * Notify any registered observers that the ParentListItem at `parentPosition` has
     * a child located at `childPosition` that has changed.
     */
    fun notifyChildItemChanged(parentPosition: Int, childPosition: Int) {
        val parentListItem = parentItemList[parentPosition]
        val parentWrapperIndex = getParentWrapperIndex(parentPosition)
        val parentWrapper = mutableItemList[parentWrapperIndex] as ParentWrapper
        parentWrapper.parentListItem = parentListItem
        if (parentWrapper.isExpanded) {
            val listChildPosition = parentWrapperIndex + childPosition + 1
            val child = parentWrapper.childItemList[childPosition]
            mutableItemList.set(listChildPosition, child)
            notifyItemChanged(listChildPosition)
        }
    }

    /**
     * Notify any registered observers that the ParentListItem at `parentPosition` has
     * `itemCount` child Objects starting at `childPositionStart` that have changed.
     */
    fun notifyChildItemRangeChanged(parentPosition: Int, childPositionStart: Int, itemCount: Int) {
        val parentListItem = parentItemList[parentPosition]
        val parentWrapperIndex = getParentWrapperIndex(parentPosition)
        val parentWrapper = mutableItemList[parentWrapperIndex] as ParentWrapper
        parentWrapper.parentListItem = parentListItem
        if (parentWrapper.isExpanded) {
            val listChildPosition = parentWrapperIndex + childPositionStart + 1
            for (i in 0 until itemCount) {
                val child = parentWrapper.childItemList[childPositionStart + i]
                if (child != null) {
                    mutableItemList.set(listChildPosition + i, child)
                }

            }
            notifyItemRangeChanged(listChildPosition, itemCount)
        }
    }

    /**
     * Notify any registered observers that the child list item contained within the ParentListItem
     * at `parentPosition` has moved from `fromChildPosition` to `toChildPosition`.
     */
    fun notifyChildItemMoved(parentPosition: Int, fromChildPosition: Int, toChildPosition: Int) {
        val parentListItem = parentItemList[parentPosition]
        val parentWrapperIndex = getParentWrapperIndex(parentPosition)
        val parentWrapper = mutableItemList[parentWrapperIndex] as ParentWrapper
        parentWrapper.parentListItem = parentListItem
        if (parentWrapper.isExpanded) {
            val fromChild = mutableItemList.removeAt(parentWrapperIndex + 1 + fromChildPosition)
            mutableItemList.add(parentWrapperIndex + 1 + toChildPosition, fromChild)
            notifyItemMoved(parentWrapperIndex + 1 + fromChildPosition, parentWrapperIndex + 1 + toChildPosition)
        }
    }

    /********************************************************* Region Utility Methods *******************************************************/

    /**
     * Generates a HashMap used to store expanded state for items in the list
     * on configuration change or whenever onResume is called.
     */
    private fun generateExpandedStateMap(): HashMap<Int, Boolean> {
        val parentListItemHashMap = HashMap<Int, Boolean>()
        var childCount = 0

        var listItem: Any?
        var parentWrapper: ParentWrapper
        val listItemCount = mutableItemList.size
        for (i in 0 until listItemCount) {
            if (mutableItemList[i] != null) {
                listItem = getListItem(i)
                if (listItem is ParentWrapper) {
                    parentWrapper = listItem
                    parentListItemHashMap.put(i - childCount, parentWrapper.isExpanded)
                } else {
                    childCount++
                }
            }
        }

        return parentListItemHashMap
    }

    private fun getParentWrapperIndex(parentIndex: Int): Int {
        var parentCount = 0
        val listItemCount = mutableItemList.size
        for (i in 0 until listItemCount) {
            if (mutableItemList[i] is ParentWrapper) {
                parentCount++

                if (parentCount > parentIndex) {
                    return i
                }
            }
        }

        return -1
    }

    private fun getParentWrapper(parentListItem: ParentListItem): ParentWrapper? {
        val listItemCount = mutableItemList.size
        for (i in 0 until listItemCount) {
            val listItem = mutableItemList[i]
            if (listItem is ParentWrapper) {
                if (listItem.parentListItem == parentListItem) {
                    return listItem
                }
            }
        }

        return null
    }

    companion object {

        private val EXPANDED_STATE_MAP = "DuaRecyclerAdapter.ExpandedStateMap"
        private val TYPE_PARENT = 0
        private val TYPE_CHILD = 1
    }
}
