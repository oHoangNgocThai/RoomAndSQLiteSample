package android.thaihn.roomandsqlitesample.room.entity

import androidx.room.Entity
import java.util.*

@Entity(tableName = "user")
data class User(

    private var birthday: Date?

)
