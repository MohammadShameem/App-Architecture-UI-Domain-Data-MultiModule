package co.example.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import co.example.cache.dao.*
import co.example.entity.room.*

@Database(entities =
[
    BusStoppageEntity::class,
    SoldTicketEntity::class,
    VehicleEntity::class,
    FareModalityEntity::class,
    TicketFormatEntity::class
],
version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun soldTicketDao() : SoldTicketDao
    abstract fun fareModalityDao() : FareModalityDao
    abstract fun busStoppageDao() : BusStoppageDao
    abstract fun vehicleListDao() : VehicleListDao
    abstract fun ticketFormatDao() : TicketFormatDao
}