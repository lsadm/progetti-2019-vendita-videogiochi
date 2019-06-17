package com.example.progetto2

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.progetto2.datamodel.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_newaccount.*

class newaccount : Fragment() {
    //attributi
    private val auth = FirebaseAuth.getInstance()
    private val TAG = "MainActivity"
    private val database = FirebaseDatabase.getInstance().reference

    //metodi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setto il titolo dell'actionBar
        (activity as AppCompatActivity).supportActionBar?.setTitle("New Account")
        //aggiungo questa riga per aggiungere un riferimento al menu
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_newaccount, container, false)
    }

    //Rende invisibile
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setto titolo e colore dell'actionBar
        (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#212121")))
        (activity as AppCompatActivity).supportActionBar?.setTitle("Creazione account")
        //rendo invisibile il bottomNavigation
        val v: View? = activity?.findViewById(R.id.bottomNavigation)
        v?.visibility=View.GONE
        //operazione da effettuare quando si clicca su conferma
        btnConferma.setOnClickListener{
            if (verificacampi()) {
                createAccount(email.text.toString(),nome.text.toString(),cellulare.text.toString(), password.text.toString())
            }
            else{
                Toast.makeText(activity,"Email o password troppo breve",Toast.LENGTH_SHORT).show()
            }
        }
    }

    //memorizza il nuovo utente sul database
    private fun writeNewUser(user : String?, usr : User) {
        database.child("users").child(user.toString()).child("Dati").child("Account").setValue(usr)
    }

    //verifica se i campi sono stati riempiti correttamente
    private fun verificacampi() : Boolean{
        return email.text.toString().isNotEmpty() && password.text.toString().isNotEmpty() && nome.text.toString().isNotEmpty() && cellulare.text.toString().isNotEmpty()
    }

    //processa la creazione di un nuovo account
    private fun createAccount(email : String, nome : String , cellulare : String , password : String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(MainActivity()) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "createUserWithEmail:success")
                val user = auth.currentUser?.uid
                val usr = User(cellulare, email, nome)
                writeNewUser(user, usr) //memorizza sul database
                Toast.makeText(context, "Utente registrato con successo", Toast.LENGTH_SHORT).show()
                //torno direttamente alla lista giochi e non al login
                Navigation.findNavController(view!!).navigateUp()
                Navigation.findNavController(view!!).navigateUp()
            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                Toast.makeText(context, "Errore nella registrazione", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
