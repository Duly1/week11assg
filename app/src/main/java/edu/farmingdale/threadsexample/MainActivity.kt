package edu.farmingdale.threadsexample

import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import edu.farmingdale.threadsexample.ui.theme.ThreadsExampleTheme
import kotlinx.coroutines.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThreadsExampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CountDownActivity(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun CountDownActivity(modifier: Modifier = Modifier) {
    var timerValue by remember { mutableStateOf(30) } // Initial timer value 30 seconds
    var isRunning by remember { mutableStateOf(false) }
    var timerText by remember { mutableStateOf(timerValue.toString()) }
    var textColor by remember { mutableStateOf(Color.Black) }
    var fontWeight by remember { mutableStateOf(FontWeight.Normal) }

    val scope = rememberCoroutineScope()

    // Countdown Timer Logic
    if (isRunning) {
        // Timer countdown logic
        object : CountDownTimer((timerValue * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerValue = (millisUntilFinished / 1000).toInt()
                timerText = timerValue.toString()

                // Change text color and font during last 10 seconds
                if (timerValue <= 10) {
                    textColor = Color.Red
                    fontWeight = FontWeight.Bold
                }
            }

            override fun onFinish() {
                timerValue = 0
                timerText = "Time's up!"
                // Play sound when timer ends
                // Here you can trigger a sound to play
            }
        }.start()
    }

    // Reset Timer Button
    fun resetTimer() {
        timerValue = 30
        timerText = timerValue.toString()
        isRunning = false
        textColor = Color.Black
        fontWeight = FontWeight.Normal
    }

    Column(
            modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
                text = timerText,
                fontSize = 48.sp,
                fontWeight = fontWeight,
                color = textColor,
                modifier = Modifier.padding(bottom = 16.dp)
        )

        // Start/Stop Timer Button
        Button(onClick = {
            isRunning = !isRunning
        }) {
            Text(text = if (isRunning) "Stop Timer" else "Start Timer")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Reset Timer Button
        Button(onClick = {
            resetTimer()
        }) {
            Text(text = "Reset Timer")
        }
    }
}

// ToDo 1: Fibonacci calculation in a background thread (No Background thread)
fun FibonacciDemoNoBgThrd(n: Int): Int {
    return if (n <= 1) n else FibonacciDemoNoBgThrd(n - 1) + FibonacciDemoNoBgThrd(n - 2)
}

// ToDo 2: Fibonacci calculation with Coroutine
@Composable
fun FibonacciDemoWithCoroutine(n: Int) {
    val fibonacciValue = remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(n) {
        scope.launch {
            fibonacciValue.value = withContext(Dispatchers.Default) {
                calculateFibonacci(n)
            }
        }
    }

    Text("Fibonacci of $n is: ${fibonacciValue.value}")
}

suspend fun calculateFibonacci(n: Int): Int {
    return if (n <= 1) n else calculateFibonacci(n - 1) + calculateFibonacci(n - 2)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ThreadsExampleTheme {
        CountDownActivity()
    }
}
