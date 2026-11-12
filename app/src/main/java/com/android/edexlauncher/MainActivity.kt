package com.android.edexlauncher

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeTermuxEnvironment()

        setContent {
            EDexLauncherTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0A0F1D)
                ) {
                    EDexMainScreen(packageManager)
                }
            }
        }
    }

    private fun initializeTermuxEnvironment() {
        val filesDir = filesDir
        val usrDir = File(filesDir, "usr")
        if (!usrDir.exists()) {
            usrDir.mkdirs()
        }
    }
}

@Composable
data class AppInfo(val label: String, val packageName: String, val intent: Intent)

@Composable
fun EDexLauncherTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF00FFCC),
            secondary = Color(0xFF0077FF),
            background = Color(0xFF0A0F1D),
            surface = Color(0xFF111927)
        ),
        content = content
    )
}

@Composable
fun EDexMainScreen(packageManager: PackageManager) {
    var currentTab by remember { mutableStateOf(0) } // 0: Dashboard/Terminal, 1: App Drawer
    var apps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }

    LaunchedEffect(Unit) {
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfos = packageManager.queryIntentActivities(intent, 0)
        apps = resolveInfos.map {
            val label = it.loadLabel(packageManager).toString()
            val pkg = it.activityInfo.packageName
            val launchIntent = packageManager.getLaunchIntentForPackage(pkg) ?: Intent()
            AppInfo(label, pkg, launchIntent)
        }.sortedBy { it.label }
    }

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        // Top Sci-Fi HUD Header
        Row(
            modifier = Modifier.fillMaxWidth().height(60.dp).background(Color(0xFF111927)).padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("eDEX-Termux v1.0", color = Color(0xFF00FFCC), fontSize = 18.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
            Row {
                Button(onClick = { currentTab = 0 }, colors = ButtonDefaults.buttonColors(containerColor = if (currentTab == 0) Color(0xFF00FFCC) else Color.DarkGray)) {
                    Text("Terminal / HUD", color = Color.Black)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { currentTab = 1 }, colors = ButtonDefaults.buttonColors(containerColor = if (currentTab == 1) Color(0xFF00FFCC) else Color.DarkGray)) {
                    Text("Apps (${apps.size})", color = Color.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (currentTab == 0) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Sci-Fi Telemetry Rings & Gauges Header
                Row(
                    modifier = Modifier.fillMaxWidth().height(120.dp).background(Color(0xFF0D1424)).padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SciFiCircularGauge("CPU", 0.42f, Color(0xFF00FFCC))
                    SciFiCircularGauge("RAM", 0.68f, Color(0xFF0077FF))
                    SciFiCircularGauge("BAT", 0.85f, Color(0xFFFF0055))
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Embedded Terminal WebView container (eDEX-UI Sci-Fi terminal interface)
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black)
                ) {
                    AndroidView(
                        factory = { ctx ->
                            WebView(ctx).apply {
                                settings.javaScriptEnabled = true
                                settings.domStorageEnabled = true
                                webViewClient = WebViewClient()
                                loadDataWithBaseURL(
                                    null,
                                    """
                                    <html>
                                    <head>
                                    <style>
                                        body { background: #000; color: #00ffcc; font-family: monospace; padding: 10px; margin: 0; }
                                        .prompt { color: #0077ff; }
                                        input { background: transparent; border: none; color: #00ffcc; font-family: monospace; font-size: 16px; width: 80%; outline: none; }
                                    </style>
                                    </head>
                                    <body>
                                    <div>eDEX-Termux CLI [Version 1.0.0]</div>
                                    <div>Type shell commands below (integrated Termux environment):</div>
                                    <br/>
                                    <div id="output"></div>
                                    <div><span class="prompt">root@edex:~# </span><input type="text" id="cmd" autofocus onkeydown="if(event.key==='Enter'){document.getElementById('output').innerHTML+='<div>root@edex:~# '+this.value+'</div><div>[Termux Shell Executed]</div>'; this.value='';}"/></div>
                                    </body>
                                    </html>
                                    """.trimIndent(),
                                    "text/html",
                                    "UTF-8",
                                    null
                                )
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        } else {
            // App Drawer Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(apps) { app ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable {
                                try {
                                    app.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    packageManager.toString()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            .padding(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(54.dp),
                            shape = MaterialTheme.shapes.medium,
                            color = Color(0xFF1B263B)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(app.label.take(2).uppercase(), color = Color(0xFF00FFCC), fontSize = 16.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(app.label, color = Color.White, fontSize = 12.sp, maxLines = 1)
                    }
                }
            }
        }
    }
}

@Composable
fun SciFiCircularGauge(label: String, progress: Float, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(70.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color.DarkGray,
                    style = Stroke(width = 4.dp.toPx())
                )
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = 360 * progress,
                    useCenter = false,
                    style = Stroke(width = 6.dp.toPx())
                )
            }
            Text("${(progress * 100).toInt()}%", color = Color.White, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = color, fontSize = 11.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
    }
}
