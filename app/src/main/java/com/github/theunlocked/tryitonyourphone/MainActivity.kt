package com.github.theunlocked.tryitonyourphone

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import com.github.theunlocked.tryitonyourphone.database.AppDatabase
import com.github.theunlocked.tryitonyourphone.database.entities.History
import com.github.theunlocked.tryitonyourphone.database.entities.HistoryKind
import com.github.theunlocked.tryitonyourphone.ui.code.CodeViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var database: AppDatabase

    var firstWakeup: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firstWakeup = true

        database = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "db").build()

        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_code, R.id.navigation_history, R.id.navigation_settings))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, _, _ ->
            invalidateOptionsMenu()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        supportActionBar?.subtitle = null
        when (findNavController(R.id.nav_host_fragment).currentDestination!!.id) {
            R.id.navigation_code, R.id.navigation_run_result -> {
                menuInflater.inflate(R.menu.menu_code, menu)
                val model = ViewModelProvider(this).get(CodeViewModel::class.java)
                supportActionBar?.subtitle = model.language.value?.name ?: "No Language Selected"
                if (model.language.value == null) {
                    menu?.findItem(R.id.menu_run_code)?.isEnabled = false
                    menu?.findItem(R.id.menu_hello_world)?.isEnabled = false
                }
            }
            R.id.navigation_language_picker, R.id.navigation_language_picker_filter_dialog ->
                menuInflater.inflate(R.menu.menu_language_picker, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.menu_select_language -> findNavController(R.id.nav_host_fragment)
                .navigate(R.id.action_navigation_code_to_navigation_language_picker)
            R.id.menu_language_filter -> findNavController(R.id.nav_host_fragment)
                .navigate(R.id.action_navigation_language_picker_to_navigation_language_picker_filter_dialog)
            R.id.menu_run_code -> findNavController(R.id.nav_host_fragment)
                .navigate(R.id.action_navigation_code_to_navigation_run_result)
            R.id.menu_clear_code -> {
                saveHistoryState()
                val model = ViewModelProvider(this).get(CodeViewModel::class.java)
                model.code.value = ""
            }
            R.id.menu_hello_world -> {
                saveHistoryState()
                val model = ViewModelProvider(this).get(CodeViewModel::class.java)
                model.language.value?.helloWorld?.apply {
                    model.code.value = code
                    model.argv.value = argv
                    model.options.value = options
                    model.cflags.value = cflags
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        saveHistoryState(HistoryKind.UNSAVED)
    }

    override fun onResume() {
        super.onResume()

        if (firstWakeup) {
            lifecycleScope.launch { database.historyDao().saveUnsaved() }
        }
        else {
            lifecycleScope.launch { database.historyDao().deleteUnsaved() }
        }
        firstWakeup = !firstWakeup
    }

    fun saveHistoryState(saveMode: HistoryKind = HistoryKind.NORMAL) {
        val model = ViewModelProvider(this).get(CodeViewModel::class.java)
        val lang = model.language.value
        val code = model.code.value

        lifecycleScope.launch {
            val latestHistory = database.historyDao().getLatestHistory()

            if (lang != null) {
                val proposedHistory = History(lang.id, code!!, historyKind = saveMode)

                if (code != "" &&
                    (latestHistory == null || !latestHistory.isSimilarTo(proposedHistory)) &&
                    code != lang.helloWorld?.code) {
                    database.historyDao().insert(proposedHistory)
                }
            }
        }
    }
}