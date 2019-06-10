package com.example.progetto2

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.view.*
import android.widget.ImageButton
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.progetto2.datamodel.Gioco
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_fragment_inserimento.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import java.io.ByteArrayOutputStream
import java.lang.Thread.sleep


class fragment_inserimento : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //aggiungo questa riga per aggiungere un riferimento al menu
        setHasOptionsMenu(true)
    }
    // Costante utilizzata per distinguere l'origine della richiesta
    var REQUEST_IMAGE_CAPTURE = 1

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
        val takePhoto = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        foto1.setOnClickListener {
            REQUEST_IMAGE_CAPTURE = 1
            // Creo un intent di tipo implicito per acquisire l'immagine
            takePhoto.resolveActivity(activity!!.packageManager)?.also {
                startActivityForResult(takePhoto, REQUEST_IMAGE_CAPTURE)
            }
        }
        foto2.setOnClickListener {
            REQUEST_IMAGE_CAPTURE = 2
            // Creo un intent di tipo implicito per acquisire l'immagine
            takePhoto.resolveActivity(activity!!.packageManager)?.also {
                startActivityForResult(takePhoto, REQUEST_IMAGE_CAPTURE)
            }
        }
        foto3.setOnClickListener {
            REQUEST_IMAGE_CAPTURE = 3
            // Creo un intent di tipo implicito per acquisire l'immagine
            takePhoto.resolveActivity(activity!!.packageManager)?.also {
                startActivityForResult(takePhoto, REQUEST_IMAGE_CAPTURE)
            }
        }

    }
    //inserimento annuncio, con tasto in alto a destra
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
                val nome = nome_gioco.text.toString()
                val luogo = luogo_gioco.text.toString()
                val prezzo = prezzo_gioco.text.toString()
                val auth = FirebaseAuth.getInstance()
                val id = auth.currentUser?.uid
                var key : DatabaseReference? = null

                if (nome.length > 0 && luogo.length > 0 && prezzo.toInt() > 0 && id != null && (checkPs4.isChecked || checkXbox.isChecked || checkNintendo.isChecked)) {
                    val database = FirebaseDatabase.getInstance().reference
                    if (checkPs4.isChecked) {
                        key = database.child("Giochi").child("Ps4")
                            .push()  //questa push mi restituisce un identificativo unico del percorso creato
                        key.setValue(
                            Gioco(
                                nome,
                                prezzo.toInt(),
                                luogo,
                                key.toString()
                            )
                        )    //in quel percorso con identificativo unico inserisco il gioco , rappresenta la lista giochi visibile a tutti
                        caricaFoto(key.toString())
                    }
                    if (checkXbox.isChecked) {
                        key = database.child("Giochi").child("Xbox")
                            .push()  //questa push mi restituisce un identificativo unico del percorso creato
                        key.setValue(
                            Gioco(
                                nome,
                                prezzo.toInt(),
                                luogo,
                                key.toString()
                            )
                        )    //in quel percorso con identificativo unico inserisco il gioco , rappresenta la lista giochi visibile a tutti
                        caricaFoto(key.toString())
                    }
                    if (checkNintendo.isChecked) {
                         key = database.child("Giochi").child("Nintendo")
                            .push()  //questa push mi restituisce un identificativo unico del percorso creato
                        key.setValue(
                            Gioco(
                                nome,
                                prezzo.toInt(),
                                luogo,
                                key.toString()
                            )
                        )    //in quel percorso con identificativo unico inserisco il gioco , rappresenta la lista giochi visibile a tutti
                        caricaFoto(key.toString())
                    }
                    database.child("users").child(id).child(nome).setValue(Gioco(nome, prezzo.toInt(), luogo,key.toString()))   //carico nel database nell'area riservata
                    Toast.makeText(activity,"Gioco inserito correttamente",Toast.LENGTH_SHORT).show()
                    //carica le foto inserite dell'annuncio sul database
                    // Create a storage reference from our app
                    Navigation.findNavController(view!!).navigateUp()
                }
                else { //se alcuni campi sono vuoti non posso caricare il gioco
                        Toast.makeText(activity,"Hai mancato qualche campo", Toast.LENGTH_SHORT).show()
                     }


        return super.onOptionsItemSelected(item)
    }

    fun caricaFoto(key : String){
        val storageRef = FirebaseStorage.getInstance().getReference()
        // Create a reference to "mountains.jpg",è il nome del file che stiamo caricando
        val foto = ArrayList<ImageButton>() //array usato per inserire 3 foto
        foto.add(foto1)
        foto.add(foto2)
        foto.add(foto3)
        for (i in 0 .. 2) {
            val mountainsRef = storageRef.child(key).child("picture" + i.toString())
            val bitmap = (foto.get(i).drawable as? BitmapDrawable)?.bitmap
            val baos = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            var uploadTask = mountainsRef.putBytes(data) //carica i byte della foto
            uploadTask.addOnFailureListener {
                Toast.makeText(activity, "Foto non inserita correttamente", Toast.LENGTH_SHORT).show()
            }.addOnSuccessListener {
                //Toast.makeText(activity,"Foto inserita correttamente",Toast.LENGTH_SHORT).show()
                //non mi serve a nulla
            }
        }
    }
    /**
     * Questo metodo viene invocato per gestire il risultato al ritorno da una activity
     * occorre determinare chi aveva generato la richiesta
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {     // Acquisizione immagine
            val immagineCatturata = data?.extras?.get("data") as Bitmap
           when(REQUEST_IMAGE_CAPTURE){
               1 -> foto1.setImageBitmap(immagineCatturata)
               2 -> foto2.setImageBitmap(immagineCatturata)
               3 -> foto3.setImageBitmap(immagineCatturata)
           }
        }
    }
}

