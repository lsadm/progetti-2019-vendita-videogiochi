package com.example.progetto2

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Setup Navigation controller cono bottoNavigation
        bottomNavigation.setupWithNavController(Navigation.findNavController(this, R.id.navHost))
    }
    /**
     * Invocata quando occorre creare un menu
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        // Imposta il menu dal file di risorse
        menuInflater.inflate(R.menu.button_login, menu)

        return true
    }

    /**
     * Processa le voci del venu
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item?.itemId) {
            R.id.fragment_login -> Navigation.findNavController(this, R.id.navHost).navigate(R.id.action_home_to_fragment_impostazioni)
            else -> return false    // Voce non processata
        }

        return true
    }

}
