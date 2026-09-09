package com.example.smartnotes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.smartnotes.data.Note
import com.example.smartnotes.viewmodel.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(navController: NavController, viewModel: NoteViewModel, noteId: Long) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var isPinned by remember { mutableStateOf(false) }
    var currentNote by remember { mutableStateOf<Note?>(null) }

    // Load existing note data securely
    LaunchedEffect(noteId) {
        if (noteId != 0L) {
            viewModel.getNoteById(noteId)?.let { note ->
                currentNote = note
                title = note.title
                content = note.content
                isPinned = note.isPinned
            }
        }
    }

    // Auto-save logic: Triggered when navigating away or screen disposal
    DisposableEffect(Unit) {
        onDispose {
            viewModel.saveNote(noteId, title, content, isPinned)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { isPinned = !isPinned }) {
                        Icon(if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin, contentDescription = "Pin Note")
                    }
                    if (noteId != 0L) {
                        IconButton(onClick = {
                            currentNote?.let { viewModel.moveToTrash(it) }
                            navController.popBackStack()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Move to Trash")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            TextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Title", style = MaterialTheme.typography.headlineMedium) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.headlineMedium,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            TextField(
                value = content,
                onValueChange = { content = it },
                placeholder = { Text("Start typing...") },
                modifier = Modifier.fillMaxSize(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }
    }
}

