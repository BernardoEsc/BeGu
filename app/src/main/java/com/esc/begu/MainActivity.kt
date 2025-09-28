package com.esc.begu

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Pantalla principal de la aplicación, correspondiente al Menú Principal
 */
class MainActivity : AppCompatActivity() {

    // Componentes de la UI
    private lateinit var transcriptionButton: Button // Botón para cambiar a la vista de reconocimiento de voz
    private lateinit var signButton: Button          // Botón para cambiar a la vista de reconocimiento de LS

    /**
     * Componentes que se crean al iniciar la Activity
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar elementos de la UI
        transcriptionButton = findViewById(R.id.transcriptionButton)
        signButton = findViewById(R.id.signButton)

        // Verificar y solicitar permisos antes de iniciar la aplicación
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, Permissions.REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Cambiar a la vista de reconocimiento de voz
        transcriptionButton.setOnClickListener {
            val intent = Intent(this, TranscriptionActivity::class.java)
            startActivity(intent)
        }

        // Cambiar a la vista de reconocimiento de LS
        signButton.setOnClickListener {
            val intent = Intent(this, SignLangActivity::class.java)
            startActivity(intent)
        }

    }

    /**
     * Comprueba si se conceden todos los permisos necesarios
     */
    private fun allPermissionsGranted() = Permissions.REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Maneja el resultado de las solicitudes de permisos
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this, "Permisos no concedidos", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    /**
     * Variables inmutables
     */
    companion object {
        const val REQUEST_CODE_PERMISSIONS = 10
    }

}