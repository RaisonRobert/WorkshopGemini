package br.com.workshop.gemini

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { TelaGemini() } }
    }
}

@Composable
fun TelaGemini(vm: GeminiViewModel = viewModel()) {
    // Só o texto sendo digitado fica na tela; o resto vive no ViewModel
    var pergunta by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1) ÁREA DA RESPOSTA (em cima) — exibe o estado do ViewModel
        Card(modifier = Modifier.weight(1f).fillMaxWidth()) {
            Column(Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
                if (vm.carregando && vm.resposta.isEmpty()) CircularProgressIndicator()
                else Text(vm.resposta)
            }
        }

        // 2) CAMPO PARA DIGITAR
        OutlinedTextField(
            value = pergunta,
            onValueChange = { pergunta = it },
            label = { Text("Digite sua pergunta") },
            modifier = Modifier.fillMaxWidth()
        )

        // 3) BOTÃO PARA INICIAR — só avisa o ViewModel
        Button(
            onClick = { vm.perguntar(pergunta) },
            enabled = pergunta.isNotBlank() && !vm.carregando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (vm.carregando) "Pensando…" else "Perguntar ao Gemini")
        }
    }
}
