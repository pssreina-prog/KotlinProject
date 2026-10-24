package br.universidade.approomproduto.db

import androidx.room.Database
import androidx.room.RoomDatabase
import br.universidade.approomproduto.dao.ProductDao
import br.universidade.approomproduto.model.Product

@Database(entities = [Product::class], version = 2) // Mudou de 1 para 2
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}