package com.example.progetto2

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
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
import com.example.progetto2.datamodel.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_dettaglio_gioco.*
import com.google.firebase.database.FirebaseDatabase
import android.view.LayoutInflater
import com.google.firebase.storage.StorageReference
import android.content.pm.ActivityInfo
import android.app.Activity



class dettaglio_gioco : Fragment() {
    //attributi
    private val auth = FirebaseAuth.getInstance()
    private val id = auth.currentUser?.uid.toString()
    private val storageRef = FirebaseStorage.getInstance().getReference()
    private val nodoRef = FirebaseDatabase.getInstance().reference
    private var gioco : Gioco? = null

    //metodi

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dettaglio_gioco, container, false)
    }

    //processa voci menu
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.Cestino  -> {
                //finestra di dialog per eliminare gioco
                val builder = AlertDialog.Builder(activity as AppCompatActivity)
                builder.setTitle(R.string.AlertMessage)
                builder.setNegativeButton(R.string.NegativeButton,DialogInterface.OnClickListener { _, which -> }) //chiude la finestra
                builder.setPositiveButton(R.string.PositiveButton,DialogInterface.OnClickListener { _, which ->
                    deleteGame() //elimina il gioco
                   Navigation.findNavController(view!!).navigateUp() //e torna indietro
                })
                builder.show() //mostra la finestra
            }
            R.id.Modifica -> {
                //crea un bundle e lo passa al fragment inserimento, poi lì verrà modificato
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
            inflater?.inflate(R.menu.menu_modifica, menu) //viene mostrato solo il menu modifica
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        activity?.requestedOrientation=(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR) //impedisce la rotazione dello schermo
        // Estraggo il parametro (gioco) dal bundle ed eventualmente lo visualizzo
        arguments?.let {
            gioco = it.getParcelable("gioco")
            gioco?.let {
                //setto il colore dell'actionBar a seconda della piattaforma specifica del gioco
                if(gioco?.console=="Ps4") (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#004097")))
                if(gioco?.console=="Xbox") (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#107C10")))
                if(gioco?.console=="Nintendo") (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#DD0001")))
                (activity as AppCompatActivity).supportActionBar?.setTitle("BuyGames")

                //legge i valori del gioco dal database utilizzando l'oggetto passato col bundle, così i dati saranno aggiornati anche dopo la modifica
                val imagRef = storageRef.child(gioco?.console.toString() + "/").child(gioco?.key.toString() + "/")
                val myRef = FirebaseDatabase.getInstance().getReference("Giochi").child(gioco?.console.toString())
                fun loadList(callback: (list: List<Gioco>) -> Unit) {
                    myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(snapshotError: DatabaseError) {}
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
                    val prezzoEuro = String.format("%d", it[it.indexOf(gioco!!)].prezzo)+"€"
                    prezzo_dettaglio.text = prezzoEuro
                }

                val childEventListener = object : ChildEventListener {
                    override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                        Log.d(TAG, "onChildAdded:" + dataSnapshot.key!!)

                        val usr = dataSnapshot.getValue(User::class.java)
                        try {
                            utente_dettaglio.text = usr?.email
                            cellulare_dettaglio.text = usr?.cell
                        }catch(e : Exception) {}

                    }
                    override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                        Log.d(TAG, "onChildChanged: ${dataSnapshot.key}")
                    }

                    override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                        Log.d(TAG, "onChildRemoved:" + dataSnapshot.key!!)
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
                nodoRef.child("users").child(gioco?.id.toString()).child("Dati").addChildEventListener(childEventListener)
                downloadFoto(imagRef)
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
        //chiamata cell
        cellulare_dettaglio.setOnClickListener {
            val seller = cellulare_dettaglio.text.toString()
            callCell(seller)
        }
        picture0.setOnClickListener {
            zoomFoto(picture0)
        }
        picture1.setOnClickListener {
            zoomFoto(picture1)
        }
        picture2.setOnClickListener {
            zoomFoto(picture2)
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        activity?.requestedOrientation=(ActivityInfo.SCREEN_ORIENTATION_SENSOR)
    }

    //scarica le foto dal database
    private fun downloadFoto(imagRef : StorageReference) {
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

    //elimina il gioco dal database
    private fun deleteGame() {
        nodoRef.child("Giochi").child(gioco!!.console.toString()).child(gioco!!.key.toString()).removeValue()
        nodoRef.child("users").child(auth.currentUser!!.uid).child("mygames").child(gioco!!.key.toString()).removeValue()
        for (i in 0 .. 2) {
            storageRef.child(gioco?.console.toString() + "/").child(gioco?.key.toString() + "/")
                .child("picture" + i.toString()).delete()
        }
    }

    //funzione per ingrandire le foto
    private fun zoomFoto(img : ImageView) {
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.foto, null)
        dialogBuilder.setView(dialogView)
        val imageview = dialogView.findViewById(R.id.imageView) as ImageView
        val bitmap = (img.drawable as? BitmapDrawable)?.bitmap
        imageview.setImageBitmap(bitmap)
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    //Chiama il proprietario gioco (collegamento al dialer)
    private fun callCell(seller : String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",seller,null))
        startActivity(intent)
    }

    //Invia email al proprietario gioco (collegamento gestore email)
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
