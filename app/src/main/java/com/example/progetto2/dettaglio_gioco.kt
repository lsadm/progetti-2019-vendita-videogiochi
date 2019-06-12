package com.example.progetto2


import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.progetto2.datamodel.Gioco
import com.example.progetto2.datamodel.Loggato
import com.example.progetto2.datamodel.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_dettaglio_gioco.*


class dettaglio_gioco : Fragment() {
    private lateinit var auth : FirebaseAuth
    private lateinit var id : String
    val storageRef = FirebaseStorage.getInstance().getReference()
    val nodoRef = FirebaseDatabase.getInstance().reference
    var gioco : Gioco? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        id = auth.currentUser!!.uid
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dettaglio_gioco, container, false)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.Cestino  -> {
                val builder = AlertDialog.Builder(activity as AppCompatActivity)
                builder.setTitle(R.string.AlertMessage)
                builder.setNegativeButton(R.string.NegativeButton,DialogInterface.OnClickListener { _, which ->
                })
                builder.setPositiveButton(R.string.PositiveButton,DialogInterface.OnClickListener { _, which ->
                    nodoRef.child("Giochi").child(gioco!!.console.toString()).child(gioco!!.key.toString()).removeValue()
                    nodoRef.child("users").child(auth.currentUser!!.uid).child(gioco!!.nome.toString()).removeValue()
                    for (i in 0 .. 2) {
                        storageRef.child(gioco?.console.toString() + "/").child(gioco?.key.toString() + "/")
                            .child("picture" + i.toString()).delete()
                }
                   Navigation.findNavController(view!!).navigateUp()
                })
                builder.show()
            }
            else -> return false
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?){
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
        inflater?.inflate(R.menu.menu_modifica, menu)
        if(Loggato().usr==null) { //se non è loggato esce login
            inflater?.inflate(R.menu.button_login, menu)
        }
        else { //altrimenti logout
            inflater?.inflate(R.menu.button_logout, menu)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Estraggo il parametro (gioco) dal bundle ed eventualmente lo visualizzo
        arguments?.let {
            gioco = it.getParcelable("gioco")   //TODO: Il nome dovrebbe essere in un unico punto!!
            gioco?.let {
                val imagRef = storageRef.child(gioco?.console.toString() + "/").child(gioco?.key.toString() + "/")
                if (gioco?.id == id) {
                    setHasOptionsMenu(true)
                }
                nome_dettaglio.text = it.nome
                luogo_dettaglio.text = it.luogo
                prezzo_dettaglio.text = String.format("%d", it.prezzo)+"€"

                val childEventListener = object : ChildEventListener {
                    override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                        Log.d(TAG, "onChildAdded:" + dataSnapshot.key!!)

                        val usr = dataSnapshot.getValue(User::class.java)
                        utente_dettaglio.text = usr?.email
                        cellulare_dettaglio.text = usr?.cell
                        // ...
                    }
                    override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                        Log.d(TAG, "onChildChanged: ${dataSnapshot.key}")

                        // A comment has changed, use the key to determine if we are displaying this
                        // comment and if so displayed the changed comment.
                        val newComment = dataSnapshot.getValue(Gioco::class.java)
                        val commentKey = dataSnapshot.key

                        // ...
                    }

                    override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                        Log.d(TAG, "onChildRemoved:" + dataSnapshot.key!!)

                        // A comment has changed, use the key to determine if we are displaying this
                        // comment and if so remove it.
                        val commentKey = dataSnapshot.key

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
                        Toast.makeText(context, "Failed to load comments.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
                nodoRef.child("users").child(gioco?.id.toString()).child("Dati").addChildEventListener(childEventListener)
                val picture = ArrayList<ImageView>()
                picture.add(picture0)
                picture.add(picture1)
                picture.add(picture2)
                for(i in 0..2) {
                    imagRef.child("picture"+i.toString()).getBytes(Long.MAX_VALUE).addOnSuccessListener {
                        // Use the bytes to display the image
                        val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                        picture.get(i).setImageBitmap(bitmap)
                    }.addOnFailureListener {
                        // Handle any errors
                    }
                }
            }
        }
    }
}
