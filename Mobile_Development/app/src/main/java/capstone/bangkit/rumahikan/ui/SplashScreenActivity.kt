package capstone.bangkit.rumahikan.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import capstone.bangkit.rumahikan.databinding.ActivitySplashScreenBinding
import capstone.bangkit.rumahikan.preference.UserPreferences
import capstone.bangkit.rumahikan.viewmodel.DataStoreViewModel
import capstone.bangkit.rumahikan.viewmodel.ViewModelFactory

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = UserPreferences.getInstance(dataStore)
        val loginViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[DataStoreViewModel::class.java]

        loginViewModel.getLoginSession().observe(this) { isLoggedIn ->

            val intent = if (isLoggedIn) {
                Intent(this, HomeActivity::class.java)
            } else {
                Intent(this, LoginActivity::class.java)
            }

            startActivity(intent)
            finish()
        }
    }
}