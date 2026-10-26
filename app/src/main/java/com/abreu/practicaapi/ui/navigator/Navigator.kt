package com.abreu.practicaapi.ui.navigator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.abreu.practicaapi.data.local.AppDatabase
import com.abreu.practicaapi.data.repository.UserRepository
import com.abreu.practicaapi.ui.screens.UserScreen
import com.abreu.practicaapi.ui.screens.UserEditScreen
import com.abreu.practicaapi.viewmodel.UserRoomVM
import com.abreu.practicaapi.viewmodel.UserRoomVMFactory
import com.abreu.practicaapi.viewmodel.UserVM

object Rutas {
    const val Usuarios = "usuarios"
    const val EditarUsuario = "editar_usuario/{id}"

    fun editarUsuario(id: Int): String {
        return "editar_usuario/$id"
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val userViewModel: UserVM = viewModel()
    val context = LocalContext.current

    val database = AppDatabase.getDatabase(context)

    val repository = remember {
        UserRepository(database.userDao())
    }

    val factory = remember {
        UserRoomVMFactory(repository)
    }

    val roomVM: UserRoomVM = viewModel(
        factory = factory
    )

    NavHost(
        navController = navController,
        startDestination = Rutas.Usuarios
    ) {
        composable(Rutas.Usuarios) {
            UserScreen(
                roomVM = roomVM,
                onEditClick = { id ->
                    navController.navigate(
                        Rutas.editarUsuario(id)
                    )
                }
            )
        }

        composable(
            route = Rutas.EditarUsuario,
            arguments = listOf(
                navArgument("id") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val id = backStackEntry.arguments?.getInt("id")

            if (id != null) {
                UserEditRoute(
                    id = id,
                    userViewModel = userViewModel,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Composable
private fun UserEditRoute(
    id: Int,
    userViewModel: UserVM,
    onBack: () -> Unit
) {
    val userState by userViewModel.user.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()
    val error by userViewModel.error.collectAsState()

    // Buscar el usuario al entrar o cuando cambie el ID
    LaunchedEffect(id) {
        userViewModel.findById(id)
    }

    val currentUser = userState

    when {
        isLoading || (currentUser == null && error == null) -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Text("Cargando usuario...")
            }
        }

        error != null -> {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Error: $error")

                Button(onClick = {
                    userViewModel.findById(id)
                }) {
                    Text("Reintentar")
                }

                Button(onClick = onBack) {
                    Text("Volver")
                }
            }
        }

        currentUser != null -> {
            UserEditScreen(
                user = currentUser,
                onSave = { updatedUser ->
                    // Aquí conectaremos el guardado con el ViewModel.
                    onBack()
                }
            )
        }
    }
}