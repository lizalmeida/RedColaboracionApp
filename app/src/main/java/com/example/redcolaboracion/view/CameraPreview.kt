package com.example.redcolaboracion.view

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

//import androidx.camera.compose.CameraPreview
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import java.io.File
import java.util.concurrent.Executor
import com.google.accompanist.permissions.rememberPermissionState

//import androidx.camera.compose.CameraXConfig
@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CameraPreview(navHostController: NavHostController) {
    val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraController = remember {
        LifecycleCameraController(context)
    }
    val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

    //var cameraProvider: ProcessCameraProvider? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),

        floatingActionButton = {
            FloatingActionButton(onClick = {
                val executor = ContextCompat.getMainExecutor(context)
                takePicture(cameraController, executor, navHostController)
            }) {
                Text(text = "Foto")
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
//        fabOffset = 16.dp
    ) {
        if (permissionState.status.isGranted) {
            CameraController(cameraController, lifecycleOwner, cameraSelector, modifier = Modifier.padding(it))
        } else {
            Text(text = "Permiso Denegado", modifier = Modifier.padding(it))
        }
    }
}

private fun takePicture(cameraController: LifecycleCameraController, executor: Executor, navHostController: NavHostController){
    val file = File.createTempFile("foto",".jpg") //Guardar en firestorage
    val outputDirectory = ImageCapture.OutputFileOptions.Builder(file).build()
    cameraController.takePicture(outputDirectory, executor, object: ImageCapture.OnImageSavedCallback{
        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            cameraController.unbind()//cierra la camara
            val savedUri = outputFileResults.savedUri ?: Uri.fromFile(file)
            println("ruta imagen: "+outputFileResults.savedUri)
            val encodedUri = Uri.encode(savedUri.toString())

            navHostController.navigate(BottomNavItem.MyRequests.route)
        }

        override fun onError(exception: ImageCaptureException) {

            println("error al tomar foto")
        }

    })
}

@Composable
fun CameraController(
    cameraController: LifecycleCameraController,
    lifecicly: LifecycleOwner,
    cameraSelector: CameraSelector,
    modifier: Modifier
) {
    cameraController.bindToLifecycle(lifecicly)
    cameraController.cameraSelector = cameraSelector
    AndroidView(modifier = modifier, factory = { context ->
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