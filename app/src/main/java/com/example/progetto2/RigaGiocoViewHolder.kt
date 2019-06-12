package com.example.progetto2

import android.view.View
import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.riga.view.*

class RigaGiocoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var Nome = view.nome
    var Prezzo = view.Prezzo
    var Luogo = view.Luogo
    var Immagine = view.game_picture
}