package com.islamicfinder.dua.activities

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.TextView
import android.widget.Toast
import com.islamicfinder.dua.R
import com.islamicfinder.dua.R.id.toolbar_dua
import com.islamicfinder.dua.utils.ItemSeperatorDecorator
import com.islamicfinder.duamodule.adapters.DuaCategoryAdapter
import com.islamicfinder.duamodule.adapters.DuaRecyclerAdapter
import com.islamicfinder.duamodule.models.Dua
import com.islamicfinder.duamodule.models.DuaCategory
import java.util.*

class DuaActivity : AppCompatActivity(), SearchView.OnQueryTextListener, View.OnFocusChangeListener {

    /******************************* Region Properties *******************************/

    private var dua: List<Dua>? = null
    private lateinit var searchView : SearchView
    private val HEADER_TITLE : String = "Duas and Dhikr"
    
    private var duaCategoryAdapter: DuaCategoryAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var tvToolbarTitle : TextView? = null

    //*********************************** Activity LifeCycle Methods Start ***********************************

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dua_activity)

        // setting toolbar and removing display title
        val toolbar_dua : Toolbar  = findViewById(toolbar_dua)
        setSupportActionBar(toolbar_dua)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        tvToolbarTitle  = findViewById(R.id.toolbar_title)
        tvToolbarTitle?.text = HEADER_TITLE
    }

    override fun onResume() {
        super.onResume()
        initialize()
    }

    override fun onPause() {
        super.onPause()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        val item = menu?.findItem(R.id.action_search)

        searchView = item?.actionView as SearchView
        searchView.queryHint = "Search"
        searchView.setOnQueryTextListener(this)
        searchView.setOnQueryTextFocusChangeListener(this)

        var searchAutoComplete: SearchView.SearchAutoComplete? = searchView . findViewById < SearchView . SearchAutoComplete >(android.support.v7.appcompat.R.id.search_src_text)
        searchAutoComplete?.setHintTextColor(Color.WHITE)
        searchAutoComplete?.setTextColor(Color.WHITE)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        duaCategoryAdapter!!.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        //duaCategoryAdapter!!.onRestoreInstanceState(savedInstanceState)
    }

    //*********************************** Activity LifeCycle Methods End ***********************************

    /*Called from onResume*/
    private fun initialize()
    {
        // Initializing Presenter
        val dua_one = Dua("Dua 1")
        val dua_two = Dua("Dua 2")
        val dua_three = Dua("Dua 3")
        val dua_four = Dua("Dua 4 ")
        val dua_five = Dua("Dua 5 ")
        val dua_six = Dua("Dua 6")
        val dua_seven = Dua("Dua 7")
        val dua_eight = Dua("Dua 8")
        val dua_nine = Dua("Dua 9")
        val dua_ten = Dua("Dua 10")
        val dua_eleven = Dua("Dua 11")
        val dua_tweleve = Dua("Dua 12")

        val dua_category_one = DuaCategory("Category 1", Arrays.asList(dua_one, dua_two, dua_three, dua_four))
        val dua_category_two = DuaCategory("Category 2", Arrays.asList(dua_five, dua_six, dua_seven, dua_eight))
        val dua_category_three = DuaCategory("Category 3", Arrays.asList(dua_nine, dua_ten, dua_eleven, dua_tweleve))
        val dua_category_four = DuaCategory("Category 4", Arrays.asList(dua_one, dua_five, dua_nine, dua_tweleve))

        val duaCategories = Arrays.asList(dua_category_one, dua_category_two, dua_category_three, dua_category_four,
                dua_category_one, dua_category_two, dua_category_three, dua_category_four,
                dua_category_one, dua_category_two, dua_category_three, dua_category_four,
                dua_category_one, dua_category_two, dua_category_three, dua_category_four)

        recyclerView = findViewById(R.id.recyclerview)
        recyclerView?.addItemDecoration( ItemSeperatorDecorator(this))
        duaCategoryAdapter = DuaCategoryAdapter(this, duaCategories)
        duaCategoryAdapter!!.setExpandCollapseListener(object : DuaRecyclerAdapter.ExpandCollapseListener {
           override fun onListItemExpanded(position: Int) {
                val expandedDuaCategory = duaCategories[position]

                val toastMsg = resources.getString(R.string.expanded, expandedDuaCategory.name)
                Toast.makeText(this@DuaActivity,
                        toastMsg,
                        Toast.LENGTH_SHORT)
                        .show()
            }

           override fun onListItemCollapsed(position: Int) {
                val collapsedDuaCategory = duaCategories[position]

                val toastMsg = resources.getString(R.string.collapsed, collapsedDuaCategory.name)
                Toast.makeText(this@DuaActivity,
                        toastMsg,
                        Toast.LENGTH_SHORT)
                        .show()
            }
        })

        recyclerView!!.adapter = duaCategoryAdapter
        recyclerView!!.layoutManager = LinearLayoutManager(this)
    }

    //*********************************** SearchView Related Methods Start ***********************************

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (hasFocus) {
            tvToolbarTitle?.visibility = INVISIBLE
        } else {
            tvToolbarTitle?.visibility = VISIBLE
        }
    }

    //*********************************** SearchView Related Methods End ***********************************


}
