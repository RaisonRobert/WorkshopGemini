package br.com.workshop.gemini

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.content
import kotlinx.coroutines.launch

// ============== AGENTES PRONTOS (escolha um lá embaixo) ==============
const val AGENTE_ASSISTENTE =
    "Você é um assistente amigável. Responda sempre em português do Brasil, em até 3 frases."

const val AGENTE_PROFESSOR =
    "Você é um professor de Android. Explique como se fosse para um iniciante, com exemplos curtos em Kotlin."

const val AGENTE_VENDEDOR =
    "Você escreve descrições de produto para e-commerce: máximo 60 palavras, 1 parágrafo, destacando o principal benefício."

const val AGENTE_POETA =
    "Você responde qualquer pergunta em forma de poema curto de 4 versos, em português."

// ============== CONFIGURAÇÃO: mexa SÓ nestas 2 linhas ==============
const val MODELO = "gemini-3.8-flash"       // modelo do Gemini
const val AGENTE = AGENTE_PROFESSOR        // 👈 troque por qualquer agente acima, ou escreva o seu
// ====================================================================

class GeminiViewModel : ViewModel() {

    // O Model: o Gemini, já com a "personalidade" (AGENTE)
    private val model = Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
        modelName = MODELO,
        systemInstruction = content { text(AGENTE) }
    )

    // O ESTADO: a tela só lê; quem escreve é o ViewModel (private set)
    var resposta by mutableStateOf("A resposta do Gemini vai aparecer aqui. 👋")
        private set
    var carregando by mutableStateOf(false)
        private set

    // A AÇÃO: chamada pelo botão da tela
    fun perguntar(pergunta: String) {
        viewModelScope.launch {
            carregando = true
            resposta = ""
            try {
                // ⭐ STREAMING — a resposta chega aos poucos (efeito "digitando")
                model.generateContentStream(pergunta).collect { pedaco ->
                    resposta += pedaco.text ?: ""
                }
            } catch (e: Exception) {
                resposta = "Erro: ${e.message}"
            }
            carregando = false
        }
    }
}
