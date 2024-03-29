package com.example.progetto2.datamodel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Gioco (var nome:String? = null, var prezzo:Int? = null, var luogo:String? = null, var key : String? = null, var id : String? = null, var console : String? = null ) : Parcelable
