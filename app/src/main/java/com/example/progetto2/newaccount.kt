package com.example.progetto2

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.progetto2.datamodel.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_newaccount.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [newaccount.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [newaccount.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class newaccount : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var auth : FirebaseAuth
    private val TAG = "MainActivity"
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
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
    //questa funzione rende invisibile il menu nel fragment impostazioni
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
    }

    fun writeNewUser(user : String?, usr : User) {   //al momento non serve a nulla questa funzione
        database.child("users").child(user.toString()).child("Dati").child("Account").setValue(usr)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val v: View? = activity?.findViewById(R.id.bottomNavigation)
        v?.visibility=View.GONE
        btnConferma.setOnClickListener{
            if (verificacampi()) {
                createAccount(email.text.toString(),nome.text.toString(),cellulare.text.toString(), password.text.toString())
            }
            else{
                Toast.makeText(activity,"Email o password troppo breve",Toast.LENGTH_SHORT).show()
            }
        }
        btnAnnulla.setOnClickListener {
            Navigation.findNavController(it).navigateUp()
        }
    }

    fun verificacampi() : Boolean{
        if(email.text.toString().length>0 && password.text.toString().length >0 && nome.text.toString().length > 0 && cellulare.text.toString().length > 0){
            return true
        }
        else{
            return false
        }
    }

    fun createAccount(email : String, nome : String , cellulare : String , password : String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(MainActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser?.uid
                    val usr = User(cellulare,email,nome)
                    writeNewUser(user, usr)
                    Toast.makeText(context,"Utente registrato con successo",Toast.LENGTH_SHORT).show()
                    Navigation.findNavController(view!!).navigateUp()
                 //   updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(context, "Errore nella registrazione",
                        Toast.LENGTH_SHORT).show()
                  //  updateUI(null)
                }

                // ...
            }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment newaccount.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            newaccount().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
