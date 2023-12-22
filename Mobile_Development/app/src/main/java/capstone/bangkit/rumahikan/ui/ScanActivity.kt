package capstone.bangkit.rumahikan.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import capstone.bangkit.rumahikan.R
import capstone.bangkit.rumahikan.adapter.ResultScan
import capstone.bangkit.rumahikan.databinding.ActivityScanBinding
import capstone.bangkit.rumahikan.utils.createCustomTempFile
import capstone.bangkit.rumahikan.utils.uriToFile
import capstone.bangkit.rumahikan.viewmodel.ScanViewModel
import com.google.android.material.snackbar.Snackbar
import java.io.File

class ScanActivity : AppCompatActivity() {
    private var _binding: ActivityScanBinding? = null
    private val binding get() = _binding!!

    private lateinit var scanViewModel: ScanViewModel

    private lateinit var currentPhotoPath: String

    private var results: ResultScan? = null

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        scanViewModel = ScanViewModel(applicationContext)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.btnScan.setOnClickListener { startTakePhoto() }
        binding.btnGallery.setOnClickListener { startGallery() }

        binding.btnGetResult.setOnClickListener {
            if (getFile != null) {
                uploadImage()
            }
        }

        scanViewModel.apply {
            loading.observe(this@ScanActivity) { isLoading ->
                showLoading(isLoading)
            }

            error.observe(this@ScanActivity) {
                if (it.isNotEmpty())
                    Snackbar.make(binding.root, getString(R.string.upload_failed), Snackbar.LENGTH_SHORT).show()
            }

            result.observe(this@ScanActivity) { response ->
                results = ResultScan(
                    prediction = response.prediction
                )
                binding.diseaseResult.text = results?.prediction
                binding.imageScanResult.setImageBitmap(BitmapFactory.decodeFile(getFile?.path))
                Snackbar.make(binding.root, getString(R.string.upload_success), Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun uploadImage() {
        binding.progressBar.visibility = View.VISIBLE
        scanViewModel.uploadImage()
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURL: Uri = FileProvider.getUriForFile(
                this@ScanActivity,
                "capstone.bangkit.rumahikan",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURL)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)

            myFile.let { file ->
                getFile = file
                binding.imageScanResult.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri

            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@ScanActivity)
                getFile = myFile
                binding.imageScanResult.setImageURI(uri)
            }
        }
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
        var getFile: File? = null
    }
}