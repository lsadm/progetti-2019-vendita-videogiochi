package com.example.progetto2

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils.indexOf
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.SearchView
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.progetto2.datamodel.Gioco
import com.example.progetto2.datamodel.flag
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_ps4_list.*
import android.os.Build




// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ps4_list.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ps4_list.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ps4_list : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var database_ps4: DatabaseReference   //mi serve per leggermi i sottonodi del database
    private lateinit var database_xbox: DatabaseReference   //mi serve per leggermi i sottonodi del database
    private lateinit var database_nintendo: DatabaseReference   //mi serve per leggermi i sottonodi del database
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        database = FirebaseDatabase.getInstance().reference
        database_ps4 = FirebaseDatabase.getInstance().getReference("Giochi").child("Ps4")
        database_xbox = FirebaseDatabase.getInstance().getReference("Giochi").child("Xbox")
        database_nintendo = FirebaseDatabase.getInstance().getReference("Giochi").child("Nintendo")
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ps4_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //colora l'actionbar con lo stesso colore della piattaforma scelta
        if(flag==1) (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#004097")))
        if(flag==2) (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#107C10")))
        if(flag==3) (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#DD0001")))
        (activity as AppCompatActivity).supportActionBar?.setTitle("Games")
        //separatori tra righe
        lista_giochi.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )

        val v: View? = activity?.findViewById(R.id.bottomNavigation)
        v?.visibility = View.VISIBLE
        val games = ArrayList<Gioco?>()
        val keys = ArrayList<String>()
        val adapter = Adapter(games, requireContext())
        lista_giochi.adapter = adapter

        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.key!!)
                // A new comment has been added, add it to the displayed list
                val g = dataSnapshot.getValue(Gioco::class.java)
                games.add(g)
                keys.add(dataSnapshot.key.toString()) //aggiungo le varie key in un vettore
                adapter.notifyItemInserted(games.indexOf(g))

                // ...
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildChanged: ${dataSnapshot.key}")
                val g = dataSnapshot.getValue(Gioco::class.java)
                val index = keys.indexOf(dataSnapshot.key.toString()) //ottengo l'indice del gioco aggiornato
                games.set(index, g)
                adapter.notifyDataSetChanged()

                // ...
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.key!!)
                val g = dataSnapshot.getValue(Gioco::class.java)
                val index = games.indexOf(g)
                games.remove(g)
                adapter.notifyItemRemoved(index)
                // ...
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.key!!)

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                val movedComment = dataSnapshot.getValue(Gioco::class.java)
                val commentKey = dataSnapshot.key

                // ...
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException())
                Toast.makeText(
                    context, "Failed to load comments.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        if (flag == 1) {
            database_ps4.addChildEventListener(childEventListener)    //il database da cui chiamo il listener fa variare il sottonodo del database che vado a leggere
        }
        if (flag == 2) {
            database_xbox.addChildEventListener(childEventListener)    //il database da cui chiamo il listener fa variare il sottonodo del database che vado a leggere
        }
        if (flag == 3) {
            database_nintendo.addChildEventListener(childEventListener)    //il database da cui chiamo il listener fa variare il sottonodo del database che vado a leggere
        }
        // Imposto il layout manager a lineare per avere scrolling in una direzione
        lista_giochi.layoutManager = LinearLayoutManager(activity)

        floatingActionButton.setOnClickListener {
            if (auth.currentUser != null) {
                Navigation.findNavController(it).navigate(R.id.action_ps4_list_to_fragment_inserimento)
            } else {
                Navigation.findNavController(it).navigate(R.id.action_home_to_fragment_impostazioni)
            }
        }
    }

    fun domyquery(query: String) {
        val games = ArrayList<Gioco?>()
        val keys = ArrayList<String>()
        val adapter = Adapter(games, requireContext())
        lista_giochi.adapter = adapter

        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.key!!)
                // A new comment has been added, add it to the displayed list
                val g = dataSnapshot.getValue(Gioco::class.java)
                games.add(g)
                keys.add(dataSnapshot.key.toString()) //aggiungo le varie key in un vettore
                adapter.notifyItemInserted(games.indexOf(g))

                // ...
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildChanged: ${dataSnapshot.key}")
                val g = dataSnapshot.getValue(Gioco::class.java)
                val index = keys.indexOf(dataSnapshot.key.toString()) //ottengo l'indice del gioco aggiornato
                games.set(index, g)
                adapter.notifyDataSetChanged()

                // ...
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.key!!)
                val g = dataSnapshot.getValue(Gioco::class.java)
                val index = games.indexOf(g)
                games.remove(g)
                adapter.notifyItemRemoved(index)
                // ...
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.key!!)

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                val movedComment = dataSnapshot.getValue(Gioco::class.java)
                val commentKey = dataSnapshot.key

                // ...
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException())
                Toast.makeText(
                    context, "Failed to load comments.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        if (flag == 1) {
            database_ps4.orderByChild("luogo").startAt(query).endAt(query+"\uf8ff").addChildEventListener(childEventListener)
            database_ps4.orderByChild("nome").startAt(query).endAt(query+"\uf8ff").addChildEventListener(childEventListener)   //il database da cui chiamo il listener fa variare il sottonodo del database che vado a leggere
        }
        if (flag == 2) {
            database_xbox.orderByChild("luogo").startAt(query).endAt(query+"\uf8ff").addChildEventListener(childEventListener)
            database_xbox.orderByChild("nome").startAt(query).endAt(query+"\uf8ff").addChildEventListener(childEventListener) //il database da cui chiamo il listener fa variare il sottonodo del database che vado a leggere
        }
        if (flag == 3) {
            database_nintendo.orderByChild("luogo").startAt(query).endAt(query+"\uf8ff").addChildEventListener(childEventListener)
            database_nintendo.orderByChild("nome").startAt(query).endAt(query+"\uf8ff").addChildEventListener(childEventListener)    //il database da cui chiamo il listener fa variare il sottonodo del database che vado a leggere
        }
    }

}
