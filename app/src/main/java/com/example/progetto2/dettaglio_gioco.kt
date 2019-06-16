package com.example.progetto2


import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
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
import com.google.firebase.FirebaseError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_dettaglio_gioco.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.riga.*


class dettaglio_gioco : Fragment() {
    private lateinit var auth : FirebaseAuth
    private lateinit var id : String
    val storageRef = FirebaseStorage.getInstance().getReference()
    val nodoRef = FirebaseDatabase.getInstance().reference
    var gioco : Gioco? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        id = auth.currentUser?.uid.toString()
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
                    nodoRef.child("users").child(auth.currentUser!!.uid).child("mygames").child(gioco!!.key.toString()).removeValue()
                    for (i in 0 .. 2) {
                        storageRef.child(gioco?.console.toString() + "/").child(gioco?.key.toString() + "/")
                            .child("picture" + i.toString()).delete()
                }
                   Navigation.findNavController(view!!).navigateUp()
                })
                builder.show()
            }
            R.id.Modifica -> {
                val b = Bundle()
                b.putParcelable("gioco",gioco)
                Navigation.findNavController(view!!).navigate(R.id.action_dettaglio_gioco_to_fragment_inserimento,b)
            }
            else -> return false
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?){
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
        if (gioco?.id == id) {
            inflater?.inflate(R.menu.menu_modifica, menu)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        // Estraggo il parametro (gioco) dal bundle ed eventualmente lo visualizzo
        arguments?.let {
            gioco = it.getParcelable("gioco")
            gioco?.let {

                if(gioco?.console=="Ps4") (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#004097")))
                if(gioco?.console=="Xbox") (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#107C10")))
                if(gioco?.console=="Nintendo") (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#DD0001")))
                (activity as AppCompatActivity).supportActionBar?.setTitle("BuyGames")
                val imagRef = storageRef.child(gioco?.console.toString() + "/").child(gioco?.key.toString() + "/")
                //li legge dal database utilizzando il gioco passato, così i dati saranno aggiornati anche dopo la modifica
                val myRef = FirebaseDatabase.getInstance().getReference("Giochi").child(gioco?.console.toString())
                fun loadList(callback: (list: List<Gioco>) -> Unit) {
                    myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(snapshotError: DatabaseError) {
                            TODO("not implemented")
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            val list : MutableList<Gioco> = mutableListOf()
                            val children = snapshot!!.children
                            children.forEach {
                                list.add(it.getValue(Gioco::class.java)!!)
                            }
                            callback(list)
                        }
                    })
                }
                loadList {
                    nome_dettaglio.text = it.get(it.indexOf(gioco!!)).nome
                    luogo_dettaglio.text = it.get(it.indexOf(gioco!!)).luogo
                    prezzo_dettaglio.text = String.format("%d", it.get(it.indexOf(gioco!!)).prezzo)+"€"
                }


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
        //invio email
        utente_dettaglio.setOnClickListener {
            //get input from EditTexts and save in variables
            val recipient = utente_dettaglio.text.toString().trim()
            val subject = "BuyGames".trim()
            val message = "Ciao".trim()
            //method call for email intent with these inputs as parameters
            sendEmail(recipient, subject, message)
        }
        cellulare_dettaglio.setOnClickListener {
            val seller = cellulare_dettaglio.text.toString()
            callCell(seller)
        }
    }

    //funzione per chiamare proprietario gioco
    private fun callCell(seller : String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",seller,null))
        startActivity(intent)
    }

    //funzione che svolge l'invio dell'email
    private fun sendEmail(recipient: String, subject: String, message: String) {
        val mIntent = Intent(Intent.ACTION_SEND)
        /*To send an email you need to specify mailto: as URI using setData() method
        and data type will be to text/plain using setType() method*/
        mIntent.data = Uri.parse("mailto:")
        mIntent.type = "text/plain"
        // put recipient email in intent
        /* recipient is put as array because you may wanna send email to multiple emails
           so enter comma(,) separated emails, it will be stored in array*/
        mIntent.putExtra(Intent.EXTRA_EMAIL,arrayOf(recipient))
        //put the Subject in the intent
        mIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        //put the message in the intent
        mIntent.putExtra(Intent.EXTRA_TEXT, message)
        try {
            //start email intent
            startActivity(Intent.createChooser(mIntent, "Choose Email Client..."))
        }
        catch (e: Exception){
            //if any thing goes wrong for example no email client application or any exception
            //get and show exception message
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        }
    }
}
