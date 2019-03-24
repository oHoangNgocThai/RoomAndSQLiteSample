package android.thaihn.roomandsqlitesample.sqlite.controller

import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns
import android.thaihn.roomandsqlitesample.sqlite.entity.Contact

class ContactController(context: Context) {

    private val contactSQLHelper = ContactSQLHelper(context)

    fun insert(contact: Contact): Long {
        val db = contactSQLHelper.writableDatabase
        val values = ContentValues().apply {
            put(ContactContract.ContactEntry.COLUMN_NAME, contact.name)
            put(ContactContract.ContactEntry.COLUMN_PHONE, contact.phone)
            put(ContactContract.ContactEntry.COLUMN_ADDRESS, contact.address)
        }
        val newRowId = db.insert(ContactContract.ContactEntry.TABLE_NAME, null, values)
        db.close()
        return newRowId
    }

    fun getContact(name: String): ArrayList<Contact> {
        val db = contactSQLHelper.readableDatabase

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        val projection = arrayOf(
            BaseColumns._ID,
            ContactContract.ContactEntry.COLUMN_NAME,
            ContactContract.ContactEntry.COLUMN_PHONE,
            ContactContract.ContactEntry.COLUMN_ADDRESS
        )

        val selection = "${ContactContract.ContactEntry.COLUMN_NAME} = ?"
        val selectionArgs = arrayOf(name)

        val sortOrder = "${ContactContract.ContactEntry.COLUMN_NAME} DESC"

        val cursor = db.query(
            ContactContract.ContactEntry.TABLE_NAME,    // The table to query
            projection, // The array of columns to return (pass null to get all)
            selection,  // The columns for the WHERE clause
            selectionArgs, // The values for the WHERE clause
            null, // don't group the rows
            null,   // don't filter by row groups
            sortOrder   // The sort order
        )

        val results = arrayListOf<Contact>()
        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val name =
                    getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NAME))
                val phone =
                    getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_PHONE))
                val address =
                    getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_ADDRESS))
                val contact = Contact(id, name, phone, address)
                results.add(contact)
            }
        }

        // close
        cursor.close()
        db.close()

        return results
    }

    fun getAllContact(): List<Contact> {
        val db = contactSQLHelper.readableDatabase

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        val projection = arrayOf(
            BaseColumns._ID,
            ContactContract.ContactEntry.COLUMN_NAME,
            ContactContract.ContactEntry.COLUMN_PHONE,
            ContactContract.ContactEntry.COLUMN_ADDRESS
        )

        val sortOrder = "${ContactContract.ContactEntry.COLUMN_NAME} DESC"

        val cursor = db.query(
            ContactContract.ContactEntry.TABLE_NAME,    // The table to query
            projection, // The array of columns to return (pass null to get all)
            null,  // The columns for the WHERE clause
            null, // The values for the WHERE clause
            null, // don't group the rows
            null,   // don't filter by row groups
            sortOrder   // The sort order
        )

        val results = arrayListOf<Contact>()
        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val name =
                    getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NAME))
                val phone =
                    getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_PHONE))
                val address =
                    getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_ADDRESS))
                val contact = Contact(id, name, phone, address)
                results.add(contact)
            }
        }

        // close
        cursor.close()
        db.close()

        return results
    }

    // Return number of row deleted
    fun delete(address: String): Int {
        val db = contactSQLHelper.writableDatabase

        // Define 'where' part of query.
        val selection = "${ContactContract.ContactEntry.COLUMN_ADDRESS} LIKE ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf(address)
        val deleteRows =
            db.delete(ContactContract.ContactEntry.TABLE_NAME, selection, selectionArgs)
        db.close()
        return deleteRows
    }

    fun delete(id: Long): Int {
        val db = contactSQLHelper.writableDatabase

        // Define 'where' part of query.
        val selection = "${BaseColumns._ID} = ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf("$id")
        val deleteRows =
            db.delete(ContactContract.ContactEntry.TABLE_NAME, selection, selectionArgs)
        db.close()
        return deleteRows
    }

    fun update(oldContact: Contact, newContact: Contact): Int {
        val db = contactSQLHelper.writableDatabase

        val values = ContentValues().apply {
            put(ContactContract.ContactEntry.COLUMN_NAME, newContact.name)
            put(ContactContract.ContactEntry.COLUMN_PHONE, newContact.phone)
            put(ContactContract.ContactEntry.COLUMN_ADDRESS, newContact.address)
        }

        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf("${oldContact.id}")

        val count = db.update(
            ContactContract.ContactEntry.TABLE_NAME,
            values,
            selection,
            selectionArgs
        )

        db.close()
        return count
    }
}