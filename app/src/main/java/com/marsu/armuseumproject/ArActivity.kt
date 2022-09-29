package com.marsu.armuseumproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.net.toUri
import androidx.navigation.findNavController
import androidx.navigation.navArgs
import com.google.ar.core.HitResult
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.marsu.armuseumproject.databinding.ActivityArBinding

const val MAX_IMAGE_HEIGHT = 300
const val MAX_IMAGE_WIDTH = 300

class ArActivity : AppCompatActivity() {

    private lateinit var arFrag: ArFragment
    private lateinit var binding: ActivityArBinding

    private var viewRenderable: ViewRenderable? = null

    private val args: ArActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val uri = args.imageUri.toUri()

        binding.arBackButton.setOnClickListener { onBackPressed() }

        arFrag = supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment
        val arLayout = LinearLayout(this)
        arLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT)
        val arImage = ImageView(this)
        arImage.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
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
            val anchor = hitResult!!.createAnchor()
            val anchorNode = AnchorNode(anchor)
            anchorNode.parent = arFrag.arSceneView.scene
            val viewNode = TransformableNode(arFrag.transformationSystem)
            viewNode.renderable = viewRenderable
            viewNode.parent = anchorNode
            viewNode.localRotation = Quaternion.axisAngle(Vector3(-1f, 0f, 0f), 90f)
            viewNode.select()
        }
    }
}