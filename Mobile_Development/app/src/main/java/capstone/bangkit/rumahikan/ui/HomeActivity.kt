package capstone.bangkit.rumahikan.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import capstone.bangkit.rumahikan.R
import capstone.bangkit.rumahikan.databinding.ActivityHomeBinding
import capstone.bangkit.rumahikan.preference.UserPreferences
import capstone.bangkit.rumahikan.viewmodel.DataStoreViewModel
import capstone.bangkit.rumahikan.viewmodel.ViewModelFactory
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class HomeActivity : AppCompatActivity() {

    private val pref by lazy {
        UserPreferences.getInstance(dataStore)
    }

    private lateinit var binding: ActivityHomeBinding

    private lateinit var userNameTextView: TextView
    private lateinit var profileImageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ifClicked()

        userNameTextView = findViewById(R.id.tv_user)

        val dataStoreViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[DataStoreViewModel::class.java]

        dataStoreViewModel.observeLoginSession().observe(this) { isLoggedIn ->
            if (isLoggedIn) {
                dataStoreViewModel.getToken().observe(this) { token ->
                    token?.let {
                        dataStoreViewModel.getName().observe(this) { name ->
                            userNameTextView.text = name
                        }
                    }
                }
            } else {
                redirectToLogin()
            }
        }
    }

    private fun redirectToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }


    private fun ifClicked() {
        binding.scanCamera.setOnClickListener {
            startActivity(Intent(this, ScanActivity::class.java))
        }

        binding.menuBucket.setOnClickListener {
            startActivity(Intent(this, FishBucketMenuActivity::class.java))
        }

        binding.settings.setOnClickListener {
            showAlertDialog()
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            updateUI(account)
        } catch (e: ApiException) {
            Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        account?.let {
            userNameTextView.text = it.displayName

            val photoUrl = it.photoUrl
            Glide.with(this).load(photoUrl).into(profileImageView)
        }
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(this)
        val alert = builder.create()
        builder
            .setTitle(getString(R.string.logout))
            .setMessage(getString(R.string.logoutDescription))
            .setPositiveButton(getString(R.string.cancelLogout)) { _, _ ->
                alert.cancel()
            }
            .setNegativeButton(getString(R.string.yes)) { _, _ ->
                logout()
            }
            .show()
    }

    private fun logout() {
        val loginViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[DataStoreViewModel::class.java]
        loginViewModel.clearDataLogin()
        Toast.makeText(this, R.string.SuccessLogout, Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

}