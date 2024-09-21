package com.example.atividadepratica1

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Navigation()
        }
    }
}

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "cadastro_produto") {
        composable("cadastro_produto") { cadastroProduto(navController) }
        composable("lista_produtos") { listarProdutos(navController) }
        composable("detalhes_produto/{produtoJson}") { backStackEntry ->
            val produtoJson = backStackEntry.arguments?.getString("produtoJson") ?: ""
            detalhesProduto(navController, produtoJson)
        }
        composable("estatisticas") { estatisticas(navController) }
    }
}

object ProdutoManager {
    val produtos = mutableListOf<Produto>()
}

@Composable
fun cadastroProduto(navController: NavHostController) {

    val context = LocalContext.current

    var nomeProduto by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }
    var qtEstoque by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "CADASTRO DE PRODUTO", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(30.dp))

        TextField(
            value = nomeProduto,
            onValueChange = { nomeProduto = it },
            label = { Text("Informe o nome do produto:") }
        )
        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = categoria,
            onValueChange = { categoria = it },
            label = { Text("Informe a categoria do produto:") }
        )
        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = preco,
            onValueChange = { preco = it },
            label = { Text("Informe o preço do produto:") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = qtEstoque,
            onValueChange = { qtEstoque = it },
            label = { Text("Informe a quantidade em estoque:") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            if (nomeProduto.isNotEmpty() && categoria.isNotEmpty() && preco.isNotEmpty() && qtEstoque.isNotEmpty()) {
                val precoDouble = preco.toDoubleOrNull() ?: 0.0
                val quantidadeInt = qtEstoque.toIntOrNull() ?: 0
                if (precoDouble > 0 && quantidadeInt >= 1) {
                    ProdutoManager.produtos.add(
                        Produto(
                            nomeProduto = nomeProduto,
                            categoria = categoria,
                            preco = precoDouble,
                            qtEstoque = quantidadeInt
                        )
                    )
                    Toast.makeText(context, "Produto cadastrado!", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(
                        context,
                        "Preço deve ser maior que 0 e quantidade maior que 1",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(context, "Todos os campos devem ser preenchidos!", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text(text = "Cadastrar")
        }
        Button(onClick = {
            navController.navigate("lista_produtos")
        }) {
            Text(text = "Lista Produtos")
        }
    }
}

@Composable
fun listarProdutos(navController: NavHostController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "LISTA DOS PRODUTOS", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(30.dp))

        LazyColumn {
            items(ProdutoManager.produtos) { produto ->
                Row(modifier = Modifier.fillMaxSize()) {
                    Text(text = "${produto.nomeProduto} (${produto.qtEstoque} unidades)")
                    Spacer(modifier = Modifier.padding(12.dp))
                    Button(onClick = {
                        val produtoJson = Gson().toJson(produto)
                        navController.navigate("detalhes_produto/$produtoJson")
                    }) {
                        Text(text = "Detalhes")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("estatisticas") }) {
            Text("Ver Estatísticas")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("Voltar")
        }
    }
}

@Composable
fun detalhesProduto(navController: NavHostController, produtoJson: String) {
    val produto = remember { Gson().fromJson(produtoJson, Produto::class.java) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "DETALHES DO PRODUTO", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Nome: ${produto.nomeProduto}")
        Text(text = "Categoria: ${produto.categoria}")
        Text(text = "Preço: R$ ${produto.preco}")
        Text(text = "Quantidade: ${produto.qtEstoque}")

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Voltar")
        }
    }
}

@Composable
fun estatisticas(navController: NavHostController) {
    val valorTotalEstoque = Estoque.calcularValorTotalEstoque()
    val quantidadeTotalProdutos = Estoque.quantidadeTotalProdutos()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "ESTATÍSTICAS DO ESTOQUE", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Valor Total do Estoque: R$ %.2f".format(valorTotalEstoque))
        Text(text = "Quantidade Total de Produtos: $quantidadeTotalProdutos")

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Voltar")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Navigation()
}
