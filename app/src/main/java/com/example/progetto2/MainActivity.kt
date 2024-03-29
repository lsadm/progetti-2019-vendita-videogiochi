package com.example.progetto2

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    //attributi
    private val PREF_NAME = "Vendita-videogiochi"      // Nome del file
    private val PREF_USERNAME = "Username"
    private val PREF_PASSWORD = "Password"
    private val PREF_AUTOLOGIN = "AutoLogin"
    private val auth = FirebaseAuth.getInstance()
    private var usr : FirebaseUser? = null

    //metodi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        trylogin() //verifico se è inserita la spunta di autologin e nel caso lo effettuo
        // Setup Navigation controller cono bottomNavigation
        bottomNavigation.setupWithNavController(Navigation.findNavController(this, R.id.navHost))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        usr = FirebaseAuth.getInstance().currentUser
        if (usr == null) { //se non è loggato compare login
            menuInflater.inflate(R.menu.button_login, menu)
        } else { //altrimenti logout
            menuInflater.inflate(R.menu.button_logout, menu)
        }
        // Inflate the options menu from XML
        val inflater = menuInflater
        inflater.inflate(R.menu.search, menu)
        // Get the SearchView and set the searchable configuration
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.app_bar_search).actionView as SearchView).apply {
            // Assumes current activity is the searchable activity
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setIconifiedByDefault(false) // Do not iconify the widget; expand it by default
            isQueryRefinementEnabled = true
        }
        val search_menuItem = menu.findItem(R.id.app_bar_search)
        search_menuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                // Do whatever you need
                menu?.removeItem(R.id.button_logout)
                menu?.removeItem(R.id.button_login)
                return true // KEEP IT TO TRUE OR IT DOESN'T OPEN !!
            }
            override fun onMenuItemActionCollapse(item: MenuItem): Boolean { //quando clicco su indietro viene ricreato il fragment list
                // Do whatever you need
                val NavHost  = supportFragmentManager.fragments.get(0) as NavHostFragment
                val fragment = NavHost.childFragmentManager.fragments.get(0) as ps4_list
                invalidateOptionsMenu()
                supportFragmentManager.beginTransaction().detach(fragment).attach(fragment).commit()
                return true // OR FALSE IF YOU DIDN'T WANT IT TO CLOSE!
            }
        })
        return true
    }

    //processa le voci del menu
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.button_login -> Navigation.findNavController(this, R.id.navHost)
                .navigate(R.id.action_home_to_fragment_impostazioni) //vado nel fragment login
            R.id.button_logout -> {
                auth.signOut() //effettua il logout
                invalidateOptionsMenu() //dopo il logout invalido il menu, così viene richiamato onCreateOptionsMenu (per far comparire login)
                Toast.makeText(this, "Logout effettuato", Toast.LENGTH_SHORT).show()
            }
            else -> return false    // Voce non processata
        }
        return true
    }

    //quando viene chiusa l'app viene eseguito il logout
    override fun onDestroy() {
        super.onDestroy()
        auth.signOut()
    }

    //funzione che effettua il login automatico
    private fun trylogin() {
        val sharedPref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val pass = sharedPref.getString(PREF_PASSWORD, "")
        val username = sharedPref.getString(PREF_USERNAME, "")
        val autoLogin = sharedPref.getBoolean(PREF_AUTOLOGIN, false)
        if (autoLogin && username != null && pass != null) {
            signIn(username, pass)
        }
    }

    //effettua il login
    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(MainActivity()) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("MainActivity", "signInWithEmail:success")
                Toast.makeText(baseContext, "Utente loggato", Toast.LENGTH_SHORT).show()
                invalidateOptionsMenu() //dopo il logout invalido il menu, così viene richiamato onCreateOptionsMenu (per visualizzare logout)
            } else {
                // If sign in fails, display a message to the user.
                Log.w("MainActivity", "signInWithEmail:failure", task.exception)
                Toast.makeText(baseContext, "Autenticazione fallita", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //funzione invocata per effettuare la ricerca (processa le query)
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
