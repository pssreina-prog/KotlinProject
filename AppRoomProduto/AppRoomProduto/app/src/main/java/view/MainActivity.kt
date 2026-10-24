package br.universidade.approomproduto.view

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import br.universidade.approomproduto.dao.ProductDao
import br.universidade.approomproduto.databinding.ActivityMainBinding
import br.universidade.approomproduto.db.AppDatabase
import br.universidade.approomproduto.model.Product
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: AppDatabase
    private lateinit var productDao: ProductDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        )
            .fallbackToDestructiveMigration()
            .build()

        productDao = db.productDao()

        binding.btnInserirProduto.setOnClickListener {
            val name = binding.editProductName.text.toString().trim()
            val priceText = binding.editProductPrice.text.toString().trim()
            val description = binding.editProductDescription.text.toString().trim()

            if (name.isEmpty()) {
                binding.editProductName.error = "Preencha o nome do produto"
                return@setOnClickListener
            }

            val price = priceText.toDoubleOrNull()
            if (price == null || price <= 0.0) {
                binding.editProductPrice.error = "Informe um preço válido maior que zero"
                return@setOnClickListener
            }

            val product = Product(name = name, price = price, description = description, quantity = 1)

            lifecycleScope.launch {
                productDao.insertAll(product)
                Log.i("TAG", "Produto inserido: $name - R$$price")
                Toast.makeText(this@MainActivity, "Produto inserido!", Toast.LENGTH_SHORT).show()

                binding.editProductName.text.clear()
                binding.editProductPrice.text.clear()
                binding.editProductDescription.text.clear()
                binding.editProductName.requestFocus()
            }
        }

        binding.btnListarProdutos.setOnClickListener {
            lifecycleScope.launch {
                val products = productDao.getAll()

                val totalQuantidade = products.size
                val valorTotalEstoque = products.sumOf { it.price }

                Log.i("TAG", "--- Resumo do Estoque ---")
                Log.i("TAG", "Quantidade de itens: $totalQuantidade | Valor Total: R$$valorTotalEstoque")

                if (products.isNotEmpty()) {
                    products.forEach {
                        Log.i("TAG", "Produto = ${it.name}, Preço = R$${it.price}, Descrição = ${it.description}")
                    }
                } else {
                    Log.i("TAG", "Nenhum produto encontrado.")
                }
            }
        }
    }
}