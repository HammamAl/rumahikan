package capstone.bangkit.rumahikan.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ListBucketDetail::class, RemoteKeys::class],
    version = 2,
    exportSchema = false
)
abstract class BucketDatabase : RoomDatabase() {

    abstract fun getListBucketDetailDao(): ListBucketDetailDao
    abstract fun getRemoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: BucketDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): BucketDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    BucketDatabase::class.java, "bucket_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}