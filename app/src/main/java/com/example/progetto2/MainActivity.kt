package com.example.progetto2

import android.app.DownloadManager
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Checkable
import android.widget.SearchView
import android.widget.Toast
import androidx.navigation.NavHost
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.progetto2.datamodel.Loggato
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_fragment_login.*

class MainActivity : AppCompatActivity() {
    private val PREF_NAME = "Vendita-videogiochi"      // Nome del file
    private val PREF_USERNAME = "Username"
    private val PREF_PASSWORD = "Password"
    private val PREF_AUTOLOGIN = "AutoLogin"

    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Setup Navigation controller cono bottoNavigation
        auth = FirebaseAuth.getInstance()
        trylogin() //verifico se è inserita la spunta di autologin e nel caso lo effettuo
        bottomNavigation.setupWithNavController(Navigation.findNavController(this, R.id.navHost))
    }

    /**
     * Invocata quando occorre creare un menu
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Imposta il menu dal file di risorse
        if (Loggato().usr == null) { //se non è loggato esce login
            menuInflater.inflate(R.menu.button_login, menu)
            //menuInflater.inflate(R.menu.search, menu)
        } else { //altrimenti logout
            menuInflater.inflate(R.menu.button_logout, menu)
            // menuInflater.inflate(R.menu.search, menu)
        }
        // Inflate the options menu from XML
        val inflater = menuInflater
        inflater.inflate(R.menu.search, menu)
        // Get the SearchView and set the searchable configuration
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.app_bar_search).actionView as SearchView).apply {
            // Assumes current activity is the searchable activity
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
        }
        return true
    }

    /**
     * Processa le voci del menu
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.fragment_login -> Navigation.findNavController(
                this,
                R.id.navHost
            ).navigate(R.id.action_home_to_fragment_impostazioni)
            R.id.button_logout -> {
                auth.signOut()
                invalidateOptionsMenu() //dopo il logout invalido il menu, così viene richiamato onCreateOptionsMenu
                Toast.makeText(this, "Logout effettuato", Toast.LENGTH_SHORT).show()
            }
            else -> return false    // Voce non processata
        }

        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        auth.signOut()
    }

    private fun trylogin() {
        val sharedPref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val pass = sharedPref.getString(PREF_PASSWORD, "")
        val username = sharedPref.getString(PREF_USERNAME, "")
        val autoLogin = sharedPref.getBoolean(PREF_AUTOLOGIN, false)
        if (autoLogin && username != null && pass != null) {
            signIn(username, pass)
        }
    }

    fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(MainActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("MainActivity", "signInWithEmail:success")
                    Toast.makeText(baseContext, "Utente loggato", Toast.LENGTH_SHORT).show()
                    invalidateOptionsMenu() //dopo il logout invalido il menu, così viene richiamato onCreateOptionsMenu

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("MainActivity", "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Autenticazione fallita", Toast.LENGTH_SHORT).show()
                }

                // ...
            }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // Verify the action and get the query
        if (Intent.ACTION_SEARCH == intent!!.action) {
            intent!!.getStringExtra(SearchManager.QUERY)?.also { query ->
                val NavHost  = supportFragmentManager.fragments.get(0) as NavHostFragment
                val fragment = NavHost.childFragmentManager.fragments.get(0) as ps4_list
                fragment.domyquery(query)
            }
        }
    }
}
