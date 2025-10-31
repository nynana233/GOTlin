package com.cdt.gotlin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starkk.sdk.StarkKClient
import com.starkk.sdk.extensions.getHousesAsFlow
import com.starkk.sdk.models.Character
import com.starkk.sdk.models.House
import com.starkk.sdk.models.StarkKPage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StarkKSampleViewModel : ViewModel() {
    private val client = StarkKClient.Builder()
        .enableLogging(true)
        .build()

    private val _characters = MutableStateFlow<List<Character>>(emptyList())
    val characters: StateFlow<List<Character>> = _characters.asStateFlow()

    private val _houses = MutableStateFlow<List<House>>(emptyList())
    val houses: StateFlow<List<House>> = _houses.asStateFlow()

    private val _singleResult = MutableStateFlow("")
    val singleResult: StateFlow<String> = _singleResult.asStateFlow()

    private val _isCharactersLoading = MutableStateFlow(false)
    val isCharactersLoading: StateFlow<Boolean> = _isCharactersLoading.asStateFlow()

    private val _isHousesLoading = MutableStateFlow(false)
    val isHousesLoading: StateFlow<Boolean> = _isHousesLoading.asStateFlow()

    private val _isSingleQueryLoading = MutableStateFlow(false)
    val isSingleQueryLoading: StateFlow<Boolean> = _isSingleQueryLoading.asStateFlow()

    private val _currentCharacterPage = MutableStateFlow(1)
    val currentCharacterPage: StateFlow<Int> = _currentCharacterPage.asStateFlow()

    /** Cursor holding the last fetched character page (used for next/previous navigation). */
    private var lastCharacterPage: StarkKPage<Character>? = null

    /**
     * Example 1: Using pagination with manual page control
     */
    fun loadCharactersPage(page: Int, pageSize: Int) {
        viewModelScope.launch {
            _isCharactersLoading.value = true

            client.getCharacters(page = page, pageSize = pageSize)
                .onSuccess { starkKPage ->
                    _characters.value = starkKPage.items
                    _currentCharacterPage.value = starkKPage.currentPage
                    lastCharacterPage = starkKPage
                }
                .onFailure { exception ->
                    _singleResult.value = "Error loading characters: ${exception.message}"
                }

            _isCharactersLoading.value = false
        }
    }

    /**
     * Navigate to next character page using the stored cursor.
     */
    fun nextCharacterPage() {
        val cursor = lastCharacterPage ?: return
        if (!cursor.hasNext) return

        viewModelScope.launch {
            _isCharactersLoading.value = true

            client.nextCharacters(cursor)
                ?.onSuccess { starkKPage ->
                    _characters.value = starkKPage.items
                    _currentCharacterPage.value = starkKPage.currentPage
                    lastCharacterPage = starkKPage
                }
                ?.onFailure { exception ->
                    _singleResult.value = "Error loading characters: ${exception.message}"
                }

            _isCharactersLoading.value = false
        }
    }

    /**
     * Navigate to previous character page using the stored cursor.
     */
    fun previousCharacterPage() {
        val cursor = lastCharacterPage ?: return
        if (!cursor.hasPrevious) return

        viewModelScope.launch {
            _isCharactersLoading.value = true

            client.previousCharacters(cursor)
                ?.onSuccess { starkKPage ->
                    _characters.value = starkKPage.items
                    _currentCharacterPage.value = starkKPage.currentPage
                    lastCharacterPage = starkKPage
                }
                ?.onFailure { exception ->
                    _singleResult.value = "Error loading characters: ${exception.message}"
                }

            _isCharactersLoading.value = false
        }
    }

    /**
     * Example 2: Using Flow for houses — auto-pagination through all houses.
     */
    fun loadHousesFlow() {
        viewModelScope.launch {
            _isHousesLoading.value = true
            _houses.value = emptyList()

            try {
                client.getHousesAsFlow(pageSize = 50).collect { pageHouses ->
                    _houses.value = _houses.value + pageHouses
                }
            } catch (e: Exception) {
                _singleResult.value = "Error loading houses: ${e.message}"
            } finally {
                _isHousesLoading.value = false
            }
        }
    }

    /**
     * Example 3: Search character by name with onSuccess/onFailure chaining.
     */
    fun queryCharacterByName(name: String) {
        viewModelScope.launch {
            _isSingleQueryLoading.value = true
            _singleResult.value = "Searching for '$name'..."

            client.getCharactersByName(name, pageSize = 10)
                .onSuccess { page ->
                    if (page.items.isEmpty()) {
                        _singleResult.value = "No characters found matching '$name'"
                    } else {
                        val character = page.items.first()
                        _singleResult.value = buildString {
                            append("✓ Found!\n\n")
                            append("Name: ${character.name}\n")
                            append("Gender: ${character.gender}\n")
                            append("Culture: ${character.culture}\n")
                            append("Born: ${character.born}\n")
                            if (character.titles.isNotEmpty()) {
                                append("Titles: ${character.titles.joinToString(", ")}\n")
                            }
                            if (character.aliases.isNotEmpty()) {
                                append("Aliases: ${character.aliases.joinToString(", ")}\n")
                            }
                        }
                    }
                }
                .onFailure { exception ->
                    _singleResult.value = "✗ Error: ${exception.message}"
                }

            _isSingleQueryLoading.value = false
        }
    }

    /**
     * Example 4: Query houses by name.
     */
    fun queryHouseByName(name: String) {
        viewModelScope.launch {
            _isSingleQueryLoading.value = true
            _singleResult.value = "Searching for '$name'..."

            client.getHousesByName(name, pageSize = 10)
                .onSuccess { page ->
                    if (page.items.isEmpty()) {
                        _singleResult.value = "No houses found matching '$name'"
                    } else {
                        val house = page.items.first()
                        _singleResult.value = buildString {
                            append("✓ Found!\n\n")
                            append("Name: ${house.name}\n")
                            append("Region: ${house.region}\n")
                            if (house.words.isNotEmpty()) {
                                append("Words: \"${house.words}\"\n")
                            }
                            if (house.titles.isNotEmpty()) {
                                append("Titles: ${house.titles.take(2).joinToString(", ")}\n")
                            }
                        }
                    }
                }
                .onFailure { exception ->
                    _singleResult.value = "✗ Error: ${exception.message}"
                }

            _isSingleQueryLoading.value = false
        }
    }

    /**
     * Example 5: Query books with pagination info.
     */
    fun queryBook() {
        viewModelScope.launch {
            _isSingleQueryLoading.value = true
            _singleResult.value = "Fetching books..."

            client.getBooks(page = 1, pageSize = 5)
                .onSuccess { page ->
                    if (page.items.isEmpty()) {
                        _singleResult.value = "No books found"
                    } else {
                        val book = page.items.random()
                        _singleResult.value = buildString {
                            append("✓ Found!\n\n")
                            append("Title: ${book.name}\n")
                            append("ISBN: ${book.isbn}\n")
                            append("Released: ${book.released}\n")
                            append("Pages: ${book.numberOfPages}\n")
                            append("Authors: ${book.authors.joinToString(", ")}\n")
                            append("\n📊 Pagination Info:\n")
                            append("Has Next: ${page.hasNext}\n")
                            append("Total Books (this page): ${page.items.size}")
                        }
                    }
                }
                .onFailure { exception ->
                    _singleResult.value = "✗ Error: ${exception.message}"
                }

            _isSingleQueryLoading.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        client.close()
    }
}

