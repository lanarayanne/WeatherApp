package com.weatherapp

import android.app.Activity
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.weatherapp.ui.theme.WeatherAppTheme

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RegisterPage(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun RegisterPage(modifier: Modifier = Modifier) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var repeatPassword by rememberSaveable { mutableStateOf("") }
    val activity = LocalActivity.current as Activity
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier.padding(10.dp).fillMaxSize().verticalScroll(scrollState).imePadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Cadastro",
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = name,
            label = { Text(text = "Informe seu nome") },
            modifier = Modifier.fillMaxWidth(fraction = 0.9f),
            onValueChange = { name = it },
        )
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            label = { Text(text = "Digite seu e-mail") },
            modifier = Modifier.fillMaxWidth(fraction = 0.9f),
            onValueChange = { email = it },
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = password,
            label = { Text(text = "Digite sua senha") },
            modifier = Modifier.fillMaxWidth(fraction = 0.9f),
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = repeatPassword,
            label = { Text(text = "Repita sua senha") },
            modifier = Modifier.fillMaxWidth(fraction = 0.9f),
            onValueChange = { repeatPassword = it },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(modifier = Modifier) {
            Button( onClick = {

                Firebase.auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(activity) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(activity,
                                "Registro OK!", Toast.LENGTH_LONG).show()

                        } else {
                            Toast.makeText(activity,
                                "Registro FALHOU!", Toast.LENGTH_LONG).show()
                        }
                    }
            },
                enabled = name.isNotEmpty() && email.isNotEmpty() && repeatPassword.isNotEmpty() && password.isNotEmpty() && password == repeatPassword) {
                Text("Registar")
            }
            Spacer(modifier = Modifier.size(24.dp))
            Button(
                onClick = { name= ""; email = ""; password = ""; repeatPassword = "" },
                enabled = email.isNotEmpty() || password.isNotEmpty() || name.isNotEmpty() || repeatPassword.isNotEmpty()
            ) {
                Text("Limpar")
            }
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}