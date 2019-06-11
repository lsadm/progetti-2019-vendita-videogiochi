package com.example.progetto2

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.progetto2.datamodel.Loggato
import com.example.progetto2.datamodel.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_fragment_login.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [fragment_impostazioni.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [fragment_impostazioni.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class fragment_impostazioni : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var auth = FirebaseAuth.getInstance()
    var user: FirebaseUser? = null
    // Chiavi nelle preferenze
    private val PREF_NAME = "Vendita-videogiochi"      // Nome del file
    private val PREF_USERNAME = "Username"
    private val PREF_PASSWORD = "Password"
    private val PREF_AUTOLOGIN = "AutoLogin"

    private lateinit var sharedPref: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        //aggiungo questa riga per aggiungere un riferimento al menu
        setHasOptionsMenu(true)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_login, container, false)
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.getCurrentUser()
        // updateUI(currentUser)
    }

    private fun updateUI(usr: FirebaseUser?) {
        if (usr != null) {
            Toast.makeText(activity, "Utente loggato", Toast.LENGTH_LONG).show()
            Navigation.findNavController(view!!).navigateUp() //torno alla schermata precedente
        }
    }


    fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(MainActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("MainActivity", "signInWithEmail:success")
                    user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("MainActivity", "signInWithEmail:failure", task.exception)
                    Toast.makeText(activity, "Autenticazione fallita", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }

                // ...
            }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val v: View? = activity?.findViewById(R.id.bottomNavigation)
        v?.visibility = View.GONE
        sharedPref = activity!!.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        leggiImpostazioni()
        btnConferma.setOnClickListener {
            if (campivalidi()) {
                salvaImpostazioni()
                signIn(email.text.toString(), password.text.toString())
            } else {
                Toast.makeText(activity, "Email o password troppo breve", Toast.LENGTH_SHORT).show()
            }
        }
        newaccountbtn.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_fragment_login_to_newaccount)
        }
        btnAnnulla.setOnClickListener {
            Navigation.findNavController(it).navigateUp()
        }
    }

    private fun campivalidi() : Boolean{
        if (email.text.toString().length > 0 && password.text.toString().length > 0){
            return true
        }
        else{
            return false
        }

    }

    private fun salvaImpostazioni() {
        val editor = sharedPref.edit()

        val username = email.text.toString()
        editor.putString(PREF_USERNAME, username)

        val password = password.text.toString()
        editor.putString(PREF_PASSWORD,password)

        val autoLogin = chkAutoLogin.isChecked
        editor.putBoolean(PREF_AUTOLOGIN, autoLogin)

        editor.apply()    // Salva le modifiche
    }

    /**
     * Legge le impostazioni e le visualizza
     * nella form
     */
    private fun leggiImpostazioni() {
        val username = sharedPref.getString(PREF_USERNAME, "")
        email.setText(username)

        val pass = sharedPref.getString(PREF_PASSWORD,"")
        password.setText(pass)


        val autoLogin = sharedPref.getBoolean(PREF_AUTOLOGIN, false)
        chkAutoLogin.isChecked = autoLogin
    }


    //questa funzione rende invisibile il menu nel fragment impostazioni
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
    }
}