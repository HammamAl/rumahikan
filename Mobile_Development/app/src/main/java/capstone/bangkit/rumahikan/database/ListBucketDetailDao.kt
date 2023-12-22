package capstone.bangkit.rumahikan.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ListBucketDetailDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBucket(buckets: List<ListBucketDetail>)

    @Query("SELECT * FROM buckets")
    fun getAllBuckets(): PagingSource<Int, ListBucketDetail>

    @Query("SELECT * FROM buckets")
    fun getAllListBuckets(): List<ListBucketDetail>

    @Query("DELETE FROM buckets")
    suspend fun deleteAll()
}