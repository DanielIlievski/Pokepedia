package com.echo.pokepedia.util

import android.view.MenuItem
import android.view.MenuItem.OnActionExpandListener
import android.view.View
import androidx.appcompat.widget.SearchView

inline fun SearchView.onQueryTextChanged(crossinline listener: (String) -> Unit) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            listener(newText.orEmpty())
            return true
        }
    })
}

fun MenuItem.viewHideOnExpandShowOnCollapse(view: View) {
    this.setOnActionExpandListener(object : OnActionExpandListener {
        override fun onMenuItemActionExpand(item: MenuItem): Boolean {
            view.visibility = View.GONE
            return true
        }

        override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
            view.visibility = View.VISIBLE
            return true
        }
    })
}