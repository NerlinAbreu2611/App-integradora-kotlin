package com.abreu.practicaapi.ui.screens

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import com.abreu.practicaapi.model.User
import com.abreu.practicaapi.viewmodel.UserVM
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.IconToggleButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abreu.practicaapi.data.local.AppDatabase
import com.abreu.practicaapi.data.repository.UserRepository
import com.abreu.practicaapi.viewmodel.UserRoomVM
import com.abreu.practicaapi.viewmodel.UserRoomVMFactory

import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.People
import androidx.compose.runtime.mutableIntStateOf
import com.abreu.practicaapi.data.mapper.toUser
import kotlinx.coroutines.delay

import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun UserScreen(
    vm: UserVM = viewModel(),
    roomVM: UserRoomVM,
    onEditClick: (Int) -> Unit,
) {
    var mostrarFavoritos by remember { mutableStateOf(false) }
    var tiempoAgotado by remember { mutableStateOf(false) }
    var intentoCarga by remember { mutableIntStateOf(0) }

    val users by vm.users.collectAsState()
    val usersFavorite by roomVM.users.collectAsState()
    val error by vm.error.collectAsState()

    // Cargar usuarios de la API al entrar en la pantalla.
    LaunchedEffect(Unit) {
        vm.findAll()
    }

    // Controlar el tiempo de espera de la lista seleccionada.
    LaunchedEffect(
        mostrarFavoritos,
        usersFavorite.isEmpty(),
        users.isEmpty(),
        error,
        intentoCarga
    ) {
        tiempoAgotado = false

        val listaVacia = if (mostrarFavoritos) {
            usersFavorite.isEmpty()
        } else {
            users.isEmpty()
        }

        if (listaVacia && (mostrarFavoritos || error == null)) {
            delay(3_000)

            val sigueVacia = if (mostrarFavoritos) {
                usersFavorite.isEmpty()
            } else {
                users.isEmpty()
            }

            if (sigueVacia) {
                tiempoAgotado = true
            }
        }
    }

    // Elegir la lista según el botón.
    val usersToShow: List<User> = if (mostrarFavoritos) {
        usersFavorite.map { it.toUser() }
    } else {
        users
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        UserHeader(
            vm = vm,
            roomVM = roomVM,
            mostrarFavoritos = mostrarFavoritos,
            onFavoritosClick = {
                if (mostrarFavoritos) {
                    // Volver a todos los usuarios.
                    mostrarFavoritos = false
                    tiempoAgotado = false
                    intentoCarga++

                    // Solicitar nuevamente los usuarios a la API.
                    vm.findAll()
                } else {
                    // Mostrar favoritos guardados en Room.
                    mostrarFavoritos = true
                    tiempoAgotado = false
                    intentoCarga++
                }
            }
        )

        when {
            // Error de la API: no confundirlo con el error de favoritos.
            !mostrarFavoritos && error != null -> {
                Text("No se pudieron cargar los usuarios: $error")

                Button(
                    onClick = {
                        tiempoAgotado = false
                        intentoCarga++
                        vm.findAll()
                    }
                ) {
                    Text("Reintentar")
                }
            }

            // Lista vacía: esperar o mostrar error al agotarse el tiempo.
            usersToShow.isEmpty() && !tiempoAgotado -> {
                CircularProgressIndicator()

                Text(
                    if (mostrarFavoritos) {
                        "Cargando favoritos..."
                    } else {
                        "Cargando usuarios..."
                    }
                )
            }

            usersToShow.isEmpty() && tiempoAgotado -> {
                Text(
                    if (mostrarFavoritos) {
                        "No se pudieron cargar los favoritos."
                    } else {
                        "No se pudieron cargar los usuarios."
                    }
                )

                Button(
                    onClick = {
                        tiempoAgotado = false
                        intentoCarga++

                        if (!mostrarFavoritos) {
                            vm.findAll()
                        }
                    }
                ) {
                    Text("Reintentar")
                }
            }

            // Mostrar los datos de la lista seleccionada.
            else -> {
                LazyColumn {
                    items(
                        items = usersToShow,
                        key = { user -> user.id }
                    ) { user ->
                        UserItem(
                            user = user,
                            onEditClick = onEditClick,
                            roomVM = roomVM
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun UserHeader(
    vm: UserVM,
    roomVM: UserRoomVM,
    onFavoritosClick: () -> Unit,
    mostrarFavoritos: Boolean
) {
    val usersRoom by roomVM.users.collectAsState()
    val users by vm.users.collectAsState()

    val totalUsers = users.size
    val totalFavoritos = usersRoom.size

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Gestión de usuarios",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
                text = "Resumen de tus usuarios registrados",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.onPrimaryContainer
                    .copy(alpha = 0.2f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Total de usuarios
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Business,
                            contentDescription = "Usuarios registrados",
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = "$totalUsers",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text("Usuarios")
                    }
                }

                // Total de favoritos
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "Usuarios favoritos",
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = "$totalFavoritos",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text("Favoritos")
                    }
                }
            }

            // Botón para ver los favoritos
            Button(
                onClick = onFavoritosClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (mostrarFavoritos) {
                        Icons.Default.People
                    } else {
                        Icons.Default.Favorite
                    },
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = if (mostrarFavoritos) {
                        "Mostrar todos los usuarios"
                    } else {
                        "Mostrar favoritos"
                    }
                )
            }
        }
    }
}


@Composable
fun UserItem(user: User, onEditClick: (Int) -> Unit, roomVM: UserRoomVM
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Encabezado: avatar, nombre e ID
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    androidx.compose.foundation.layout.Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.name
                                .split(" ")
                                .take(2)
                                .mapNotNull { it.firstOrNull() }
                                .joinToString("")
                                .uppercase(),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "Usuario registrado",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = "ID ${user.id}",
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Correo electrónico
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Correo",
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "Correo electrónico",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = user.email,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Empresa
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Business,
                    contentDescription = "Empresa",
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "Empresa",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = user.company.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        onEditClick(user.id)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Editar usuario")
                }

                var seleccionado by remember(user.id) {
                    mutableStateOf(roomVM.isFavorite(user.id))
                }

                IconToggleButton(
                    checked = seleccionado,
                    onCheckedChange = { nuevoEstado ->
                        seleccionado = nuevoEstado

                        if (nuevoEstado) {
                            roomVM.addUser(user)
                        } else {
                            roomVM.removeUser(user.id)
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (seleccionado)
                            Icons.Default.Favorite
                        else
                            Icons.Default.FavoriteBorder,
                        contentDescription = if (seleccionado)
                            "Quitar de favoritos"
                        else
                            "Agregar a favoritos",
                        tint = if (seleccionado)
                            Color.Red
                        else
                            MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
