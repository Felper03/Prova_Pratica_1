package com.example.atividadepratica1

class Estoque {
    companion object {
        val produtos = mutableListOf<Produto>()

        fun adicionarProduto(produto: Produto) {
            produtos.add(produto)
        }

        fun calcularValorTotalEstoque(): Double {
            return produtos.sumOf { it.preco * it.qtEstoque }
        }

        fun quantidadeTotalProdutos(): Int {
            return produtos.sumOf { it.qtEstoque }
        }
    }
}