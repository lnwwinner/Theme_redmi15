package com.example.ui.screens

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.viewmodel.GenerationState
import com.example.viewmodel.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiGenerationScreen(viewModel: ThemeViewModel) {
    var prompt by remember { mutableStateOf("") }
    var selectedQuality by remember { mutableStateOf("Flash (General)") }
    var selectedRatio by remember { mutableStateOf("16:9") }
    var selectedSize by remember { mutableStateOf("1K") }

    val generationState by viewModel.generationState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Generate Assets (Gemini)") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = prompt,
                onValueChange = { prompt = it },
                label = { Text("Describe the wallpaper or icon") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Text("Quality", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Flash (General)", "Pro (Studio)").forEach { q ->
                    FilterChip(
                        selected = selectedQuality == q,
                        onClick = { selectedQuality = q },
                        label = { Text(q) }
                    )
                }
            }

            Text("Aspect Ratio", style = MaterialTheme.typography.titleMedium)
            val ratios = listOf("1:1", "4:3", "3:4", "16:9", "9:16", "21:9")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ratios.take(3).forEach { r ->
                    FilterChip(
                        selected = selectedRatio == r,
                        onClick = { selectedRatio = r },
                        label = { Text(r) }
                    )
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ratios.drop(3).forEach { r ->
                    FilterChip(
                        selected = selectedRatio == r,
                        onClick = { selectedRatio = r },
                        label = { Text(r) }
                    )
                }
            }

            Text("Image Size", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("1K", "2K", "4K").forEach { s ->
                    FilterChip(
                        selected = selectedSize == s,
                        onClick = { selectedSize = s },
                        label = { Text(s) }
                    )
                }
            }

            Button(
                onClick = {
                    val quality = if (selectedQuality.contains("Pro")) "Pro" else "Flash"
                    viewModel.generateImage(prompt, quality, selectedRatio, selectedSize)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = prompt.isNotBlank() && generationState !is GenerationState.Loading
            ) {
                if (generationState is GenerationState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Generate Image")
                }
            }

            when (val state = generationState) {
                is GenerationState.Success -> {
                    val bytes = Base64.decode(state.base64Image, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Generated Asset",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
                is GenerationState.Error -> {
                    Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                }
                else -> {}
            }
        }
    }
}
