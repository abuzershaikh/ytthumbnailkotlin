package com.example.thumbnailmaker.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thumbnailmaker.model.ToolType // Ensure ToolType is imported
import com.example.thumbnailmaker.ui.theme.ThumbnailMakerTheme
import com.example.thumbnailmaker.viewmodel.MainViewModel
import android.net.Uri // Required for the new launcher

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ThumbnailMakerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainAppScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(mainViewModel: MainViewModel = viewModel()) {
    val context = LocalContext.current

    // Launcher for background image
    val backgroundImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            mainViewModel.setBackgroundImageUri(it)
        }
    }

    // Launcher for adding image elements
    val imageElementLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            mainViewModel.addImageElement(it)
        }
    }

    Scaffold(
        bottomBar = {
            BottomToolbarView(mainViewModel = mainViewModel) { toolType ->
                Log.d("Toolbar", "Selected tool: $toolType")
                Toast.makeText(context, "Selected: $toolType", Toast.LENGTH_SHORT).show()
                when (toolType) {
                    ToolType.CHANGE_BACKGROUND -> backgroundImageLauncher.launch("image/*")
                    ToolType.ADD_IMAGE -> imageElementLauncher.launch("image/*")
                    ToolType.ADD_TEXT -> mainViewModel.addTextElement()
                    ToolType.ADD_SHAPE -> mainViewModel.addShapeElement(com.example.thumbnailmaker.model.ShapeType.RECTANGLE) // Default to Rectangle
                    // Handle other tool types as needed
                    else -> { /* Do nothing or log */ }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Box(modifier = Modifier.weight(1f)) { // Canvas takes up available space
                CanvasView(mainViewModel = mainViewModel)
            }
            // Control Panel appears here if an element is selected
            mainViewModel.selectedElement.value?.let {
                ControlPanelView(mainViewModel = mainViewModel)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ThumbnailMakerTheme {
        MainAppScreen()
    }
}
