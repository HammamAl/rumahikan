package capstone.bangkit.rumahikan.di

import android.content.Context
import capstone.bangkit.rumahikan.api.ApiConfig
import capstone.bangkit.rumahikan.database.BucketDatabase
import capstone.bangkit.rumahikan.repository.MainRepository

object Injection {
    fun provideRepository(context: Context): MainRepository {
        val database = BucketDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return MainRepository(database, apiService)
    }
}