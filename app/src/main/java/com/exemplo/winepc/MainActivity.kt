package com.exemplo.winepc

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var launcher: WineLauncher
    private lateinit var txtStatus: TextView
    private lateinit var btnScaling: Button
    private var selectedExe: File? = null
    private var fullscreen = true

    private val pickExe = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) importExe(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        launcher = WineLauncher(this)
        txtStatus = findViewById(R.id.txtStatus)
        btnScaling = findViewById(R.id.btnScaling)

        findViewById<Button>(R.id.btnPick).setOnClickListener {
            pickExe.launch(arrayOf("*/*"))
        }
        findViewById<Button>(R.id.btnRun).setOnClickListener { runGame() }
        findViewById<Button>(R.id.btnFullscreen).setOnClickListener {
            fullscreen = !fullscreen
            applyFullscreen()
        }
        btnScaling.setOnClickListener {
            val on = ScalingRenderer.toggle()
            btnScaling.text = if (on) "Upscaling: ligado" else "Upscaling: desligado"
        }
        findViewById<Button>(R.id.btnPermission).setOnClickListener { requestStorage() }

        applyFullscreen()
    }

    private fun importExe(uri: Uri) {
        try {
            val name = uri.lastPathSegment?.substringAfterLast('/') ?: "game.exe"
            val dir = File(filesDir, "games").apply { mkdirs() }
            val dest = File(dir, name)
            contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(dest).use { input.copyTo(it) }
            } ?: throw IllegalStateException("Não foi possível abrir o arquivo")
            selectedExe = dest
            txtStatus.text = "Selecionado: ${dest.name}"
        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao importar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun runGame() {
        val exe = selectedExe
        if (exe == null) {
            Toast.makeText(this, "Escolha um .exe primeiro", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            launcher.run(exe, fullscreen = fullscreen, scaling = ScalingRenderer.enabled)
        } catch (e: Exception) {
            Toast.makeText(this, e.message ?: "Falha ao iniciar", Toast.LENGTH_LONG).show()
        }
    }

    private fun requestStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
            } else {
                Toast.makeText(this, "Permissão já concedida", Toast.LENGTH_SHORT).show()
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                1001
            )
        }
    }

    private fun applyFullscreen() {
        WindowCompat.setDecorFitsSystemWindows(window, !fullscreen)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        if (fullscreen) {
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller.hide(WindowInsetsCompat.Type.systemBars())
        } else {
            controller.show(WindowInsetsCompat.Type.systemBars())
        }
    }
}
