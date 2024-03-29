package com.furqoncreative.generai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.furqoncreative.generai.ui.theme.GeneraiTheme
import com.furqoncreative.generai.ui.theme.Typography
import com.google.ai.client.generativeai.GenerativeModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GeneraiTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val generativeModel = GenerativeModel(
                        modelName = "gemini-pro", apiKey = BuildConfig.apiKey
                    )
                    val viewModel = ContentGeneratorViewModel(generativeModel)
                    ContentGeneratorRoute(viewModel)
                }
            }
        }
    }
}

@Composable
internal fun ContentGeneratorRoute(
    contentGeneratorViewModel: ContentGeneratorViewModel = viewModel()
) {
    val summarizeUiState by contentGeneratorViewModel.uiState.collectAsState()

    ContentGeneratorScreen(summarizeUiState,
        onSummarizeClicked = { inputText, format, tone, length ->
            contentGeneratorViewModel.generateContent(
                topic = inputText, format = format, tone = tone, length = length
            )
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentGeneratorScreen(
    uiState: ContentGeneratorUiState = ContentGeneratorUiState.Initial,
    onSummarizeClicked: (String, Format, Tone, Length) -> Unit = { _: String, _: Format, _: Tone, _: Length -> }
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        TopAppBar(title = {
            Text(text = "Generai", style = Typography.titleLarge)
        })

        ContentGenerator(
            onSummarizeClicked = { inputText, format, tone, length ->
                onSummarizeClicked(inputText, format, tone, length)
            })

        when (uiState) {
            ContentGeneratorUiState.Initial -> {
                // Nothing is shown
            }

            ContentGeneratorUiState.Loading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(all = 8.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    CircularProgressIndicator()
                }
            }

            is ContentGeneratorUiState.Success -> {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Result", style = Typography.titleMedium)
                    Row(modifier = Modifier.padding(all = 8.dp)) {
                        Icon(
                            Icons.Outlined.Person, contentDescription = "Person Icon"
                        )
                        Text(
                            text = uiState.outputText,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }

            is ContentGeneratorUiState.Error -> {
                Text(
                    text = uiState.errorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(all = 8.dp)
                )
            }
        }

    }
}

@Composable
fun ContentGenerator(
    modifier: Modifier = Modifier,
    onSummarizeClicked: (String, Format, Tone, Length) -> Unit = { _: String, _: Format, _: Tone, _: Length -> }
) {
    var topic by remember { mutableStateOf("") }
    var selectedFormat by remember { mutableStateOf(Format.PARAGRAPH) }
    var selectedTone by remember { mutableStateOf(Tone.CASUAL) }
    var selectedLength by remember { mutableStateOf(Length.MEDIUM) }

    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = stringResource(R.string.topic_label), style = Typography.titleMedium)

        TextField(
            value = topic,
            maxLines = 10,
            placeholder = { Text(stringResource(R.string.topic_hint)) },
            onValueChange = { topic = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        )

        Text(text = stringResource(R.string.tone_label), style = Typography.titleMedium)

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Tone.entries.forEach { tone ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    RadioButton(
                        selected = (tone == selectedTone),
                        onClick = { selectedTone = tone }
                    )
                    Text(
                        text = tone.name,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        Text(text = stringResource(R.string.format_label), style = Typography.titleMedium)

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Format.entries.forEach { format ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    RadioButton(
                        selected = (format == selectedFormat),
                        onClick = { selectedFormat = format }
                    )
                    Text(
                        text = format.name.replace("_", " "),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        Text(text = stringResource(R.string.length_label), style = Typography.titleMedium)

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Length.entries.forEach { length ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    RadioButton(
                        selected = (length == selectedLength),
                        onClick = { selectedLength = length }
                    )
                    Text(
                        text = length.name,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        Button(
            onClick = {
                onSummarizeClicked(topic, selectedFormat, selectedTone, selectedLength)
            },
            modifier = Modifier
                .padding(all = 4.dp)
                .fillMaxWidth()
                .height(55.dp)
        ) {
            Text(stringResource(R.string.generate_content_label))
        }


    }
}

@Composable
@Preview(showSystemUi = true)
fun ContentGeneratorScreenPreview() {
    ContentGeneratorScreen()
}