package com.example.tvseries

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.tvseries.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    var binding: ActivityMainBinding? = null
    var searchView: SearchView? = null
    lateinit var onShowFilter: (filter: String) -> Unit
    lateinit var onClearFilter: () -> Unit
    lateinit var onVisibleFilter: () -> Boolean

    fun setFilterCallback(callback: (filter: String) -> Unit) {
        onShowFilter = callback
    }

    fun setClearFilterCallback(callback: () -> Unit) {
        onClearFilter = callback
    }

    fun setVisibleFilter(callback: () -> Boolean) {
        onVisibleFilter = callback
    }

    private val navController by lazy {
        supportFragmentManager.findFragmentById(R.id.nav_host_fragment)!!.findNavController()
    }
    private lateinit var controller: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setupToolbar()
    }

    private fun setupToolbar() {
       setSupportActionBar(binding?.mainToolbar)
       setVisibleFilter {
           setVisibleFilter()
       }
       supportActionBar?.apply { setupActionBarWithNavController(navController) }
       binding?.mainToolbar?.setNavigationOnClickListener {
           navController.navigateUp()
       }
       binding?.mainToolbar?.inflateMenu(R.menu.toolbar_menus)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val visible = onVisibleFilter()
        if (visible) {
            menuInflater.inflate(R.menu.toolbar_menus, menu)
            searchView =
                binding?.mainToolbar?.menu?.findItem(R.id.mnShowSearch)?.actionView as SearchView
            searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    onShowFilter(query)
                    return false
                }
                override fun onQueryTextChange(newText: String): Boolean {
                    return false
                }
            })
            searchView?.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewDetachedFromWindow(arg0: View) {
                    onClearFilter()
                }
                override fun onViewAttachedToWindow(arg0: View) {
                }
            })
        }
        return true
    }

    private val listener = NavController.OnDestinationChangedListener { controller, destination, arguments ->
        val visible = onVisibleFilter()
        binding?.mainToolbar?.menu?.get(0).let {
            it?.isVisible = visible
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        controller.handleDeepLink(intent)
    }

    private fun setVisibleFilter(): Boolean {
        return true
    }

}