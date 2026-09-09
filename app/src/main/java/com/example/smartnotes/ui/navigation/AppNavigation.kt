package com.example.smartnotes.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.smartnotes.ui.screens.EditNoteScreen
import com.example.smartnotes.ui.screens.NotesListScreen
import com.example.smartnotes.ui.screens.SettingsScreen
import com.example.smartnotes.ui.screens.TrashScreen
import com.example.smartnotes.viewmodel.NoteViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // Ensures the same ViewModel instance is shared across navigation routes
    val viewModel: NoteViewModel = viewModel()

    NavHost(navController = navController, startDestination = "notes_list") {
        composable("notes_list") {
            NotesListScreen(navController = navController, viewModel = viewModel)
        }
        composable(
            route = "edit_note?noteId={noteId}",
            arguments = listOf(navArgument("noteId") {
                type = NavType.LongType
                defaultValue = 0L
            })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId") ?: 0L
            EditNoteScreen(navController = navController, viewModel = viewModel, noteId = noteId)
        }
        composable("trash") {
            TrashScreen(navController = navController, viewModel = viewModel)
        }
        composable("settings") {
            SettingsScreen(navController = navController)
        }
    }
}

