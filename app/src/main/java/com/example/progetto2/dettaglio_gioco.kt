package com.example.progetto2


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.progetto2.datamodel.Gioco
import kotlinx.android.synthetic.main.fragment_dettaglio_gioco.*


class dettaglio_gioco : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dettaglio_gioco, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Estraggo il parametro (gioco) dal bundle ed eventualmente lo visualizzo
        arguments?.let {
            val birra: Gioco? = it.getParcelable("gioco")   //TODO: Il nome dovrebbe essere in un unico punto!!
            birra?.let {
                nome_dettaglio.text = it.nome
                luogo_dettaglio.text = it.luogo
                prezzo_dettaglio.text = String.format("%d", it.prezzo)
            }
        }
    }
}
