package com.example.redcolaboracion.view

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.example.redcolaboracion.navigation.BottomNavItem
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import java.io.File
import java.util.concurrent.Executor
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.Firebase
import com.google.firebase.storage.storage

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CameraPreview(navHostController: NavHostController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
    val cameraController = remember {
        LifecycleCameraController(context)
    }

    val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                val executor = ContextCompat.getMainExecutor(context)
                takePicture(cameraController, executor, navHostController)
                      },
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = "Tomar Foto")
        }
        if (permissionState.status.isGranted) {
            CameraController(cameraController, lifecycleOwner, cameraSelector)
        } else {
            Text(text = "Permiso Denegado", modifier = Modifier.fillMaxSize())
        }
    }
}

private fun takePicture(cameraController: LifecycleCameraController, executor: Executor, navHostController: NavHostController){
    val file = File.createTempFile("foto",".jpg")
    val outputDirectory = ImageCapture.OutputFileOptions.Builder(file).build()
    val storageRef = Firebase.storage.reference
    val photoRef = storageRef.child("images/foto.jpg")

    cameraController.takePicture(outputDirectory, executor, object: ImageCapture.OnImageSavedCallback{
        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            cameraController.unbind()
            val uploadTask = photoRef.putFile(Uri.fromFile(file))
            uploadTask.addOnSuccessListener {
                navHostController.navigate(BottomNavItem.Profile.route)
            }.addOnFailureListener { exception ->
                println("Error al tomar foto")
            }
        }
        override fun onError(exception: ImageCaptureException) {
            println("Error al tomar foto")
        }
    })
}

@Composable
fun CameraController(
    cameraController: LifecycleCameraController,
    lifecicly: LifecycleOwner,
    cameraSelector: CameraSelector
) {
    cameraController.bindToLifecycle(lifecicly)
    cameraController.cameraSelector = cameraSelector
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
        val previewView = PreviewView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
        }
        previewView.controller = cameraController
        previewView
    })
}