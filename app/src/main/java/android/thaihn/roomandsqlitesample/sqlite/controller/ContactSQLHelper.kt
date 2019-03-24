package android.thaihn.roomandsqlitesample.sqlite.controller

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log

class ContactSQLHelper(context: Context) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_CONTACT)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        Log.d(TAG, "onUpgrade: oldVersion:$oldVersion---newVersion:$newVersion")
        db?.execSQL(SQL_DELETE_CONTACT)
        onCreate(db)

        // update schema and kept data of user, using oldVersion for check
        if (oldVersion <  2) {
//            upgradeVersion2(db)
        }
        if (oldVersion <  3) {
//            upgradeVersion3(db)
        }
        if (oldVersion <  4) {
//            upgradeVersion4(db)
        }
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "onDowngrade: oldVersion:$oldVersion---newVersion:$newVersion")
        onUpgrade(db, oldVersion, newVersion)
    }

    private val SQL_CREATE_CONTACT = "CREATE TABLE ${ContactContract.ContactEntry.TABLE_NAME} (" +
            " ${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "${ContactContract.ContactEntry.COLUMN_NAME} TEXT, " +
            "${ContactContract.ContactEntry.COLUMN_PHONE} TEXT, " +
            "${ContactContract.ContactEntry.COLUMN_ADDRESS} TEXT " +
            ")"

    private val SQL_DELETE_CONTACT =
            "DROP TABLE IF EXISTS ${ContactContract.ContactEntry.TABLE_NAME}"

    companion object {
        private val TAG = ContactSQLHelper::class.java.simpleName
        // If you change the database schema, you must increment the database version
        const val DATABASE_NAME = "Contact.db"
        const val DATABASE_VERSION = 1
    }
}
