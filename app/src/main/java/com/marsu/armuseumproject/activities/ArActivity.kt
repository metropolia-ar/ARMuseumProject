package com.marsu.armuseumproject.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.net.toUri
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

class ArActivity : AppCompatActivity() {

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
        val view = binding.root
        setContentView(view)

        // Enable image deletion and hide planeRenderer if there is an image on the screen
        arActivityViewModel.currentImageNode.observe(this, Observer {
            binding.arDeleteButton.isEnabled = it != null
            arFrag.arSceneView.planeRenderer.isVisible = it == null
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
                intermediateNode!!.select()
                arActivityViewModel.currentImageNode.postValue(currentImageNode)
            }
        }
    }

    private fun deleteImage() {
        if (arActivityViewModel.currentImageNode.value != null && intermediateNode != null) {
            intermediateNode!!.removeChild(arActivityViewModel.currentImageNode.value)
            arActivityViewModel.currentImageNode.postValue(null)
            intermediateNode = null
        }
    }
}