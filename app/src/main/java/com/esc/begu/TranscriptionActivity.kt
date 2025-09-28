package com.esc.begu

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

import android.view.View
import android.view.WindowManager
//import java.util.*

// Camera
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat

// Speech
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.core.app.ActivityCompat
import com.esc.begu.MainActivity.Companion.REQUEST_CODE_PERMISSIONS

/**
 * Pantalla correspondiente a la Transcripcción de Voz a Texto
 */
class TranscriptionActivity : AppCompatActivity(),
    RecognitionListener {

    // Componentes de la UI
    private lateinit var cameraView: PreviewView            // Muestra la vista de la cámara.
    private lateinit var backButton: ImageButton            // Botón para volver al menú principal
    private lateinit var signButton: ImageButton            // Botón para cambiar a la vista de reconocimiento de LS
    private lateinit var micButton: ImageButton             // Botón para encender el micrófono
    private lateinit var cameraSwitchButton: ImageButton    // Botón para cambiar la cámara
    private lateinit var cameraButton: ImageButton          // Botón abrir el menú de opciones

    // Componentes para la transcripción de voz a texto
    private lateinit var speechRecognizer: SpeechRecognizer
    private var isListening: Boolean = false
    private lateinit var textView: TextView
    private lateinit var editTextView: EditText
    private var textSave: String = ""
    private val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

    // Componentes de la cámara
    private var cameraProvider: ProcessCameraProvider? = null
    private var imageCapture: ImageCapture? = null
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA  // Seleccionar cámara trasera por defecto
    private var isFrontCamera: Boolean = false
    private var isCameraOn: Boolean = true
    private lateinit var blackOverlay: View

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ocultar barra de estado (barra superior)
        @Suppress("DEPRECATION")
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // Ocultar barra de navegación (barra inferior)
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                )

        setContentView(R.layout.activity_transcription)

        // Inicializar elementos de la UI
        cameraView = findViewById(R.id.cameraView)
        backButton = findViewById(R.id.backButton)
        signButton = findViewById(R.id.signImageButton)

        micButton = findViewById(R.id.micButton)

        cameraButton = findViewById(R.id.cameraButton)
        cameraSwitchButton = findViewById(R.id.cameraSwitchButton)

        textView = findViewById(R.id.textView)
        editTextView = findViewById(R.id.editTextView)
        blackOverlay = findViewById(R.id.blackOverlay)

        // Activar cámara
        startCamera()

        // Inicializar reconocedor de voz
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this@TranscriptionActivity)

        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-CL")
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "es-CL")
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, "es-CL")

        //recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //recognizerIntent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true) // Forzar modo offline

        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true) //Transcripcion en tiempo real activado

        speechRecognizer.setRecognitionListener(this@TranscriptionActivity)

        // Cambiar a la vista del menú principal
        backButton.setOnClickListener {
            speechRecognizer.destroy()
            val intent = Intent(this@TranscriptionActivity, MainActivity::class.java)
            startActivity(intent)
        }

        // Cambiar a la vista de reconocimiento de LS
        signButton.setOnClickListener {
            speechRecognizer.destroy()
            val intent = Intent(this@TranscriptionActivity, SignLangActivity::class.java)
            startActivity(intent)
        }

        // Activar/Desactivar micrófono e iniciar/detener reconocimiento de voz
        micButton.setOnClickListener {
            if (!isListening) {
                // Cambiar elementos de la UI
                if (isCameraOn) textView.visibility = View.VISIBLE
                micButton.setImageResource(R.drawable.baseline_mic_24)

                // Comenzar transcripcion de voz a texto
                isListening = true

                textSave = (editTextView.text.toString() + " ")
                speechRecognizer.startListening(recognizerIntent)
            }

            else {
                // Cambiar elementos de la UI
                if (isCameraOn) textView.visibility = View.INVISIBLE
                micButton.setImageResource(R.drawable.baseline_mic_off_24)

                // Detener transcripcion de voz a texto
                isListening = false
                speechRecognizer.stopListening()
                textView.text = " "
            }
        }

        // Cambiar la vista de la cámara
        cameraSwitchButton.setOnClickListener {
            if (isFrontCamera) {
                cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                isFrontCamera = false
            }
            else {
                cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                isFrontCamera = true
            }

            startCamera()
        }

        cameraButton.setOnClickListener {
            if (isCameraOn) {
                // Cambiar elementos de la UI
                cameraSwitchButton.visibility = View.INVISIBLE
                textView.visibility = View.INVISIBLE
                editTextView.visibility = View.VISIBLE

                micButton.setImageResource(R.drawable.baseline_mic_off_24)
                cameraButton.setImageResource(R.drawable.outline_videocam_off_24)

                // Detener transcripcion de voz a texto
                isListening = false
                speechRecognizer.stopListening()
                textView.text = " "

                // Detener Camara
                cameraProvider?.unbindAll()
                blackOverlay.visibility = View.VISIBLE

                isCameraOn = false
            }

            else {
                // Cambiar elementos de la UI
                cameraSwitchButton.visibility = View.VISIBLE
                textView.visibility = View.INVISIBLE
                editTextView.visibility = View.INVISIBLE
                micButton.setImageResource(R.drawable.baseline_mic_off_24)
                cameraButton.setImageResource(R.drawable.outline_videocam_24)

                // Detener transcripcion de voz a texto
                isListening = false
                speechRecognizer.stopListening()
                textView.text = " "

                // Activar Camara
                startCamera()
                blackOverlay.visibility = View.INVISIBLE

                isCameraOn = true
            }

        }

    }

    // Función que activa y muestra la vista de la cámara
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this@TranscriptionActivity)

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            imageCapture = ImageCapture.Builder().build()

            // Vista previa de CameraX
            val preview = Preview.Builder()
                .build()
                .also { it.surfaceProvider = cameraView.surfaceProvider }

            try {
                // Actualizar vista previa
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(this@TranscriptionActivity, cameraSelector, preview, imageCapture)
            } catch (_: Exception) {
                Toast.makeText(this@TranscriptionActivity, "No se pudo iniciar la cámara", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this@TranscriptionActivity))
    }

    override fun onResume() {
        super.onResume()
        // Redundancia
        if (isListening) {
            // Cambiar Visibilidad de Elementos del UI
            micButton.visibility = View.VISIBLE
            textView.visibility = View.INVISIBLE

            // Detener transcripcion de voz a texto
            isListening = false
            speechRecognizer.stopListening()
            textView.text = " "
        }

        // Verificar y solicitar permisos antes de iniciar la aplicación
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, Permissions.REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    override fun onPause() {
        super.onPause()
        // Detener reconocimiento de voz
        if (isListening) {
            // Cambiar Visibilidad de Elementos del UI
            micButton.visibility = View.VISIBLE
            textView.visibility = View.INVISIBLE

            // Detener transcripcion de voz a texto
            isListening = false
            speechRecognizer.stopListening()
            textView.text = " "
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        speechRecognizer.destroy()
    }

    // Función para comprobar si se conceden todos los permisos necesarios
    private fun allPermissionsGranted() = Permissions.REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    // Funcón que maneja el resultado de las solicitudes de permisos
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this, "Permisos no concedidos", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    // ///// Funciones de RecognitionListener{} ////////////////////////////////////////////////////
    override fun onReadyForSpeech(params: Bundle?) {}
    override fun onBeginningOfSpeech() {}
    override fun onRmsChanged(rmsdB: Float) {}
    override fun onBufferReceived(buffer: ByteArray?) {}
    override fun onEndOfSpeech() {}

    override fun onError(error: Int) {
        textView.text = " "

        // Continuar con el reconocimiento de voz
        if (isListening) {
            speechRecognizer.startListening(recognizerIntent)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onResults(results: Bundle?) {
        val transcription = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        textView.text = transcription?.get(0)?.takeLast(90) ?: " "
        editTextView.setText(textSave + transcription?.get(0))
        editTextView.setSelection(editTextView.text.length)

        // Continuar con el reconocimiento de voz
        if (isListening) {
            textSave = (editTextView.text.toString() + " ")
            speechRecognizer.startListening(recognizerIntent)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onPartialResults(partialResults: Bundle?) {
        val transcription = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        textView.text = transcription?.get(0)?.takeLast(90) ?: " "
        editTextView.setText(textSave + transcription?.get(0))
        editTextView.setSelection(editTextView.text.length)
    }

    override fun onEvent(eventType: Int, params: Bundle?) {}
    ////////////////////////////////////////////////////////////////////////////////////////////////
}