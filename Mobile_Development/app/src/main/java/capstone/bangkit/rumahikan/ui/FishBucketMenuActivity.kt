package capstone.bangkit.rumahikan.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import capstone.bangkit.rumahikan.adapter.BucketListAdapter
import capstone.bangkit.rumahikan.adapter.LoadingStateAdapter
import capstone.bangkit.rumahikan.database.ListBucketDetail
import capstone.bangkit.rumahikan.databinding.ActivityFishBucketMenuBinding
import capstone.bangkit.rumahikan.preference.UserPreferences
import capstone.bangkit.rumahikan.viewmodel.DataStoreViewModel
import capstone.bangkit.rumahikan.viewmodel.MainViewModel
import capstone.bangkit.rumahikan.viewmodel.MainViewModelFactory
import capstone.bangkit.rumahikan.viewmodel.ViewModelFactory

class FishBucketMenuActivity : AppCompatActivity() {
    private val pref by lazy {
        UserPreferences.getInstance(dataStore)
    }

    private lateinit var binding: ActivityFishBucketMenuBinding
    private lateinit var token: String
    private val homePageViewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory(this))[MainViewModel::class.java]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFishBucketMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ifClicked()

        val layoutManager = LinearLayoutManager(this)
        binding.rvFishbucket.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvFishbucket.addItemDecoration(itemDecoration)

        val dataStoreViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[DataStoreViewModel::class.java]
        dataStoreViewModel.getToken().observe(this) {
            token = it
            setUserData(it)
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    private fun setUserData(token: String) {
        val adapter = BucketListAdapter()
        binding.rvFishbucket.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            })

        homePageViewModel.getPagingBuckets(token).observe(this) {
            adapter.submitData(lifecycle, it)
        }

        adapter.setOnItemClickCallback(object : BucketListAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListBucketDetail) {
                sendSelectedUser(data)
            }
        })
    }

    private fun sendSelectedUser(data: ListBucketDetail) {
        val intent = Intent(this, BucketDetailActivity::class.java)
        intent.putExtra(BucketDetailActivity.EXTRA_BUCKET, data)
        startActivity(intent)
    }

    private fun ifClicked() {
        binding.btnAddBucket.setOnClickListener {
            startActivity(Intent(this, AddBucketActivity::class.java))
        }
    }
}