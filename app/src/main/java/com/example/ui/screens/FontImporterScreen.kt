package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontImporterScreen(onBack: () -> Unit) {
    var selectedFontUri by remember { mutableStateOf<Uri?>(null) }
    
    val presetFonts = listOf("MiSans (Default)", "Roboto", "Prompt", "Sarabun", "Kanit", "Chakra Petch")
    var selectedPresetFont by remember { mutableStateOf(presetFonts[0]) }

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedFontUri = uri
        selectedPresetFont = "" // Clear preset selection if user imports custom font
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("นำเข้าแบบอักษร (Fonts)", fontWeight = FontWeight.Bold) },
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
            Text("คลังฟอนต์สำเร็จรูป (Preset Fonts)", fontWeight = FontWeight.Bold)
            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(presetFonts.size) { index ->
                    FilterChip(
                        selected = selectedPresetFont == presetFonts[index],
                        onClick = {
                            selectedPresetFont = presetFonts[index]
                            selectedFontUri = null // Clear imported font if user selects preset
                        },
                        label = { Text(presetFonts[index], fontWeight = FontWeight.Medium) },
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                    )
                }
            }

            Text("หรือนำเข้าฟอนต์ของคุณเอง", fontWeight = FontWeight.Bold)
            Button(
                onClick = { launcher.launch("*/*") },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
            ) {
                Text("นำเข้าไฟล์ฟอนต์ (.ttf, .otf)", fontWeight = FontWeight.Bold)
            }

            if (selectedFontUri != null) {
                Text("ไฟล์ที่นำเข้า: ${selectedFontUri?.lastPathSegment ?: "font.ttf"}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
            } else if (selectedPresetFont.isNotEmpty()) {
                Text("ฟอนต์ที่เลือก: $selectedPresetFont", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("ตัวอย่าง (Preview)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    
                    Text("ส่วนหัว (Header)", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("นี่คือข้อความตัวอย่างสำหรับเนื้อหาหลัก (Body text) เพื่อให้คุณเห็นว่าฟอนต์นี้มีลักษณะอย่างไรเมื่อนำไปใช้จริงในระบบ HyperOS", fontSize = 16.sp)
                    
                    Button(onClick = {}) {
                        Text("ปุ่มตัวอย่าง (Button)")
                    }
                }
            }
        }
    }
}
