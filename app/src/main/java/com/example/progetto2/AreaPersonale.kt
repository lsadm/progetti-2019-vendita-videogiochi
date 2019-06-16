package com.example.progetto2

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import android.widget.Toast
import com.example.progetto2.datamodel.Gioco
import com.example.progetto2.datamodel.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_area_personale.*
import kotlinx.android.synthetic.main.fragment_dettaglio_gioco.*
import kotlinx.android.synthetic.main.fragment_ps4_list.*
import java.lang.Exception


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AreaPersonale.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [AreaPersonale.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class AreaPersonale : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var database : DatabaseReference
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        database = FirebaseDatabase.getInstance().getReference("users")
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_area_personale, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
            menu?.removeItem(R.id.app_bar_search)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#212121")))
        (activity as AppCompatActivity).supportActionBar?.setTitle("Area personale")

        lista_mieigiochi.addItemDecoration(DividerItemDecoration(context,LinearLayoutManager.VERTICAL))

        val v: View? = activity?.findViewById(R.id.bottomNavigation)
        v?.visibility=View.VISIBLE
        val games=ArrayList<Gioco?>()
        val keys = ArrayList<String>()
        val adapter = Adapter(games,requireContext())
        lista_mieigiochi.adapter = adapter
        var cont=0

        val myRef = FirebaseDatabase.getInstance().getReference("users").child(user).child("Dati")
        fun loadList(callback: (list: List<User>) -> Unit) {
            myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(snapshotError: DatabaseError) {
                    TODO("not implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val list : MutableList<User> = mutableListOf()
                    val children = snapshot!!.children
                    children.forEach {
                        list.add(it.getValue(User::class.java)!!)
                    }
                    callback(list)
                }
            })
        }
        loadList {
            email.setText(it.get(0).email)
            cell.setText(it.get(0).cell)
        }

        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.key!!)
                // A new comment has been added, add it to the displayed list
                val g = dataSnapshot.getValue(Gioco::class.java)
                games.add(g)
                keys.add(dataSnapshot.key.toString()) //aggiungo le varie key in un vettore
                adapter.notifyItemInserted(games.indexOf(g))
                cont++
                annunci.setText(cont.toString())
            }
            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildChanged: ${dataSnapshot.key}")
                val g = dataSnapshot.getValue(Gioco::class.java)
                val index = keys.indexOf(dataSnapshot.key.toString()) //ottengo l'indice del gioco aggiornato
                games.set(index,g)
                adapter.notifyDataSetChanged()
            }
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.key!!)
                val g = dataSnapshot.getValue(Gioco::class.java)
                val index = games.indexOf(g)
                games.remove(g)
                adapter.notifyItemRemoved(index)
            }
            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.key!!)

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                val movedComment = dataSnapshot.getValue(Gioco::class.java)
                val commentKey = dataSnapshot.key
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException())
                Toast.makeText(context, "Failed to load comments.",
                    Toast.LENGTH_SHORT).show()
            }
        }
        try {
            database.child(user).child("mygames")
                .addChildEventListener(childEventListener)    //il database da cui chiamo il listener fa variare il sottonodo del database che vado a leggere
        }
        catch (e : Exception){
            Toast.makeText(activity,"Non sei loggato", Toast.LENGTH_SHORT).show()
        }

        // Imposto il layout manager a lineare per avere scrolling in una direzione
        lista_mieigiochi.layoutManager = LinearLayoutManager(activity)

    }
}
