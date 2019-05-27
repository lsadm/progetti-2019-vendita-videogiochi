package com.example.progetto2

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.view.*
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.progetto2.datamodel.Gioco
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_fragment_inserimento.*

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
            R.id.menu_inserimento -> {      // Conferma
                val nome = nome_gioco.text.toString()
                val luogo = luogo_gioco.text.toString()
                val prezzo = prezzo_gioco.text.toString()

                if (nome.length > 0 && luogo.length > 0 && prezzo.toInt() > 0) {
                    val database = FirebaseDatabase.getInstance()
                    val myref = database.getReference(nome)
                    myref.setValue(Gioco(nome, prezzo.toInt(), luogo))
                    Toast.makeText(activity,"Gioco inserito correttamente",Toast.LENGTH_SHORT).show()
                    Navigation.findNavController(view!!).navigateUp()
                }
                else {  Toast.makeText(activity,"Hai mancato qualche campo", Toast.LENGTH_SHORT).show() }
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