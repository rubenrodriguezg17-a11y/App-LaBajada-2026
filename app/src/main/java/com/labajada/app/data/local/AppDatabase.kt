package com.labajada.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.labajada.app.data.local.dao.CartDao
import com.labajada.app.data.local.dao.DishDao
import com.labajada.app.data.local.dao.OrderDao
import com.labajada.app.data.local.dao.RestaurantDao
import com.labajada.app.data.local.dao.SearchDao
import com.labajada.app.data.local.dao.UserDao
import com.labajada.app.data.local.entity.CartEntity
import com.labajada.app.data.local.entity.CartItemEntity
import com.labajada.app.data.local.entity.DishEntity
import com.labajada.app.data.local.entity.FavoriteRestaurantEntity
import com.labajada.app.data.local.entity.OrderEntity
import com.labajada.app.data.local.entity.OrderItemEntity
import com.labajada.app.data.local.entity.RestaurantEntity
import com.labajada.app.data.local.entity.SearchHistoryEntity
import com.labajada.app.data.local.entity.SessionEntity
import com.labajada.app.data.local.entity.UserEntity
import com.labajada.app.domain.model.OrderItem

@Database(
    entities = [
        FavoriteRestaurantEntity::class,
        SearchHistoryEntity::class,
        DishEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        CartEntity::class,
        CartItemEntity::class,
        UserEntity::class,
        RestaurantEntity::class,
        SessionEntity::class
    ],
    version = 10, // subida desde 9: nuevo índice único (buyerId, searchQuery) en search_history
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun restaurantDao(): RestaurantDao
    abstract fun searchDao(): SearchDao
    abstract fun dishDao(): DishDao
    abstract fun orderDao(): OrderDao
    abstract fun cartDao(): CartDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "la_bajada_database"
                )
                    // TODO(antes de publicar en producción): reemplazar por Migrations reales.
                    // Aceptado temporalmente mientras no hay usuarios reales (borra todos los
                    // datos locales -sesión, carrito, pedidos- en cada cambio de versión de esquema).
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}