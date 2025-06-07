package com.example.thumbnailmaker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thumbnailmaker.model.ImageElement
import com.example.thumbnailmaker.model.ShapeElement
import com.example.thumbnailmaker.model.ShapeType // Import for control panel logic
import com.example.thumbnailmaker.model.TextElement
import com.example.thumbnailmaker.viewmodel.MainViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class) // For TextField
@Composable
fun ControlPanelView(mainViewModel: MainViewModel) {
    val selectedElement by mainViewModel.selectedElement

    selectedElement?.let { element ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Editing: ${element.id} (${element::class.simpleName})",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { mainViewModel.lockSelectedElement() }) {
                    Icon(Icons.Filled.Lock, contentDescription = "Lock Element")
                }
                IconButton(onClick = { mainViewModel.deleteSelectedElement() }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete Element")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            when (element) {
                is TextElement -> {
                    val textElement = element // Smart cast
                    Text("Text Controls:", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 4.dp))
                    TextField(
                        value = textElement.text,
                        onValueChange = { textElement.text = it },
                        label = { Text("Text") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Font Size: ${textElement.fontSize.value.roundToInt()}.sp", fontSize = 12.sp)
                    Slider(value = textElement.fontSize.value, onValueChange = { textElement.fontSize = it.sp }, valueRange = 12f..96f)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = textElement.isBold, onCheckedChange = { textElement.isBold = it })
                        Text("Bold", fontSize = 12.sp)
                        Spacer(modifier = Modifier.weight(1f))
                        Checkbox(checked = textElement.isItalic, onCheckedChange = { textElement.isItalic = it })
                        Text("Italic", fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))

                    Text("Box Corner Radius: ${textElement.cornerRadius.value.roundToInt()}.dp", fontSize = 12.sp)
                    Slider(value = textElement.cornerRadius.value, onValueChange = { textElement.cornerRadius = it.dp }, valueRange = 0f..100f) // Updated range
                    Spacer(modifier = Modifier.height(4.dp))

                    Text("Rotation: ${textElement.rotation.roundToInt()}°", fontSize = 12.sp)
                    Slider(value = textElement.rotation, onValueChange = { textElement.rotation = it }, valueRange = -180f..180f)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row {
                        Button(onClick = { /* TODO: Implement text color picker */ }, modifier = Modifier.weight(1f).padding(end = 4.dp)) { Text("Text Color", fontSize = 10.sp) }
                        Button(onClick = { /* TODO: Implement background color picker */ }, modifier = Modifier.weight(1f).padding(start = 4.dp)) { Text("BG Color", fontSize = 10.sp) }
                    }
                }
                is ImageElement -> {
                    val imageElement = element // Smart cast
                    Text("Image Controls:", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 4.dp))

                    Text("Corner Radius: ${imageElement.cornerRadius.value.roundToInt()}.dp", fontSize = 12.sp)
                    Slider(
                        value = imageElement.cornerRadius.value,
                        onValueChange = { imageElement.cornerRadius = it.dp },
                        valueRange = 0f..100f // Updated range
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Text("Rotation: ${imageElement.rotation.roundToInt()}°", fontSize = 12.sp)
                    Slider(
                        value = imageElement.rotation,
                        onValueChange = { imageElement.rotation = it },
                        valueRange = -180f..180f
                    )
                    // Optional controls for position, width, height can be added here
                }
                is ShapeElement -> {
                    val shapeElement = element // Smart cast
                    Text("Shape Controls: ${shapeElement.type}", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 4.dp))

                    Text("Width: ${shapeElement.width.value.roundToInt()}.dp", fontSize = 12.sp)
                    Slider(value = shapeElement.width.value, onValueChange = { shapeElement.width = it.dp }, valueRange = 10f..500f)
                    Spacer(modifier = Modifier.height(4.dp))

                    Text("Height: ${shapeElement.height.value.roundToInt()}.dp", fontSize = 12.sp)
                    Slider(value = shapeElement.height.value, onValueChange = { shapeElement.height = it.dp }, valueRange = 10f..500f)
                    Spacer(modifier = Modifier.height(4.dp))

                    if (shapeElement.type == ShapeType.RECTANGLE) {
                        Text("Corner Radius: ${shapeElement.cornerRadius.value.roundToInt()}.dp", fontSize = 12.sp)
                        Slider(value = shapeElement.cornerRadius.value, onValueChange = { shapeElement.cornerRadius = it.dp }, valueRange = 0f..(Math.min(shapeElement.width.value, shapeElement.height.value) / 2f))
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Text("Rotation: ${shapeElement.rotation.roundToInt()}°", fontSize = 12.sp)
                    Slider(value = shapeElement.rotation, onValueChange = { shapeElement.rotation = it }, valueRange = -180f..180f)
                    Spacer(modifier = Modifier.height(4.dp))

                    Text("Stroke Width: ${shapeElement.strokeWidth.value.roundToInt()}.dp", fontSize = 12.sp)
                    Slider(value = shapeElement.strokeWidth.value, onValueChange = { shapeElement.strokeWidth = it.dp }, valueRange = 0f..20f)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row {
                        Button(onClick = { /* TODO: Implement fill color picker */ }, modifier = Modifier.weight(1f).padding(end = 4.dp)) { Text("Fill Color", fontSize = 10.sp) }
                        Button(onClick = { /* TODO: Implement stroke color picker */ }, modifier = Modifier.weight(1f).padding(start = 4.dp)) { Text("Stroke Color", fontSize = 10.sp) }
                    }
                }
            }
        }
    }
}
