package android.thaihn.roomandsqlitesample.room.dao

import android.thaihn.roomandsqlitesample.room.entity.User
import androidx.room.Dao
import androidx.room.Query
import java.util.*

@Dao
interface UserDao {

    @Query("SELECT * FROM user WHERE birthday BETWEEN :from AND :to")
    fun findUsersBornBetweenDates(from: Date, to: Date): List<User>

}