package com.example.thumbnailmaker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ResizeHandle(
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
    color: Color = Color.Red
) {
    Box(
        modifier = modifier
            .size(size)
            .background(color, CircleShape)
    )
}
