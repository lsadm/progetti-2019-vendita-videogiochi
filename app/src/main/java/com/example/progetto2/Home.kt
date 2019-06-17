package com.example.progetto2

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_home.*
import android.support.v7.app.AppCompatActivity
import android.view.*
import com.example.progetto2.datamodel.flag
import com.google.firebase.auth.FirebaseAuth

class Home : Fragment() {
    //metodi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setto titolo e colore actionBar
        (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#212121")))
        (activity as AppCompatActivity).supportActionBar?.setTitle("BuyGames")
        //rendo invisibile il bottomNavigation
        val v: View? = activity?.findViewById(R.id.bottomNavigation)
        v?.visibility=View.GONE

        ps4Button.setOnClickListener {
            flag = 1
            Navigation.findNavController(it).navigate(R.id.action_home_to_ps4_list)
        }
        xboxButton.setOnClickListener {
            flag = 2
            Navigation.findNavController(it).navigate(R.id.action_home_to_ps4_list)
        }
        nintendoButton.setOnClickListener {
            flag = 3
            Navigation.findNavController(it).navigate(R.id.action_home_to_ps4_list)
        }
    }

    //visualizzo il menu login o logout a secondo se l'utente è loggato o no
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
        val usr = FirebaseAuth.getInstance().currentUser
        if(usr==null) { //se non è loggato esce login
            inflater?.inflate(R.menu.button_login, menu)
        }
        else { //altrimenti logout
            inflater?.inflate(R.menu.button_logout, menu)
        }
    }
}
