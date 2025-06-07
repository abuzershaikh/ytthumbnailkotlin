package com.example.thumbnailmaker.ui

import androidx.compose.foundation.Image // For background
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text // For placeholder if no background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.CircleShape // For Circle ShapeElement
// RoundedCornerShape already imported
import androidx.compose.ui.text.font.FontStyle
import coil.compose.AsyncImage // For ImageElements
import coil.compose.rememberAsyncImagePainter // For background
import com.example.thumbnailmaker.model.ImageElement
import com.example.thumbnailmaker.model.TextElement
import com.example.thumbnailmaker.model.ShapeElement // Import ShapeElement
import com.example.thumbnailmaker.model.ShapeType // Import ShapeType
import com.example.thumbnailmaker.viewmodel.MainViewModel
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

// Enum for handle identification
enum class HandlePosition { TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT }

// Extension function to rotate an Offset
fun Offset.rotateBy(degrees: Float, center: Offset = Offset.Zero): Offset {
    val radians = Math.toRadians(degrees.toDouble())
    val cosAngle = cos(radians)
    val sinAngle = sin(radians)
    val xRelativeToCenter = this.x - center.x
    val yRelativeToCenter = this.y - center.y
    val newX = xRelativeToCenter * cosAngle - yRelativeToCenter * sinAngle + center.x
    val newY = xRelativeToCenter * sinAngle + yRelativeToCenter * cosAngle + center.y
    return Offset(newX.toFloat(), newY.toFloat())
}

@Composable
fun CanvasView(mainViewModel: MainViewModel) {
    val backgroundImageUri by mainViewModel.backgroundImageUri
    val minElementSize = 20.dp // Minimum size for elements during resize

    Box(
        modifier = Modifier.fillMaxSize()
        // .background(Color.LightGray) // Optional: Canvas background color if no image
    ) {
        // Display background image if selected
        backgroundImageUri?.let { uri ->
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = "Background Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Canvas Area - Pick a background or add elements")
        }

        // Display canvas elements
        mainViewModel.canvasElements.forEach { element ->
            if (element is ImageElement) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(element.position.x.roundToInt(), element.position.y.roundToInt()) }
                        .size(element.width, element.height)
                        .rotate(element.rotation)
                        .clip(RoundedCornerShape(element.cornerRadius))
                        .pointerInput(element) {
                            detectDragGestures { change, dragAmount ->
                                if (mainViewModel.selectedElement.value == element) {
                                    change.consume()
                                    element.position = Offset(
                                        element.position.x + dragAmount.x,
                                        element.position.y + dragAmount.y
                                    )
                                }
                            }
                        }
                        .clickable { mainViewModel.selectElement(element) }
                        .then(if (element.isSelected) Modifier.border(2.dp, Color.Cyan) else Modifier)
                ) {
                    AsyncImage(
                        model = element.imageUri,
                        contentDescription = "Image Element ${element.id}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    if (element.isSelected) {
                        val handleSizeDp = 16.dp; val handleSizePx = handleSizeDp.toPx()
                        HandlePosition.values().forEach { handlePos ->
                            val (initialHandleOffsetX, initialHandleOffsetY) = when (handlePos) {
                                HandlePosition.TOP_LEFT, HandlePosition.BOTTOM_LEFT -> -element.width.toPx() / 2
                                HandlePosition.TOP_RIGHT, HandlePosition.BOTTOM_RIGHT -> element.width.toPx() / 2
                            }; val rotatedHandleOffset = Offset(initialHandleOffsetX, when (handlePos) {
                                HandlePosition.TOP_LEFT, HandlePosition.TOP_RIGHT -> -element.height.toPx() / 2
                                HandlePosition.BOTTOM_LEFT, HandlePosition.BOTTOM_RIGHT -> element.height.toPx() / 2
                            }).rotateBy(element.rotation, Offset.Zero)
                            val handleBoxX = (element.width.toPx() / 2) + rotatedHandleOffset.x - (handleSizePx / 2)
                            val handleBoxY = (element.height.toPx() / 2) + rotatedHandleOffset.y - (handleSizePx / 2)
                            ResizeHandle(modifier = Modifier.offset { IntOffset(handleBoxX.roundToInt(), handleBoxY.roundToInt()) }.pointerInput(element, handlePos) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume(); val originalWidthPx = element.width.toPx(); val originalHeightPx = element.height.toPx()
                                    var newWidthPx = originalWidthPx; var newHeightPx = originalHeightPx
                                    when (handlePos) {
                                        HandlePosition.TOP_LEFT -> { newWidthPx -= dragAmount.x; newHeightPx -= dragAmount.y }
                                        HandlePosition.TOP_RIGHT -> { newWidthPx += dragAmount.x; newHeightPx -= dragAmount.y }
                                        HandlePosition.BOTTOM_LEFT -> { newWidthPx -= dragAmount.x; newHeightPx += dragAmount.y }
                                        HandlePosition.BOTTOM_RIGHT -> { newWidthPx += dragAmount.x; newHeightPx += dragAmount.y }
                                    }
                                    element.width = newWidthPx.toDp().coerceAtLeast(minElementSize); element.height = newHeightPx.toDp().coerceAtLeast(minElementSize)
                                    when (handlePos) {
                                        HandlePosition.TOP_LEFT -> element.position = Offset(element.position.x + (originalWidthPx - element.width.toPx()), element.position.y + (originalHeightPx - element.height.toPx()))
                                        HandlePosition.TOP_RIGHT -> element.position = Offset(element.position.x, element.position.y + (originalHeightPx - element.height.toPx()))
                                        HandlePosition.BOTTOM_LEFT -> element.position = Offset(element.position.x + (originalWidthPx - element.width.toPx()), element.position.y)
                                        else -> {}
                                    }
                                }
                            }, size = handleSizeDp)
                        }
                    }
                }
            } else if (element is TextElement) {
                Box( // Outer Box for positioning, rotation, and handles
                    modifier = Modifier
                        .offset { IntOffset(element.position.x.roundToInt(), element.position.y.roundToInt()) }
                        .size(element.width, element.height) // Text element now has explicit size
                        .rotate(element.rotation)
                        .pointerInput(element) { // For dragging the whole element
                            detectDragGestures { change, dragAmount ->
                                if (mainViewModel.selectedElement.value == element) {
                                    change.consume()
                                    element.position = Offset(
                                        element.position.x + dragAmount.x,
                                        element.position.y + dragAmount.y
                                    )
                                }
                            }
                        }
                        .clickable { mainViewModel.selectElement(element) }
                        .then(if (element.isSelected) Modifier.border(2.dp, Color.Green) else Modifier)
                ) {
                    Box( // Inner Box for text content, background, padding, and clipping
                        modifier = Modifier
                            .fillMaxSize() // Fill the outer sized Box
                            .clip(RoundedCornerShape(element.cornerRadius))
                            .background(element.backgroundColor)
                            .padding(element.padding),
                        contentAlignment = Alignment.Center // Center the Text within this inner Box
                    ) {
                        Text(
                            text = element.text,
                            color = element.color,
                            fontSize = element.fontSize,
                            fontWeight = if (element.isBold) FontWeight.Bold else FontWeight.Normal,
                            fontStyle = if (element.isItalic) FontStyle.Italic else FontStyle.Normal,
                        )
                    }
                    // Display Resize Handles if selected
                    if (element.isSelected) {
                        val handleSizeDp = 16.dp; val handleSizePx = handleSizeDp.toPx()
                        HandlePosition.values().forEach { handlePos ->
                            val (initialHandleOffsetX, initialHandleOffsetY) = when (handlePos) {
                                HandlePosition.TOP_LEFT, HandlePosition.BOTTOM_LEFT -> -element.width.toPx() / 2
                                HandlePosition.TOP_RIGHT, HandlePosition.BOTTOM_RIGHT -> element.width.toPx() / 2
                            }; val rotatedHandleOffset = Offset(initialHandleOffsetX, when (handlePos) {
                                HandlePosition.TOP_LEFT, HandlePosition.TOP_RIGHT -> -element.height.toPx() / 2
                                HandlePosition.BOTTOM_LEFT, HandlePosition.BOTTOM_RIGHT -> element.height.toPx() / 2
                            }).rotateBy(element.rotation, Offset.Zero)
                            val handleBoxX = (element.width.toPx() / 2) + rotatedHandleOffset.x - (handleSizePx / 2)
                            val handleBoxY = (element.height.toPx() / 2) + rotatedHandleOffset.y - (handleSizePx / 2)
                            ResizeHandle(modifier = Modifier.offset { IntOffset(handleBoxX.roundToInt(), handleBoxY.roundToInt()) }.pointerInput(element, handlePos) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume(); val originalWidthPx = element.width.toPx(); val originalHeightPx = element.height.toPx()
                                    var newWidthPx = originalWidthPx; var newHeightPx = originalHeightPx
                                    when (handlePos) {
                                        HandlePosition.TOP_LEFT -> { newWidthPx -= dragAmount.x; newHeightPx -= dragAmount.y }
                                        HandlePosition.TOP_RIGHT -> { newWidthPx += dragAmount.x; newHeightPx -= dragAmount.y }
                                        HandlePosition.BOTTOM_LEFT -> { newWidthPx -= dragAmount.x; newHeightPx += dragAmount.y }
                                        HandlePosition.BOTTOM_RIGHT -> { newWidthPx += dragAmount.x; newHeightPx += dragAmount.y }
                                    }
                                    element.width = newWidthPx.toDp().coerceAtLeast(minElementSize); element.height = newHeightPx.toDp().coerceAtLeast(minElementSize)
                                    when (handlePos) {
                                        HandlePosition.TOP_LEFT -> element.position = Offset(element.position.x + (originalWidthPx - element.width.toPx()), element.position.y + (originalHeightPx - element.height.toPx()))
                                        HandlePosition.TOP_RIGHT -> element.position = Offset(element.position.x, element.position.y + (originalHeightPx - element.height.toPx()))
                                        HandlePosition.BOTTOM_LEFT -> element.position = Offset(element.position.x + (originalWidthPx - element.width.toPx()), element.position.y)
                                        else -> {}
                                    }
                                }
                            }, size = handleSizeDp)
                        }
                    }
                }
            } else if (element is ShapeElement) {
                val shapeModifier = when (element.type) {
                    ShapeType.RECTANGLE -> RoundedCornerShape(element.cornerRadius)
                    ShapeType.CIRCLE -> CircleShape
                }
                Box(
                    modifier = Modifier
                        .offset { IntOffset(element.position.x.roundToInt(), element.position.y.roundToInt()) }
                        .size(element.width, element.height)
                        .rotate(element.rotation)
                        .clip(shapeModifier)
                        .background(element.fillColor)
                        .then(if (element.strokeWidth > 0.dp) { Modifier.border(element.strokeWidth, element.strokeColor, shapeModifier) } else { Modifier })
                        .pointerInput(element) {
                            detectDragGestures { change, dragAmount ->
                                if (mainViewModel.selectedElement.value == element) {
                                    change.consume()
                                    element.position = Offset(
                                        element.position.x + dragAmount.x,
                                        element.position.y + dragAmount.y
                                    )
                                }
                            }
                        }
                        .clickable { mainViewModel.selectElement(element) }
                        .then(if (element.isSelected) Modifier.border(2.dp, Color.Blue) else Modifier)
                ) {
                    if (element.isSelected) {
                        val handleSizeDp = 16.dp; val handleSizePx = handleSizeDp.toPx()
                        HandlePosition.values().forEach { handlePos ->
                            val (initialHandleOffsetX, initialHandleOffsetY) = when (handlePos) {
                                HandlePosition.TOP_LEFT, HandlePosition.BOTTOM_LEFT -> -element.width.toPx() / 2
                                HandlePosition.TOP_RIGHT, HandlePosition.BOTTOM_RIGHT -> element.width.toPx() / 2
                            }; val rotatedHandleOffset = Offset(initialHandleOffsetX, when (handlePos) {
                                HandlePosition.TOP_LEFT, HandlePosition.TOP_RIGHT -> -element.height.toPx() / 2
                                HandlePosition.BOTTOM_LEFT, HandlePosition.BOTTOM_RIGHT -> element.height.toPx() / 2
                            }).rotateBy(element.rotation, Offset.Zero)
                            val handleBoxX = (element.width.toPx() / 2) + rotatedHandleOffset.x - (handleSizePx / 2)
                            val handleBoxY = (element.height.toPx() / 2) + rotatedHandleOffset.y - (handleSizePx / 2)
                            ResizeHandle(modifier = Modifier.offset { IntOffset(handleBoxX.roundToInt(), handleBoxY.roundToInt()) }.pointerInput(element, handlePos) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume(); val originalWidthPx = element.width.toPx(); val originalHeightPx = element.height.toPx()
                                    var newWidthPx = originalWidthPx; var newHeightPx = originalHeightPx
                                    when (handlePos) {
                                        HandlePosition.TOP_LEFT -> { newWidthPx -= dragAmount.x; newHeightPx -= dragAmount.y }
                                        HandlePosition.TOP_RIGHT -> { newWidthPx += dragAmount.x; newHeightPx -= dragAmount.y }
                                        HandlePosition.BOTTOM_LEFT -> { newWidthPx -= dragAmount.x; newHeightPx += dragAmount.y }
                                        HandlePosition.BOTTOM_RIGHT -> { newWidthPx += dragAmount.x; newHeightPx += dragAmount.y }
                                    }
                                    element.width = newWidthPx.toDp().coerceAtLeast(minElementSize); element.height = newHeightPx.toDp().coerceAtLeast(minElementSize)
                                    when (handlePos) {
                                        HandlePosition.TOP_LEFT -> element.position = Offset(element.position.x + (originalWidthPx - element.width.toPx()), element.position.y + (originalHeightPx - element.height.toPx()))
                                        HandlePosition.TOP_RIGHT -> element.position = Offset(element.position.x, element.position.y + (originalHeightPx - element.height.toPx()))
                                        HandlePosition.BOTTOM_LEFT -> element.position = Offset(element.position.x + (originalWidthPx - element.width.toPx()), element.position.y)
                                        else -> {}
                                    }
                                }
                            }, size = handleSizeDp)
                        }
                    }
                }
            }
        }
    }
}
