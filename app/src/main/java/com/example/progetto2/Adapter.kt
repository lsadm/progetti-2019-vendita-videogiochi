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
    val storageRef = FirebaseStorage.getInstance().getReference()


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

        viewHolder.Nome.text = gioco?.nome
        viewHolder.Prezzo.text= gioco?.prezzo.toString()
        viewHolder.Luogo.text= gioco?.luogo

        imagRef.downloadUrl.addOnSuccessListener {
            GlideApp.with(context).load(it).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(viewHolder.Immagine)
        }

        // Imposto il listner per passare a visualizzare il dettaglio
        viewHolder.itemView.setOnClickListener {
            // Creo un bundle e vi inserisco il gioco da visualizzare
            val b = Bundle()
            b.putParcelable("gioco",gioco)     //TODO: Il nome dell'ogggetto andrebbe inserito in un solo punto!!
            //ho due recycle view quindi per distinguerle uso questo parametro "chiamante"
            //se è 0 allora sto nella ps4_list, altrimenti nell'area personale
            Navigation.findNavController(it).navigate(R.id.action_to_dettaglio_gioco, b)
        }
    }
}