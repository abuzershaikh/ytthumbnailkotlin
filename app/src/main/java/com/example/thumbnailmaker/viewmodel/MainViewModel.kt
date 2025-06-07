package com.example.thumbnailmaker.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.thumbnailmaker.model.EditableElement
import com.example.thumbnailmaker.model.ImageElement
import com.example.thumbnailmaker.model.TextElement
import com.example.thumbnailmaker.model.ShapeElement // Now needed
import com.example.thumbnailmaker.model.ShapeType // For addShapeElement


class MainViewModel : ViewModel() {
    val backgroundImageUri = mutableStateOf<Uri?>(null)
    val selectedElement = mutableStateOf<EditableElement?>(null)
    val canvasElements = mutableStateListOf<EditableElement>()

    fun setBackgroundImageUri(uri: Uri) {
        backgroundImageUri.value = uri
    }

    fun selectElement(element: EditableElement?) {
        // Deselect previously selected element if it's an ImageElement (or other types that support isSelected)
        selectedElement.value?.let { currentSelected ->
            when (currentSelected) {
                is ImageElement -> currentSelected.isSelected = false
                is TextElement -> currentSelected.isSelected = false
                is ShapeElement -> currentSelected.isSelected = false
                // Add other types here if they gain an isSelected property
                else -> {} // No isSelected property to worry about
            }
        }

        // Select the new element
        if (element != null) {
            when (element) {
                is ImageElement -> element.isSelected = true
                is TextElement -> element.isSelected = true
                is ShapeElement -> element.isSelected = true
                // Add other types here
                else -> {}
            }
        }
        selectedElement.value = element
        Log.d("ViewModel", "Selected element: $element, All elements: ${canvasElements.joinToString { it.id }}")
    }

    fun deleteSelectedElement() {
        selectedElement.value?.let { elementToDelete ->
            Log.d("ViewModel", "Deleting element: ${elementToDelete.id}")
            canvasElements.remove(elementToDelete)
            selectElement(null) // Clear selection
        }
    }

    fun addImageElement(uri: Uri) {
        val newImageElement = ImageElement(
            id = "img_${System.currentTimeMillis()}",
            imageUri = uri
        )
        canvasElements.add(newImageElement)
        selectElement(newImageElement) // Select the newly added image
        Log.d("ViewModel", "Added image element: ${newImageElement.id}")
    }

    fun addTextElement() {
        val newTextElement = TextElement(
            id = "txt_${System.currentTimeMillis()}"
            // Other properties will use defaults from TextElement data class
        )
        canvasElements.add(newTextElement)
        selectElement(newTextElement) // Select the newly added text element
        Log.d("ViewModel", "Added text element: ${newTextElement.id}")
    }

    fun addShapeElement(shapeType: ShapeType) {
        val newShapeElement = ShapeElement(
            id = "shp_${System.currentTimeMillis()}",
            type = shapeType
        )
        // Specific adjustments for circle if needed (e.g. ensuring width=height or specific cornerRadius)
        // if (shapeType == ShapeType.CIRCLE) {
        //    newShapeElement.height = newShapeElement.width // Example
        // }
        canvasElements.add(newShapeElement)
        selectElement(newShapeElement)
        Log.d("ViewModel", "Added shape element: ${newShapeElement.id}, type: $shapeType")
    }

    fun lockSelectedElement() {
        selectedElement.value?.let {
            Log.d("ViewModel", "Toggling lock for element: ${it.id}")
            // Placeholder for lock logic
        }
    }
}
