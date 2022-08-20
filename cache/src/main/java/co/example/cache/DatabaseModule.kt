package co.example.cache

import android.app.Application
import androidx.room.Room
import co.example.cache.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(application: Application):AppDatabase{
        return Room.databaseBuilder(application, AppDatabase::class.java,
            "intra_city_counter_autosync.db").fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun provideSoldTicketDao(database: AppDatabase) : SoldTicketDao {
        return database.soldTicketDao()
    }
    @Singleton
    @Provides
    fun provideFareModalityDao(database: AppDatabase) : FareModalityDao {
        return database.fareModalityDao()
    }
    @Singleton
    @Provides
    fun provideStoppageDao(database: AppDatabase) : BusStoppageDao {
        return database.busStoppageDao()
    }

    @Singleton
    @Provides
    fun provideVehicleListDao(database: AppDatabase) : VehicleListDao {
        return database.vehicleListDao()
    }

    @Singleton
    @Provides
    fun provideTicketFormatDao(database: AppDatabase) : TicketFormatDao {
        return database.ticketFormatDao()
    }
}
