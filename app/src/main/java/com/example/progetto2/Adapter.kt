package com.example.progetto2

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.progetto2.datamodel.Gioco

class Adapter(val dataset: ArrayList<Gioco?>, val context: Context) : RecyclerView.Adapter<RigaGiocoViewHolder>() {

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
    }
}