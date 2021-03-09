package dev.decagon.godday.contacts.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.parcelize.Parcelize

/**
  This is class is used to model our contact object.
  It is parcelize because we transfer it object between fragments as parcelable
 **/

@Parcelize
data class Contact(
    @get:Exclude
    var id: String? = null,
    var name: String? = null,
    var phoneNumber: String? = null,
    var email: String? = null
) : Parcelable