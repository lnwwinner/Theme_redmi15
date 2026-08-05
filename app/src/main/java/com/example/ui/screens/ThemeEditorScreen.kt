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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Message
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
        CategoryItem("palette", "สร้างจานสี", Icons.Default.Palette),
        CategoryItem("iconPack", "ปรับแต่งไอคอน", Icons.Default.FormatPaint),
        CategoryItem("fonts", "แบบอักษร", Icons.Default.FontDownload),
        CategoryItem("lockStyle", "รูปแบบการล็อก", Icons.Default.Lock),
        CategoryItem("statusBar", "แถบสถานะ", Icons.Default.SignalCellular4Bar),
        CategoryItem("icons", "ไอคอน", Icons.Default.Apps),
        CategoryItem("messaging", "ข้อความ", Icons.AutoMirrored.Filled.Message),
        CategoryItem("dialer", "แป้นโทร", Icons.Default.Dialpad),
        CategoryItem("favorites", "ไอคอนที่ใช้บ่อย", Icons.Default.Home),
        CategoryItem("bootAnimation", "ภาพเคลื่อนไหวขณะเปิดเครื่อง", Icons.Default.Animation)
    )

    if (selectedCategory == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("ปรับแต่งธีม", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                    ),
                    actions = {
                        val context = androidx.compose.ui.platform.LocalContext.current
                        IconButton(onClick = {
                            val uri = com.example.util.ExportManager.exportTheme(context, themeState)
                            if (uri != null) {
                                com.example.util.ExportManager.shareTheme(context, uri)
                            }
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Export MTZ")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { /* Preview Theme */ },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Preview, contentDescription = "Preview")
                }
            },
            containerColor = MaterialTheme.colorScheme.background
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
                                .height(220.dp)
                                .padding(horizontal = 24.dp, vertical = 16.dp)
                                .clip(RoundedCornerShape(32.dp))
                                .border(4.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(32.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(24.dp),
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
        when (selectedCategory) {
            "palette" -> PaletteGeneratorScreen(viewModel, onBack = { selectedCategory = null })
            "iconPack" -> IconPackScreen(onBack = { selectedCategory = null })
            "fonts" -> FontImporterScreen(onBack = { selectedCategory = null })
            else -> {
                val cat = categories.find { it.id == selectedCategory }
                CategorySelectionScreen(
                    title = cat?.title ?: "",
                    onBack = { selectedCategory = null },
                    onSelect = { viewModel.updateThemeCategory(cat!!.id, it) }
                )
            }
        }
    }
}

@Composable
fun CategoryCard(item: CategoryItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.2f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = item.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
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
                title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(24.dp),
            modifier = Modifier.padding(padding),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
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
                            .aspectRatio(0.45f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(
                                width = if (selectedIndex == index) 4.dp else 0.dp,
                                color = if (selectedIndex == index) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = RoundedCornerShape(20.dp)
                            )
                    ) {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = null,
                            modifier = Modifier.align(Alignment.Center).size(32.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = options[index],
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (selectedIndex == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

data class CategoryItem(val id: String, val title: String, val icon: ImageVector)
