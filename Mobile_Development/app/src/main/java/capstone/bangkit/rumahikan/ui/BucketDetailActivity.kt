package capstone.bangkit.rumahikan.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import capstone.bangkit.rumahikan.databinding.ActivityBucketDetailBinding

class BucketDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBucketDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBucketDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
    companion object {
        const val EXTRA_BUCKET = "extra_bucket"
    }
}