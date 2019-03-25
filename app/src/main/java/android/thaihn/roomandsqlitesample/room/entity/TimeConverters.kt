package android.thaihn.roomandsqlitesample.room.entity

import androidx.room.TypeConverter
import java.util.*

class TimeConverters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimeStamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}
