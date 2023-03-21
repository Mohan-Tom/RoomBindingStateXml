package com.example.roombindingstatexml

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Contact::class],
    version = 2
)
abstract class ContactDatabase : RoomDatabase() {
    abstract val dao: ContactDao

    class Factory {
        fun createInstance(context: Context): ContactDatabase = Room
            .databaseBuilder(context, ContactDatabase::class.java, "contacts.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    companion object {
        @Volatile private var INSTANCE: ContactDatabase? = null

        @Synchronized
        fun getInstance(application: Context): ContactDatabase =
            INSTANCE ?: synchronized(this) { INSTANCE ?: buildDatabase(application) }

        private fun buildDatabase(application: Context): ContactDatabase =
            Factory().createInstance(application).also { INSTANCE = it }

    }
}