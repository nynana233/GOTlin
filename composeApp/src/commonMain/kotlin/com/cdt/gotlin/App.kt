package com.cdt.gotlin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.starkk.sdk.models.Character
import com.starkk.sdk.models.House
import kotlinx.coroutines.launch

@Composable
fun App() {
    MaterialTheme {
        StarkKSampleApp()
    }
}

@Composable
fun StarkKSampleApp(modifier: Modifier = Modifier) {
    val viewModel = remember { StarkKSampleViewModel() }
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "\uD83D\uDC3A StarkK SDK Demo",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Tab Selection
        TabRow(selectedTabIndex = selectedTab, modifier = Modifier.fillMaxWidth()) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Houses") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Characters") }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Single Query") }
            )
        }

        // Tab Content
        when (selectedTab) {
            0 -> HousesTab(viewModel)
            1 -> CharactersTab(viewModel)
            2 -> SingleQueryTab(viewModel)
        }
    }
}

@Composable
fun CharactersTab(viewModel: StarkKSampleViewModel) {
    val characters by viewModel.characters.collectAsState()
    val isLoading by viewModel.isCharactersLoading.collectAsState()
    val currentPage by viewModel.currentCharacterPage.collectAsState()
    val scope = rememberCoroutineScope()
    var pageSize by remember { mutableIntStateOf(20) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Page Size: $pageSize",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            viewModel.previousCharacterPage()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading && currentPage > 1
                ) {
                    Text("← Prev")
                }

                Text(
                    text = "Page $currentPage",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                Button(
                    onClick = {
                        scope.launch {
                            viewModel.nextCharacterPage()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading
                ) {
                    Text("Next →")
                }
            }
        }

        Button(
            onClick = {
                scope.launch {
                    viewModel.loadCharactersPage(page = 1, pageSize = pageSize)
                }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(end = 8.dp).size(32.dp),
                    strokeWidth = 2.dp
                )
            }
            Text(if (isLoading) "Loading..." else "Reload Pages")
        }

        if (characters.isEmpty() && !isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Tap 'Reload Pages' to fetch paginated character data", fontSize = 14.sp)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(characters) { character ->
                    CharacterCard(character)
                }
            }
        }
    }
}

@Composable
fun HousesTab(viewModel: StarkKSampleViewModel) {
    val houses by viewModel.houses.collectAsState()
    val isLoading by viewModel.isHousesLoading.collectAsState()
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = {
                scope.launch {
                    viewModel.loadHousesFlow()
                }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(end = 8.dp),
                    strokeWidth = 2.dp
                )
            }
            Text(if (isLoading) "Loading..." else "Load All Houses (Flow)")
        }

        if (houses.isEmpty() && !isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Tap 'Load All Houses' to fetch data using Flow", fontSize = 14.sp)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(houses) { house ->
                    HouseCard(house)
                }
            }
        }
    }
}

@Composable
fun SingleQueryTab(viewModel: StarkKSampleViewModel) {
    val result by viewModel.singleResult.collectAsState()
    val isLoading by viewModel.isSingleQueryLoading.collectAsState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Single Query Example (Result<T> Error Handling)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Button(
            onClick = {
                scope.launch {
                    viewModel.queryCharacterByName("Jon Snow")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(end = 8.dp),
                    strokeWidth = 2.dp
                )
            }
            Text(if (isLoading) "Searching..." else "Search 'Jon Snow'")
        }

        Button(
            onClick = {
                scope.launch {
                    viewModel.queryHouseByName("House Stark")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(end = 8.dp),
                    strokeWidth = 2.dp
                )
            }
            Text(if (isLoading) "Searching..." else "Search 'House Stark'")
        }

        Button(
            onClick = {
                scope.launch {
                    viewModel.queryBook()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(end = 8.dp),
                    strokeWidth = 2.dp
                )
            }
            Text(if (isLoading) "Loading..." else "Get Random Book")
        }

        if (result.isNotEmpty()) {
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text(
                text = "Result:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text(text = result, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun CharacterCard(character: Character) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = character.name.ifEmpty { "Unknown" },
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "Gender: ${character.gender.ifEmpty { "N/A" }}", fontSize = 12.sp)
                Text(text = "Culture: ${character.culture.ifEmpty { "N/A" }}", fontSize = 12.sp)
            }
            if (character.titles.isNotEmpty()) {
                Text(
                    text = "Titles: ${character.titles.joinToString(", ")}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun HouseCard(house: House) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = house.name.ifEmpty { "Unknown" },
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(text = "Region: ${house.region.ifEmpty { "N/A" }}", fontSize = 12.sp)
            if (house.words.isNotEmpty()) {
                Text(
                    text = "\"${house.words}\"",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.SemiBold
                )
            }
            if (house.titles.isNotEmpty()) {
                Text(
                    text = "Titles: ${house.titles.take(2).joinToString(", ")}",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }
        }
    }
}