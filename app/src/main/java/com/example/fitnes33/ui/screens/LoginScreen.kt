package com.example.fitnes33.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnes33.ui.theme.*

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    preferencesManager: com.example.fitnes33.util.PreferencesManager
) {
    var isSignUp by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        FireFitBlue,
                        FireFitViolet
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Logo/Título
            Text(
                text = "Mi Tiempo Activo",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = FireFitWhite,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Card de Login/SignUp
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = FireFitDarkGray.copy(alpha = 0.9f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Toggle SignUp/Login
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { isSignUp = false },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!isSignUp) FireFitOrange else FireFitDarkGray
                            )
                        ) {
                            Text("Login")
                        }
                        Button(
                            onClick = { isSignUp = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSignUp) FireFitOrange else FireFitDarkGray
                            )
                        ) {
                            Text("Sign Up")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Campo de nombre de usuario
                    OutlinedTextField(
                        value = userName,
                        onValueChange = { userName = it },
                        label = { Text("Nombre de usuario") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FireFitOrange,
                            unfocusedBorderColor = FireFitGray,
                            focusedLabelColor = FireFitOrange
                        )
                    )
                    
                    // Campo de contraseña
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FireFitOrange,
                            unfocusedBorderColor = FireFitGray,
                            focusedLabelColor = FireFitOrange
                        )
                    )
                    
                    // Campo de confirmar contraseña (solo en SignUp)
                    AnimatedVisibility(
                        visible = isSignUp,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Confirmar contraseña") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FireFitOrange,
                                unfocusedBorderColor = FireFitGray,
                                focusedLabelColor = FireFitOrange
                            )
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Botón de acción
                    Button(
                        onClick = {
                            if (isSignUp) {
                                if (password == confirmPassword && userName.isNotBlank()) {
                                    preferencesManager.saveUserName(userName)
                                    preferencesManager.saveUserLoggedIn(true)
                                    onLoginSuccess()
                                }
                            } else {
                                if (userName.isNotBlank()) {
                                    preferencesManager.saveUserName(userName)
                                    preferencesManager.saveUserLoggedIn(true)
                                    onLoginSuccess()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FireFitCoral
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = if (isSignUp) "Registrarse" else "Iniciar Sesión",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

