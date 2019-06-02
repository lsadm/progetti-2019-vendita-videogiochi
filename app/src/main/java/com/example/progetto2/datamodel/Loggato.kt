package com.example.progetto2.datamodel

import com.google.firebase.auth.FirebaseAuth

class Loggato {
    var auth = FirebaseAuth.getInstance()
    var usr=auth.currentUser
}