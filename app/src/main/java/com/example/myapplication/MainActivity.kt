package com.example.myapplication

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize and start the media player
        mediaPlayer = MediaPlayer.create(this, R.raw.weightless).apply {
            isLooping = true
            start()
        }

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val languageIndex = remember { mutableStateOf(0) }

                NavHost(navController = navController, startDestination = "main") {
                    composable("main") {
                        MainScreen(
                            mediaPlayer = mediaPlayer,
                            navController = navController,
                            languageIndex = languageIndex
                        )
                    }
                    composable(
                        route = "imageScreen/{languageIndex}",
                        arguments = listOf(navArgument("languageIndex") { defaultValue = 0 })
                    ) { backStackEntry ->
                        val langIndex = backStackEntry.arguments?.getInt("languageIndex") ?: 0
                        ImageScreenWithLiveBackground(languageIndex = langIndex)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

@Composable
fun MainScreen(
    mediaPlayer: MediaPlayer?,
    navController: NavHostController,
    languageIndex: MutableState<Int>
) {
    var isPlaying by remember { mutableStateOf(mediaPlayer?.isPlaying ?: true) }
    var greetingIndex by remember { mutableStateOf(0) }

    val greetings = listOf(
        "Hello!" to "English",
        "Hola!" to "Español",
        "Bonjour!" to "Français",
        "Hallo!" to "Deutsch",
        "你好!" to "汉语"
    )

    val nextTexts = listOf("Next", "Próximo", "Suivant", "Nächster", "下一个")

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .animatedGradientBackground(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var isVisible by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    isVisible = true
                }

                // Speaker Icon for mute/unmute
                Icon(
                    imageVector = if (isPlaying) {
                        ImageVector.vectorResource(id = R.drawable.ic_speaker)
                    } else {
                        ImageVector.vectorResource(id = R.drawable.ic_speaker_off)
                    },
                    contentDescription = if (isPlaying) "Mute" else "Unmute",
                    modifier = Modifier
                        .size(56.dp)
                        .padding(16.dp)
                        .clickable {
                            if (mediaPlayer?.isPlaying == true) {
                                mediaPlayer.pause()
                                isPlaying = false
                            } else {
                                mediaPlayer?.start()
                                isPlaying = true
                            }
                        }
                )

                Spacer(modifier = Modifier.height(16.dp))

                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(animationSpec = tween(durationMillis = 3000)),
                    exit = fadeOut(animationSpec = tween(durationMillis = 1000))
                ) {
                    AnimatedContent(targetState = greetings[languageIndex.value]) { greeting ->
                        Greeting(greeting.first, greeting.second)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LanguagePicker(
                    languages = greetings.map { it.second },
                    selectedIndex = languageIndex.value,
                    onValueChange = { newIndex ->
                        languageIndex.value = newIndex
                        greetingIndex = newIndex
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                AnimatedContent(targetState = nextTexts[languageIndex.value]) { nextText ->
                    Button(onClick = {
                        navController.navigate("imageScreen/${languageIndex.value}")
                    }) {
                        Text(nextText)
                    }
                }
            }
        }
    }
}

@Composable
fun ImageScreenWithLiveBackground(languageIndex: Int) {
    var currentIndex by remember { mutableStateOf(0) }
    val images = listOf(
        R.drawable.image1, // Replace with your image resource
        R.drawable.image2  // Replace with your image resource
    )
    val captions = listOf(
        listOf(
            "Click the word \"Add\" to be taken to the next page.",
            "Enter the name of the camera and its URL, then click \"Save.\""
        ),
        listOf(
            "Haga clic en la palabra \"Agregar\" para ir a la página siguiente.",
            "Ingrese el nombre de la cámara y su URL, luego haga clic en \"Guardar\"."
        ),
        listOf(
            "Cliquez sur le mot \"Ajouter\" pour être dirigé vers la page suivante.",
            "Entrez le nom de la caméra et son URL, puis cliquez sur \"Enregistrer\"."
        ),
        listOf(
            "Klicken Sie auf das Wort \"Hinzufügen\", um zur nächsten Seite zu gelangen.",
            "Geben Sie den Namen der Kamera und ihre URL ein und klicken Sie dann auf \"Speichern\"."
        ),
        listOf(
            "点击“添加”一词进入下一页。",
            "输入摄像机的名称和URL，然后点击“保存”。"
        )
    )

    val nextTexts = listOf("Next", "Próximo", "Suivant", "Nächster", "下一个")
    val finishTexts = listOf("Finish", "Finalizar", "Terminer", "Fertig", "完成")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .animatedGradientBackground()
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = images[currentIndex]),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .padding(16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = captions[languageIndex][currentIndex], fontSize = 20.sp, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                if (currentIndex < images.size - 1) {
                    currentIndex = (currentIndex + 1) % images.size
                } else {
                    // Handle finish action
                }
            }) {
                Text(text = if (currentIndex < images.size - 1) nextTexts[languageIndex] else finishTexts[languageIndex])
            }
        }
    }
}

@Composable
fun LanguagePicker(
    languages: List<String>,
    selectedIndex: Int,
    onValueChange: (Int) -> Unit
) {
    val visibleItems = 3
    val itemHeight = 56.dp

    LazyColumn(
        modifier = Modifier
            .height(itemHeight * visibleItems)
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    val newIndex = (selectedIndex - dragAmount / itemHeight.toPx()).roundToInt()
                        .coerceIn(0, languages.size - 1)
                    onValueChange(newIndex)
                }
            }
    ) {
        itemsIndexed(languages) { index, language ->
            LanguageItem(
                language = language,
                isSelected = index == selectedIndex,
                onClick = { onValueChange(index) }
            )
        }
    }
}

@Composable
fun LanguageItem(
    language: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(if (isSelected) Color.LightGray else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = language,
            fontSize = 20.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.Black else Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun Modifier.animatedGradientBackground(): Modifier {
    val infiniteTransition = rememberInfiniteTransition()
    val color1 by infiniteTransition.animateColor(
        initialValue = Color(0xFF1A3855),
        targetValue = Color(0xFF0F406E),
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val color2 by infiniteTransition.animateColor(
        initialValue = Color(0xFF0F406E),
        targetValue = Color(0xFF4F7192),
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    return this.background(
        brush = Brush.linearGradient(
            colors = listOf(color1, color2, Color(0xFF1A3855)),
            start = Offset(0f, 0f),
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        )
    )
}

@Composable
fun Greeting(greeting: String, language: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = greeting,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .background(
                    color = Color.LightGray,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        MainScreen(mediaPlayer = null, navController = rememberNavController(), languageIndex = remember { mutableStateOf(0) })
    }
}
