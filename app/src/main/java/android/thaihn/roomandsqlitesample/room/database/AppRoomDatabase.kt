package android.thaihn.roomandsqlitesample.room.database

import android.content.Context
import android.thaihn.roomandsqlitesample.room.dao.ContactDAO
import android.thaihn.roomandsqlitesample.room.entity.ContactEntity
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ContactEntity::class],
    exportSchema = true,
    version = AppRoomDatabase.DATABASE_VERSION
)
abstract class AppRoomDatabase : RoomDatabase() {

    abstract fun getContactDao(): ContactDAO

    companion object {
        const val DATABASE_NAME = "RoomContactDB"
        const val DATABASE_VERSION = 2

        var INSTANCE: AppRoomDatabase? = null

        fun getAppDataBase(context: Context): AppRoomDatabase? {
            if (INSTANCE == null) {
                synchronized(AppRoomDatabase::class) {
                    INSTANCE = Room
                        .databaseBuilder(
                            context.applicationContext,
                            AppRoomDatabase::class.java,
                            DATABASE_NAME
                        ).allowMainThreadQueries()
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyDataBase() {
            INSTANCE = null
        }
    }
}
