package com.example.matchmate.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.matchmate.security.KeystoreDataCipher

@Database(entities = [MatchEntity::class], version = 3, exportSchema = false)
abstract class MatchDatabase : RoomDatabase() {
    abstract fun matchDao(): MatchDao
}

val MATCH_DATABASE_MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        val cipher = KeystoreDataCipher()
        db.query("SELECT id, name, location, email, phone FROM matches").use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow("id")
            val nameIndex = cursor.getColumnIndexOrThrow("name")
            val locationIndex = cursor.getColumnIndexOrThrow("location")
            val emailIndex = cursor.getColumnIndexOrThrow("email")
            val phoneIndex = cursor.getColumnIndexOrThrow("phone")
            while (cursor.moveToNext()) {
                db.execSQL(
                    "UPDATE matches SET name = ?, location = ?, email = ?, phone = ? WHERE id = ?",
                    arrayOf(
                        cipher.encrypt(cursor.getString(nameIndex)),
                        cipher.encrypt(cursor.getString(locationIndex)),
                        cipher.encrypt(cursor.getString(emailIndex)),
                        cipher.encrypt(cursor.getString(phoneIndex)),
                        cursor.getString(idIndex),
                    ),
                )
            }
        }
    }
}

val MATCH_DATABASE_MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE matches ADD COLUMN page INTEGER NOT NULL DEFAULT 1")
    }
}
