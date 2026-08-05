package com.example.ui.screens

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeEditorScreen(viewModel: ThemeViewModel) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    val themeState by viewModel.themeState.collectAsState()
    
    val categories = listOf(
        CategoryItem("lockStyle", "รูปแบบการล็อก", Icons.Default.Lock),
        CategoryItem("statusBar", "แถบสถานะ", Icons.Default.SignalCellular4Bar),
        CategoryItem("icons", "ไอคอน", Icons.Default.Apps),
        CategoryItem("messaging", "ข้อความ", Icons.Default.Message),
        CategoryItem("dialer", "แป้นโทร", Icons.Default.Dialpad),
        CategoryItem("favorites", "ไอคอนที่ใช้บ่อย", Icons.Default.Home),
        CategoryItem("bootAnimation", "ภาพเคลื่อนไหวขณะเปิดเครื่อง", Icons.Default.Animation)
    )

    if (selectedCategory == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("ปรับแต่งธีม (Customize Theme)") }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { /* Preview Theme */ }) {
                    Icon(Icons.Default.Preview, contentDescription = "Preview")
                }
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                // Generated wallpaper banner
                if (themeState.generatedWallpaperBase64 != null) {
                    val bytes = Base64.decode(themeState.generatedWallpaperBase64, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Generated Wallpaper",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(16.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(categories.size) { index ->
                        val item = categories[index]
                        CategoryCard(item) {
                            selectedCategory = item.id
                        }
                    }
                }
            }
        }
    } else {
        val cat = categories.find { it.id == selectedCategory }
        CategorySelectionScreen(
            title = cat?.title ?: "",
            onBack = { selectedCategory = null },
            onSelect = { viewModel.updateThemeCategory(cat!!.id, it) }
        )
    }
}

@Composable
fun CategoryCard(item: CategoryItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.5f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = item.title, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelectionScreen(title: String, onBack: () -> Unit, onSelect: (Int) -> Unit) {
    var selectedIndex by remember { mutableStateOf(0) }
    val options = listOf("ค่าเริ่มต้น", "ไร้ขีดจำกัด", "สำรองข้อมูลธีม", "Black Dragon 26", "bluestyle", "Colorful colors")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.padding(padding),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(options.size) { index ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        selectedIndex = index
                        onSelect(index)
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.5f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.LightGray)
                            .border(
                                width = if (selectedIndex == index) 3.dp else 0.dp,
                                color = if (selectedIndex == index) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        // Placeholder for theme preview image
                        Icon(
                            Icons.Default.Image,
                            contentDescription = null,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = options[index], fontSize = 12.sp, maxLines = 1)
                }
            }
        }
    }
}

data class CategoryItem(val id: String, val title: String, val icon: ImageVector)
