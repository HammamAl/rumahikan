package capstone.bangkit.rumahikan.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import capstone.bangkit.rumahikan.R
import capstone.bangkit.rumahikan.databinding.ActivitySignupBinding
import capstone.bangkit.rumahikan.preference.UserPreferences
import capstone.bangkit.rumahikan.response.LoginDataAccount
import capstone.bangkit.rumahikan.response.RegisterDataAccount
import capstone.bangkit.rumahikan.viewmodel.DataStoreViewModel
import capstone.bangkit.rumahikan.viewmodel.MainViewModel
import capstone.bangkit.rumahikan.viewmodel.MainViewModelFactory
import capstone.bangkit.rumahikan.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

class SignupActivity : AppCompatActivity(){
    private lateinit var binding: ActivitySignupBinding

    private val signupViewModel: MainViewModel by lazy {
        ViewModelProvider(this@SignupActivity, MainViewModelFactory(this))[MainViewModel::class.java]
    }

    private val loginViewModel: MainViewModel by lazy {
        ViewModelProvider(this@SignupActivity, MainViewModelFactory(this))[MainViewModel::class.java]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ifClicked()

        val pref = UserPreferences.getInstance(dataStore)
        val dataStoreViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[DataStoreViewModel::class.java]
        dataStoreViewModel.getLoginSession().observe(this) { sessionTrue ->
            if (sessionTrue) {
                val intent = Intent(this@SignupActivity, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        signupViewModel.message.observe(this) { messageRegist ->
            responseRegister(
                messageRegist
            )
        }

        signupViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        loginViewModel.message.observe(this) { messageLogin ->
            responseLogin(
                messageLogin,
                dataStoreViewModel
            )
        }

        loginViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun responseLogin(
        message: String,
        dataStoreViewModel: DataStoreViewModel
    ) {
        if (message.contains("Hello")) {
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
            val user = loginViewModel.userlogin.value
            dataStoreViewModel.saveLoginSession(true)
            dataStoreViewModel.saveToken(user?.loginResult!!.token)
            dataStoreViewModel.saveName(user.loginResult.name)
        } else {
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun responseRegister(
        message: String,
    ) {
        if (message == "Account successfully created") {
            Snackbar.make(binding.root, getString(R.string.accountSuccessCreated), Snackbar.LENGTH_SHORT).show()
            val userLogin = LoginDataAccount(
                binding.inputEmail.text.toString(),
                binding.inputPassword.text.toString()
            )
            loginViewModel.login(userLogin)
        } else {
            if (message.contains("Email has already registered, please try again")) {
                binding.inputEmail.setErrorMessage(
                    resources.getString(R.string.emailTaken),
                    binding.inputEmail.text.toString()
                )
                Snackbar.make(binding.root, getString(R.string.emailTaken), Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun ifClicked() {
        binding.btnSignup.setOnClickListener {
            binding.apply {
                inputName.clearFocus()
                inputEmail.clearFocus()
                inputPassword.clearFocus()
                inputPasswordConfirm.clearFocus()
            }

            if (binding.inputName.isNameValid && binding.inputEmail.isEmailValid && binding.inputPassword.isPasswordValid && binding.inputPasswordConfirm.isPasswordSameValid) {
                val dataRegisterAccount = RegisterDataAccount(
                    name = binding.inputName.text.toString().trim(),
                    email = binding.inputEmail.text.toString().trim(),
                    password = binding.inputPassword.text.toString().trim()
                )
                signupViewModel.register(dataRegisterAccount)
            } else {
                if (!binding.inputName.isNameValid) binding.inputName.error =
                    resources.getString(R.string.nameNone)
                if (!binding.inputEmail.isEmailValid) binding.inputEmail.error =
                    resources.getString(R.string.emailNone)
                if (!binding.inputPassword.isPasswordValid) binding.inputPassword.error =
                    resources.getString(R.string.passwordNone)
                if (!binding.inputPasswordConfirm.isPasswordSameValid) binding.inputPasswordConfirm.error =
                    resources.getString(R.string.passwordConfirmNone)

                Snackbar.make(binding.root, R.string.invalidLogin, Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.tvLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        finish()
        return super.onSupportNavigateUp()
    }
}