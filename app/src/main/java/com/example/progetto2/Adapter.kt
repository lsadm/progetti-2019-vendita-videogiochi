package com.example.progetto2

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.progetto2.datamodel.Gioco

class Adapter(val dataset: ArrayList<Gioco?>, val context: Context, val chiamante: Int) : RecyclerView.Adapter<RigaGiocoViewHolder>() {

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

        viewHolder.Nome.text = gioco?.nome
        viewHolder.Prezzo.text= gioco?.prezzo.toString()
        viewHolder.Luogo.text= gioco?.luogo

        // Imposto il listner per passare a visualizzare il dettaglio
        viewHolder.itemView.setOnClickListener {

            // Creo un bundle e vi inserisco la birra da visualizzare
            val b = Bundle()
            b.putParcelable("gioco",gioco)     //TODO: Il nome dell'ogggetto andrebbe inserito in un solo punto!!
            //ho due recycle view quindi per distinguerle uso questo parametro "chiamante"
            //se è 0 allora sto nella ps4_list, altrimenti nell'area personale
            if(chiamante==0) Navigation.findNavController(it).navigate(R.id.action_ps4_list_to_dettaglio_gioco, b)
            else Navigation.findNavController(it).navigate(R.id.action_fragment_area_personale_to_dettaglio_gioco, b)
        }
    }
}