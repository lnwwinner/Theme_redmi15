package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconPackScreen(onBack: () -> Unit) {
    val colors = listOf(Color(0xFFEF4444), Color(0xFF10B981), Color(0xFF3B82F6), Color(0xFF9333EA), Color(0xFFEC4899), Color(0xFFFB923C))
    val icons = listOf(Icons.Default.Phone, Icons.Default.Message, Icons.Default.Camera, Icons.Default.Settings, Icons.Default.BrowserUpdated, Icons.Default.Map)
    
    var selectedColor by remember { mutableStateOf(colors[3]) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ปรับแต่งไอคอน (Icon Pack)", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text("เลือกสีไอคอน", fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                colors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(3.dp, if (selectedColor == color) MaterialTheme.colorScheme.onBackground else Color.Transparent, CircleShape)
                            .clickable { selectedColor = color }
                    )
                }
            }

            Text("ตัวอย่างไอคอน", fontWeight = FontWeight.Bold)
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(icons.size) { index ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(selectedColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icons[index],
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = selectedColor
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { /* Apply */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("นำไปใช้ (Apply to Theme)", fontWeight = FontWeight.Bold)
            }
        }
    }
}
