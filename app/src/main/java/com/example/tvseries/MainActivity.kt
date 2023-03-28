package com.example.tvseries

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.tvseries.databinding.ActivityMainBinding
import com.example.tvseries.viewmodel.VMShow

class MainActivity : AppCompatActivity() {

    var binding: ActivityMainBinding? = null
    private val navController by lazy {
        supportFragmentManager.findFragmentById(R.id.nav_host_fragment)!!.findNavController()
    }
    private lateinit var controller: NavController
    private val viewModel: VMShow by lazy {
        ViewModelProvider(this)[VMShow::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setupToolbar()
    }


    private fun setupToolbar() {
       setSupportActionBar(binding?.mainToolbar)
       supportActionBar?.apply { setupActionBarWithNavController(navController) }
       binding?.mainToolbar?.setNavigationOnClickListener {
           navController.navigateUp()
       }
       binding?.mainToolbar?.inflateMenu(R.menu.toolbar_menus)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menus, menu)
        binding?.mainToolbar?.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.mnShowSearch -> {
                }
            }
            true
        }

        val searchView = binding?.mainToolbar?.menu?.findItem(R.id.mnShowSearch)?.actionView as SearchView


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.getShowByFilter(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

        })

        searchView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewDetachedFromWindow(arg0: View) {
            }

            override fun onViewAttachedToWindow(arg0: View) {
            }
        })
        return true
    }

    private val listener = NavController.OnDestinationChangedListener { controller, destination, arguments ->
        binding?.mainToolbar?.menu?.get(0)?.isVisible = (destination.id == R.id.UIShows)
    }

    override fun onResume() {
        super.onResume()
        controller = Navigation.findNavController(this, R.id.nav_host_fragment)
        controller.addOnDestinationChangedListener(listener)
    }

    override fun onPause() {
        controller.removeOnDestinationChangedListener(listener)
        super.onPause()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        controller.handleDeepLink(intent)
    }

}