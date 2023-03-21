package com.example.roombindingstatexml

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Contact(
    val firstName: String,
    var lastName: String,
    val phoneNumber: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}