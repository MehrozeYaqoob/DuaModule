package com.islamicfinder.duamodule.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.islamicfinder.dua.R
import com.islamicfinder.duamodule.*
import com.islamicfinder.duamodule.interfaces.ParentListItem
import com.islamicfinder.duamodule.models.DuaCategory
import com.islamicfinder.duamodule.models.Dua
import com.islamicfinder.duamodule.viewholders.DuaCategoryViewHolder
import com.islamicfinder.duamodule.viewholders.DuaViewHolder

class DuaCategoryAdapter(private var context: Context, parentItemList: List<ParentListItem>) : DuaRecyclerAdapter<DuaCategoryViewHolder, DuaViewHolder>(parentItemList) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    /******************************* Region DuaRecyclerAdapter Methods *******************************/

    override fun onCreateParentViewHolder(parentViewGroup: ViewGroup): DuaCategoryViewHolder {
        val movieCategoryView = inflater.inflate(R.layout.dua_category_view, parentViewGroup, false)
        return DuaCategoryViewHolder(movieCategoryView)
    }

    override fun onCreateChildViewHolder(childViewGroup: ViewGroup): DuaViewHolder {
        val duaView = inflater.inflate(R.layout.dua_view, childViewGroup, false)
        return DuaViewHolder(duaView)
    }

    override fun onBindParentViewHolder(duaCategoryViewHolder: DuaCategoryViewHolder, position: Int, parentListItem: ParentListItem) {
        val duaCategory = parentListItem as DuaCategory
        duaCategoryViewHolder.bind(duaCategory)
    }

    override fun onBindChildViewHolder(duaViewHolder: DuaViewHolder, position: Int, childListItem: Any) {
        val dua = childListItem as Dua
        duaViewHolder.bind(dua)
    }
}
