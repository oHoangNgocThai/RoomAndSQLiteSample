package android.thaihn.roomandsqlitesample.sqlite.controller

import android.provider.BaseColumns

object ContactContract {

    object ContactEntry : BaseColumns {
        const val TABLE_NAME = "contact_entry"
        const val COLUMN_NAME = "name"
        const val COLUMN_PHONE = "phone"
        const val COLUMN_ADDRESS = "address"
    }
}