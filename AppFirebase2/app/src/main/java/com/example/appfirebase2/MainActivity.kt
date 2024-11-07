package com.example.appfirebase2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.appfirebase2.ui.theme.AppFirebase2Theme
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            AppFirebase2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App(db)
                }
            }
        }
    }
}

@Composable
fun App(db: FirebaseFirestore) {
    var nome by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var clientes by remember { mutableStateOf<List<HashMap<String, String>>>(emptyList()) }

    LaunchedEffect(Unit) {
        db.collection("Clientes")
            .get()
            .addOnSuccessListener { documents ->
                val listaClientes = documents.map { document ->
                    hashMapOf(
                        "nome" to "${document.data["nome"]}",
                        "telefone" to "${document.data["telefone"]}"
                    )
                }
                clientes = listaClientes
                Log.d(TAG, "Clientes carregados: $clientes")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Erro ao obter documentos.", e)
            }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Vinicius Valero Chabariberi - 3°DS AMS", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                painter = painterResource(id = R.drawable.fotovini),
                contentDescription = "Imagem personalizada",
                modifier = Modifier.size(100.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(Modifier.fillMaxWidth()) {
            Text("Nome:", modifier = Modifier.weight(0.3f))
            TextField(
                value = nome,
                onValueChange = { nome = it },
                modifier = Modifier.weight(0.7f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth()) {
            Text("Telefone:", modifier = Modifier.weight(0.3f))
            TextField(
                value = telefone,
                onValueChange = { telefone = it },
                modifier = Modifier.weight(0.7f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            val pessoa = hashMapOf(
                "nome" to nome,
                "telefone" to telefone
            )

            db.collection("Clientes").add(pessoa)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "Documento salvo com ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Erro ao adicionar documento", e)
                }
        }) {
            Text("Cadastrar")
        }

        Spacer(modifier = Modifier.height(20.dp))
        LazyColumn {
            items(clientes) { cliente ->
                Row(Modifier.fillMaxWidth().padding(8.dp)) {
                    Text(text = cliente["nome"] ?: "--", modifier = Modifier.weight(0.5f))
                    Text(text = cliente["telefone"] ?: "--", modifier = Modifier.weight(0.5f))
                }
            }
        }
    }
}

private const val TAG = "MainActivity"