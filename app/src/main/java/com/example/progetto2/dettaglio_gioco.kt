package com.example.progetto2


import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.ImageView
import com.example.progetto2.datamodel.Gioco
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_dettaglio_gioco.*


class dettaglio_gioco : Fragment() {
    private lateinit var auth : FirebaseAuth
    private lateinit var id : String
    val storageRef = FirebaseStorage.getInstance().getReference()

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

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?){
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_modifica, menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Estraggo il parametro (gioco) dal bundle ed eventualmente lo visualizzo
        arguments?.let {
            val gioco : Gioco? = it.getParcelable("gioco")   //TODO: Il nome dovrebbe essere in un unico punto!!
            gioco?.let {
                val imagRef = storageRef.child(gioco?.key.toString())
                if (gioco.id == id) {
                    setHasOptionsMenu(true)
                }
                nome_dettaglio.text = it.nome
                luogo_dettaglio.text = it.luogo
                prezzo_dettaglio.text = String.format("%d", it.prezzo)+"€"
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
