package com.example.progetto2

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.*
import androidx.navigation.Navigation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.progetto2.datamodel.Gioco
import com.example.progetto2.datamodel.flag
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_fragment_inserimento.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import java.io.ByteArrayOutputStream
import java.lang.Thread.sleep
import java.util.concurrent.Semaphore


class fragment_inserimento : Fragment(), AdapterView.OnItemSelectedListener {
    var mod = 0
    val database = FirebaseDatabase.getInstance().reference
    val storageRef = FirebaseStorage.getInstance().getReference()
    val foto = ArrayList<ImageButton>() //array usato per inserire 3 foto
    var gioco : Gioco? =null
    var console_spinner : String? = null
    var x = intArrayOf(0,0,0)
    var foto_fatte : Int =0
    var foto_caricate : Int = 0

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
        foto.add(foto1)
        foto.add(foto2)
        foto.add(foto3)
        val v: View? = activity?.findViewById(R.id.bottomNavigation)
        v?.visibility=View.GONE
        arguments?.let{
            //modifico il gioco
            mod = 1

            gioco = it.getParcelable("gioco")   //TODO: Il nome dovrebbe essere in un unico punto!!
            gioco?.let {
                nome_gioco.setText(gioco?.nome)
                prezzo_gioco.setText(gioco?.prezzo.toString())
                luogo_gioco.setText(gioco?.luogo)
                spinner.visibility = View.GONE  //nascondo spinner nella modifica
                for(i in 0..2) {
                    val imagRef = storageRef.child(gioco?.console.toString() + "/").child(gioco?.key.toString() + "/")
                    imagRef.child("picture"+i.toString()).downloadUrl.addOnSuccessListener {
                        x[i]=1
                        GlideApp.with(this).load(it).into(foto.get(i))
                    }.addOnFailureListener {
                        // Handle any errors
                    }
                }
                foto_fatte=x[0]+x[1]+x[2]
                }
        }
        //se non devo modificare il gioco mostro il colore di default e la scritta inserimento
        if(mod!=1) {
            (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#212121")))
            (activity as AppCompatActivity).supportActionBar?.setTitle("Inserimento gioco")
        }
        else (activity as AppCompatActivity).supportActionBar?.setTitle("Modifica gioco")
        //altrimenti lascio il colore della console e il titolo modifica

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
        //spinner
        val spinner: Spinner = activity!!.findViewById(R.id.spinner)
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            context,
            R.array.console_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
            spinner.onItemSelectedListener = this
        }

    }
    //inserimento annuncio, con tasto in alto a destra
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
                val nome = nome_gioco.text.toString()
                val luogo = luogo_gioco.text.toString()
                val prezzo = prezzo_gioco.text.toString()
                val auth = FirebaseAuth.getInstance()
                val id = auth.currentUser?.uid
                var key : String?
                var console : String? = null
                gioco?.nome=nome
                gioco?.luogo = luogo
                gioco?.prezzo=prezzo.toInt()


                if (nome.length > 0 && luogo.length > 0 && prezzo.toInt() > 0 && id != null ) {
                        key = get_key(console.toString())
                        console = get_console()
                        database.child("Giochi").child(console.toString()).child(key.toString()).setValue(
                            Gioco(
                                nome,
                                prezzo.toInt(),
                                luogo,
                                key,
                                id,
                                console
                            )
                        )    //in quel percorso con identificativo unico inserisco il gioco , rappresenta la lista giochi visibile a tutti
                       database.child("users").child(id).child("mygames").child(key.toString()).setValue(
                           Gioco(
                               nome,
                               prezzo.toInt(),
                               luogo,
                               key,
                               id,
                               console
                           ))  //carico nel database nell'area riservata

                    Toast.makeText(activity,"Caricamento in corso", Toast.LENGTH_SHORT).show()
                    if(foto_fatte!=0) { //se ci sono foto da caricare
                            caricaFoto(key.toString(),console.toString())
                        }
                    else Navigation.findNavController(view!!).navigateUp()
                    //Toast.makeText(activity,"Gioco inserito correttamente",Toast.LENGTH_SHORT).show()
                }
                else { //se alcuni campi sono vuoti non posso caricare il gioco
                        Toast.makeText(activity,"Hai mancato qualche campo", Toast.LENGTH_SHORT).show()
                     }


        return super.onOptionsItemSelected(item)
    }

    fun caricaFoto(key : String, console : String){
        // Create a reference to "mountains.jpg",è il nome del file che stiamo caricando
        for (i in 0 .. 2) {
            val mountainsRef = storageRef.child(console).child(key).child("picture" + i.toString())
            val bitmap = (foto.get(i).drawable as? BitmapDrawable)?.bitmap
            val baos = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            if(data.isNotEmpty()) {
                //l'utente ha caricato delle foto, quindi bisogna caricarle
                var uploadTask = mountainsRef.putBytes(data) //carica i byte della foto
                uploadTask.addOnCompleteListener {
                    foto_caricate++
                    //se è l'ultima
                    if(it.isSuccessful && foto_caricate==foto_fatte) Navigation.findNavController(view!!).navigateUp()
                }.addOnFailureListener {
                    Toast.makeText(activity, "Foto non inserita correttamente", Toast.LENGTH_SHORT).show()
                }.addOnSuccessListener {
                }
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
               1 -> { foto1.setImageBitmap(immagineCatturata) //tiene conto di quante foto caricate
                   x[0]=1 }
               2 -> { foto2.setImageBitmap(immagineCatturata)
                   x[1]=1 }
               3 -> { foto3.setImageBitmap(immagineCatturata)
                   x[2]=1 }
           }
            if(foto_fatte<3) foto_fatte=x[0]+x[1]+x[2]
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as AppCompatActivity).supportActionBar?.setTitle("Buy Games")
    }

    //funzione usata per distinguere i due casi: creazione e modifica
    private fun get_key(console:String) : String? {
        if(mod==0) {
            return database.child("Giochi").child(console)
                .push().key  //questa push mi restituisce un identificativo unico del percorso creato
        }
        else {
            return gioco?.key
        }
    }


    //funzione usata per distinguere i due casi: creazione e modifica
    private fun get_console() : String? {
        if(mod==0) {
            return console_spinner
        }
        else {
            return gioco?.console
        }
    }


    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        console_spinner = parent?.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}

