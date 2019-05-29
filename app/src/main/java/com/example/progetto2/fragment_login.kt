package com.example.progetto2

import android.content.Context
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
    private var listener: OnFragmentInteractionListener? = null
    var auth = FirebaseAuth.getInstance()
    var user : FirebaseUser? = null



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

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.getCurrentUser()
       // updateUI(currentUser)
    }
    fun updateUI(usr : FirebaseUser?){
        if (usr!= null){
            Toast.makeText(activity,"Utente loggato", Toast.LENGTH_LONG).show()
        }
    }


    fun signIn(email : String, password: String){
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
                    Toast.makeText(activity,"Autenticazione fallita", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }

                // ...
            }
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val v: View? = activity?.findViewById(R.id.bottomNavigation)
        v?.visibility=View.GONE
        btnConferma.setOnClickListener{
            if (email.text.toString().length>0 && password.text.toString().length >0) {
                signIn(email.text.toString(), password.text.toString())
            }
            else {
                Toast.makeText(activity,"Email o password troppo breve",Toast.LENGTH_SHORT).show()
            }
        }
        newaccountbtn.setOnClickListener{
            Navigation.findNavController(it).navigate(R.id.action_fragment_login_to_newaccount)
        }
        btnAnnulla.setOnClickListener{
            Navigation.findNavController(it).navigateUp()
        }
    }

    //questa funzione rende invisibile il menu nel fragment impostazioni
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
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
         * @return A new instance of fragment fragment_impostazioni.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fragment_impostazioni().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
