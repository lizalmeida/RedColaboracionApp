package com.example.redcolaboracion.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
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
import androidx.core.content.FileProvider
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import java.io.File
import java.util.concurrent.Executor
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable

fun CameraPreview(navController: NavController){
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
                takePicture(
                    cameraController = cameraController,
                    executor = executor,
                    navController = navController,
                    context = context
                ) },
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

fun takePicture(cameraController: LifecycleCameraController, executor: Executor, navController: NavController, context: Context){
    val TAG = "takePicture"
    val photoFile = File.createTempFile("foto_perfil", ".jpg", context.getFilesDir())
    val photoURI: Uri = FileProvider.getUriForFile(context, "com.example.redcolaboracion.provider", photoFile)
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    cameraController.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                cameraController.unbind()
                navController.previousBackStackEntry?.savedStateHandle?.set("photoUri", photoURI.toString())
                navController.popBackStack()
            }
            override fun onError(exception: ImageCaptureException) {
                Log.e(TAG, "Error al tomar la foto: $exception")
            }
        }
    )
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