package android.thaihn.roomandsqlitesample.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class ContactEntity(

    @ColumnInfo(name = "name") val name: String,

    @ColumnInfo(name = "phone") val phone: String,

    @ColumnInfo(name = "address") val address: String

) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
