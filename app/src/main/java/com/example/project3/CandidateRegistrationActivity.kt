package com.example.project3

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.project3.databinding.ActivityCandidateRegistrationBinding
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class CandidateRegistrationActivity : AppCompatActivity() {
    private lateinit var binding:ActivityCandidateRegistrationBinding
    private lateinit  var  mRegistrationFile:File
    private lateinit  var  mStoragePath:File
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCandidateRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Check if external storage is writable
        binding.btnDownloadRegister.setOnClickListener {
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Toast.makeText(this, "Storage not available", Toast.LENGTH_SHORT).show();
            }else{
                mStoragePath = File(filesDir, "Files");
                if (!mStoragePath.exists()) {
                    mStoragePath.mkdirs()
                }
                mRegistrationFile = File(mStoragePath, "registration.pdf")
                savePdfToStorage(R.raw.registration)
            }
        }

    }

    private fun savePdfToStorage(resourceId :Int) {

        try{
            this.resources.openRawResource(resourceId).use { inputStream ->
                FileOutputStream(mRegistrationFile).use { outputStream ->
                    val buffer = ByteArray(1024)
                    var length: Int
                    while (inputStream.read(buffer).also { length = it } > 0) {
                        outputStream.write(buffer, 0, length)
                    }
                }
            }
            Toast.makeText(this, "PDF saved to Downloads folder", Toast.LENGTH_SHORT).show();
            val pdfView=binding.reportPdfViewer
            pdfView.initWithUrl(
                url = getFileUrl(mRegistrationFile),
                lifecycleCoroutineScope = lifecycleScope,
                lifecycle = lifecycle
            )
            val downloadManager = this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            val request = DownloadManager.Request(getFileUrl(mRegistrationFile).toUri()).apply {
                setTitle("Downloading register.pdf")
                setDescription("File is being downloaded...")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "register.pdf")
            }

            try {
                downloadManager.enqueue(request)
                Toast.makeText(this, "Download started", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Download failed", Toast.LENGTH_SHORT).show()
            }

            } catch ( e:Exception) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to save PDF", Toast.LENGTH_SHORT).show();
            }
    }

    private fun getFileUrl(file: File): String {
        return file.toURI().toURL().toString()
    }


}