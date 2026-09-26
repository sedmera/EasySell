package com.example.easysell.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        Product::class,
        OrderEntity::class,
        OrderItemEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class ProductDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao

    companion object {

        @Volatile
        private var INSTANCE: ProductDatabase? = null

        fun getDatabase(context: Context): ProductDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ProductDatabase::class.java,
                    "product_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {

    override fun migrate(db: SupportSQLiteDatabase) {

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS orders (
                id TEXT NOT NULL,
                name TEXT NOT NULL,
                createdAt INTEGER NOT NULL,
                total REAL NOT NULL,
                PRIMARY KEY(id)
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS order_items (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                orderId TEXT NOT NULL,
                productId INTEGER,
                productName TEXT NOT NULL,
                unitPrice REAL NOT NULL,
                quantity INTEGER NOT NULL,
                FOREIGN KEY(orderId)
                    REFERENCES orders(id)
                    ON UPDATE NO ACTION
                    ON DELETE CASCADE
            )
        """.trimIndent())

        db.execSQL("""
            CREATE INDEX IF NOT EXISTS index_order_items_orderId
            ON order_items(orderId)
        """.trimIndent())
    }
}

fun generateOrderId(name: String): String {
    val normalized = java.text.Normalizer
        .normalize(name, java.text.Normalizer.Form.NFD)
        .replace("\\p{M}".toRegex(), "")

    val slug = normalized
        .lowercase()
        .replace("[^a-z0-9]+".toRegex(), "-")
        .trim('-')
        .ifBlank { "order" }

    val suffix = java.util.UUID
        .randomUUID()
        .toString()
        .replace("-", "")
        .take(8)

    return "$slug-$suffix"
}
