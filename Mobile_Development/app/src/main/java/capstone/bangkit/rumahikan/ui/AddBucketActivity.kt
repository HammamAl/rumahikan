package capstone.bangkit.rumahikan.ui

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import capstone.bangkit.rumahikan.R
import capstone.bangkit.rumahikan.databinding.ActivityAddBucketBinding
import capstone.bangkit.rumahikan.preference.UserPreferences
import capstone.bangkit.rumahikan.viewmodel.DataStoreViewModel
import capstone.bangkit.rumahikan.viewmodel.MainViewModel
import capstone.bangkit.rumahikan.viewmodel.MainViewModelFactory
import capstone.bangkit.rumahikan.viewmodel.ViewModelFactory
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Locale

class AddBucketActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBucketBinding
    private lateinit var token: String

    private var getFile: File? = null
    private lateinit var fileFinal: File

    private val addBucketViewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory(this))[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBucketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ifClicked()

        val preferences = UserPreferences.getInstance(dataStore)
        val dataStoreViewModel =
            ViewModelProvider(this, ViewModelFactory(preferences))[DataStoreViewModel::class.java]

        dataStoreViewModel.getToken().observe(this) {
            token = it
        }

        addBucketViewModel.message.observe(this) {
            showToastAndNavigate(it)
        }

        addBucketViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun ifClicked() {
        binding.btnAddDrum.setOnClickListener {

            if (getFile == null) {
                showToastAndNavigate(resources.getString(R.string.warningAddImage))
                return@setOnClickListener
            }

            val title = binding.inputBucketName.text.toString().trim()
            if (title.isEmpty()) {
                binding.inputBucketName.error = resources.getString(R.string.warningAddBucketName)
                return@setOnClickListener
            }

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val file = getFile as File

                    var compressedFile: File? = null
                    var compressedFileSize = file.length()

                    while (compressedFileSize > 1 * 1024 * 1024) {
                        compressedFile = withContext(Dispatchers.Default) {
                            Compressor.compress(applicationContext, file)
                        }
                        compressedFileSize = compressedFile.length()
                    }

                    fileFinal = compressedFile ?: file

                    Log.d("Upload", "File size: ${fileFinal.length()}")
                }

                val requestImageFile =
                    fileFinal.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    fileFinal.name,
                    requestImageFile
                )

                val titlePart = title.toRequestBody("text/plain".toMediaType())

                addBucketViewModel.upload(imageMultipart, titlePart)
            }
        }

        binding.btnCamera.setOnClickListener {
            startTakePhoto()
        }

        binding.btnGallery.setOnClickListener {
            startGallery()
        }
    }

    private var anyPhoto = false
    private lateinit var currentPhotoPath: String
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile
            val result = BitmapFactory.decodeFile(myFile.path)
            anyPhoto = true
            binding.imageAddFish.setImageBitmap(result)
            binding.inputBucketName.requestFocus()
        }
    }

    private val timeStamp: String = SimpleDateFormat(
        FILENAME_FORMAT,
        Locale.US
    ).format(System.currentTimeMillis())

    private fun createCustomTempFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timeStamp, ".jpg", storageDir)
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddBucketActivity,
                getString(R.string.package_name),
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun uriToFile(selectedImg: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val myFile = createCustomTempFile(context)

        val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
        val outputStream: OutputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return myFile
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddBucketActivity)
            getFile = myFile
            binding.imageAddFish.setImageURI(selectedImg)
            binding.inputBucketName.requestFocus()
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)
    }

    private fun showToastAndNavigate(msg: String) {
        Toast.makeText(
            this@AddBucketActivity,
            StringBuilder(getString(R.string.message)).append(msg),
            Toast.LENGTH_SHORT
        ).show()

        if (msg == "Fish Bucket created successfully") {
            navigateToHomeActivity()
        }
    }

    private fun navigateToHomeActivity() {
        val intent = Intent(this@AddBucketActivity, FishBucketMenuActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        finish()
        return super.onSupportNavigateUp()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE

        if (!isLoading) {
            showToastAndNavigate("Fish Bucket created successfully")
        }
    }

    companion object {
        const val FILENAME_FORMAT = "MMddyyyy"
    }
}