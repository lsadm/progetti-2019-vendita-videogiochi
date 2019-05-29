package com.example.progetto2

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.view.*
import android.widget.Toast
import com.example.progetto2.datamodel.Gioco
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_fragment_inserimento.*
import com.google.firebase.auth.FirebaseAuth
import java.io.ByteArrayOutputStream


class fragment_inserimento : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //aggiungo questa riga per aggiungere un riferimento al menu
        setHasOptionsMenu(true)
    }
    // Costante utilizzata per distinguere l'origine della richiesta
    val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_fragment_inserimento, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?){
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
        inflater?.inflate(R.menu.menu_inserimento, menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val v: View? = activity?.findViewById(R.id.bottomNavigation)
        v?.visibility=View.GONE
        // Imposta il funzionamento del pulsante per l'acqisizione dell'immagine
        foto1.setOnClickListener {
            // Creo un intent di tipo implicito per acquisire l'immagine
            val takePhoto = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePhoto.resolveActivity(activity!!.packageManager)?.also {
                startActivityForResult(takePhoto, REQUEST_IMAGE_CAPTURE)
            }
        }
    }
    //inserimento annuncio, con tasto in alto a destra
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.ps4_list -> {      // Conferma
                val nome = nome_gioco.text.toString()
                val luogo = luogo_gioco.text.toString()
                val prezzo = prezzo_gioco.text.toString()
                val auth = FirebaseAuth.getInstance()
                val id = auth.currentUser?.uid

                if (nome.length > 0 && luogo.length > 0 && prezzo.toInt() > 0 && id != null) {
                    val database = FirebaseDatabase.getInstance().reference
                    database.child("users").child(id).child(nome).setValue(Gioco(nome, prezzo.toInt(), luogo))   //carico nel database nell'area riservata
                    val key = database.child("Giochi").push()  //questa push mi restituisce un identificativo unico del percorso creato
                    key.setValue(Gioco(nome, prezzo.toInt(), luogo))    //in quel percorso con identificativo unico inserisco il gioco , rappresenta la lista giochi visibile a tutti
                    Toast.makeText(activity,"Gioco inserito correttamente",Toast.LENGTH_SHORT).show()
                    //carica le foto inserite dell'annuncio sul database
                    // Create a storage reference from our app
                    val storageRef = FirebaseStorage.getInstance().getReference()
                    // Create a reference to "mountains.jpg",è il nome del file che stiamo caricando
                    val mountainsRef = storageRef.child(nome)
                    val bitmap = (foto1.drawable as? BitmapDrawable)?.bitmap
                    val baos = ByteArrayOutputStream()
                    bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()
                    var uploadTask = mountainsRef.putBytes(data) //carica i byte della foto
                    uploadTask.addOnFailureListener {
                        Toast.makeText(activity,"Foto non inserita correttamente",Toast.LENGTH_SHORT).show()
                    }.addOnSuccessListener {
                        Toast.makeText(activity,"Foto inserita correttamente",Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                        Toast.makeText(activity,"Hai mancato qualche campo", Toast.LENGTH_SHORT).show()
                     }
                }
            }
        return super.onOptionsItemSelected(item)
    }
    /**
     * Questo metodo viene invocato per gestire il risultato al ritorno da una activity
     * occorre determinare chi aveva generato la richiesta
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {     // Acquisizione immagine
            val immagineCatturata = data?.extras?.get("data") as Bitmap
            foto1.setImageBitmap(immagineCatturata)
        }
    }
}

