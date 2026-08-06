package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(viewModel: ThemeViewModel) {
    val templates = listOf(
        TemplateItem("Minimalist", "สะอาดตา สบายตา", Color(0xFFF1F5F9), Color(0xFF64748B)),
        TemplateItem("Dark Mode", "ประหยัดแบตเตอรี่", Color(0xFF1E293B), Color(0xFF94A3B8)),
        TemplateItem("Vibrant", "สีสันสดใส", Color(0xFFFEF3C7), Color(0xFFF59E0B)),
        TemplateItem("Neon Cyber", "สไตล์ล้ำยุค", Color(0xFF0F172A), Color(0xFF38BDF8)),
        TemplateItem("Pastel Dream", "อ่อนหวาน นุ่มนวล", Color(0xFFFDF4FF), Color(0xFFE879F9)),
        TemplateItem("Nature", "แรงบันดาลใจจากธรรมชาติ", Color(0xFFF0FDF4), Color(0xFF22C55E))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("แม่แบบธีม (Templates)", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(24.dp),
            modifier = Modifier.padding(padding),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(templates.size) { index ->
                val template = templates[index]
                TemplateCard(template = template, onClick = {
                    // Update view model or select template
                })
            }
        }
    }
}

@Composable
fun TemplateCard(template: TemplateItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.7f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(template.bgColor)
                    .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
            ) {
                // Mockup UI in the template
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth(0.5f).height(12.dp).background(template.accentColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp)))
                    Box(modifier = Modifier.fillMaxWidth(0.8f).height(24.dp).background(template.accentColor, RoundedCornerShape(8.dp)))
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.size(32.dp).background(template.accentColor, RoundedCornerShape(8.dp)))
                        Box(modifier = Modifier.size(32.dp).background(template.accentColor, RoundedCornerShape(8.dp)))
                        Box(modifier = Modifier.size(32.dp).background(template.accentColor, RoundedCornerShape(8.dp)))
                        Box(modifier = Modifier.size(32.dp).background(template.accentColor, RoundedCornerShape(8.dp)))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = template.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = template.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

data class TemplateItem(val name: String, val description: String, val bgColor: Color, val accentColor: Color)
