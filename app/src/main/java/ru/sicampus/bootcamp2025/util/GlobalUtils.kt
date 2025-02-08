package ru.sicampus.bootcamp2025.util

import android.view.View
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.Navigation

fun navigateTo(view: View, @IdRes actionId: Int) {
    Navigation.findNavController(view).navigate(actionId)
}

fun navigateTo(navController: NavController, @IdRes actionId: Int) {
    navController.navigate(actionId)
}