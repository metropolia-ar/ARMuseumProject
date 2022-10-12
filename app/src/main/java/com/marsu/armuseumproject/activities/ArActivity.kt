package com.marsu.armuseumproject.activities

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.navArgs
import com.google.ar.core.HitResult
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.marsu.armuseumproject.R
import com.marsu.armuseumproject.databinding.ActivityArBinding
import com.marsu.armuseumproject.viewmodels.ArActivityViewModel

const val MAX_IMAGE_HEIGHT = 300
const val MAX_IMAGE_WIDTH = 300

class ArActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var arFrag: ArFragment
    private lateinit var binding: ActivityArBinding
    private lateinit var arActivityViewModel: ArActivityViewModel

    private var viewRenderable: ViewRenderable? = null
    private var intermediateNode: TransformableNode? = null

    private val args: ArActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArBinding.inflate(layoutInflater)
        arActivityViewModel = ArActivityViewModel(application)
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        registerSensor(sensorManager, sensor)

        val view = binding.root
        setContentView(view)

        // Enable image deletion and hide planeRenderer if there is an image on the screen
        arActivityViewModel.currentImageNode.observe(this, Observer {
            binding.arDeleteButton.isEnabled = it != null
            arFrag.arSceneView.planeRenderer.isVisible = it == null
        })

        // Observe whether phone is held upright and enable/disable adding images accordingly
        arActivityViewModel.isPhoneUpright.observe(this, Observer {
            if (this::arFrag.isInitialized) {
                if (it) {
                    binding.arGravityAlert.isVisible = false
                    if (arActivityViewModel.currentImageNode.value == null) {
                        arFrag.arSceneView.planeRenderer.isVisible = true
                    }
                } else {
                    if (arActivityViewModel.currentImageNode.value == null) {
                        binding.arGravityAlert.isVisible = true
                        arFrag.arSceneView.planeRenderer.isVisible = false
                    }
                }
            }
        })

        val uri = args.imageUri.toUri()

        binding.arBackButton.setOnClickListener { onBackPressed() }
        binding.arDeleteButton.setOnClickListener { deleteImage() }

        arFrag = supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment

        // Create layout for ViewRenderable with the selected image
        val arLayout = LinearLayout(this)
        arLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT)
        val arImage = ImageView(this)
        arImage.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        arImage.adjustViewBounds = true
        arImage.maxHeight = MAX_IMAGE_HEIGHT
        arImage.maxWidth = MAX_IMAGE_WIDTH
        arImage.setImageURI(uri)
        arLayout.addView(arImage)

        ViewRenderable.builder()
            .setView(this, arLayout)
            .build()
            .thenAccept{viewRenderable = it}

        arFrag.setOnTapArPlaneListener { hitResult: HitResult?, _, _ ->
            viewRenderable ?: return@setOnTapArPlaneListener
            if (arActivityViewModel.currentImageNode.value == null) {
                val anchor = hitResult!!.createAnchor()
                val anchorNode = AnchorNode(anchor)
                anchorNode.parent = arFrag.arSceneView.scene
                // Create an intermediate node to force correct rotation for rendered image
                intermediateNode = TransformableNode(arFrag.transformationSystem)
                intermediateNode!!.parent = anchorNode
                val anchorUp = anchorNode.up
                intermediateNode!!.setLookDirection(Vector3.up(), anchorUp)
                val currentImageNode = Node()
                currentImageNode.renderable = viewRenderable
                currentImageNode.parent = intermediateNode
                currentImageNode.localRotation = Quaternion.axisAngle(Vector3(-1f, 0f, 0f), 90f)
                arActivityViewModel.currentImageNode.postValue(currentImageNode)
            }
        }
    }

    // Delete image currently placed on the screen
    private fun deleteImage() {
        if (arActivityViewModel.currentImageNode.value != null && intermediateNode != null) {
            intermediateNode!!.removeChild(arActivityViewModel.currentImageNode.value)
            arActivityViewModel.currentImageNode.postValue(null)
            intermediateNode = null
        }
    }

    // Register gravity sensor
    private fun registerSensor(sensorManager: SensorManager, sensor: Sensor?) {
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d("SENSOR_GRAVITY", "Registered listener to sensor succesfully!")
        } else {
            Log.d("SENSOR_GRAVITY", "Failed to register listener to sensor!")
        }
    }

    // When gravity sensor detects change, send data to viewModel to calculate if phone is upright
    override fun onSensorChanged(p0: SensorEvent?) {
        Log.d("ON_SENSOR_CHANGED", p0?.values.contentToString())
        arActivityViewModel.calculateGravityData(
            p0?.values?.get(0) ?: 0.0F,
            p0?.values?.get(1) ?: 0.0F,
            p0?.values?.get(2) ?: 0.0F
        )
    }

    // Mandatory override for SensorEventListener
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        Log.d("ON_ACCURACY_CHANGED_SENSOR", p0.toString())
        Log.d("ON_ACCURACY_CHANGED_CHANGE", p1.toString())
    }
}