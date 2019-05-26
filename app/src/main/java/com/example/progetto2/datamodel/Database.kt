package com.example.progetto2.datamodel

object Database {

    private var Giochi=ArrayList<Gioco>()
    init {
        for (i in 1 .. 10)
        Giochi.add(Gioco("Red Dead Redemption 2",30, "Aversa"))
    }
    // Restituisce l'elenco di tutti i giochi presenti
    fun getElencoGiochi() : ArrayList<Gioco> {
        return Giochi
    }
}