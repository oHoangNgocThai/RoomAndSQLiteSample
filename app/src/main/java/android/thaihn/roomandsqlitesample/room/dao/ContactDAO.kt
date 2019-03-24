package android.thaihn.roomandsqlitesample.room.dao

import android.thaihn.roomandsqlitesample.room.entity.ContactEntity
import android.thaihn.roomandsqlitesample.sqlite.entity.Contact
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ContactDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(vararg contactEntity: ContactEntity)

    @Update
    fun updateUser(vararg contactEntity: ContactEntity)

    @Delete
    fun deleteUser(vararg contactEntity: ContactEntity)

    @Query("SELECT * FROM contacts")
    fun getAllUser(): LiveData<List<Contact>>

}
