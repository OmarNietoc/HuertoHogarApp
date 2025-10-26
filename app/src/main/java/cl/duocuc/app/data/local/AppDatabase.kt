package cl.duocuc.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cl.duocuc.app.data.local.favoritos.ProductoFavoritoEntity
import cl.duocuc.app.data.local.favoritos.ProductoFavoritoDao
import cl.duocuc.app.data.local.order.OrderItemDao
import cl.duocuc.app.data.local.productos.ProductoDao
import cl.duocuc.app.data.local.productos.ProductoEntity
import cl.duocuc.app.data.local.recordatorio.RecordatorioEntity
import cl.duocuc.app.data.local.recordatorio.ReminderDao
import cl.duocuc.app.data.local.order.OrderItemEntity



@Database(
    entities = [
        RecordatorioEntity::class,
        ProductoEntity::class,
        ProductoFavoritoEntity::class, // nueva entidad
        OrderItemEntity::class
    ],
    version = 5, // Aumenta la versión al cambiar entidades
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun reminderDao(): ReminderDao

    abstract fun productoDao(): ProductoDao
    abstract fun productoFavoritoDao(): ProductoFavoritoDao // Nuevo DAO
    abstract fun orderItemDao() : OrderItemDao



    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "duocapp.db"
                )
                    // 👇 para desarrollo (borra y recrea DB al cambiar estructura)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
