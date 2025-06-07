package com.example.thumbnailmaker.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thumbnailmaker.model.ToolType
import com.example.thumbnailmaker.viewmodel.MainViewModel
import com.example.thumbnailmaker.model.TextElement
import com.example.thumbnailmaker.model.ImageElement
import androidx.compose.material3.Button // Added for test buttons

@Composable
fun BottomToolbarView(
    mainViewModel: MainViewModel, // Added MainViewModel
    onToolSelected: (ToolType) -> Unit
) {
    BottomAppBar {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ToolButton(
                icon = Icons.Filled.TextFields,
                label = "Text",
                toolType = ToolType.ADD_TEXT,
                onToolSelected = onToolSelected
            )
            ToolButton(
                icon = Icons.Filled.Image,
                label = "Image",
                toolType = ToolType.ADD_IMAGE,
                onToolSelected = onToolSelected
            )
            ToolButton(
                icon = Icons.Filled.Category,
                label = "Shape",
                toolType = ToolType.ADD_SHAPE,
                onToolSelected = onToolSelected
            )
            ToolButton(
                icon = Icons.Filled.Wallpaper,
                label = "Background",
                toolType = ToolType.CHANGE_BACKGROUND,
                onToolSelected = onToolSelected
            )
            ToolButton(
                icon = Icons.Filled.Save,
                label = "Save",
                toolType = ToolType.SAVE_EXPORT,
                onToolSelected = onToolSelected
            )

            // Temporary Test Button (Removed "Select Text" and "Select Img")
            Button(onClick = { mainViewModel.selectElement(null) }, modifier = Modifier.padding(horizontal = 4.dp)) { Text("Clear Sel.", fontSize = 10.sp) }
        }
    }
}

@Composable
private fun ToolButton(
    icon: ImageVector,
    label: String,
    toolType: ToolType,
    onToolSelected: (ToolType) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        IconButton(onClick = { onToolSelected(toolType) }) {
            Icon(icon, contentDescription = label)
        }
        Text(text = label, fontSize = 10.sp)
    }
}
