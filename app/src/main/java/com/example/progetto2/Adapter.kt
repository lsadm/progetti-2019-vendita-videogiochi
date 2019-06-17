package com.example.progetto2

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.progetto2.datamodel.Gioco
import com.google.firebase.storage.FirebaseStorage

class Adapter(val dataset: ArrayList<Gioco?>, val context: Context) : RecyclerView.Adapter<RigaGiocoViewHolder>() {
    //attributi
    val storageRef = FirebaseStorage.getInstance().getReference()

    //metodi

    // Invocata per creare un ViewHolder
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RigaGiocoViewHolder {
        // Crea e restituisce un viewholder, effettuando l'inflate del layout relativo alla riga
        return RigaGiocoViewHolder(LayoutInflater.from(context).inflate(R.layout.riga, viewGroup, false))
    }

    // Invocata per conoscere quanti elementi contiene il dataset
    override fun getItemCount(): Int {
        return dataset.size
    }

    // Invocata per visualizzare all'interno del ViewHolder il dato corrispondente alla riga
    override fun onBindViewHolder(viewHolder: RigaGiocoViewHolder, position: Int) {
        val gioco = dataset.get(position)
        val imagRef = storageRef.child(gioco?.console.toString() + "/").child(gioco?.key.toString() + "/").child("picture0")
        val prezzo_euro=gioco?.prezzo.toString()+"€"

        //carica gli elementi del viewholder con i dati del gioco
        viewHolder.Nome.text = gioco?.nome
        viewHolder.Prezzo.text= prezzo_euro
        viewHolder.Luogo.text= gioco?.luogo
        //scarica la foto dal database e la setta nella riga
        imagRef.downloadUrl.addOnSuccessListener {
            GlideApp.with(context).load(it).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(viewHolder.Immagine)
        }
        // Imposto il listner per passare a visualizzare il dettaglio (permette di passare al fragment dettaglio gioco)
        viewHolder.itemView.setOnClickListener {
            // Creo un bundle e vi inserisco il gioco da visualizzare
            val b = Bundle()
            b.putParcelable("gioco",gioco)     //TODO: Il nome dell'ogggetto andrebbe inserito in un solo punto!!
            Navigation.findNavController(it).navigate(R.id.action_to_dettaglio_gioco, b)
        }
    }
}