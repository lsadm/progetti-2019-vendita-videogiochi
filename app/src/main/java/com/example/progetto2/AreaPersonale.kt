package com.example.progetto2

import android.content.ContentValues.TAG
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.progetto2.datamodel.Gioco
import com.example.progetto2.datamodel.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_area_personale.*
import java.lang.Exception

class AreaPersonale : Fragment() {
    //attributi
    private val database = FirebaseDatabase.getInstance().getReference("users")
    private val user = FirebaseAuth.getInstance().currentUser?.uid

    //metodi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) //avvisa che deve essere invocata la funzione onCreateOptionsMenu
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
            menu?.removeItem(R.id.app_bar_search) //rimuove l'action bar dall'area personale
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.requestedOrientation=(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR) //impedisce la rotazione dello schermo
        super.onViewCreated(view, savedInstanceState)
        //setto colore e titolo dell'action bar
        (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#212121")))
        (activity as AppCompatActivity).supportActionBar?.title="Area personale"

        //divide le varie righe della recycleView
        lista_mieigiochi.addItemDecoration(DividerItemDecoration(context,LinearLayoutManager.VERTICAL))

        val v: View? = activity?.findViewById(R.id.bottomNavigation)
        v?.visibility=View.VISIBLE
        val games=ArrayList<Gioco?>()
        val keys = ArrayList<String>()
        val adapter = Adapter(games,requireContext())
        lista_mieigiochi.adapter = adapter
        var cont=0 //contatore di righe inserite nella recycleView

        //setta la textView annunci col valore di cont, inizialmente a 0
        annunci.text=cont.toString()

        //Vari listener (di Firebase) per aggiornare dinamicamente la recycleView
        val childEventListener = object : ChildEventListener {
            //inserimento elemento
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.key!!)
                // A new comment has been added, add it to the displayed list
                val g = dataSnapshot.getValue(Gioco::class.java)
                games.add(g)
                keys.add(dataSnapshot.key.toString()) //aggiungo le varie key in un vettore
                adapter.notifyItemInserted(games.indexOf(g))
                cont++
                try { annunci.text=cont.toString() }catch(e:Exception) {}
            }
            //modifica elemento
            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildChanged: ${dataSnapshot.key}")
                val g = dataSnapshot.getValue(Gioco::class.java)
                val index = keys.indexOf(dataSnapshot.key.toString()) //ottengo l'indice del gioco aggiornato
                games.set(index,g)
                adapter.notifyDataSetChanged()
            }
            //rimozione elemento
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.key!!)
                val g = dataSnapshot.getValue(Gioco::class.java)
                val index = games.indexOf(g)
                games.remove(g)
                adapter.notifyItemRemoved(index)
            }
            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.key!!)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException())
                Toast.makeText(context, "Failed to load comments.",
                    Toast.LENGTH_SHORT).show()
            }
        }

        // Imposto il layout manager a lineare per avere scrolling in una direzione
        lista_mieigiochi.layoutManager = LinearLayoutManager(activity)

        if(user!=null) {
            //scarico dal database le informazioni del singolo utente e le aggiungo in una lista
            dataUser()
            //chiamata al listener per caricare e modificare la recycleView
            database.child(user.toString()).child("mygames")
                .addChildEventListener(childEventListener) //il database da cui chiamo il listener fa variare il sottonodo del database che vado a leggere

        }
        //l'utente non è loggato quindi viene reindirizzato al login
        else {
            Toast.makeText(activity, "Non sei loggato", Toast.LENGTH_SHORT).show()
            Navigation.findNavController(view).navigateUp()
            Navigation.findNavController(view).navigate(R.id.action_home_to_fragment_impostazioni)
        }
    }

    //quando lascio il fragment abilito la rotazione
    override fun onDestroyView() {
        super.onDestroyView()
        activity?.requestedOrientation=(ActivityInfo.SCREEN_ORIENTATION_SENSOR)
    }

    //scarica i dati dell'utente dal database
    private fun dataUser() {
        val myRef = FirebaseDatabase.getInstance().getReference("users").child(user.toString()).child("Dati")
        fun loadList(callback: (list: List<User>) -> Unit) {
            myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(snapshotError: DatabaseError) {}
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list: MutableList<User> = mutableListOf()
                    val children = snapshot!!.children
                    children.forEach {
                        list.add(it.getValue(User::class.java)!!)
                    }
                    callback(list)
                }
            })
        }
        //carico le textView usando gli elementi della lista
        loadList {
            try {
                email.text = it[0].email.toString()
                cell.text = it[0].cell.toString()
            }catch (e: Exception) {}
        }
    }
}
