package com.example.thumbnailmaker.model

import android.net.Uri
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

sealed interface EditableElement {
    val id: String // Unique ID for each element, ensured it's val
    // Common properties like position, size, rotation can be added later
}

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight // Not used in data class, but good for context
import androidx.compose.ui.text.font.FontStyle // Not used in data class, but good for context
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

data class TextElement(
    override val id: String, // id is val
    var text: String by mutableStateOf("Sample Text"),
    var position: Offset by mutableStateOf(Offset(50f, 50f)),
    var fontSize: TextUnit by mutableStateOf(24.sp),
    var color: Color by mutableStateOf(Color.Black),
    // var fontFamilyName: String by mutableStateOf("Default"), // For later font selection
    var isBold: Boolean by mutableStateOf(false),
    var isItalic: Boolean by mutableStateOf(false),
    var backgroundColor: Color by mutableStateOf(Color.Transparent),
    var cornerRadius: Dp by mutableStateOf(0.dp), // For text box background
    var padding: Dp by mutableStateOf(4.dp),
    var rotation: Float by mutableStateOf(0f),
    var isSelected: Boolean by mutableStateOf(false),
    var width: Dp by mutableStateOf(150.dp), // Added default width
    var height: Dp by mutableStateOf(50.dp)  // Added default height
) : EditableElement

data class ImageElement(
    override val id: String, // id is val
    val imageUri: Uri, // URI of the image, changed to val as it's fundamental
    var position: Offset by mutableStateOf(Offset(100f, 100f)),
    var width: Dp by mutableStateOf(150.dp),
    var height: Dp by mutableStateOf(150.dp),
    var rotation: Float by mutableStateOf(0f),
    var cornerRadius: Dp by mutableStateOf(0.dp),
    var isSelected: Boolean by mutableStateOf(false) // To show selection highlight
) : EditableElement

data class ShapeElement(
    override val id: String, // id is val
    var type: ShapeType = ShapeType.RECTANGLE,
    var position: Offset by mutableStateOf(Offset(70f, 70f)),
    var width: Dp by mutableStateOf(100.dp),
    var height: Dp by mutableStateOf(100.dp), // For Circle, width and height ideally kept same for true circle
    var fillColor: Color by mutableStateOf(Color.LightGray), // Changed default for visibility
    var strokeColor: Color by mutableStateOf(Color.Black),
    var strokeWidth: Dp by mutableStateOf(1.dp), // Default to 1dp stroke for visibility
    var cornerRadius: Dp by mutableStateOf(0.dp), // Applicable to RECTANGLE
    var rotation: Float by mutableStateOf(0f),
    var isSelected: Boolean by mutableStateOf(false)
) : EditableElement
