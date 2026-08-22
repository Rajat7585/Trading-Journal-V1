package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [TradeEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tradeDao(): TradeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "trade_archive_database"
                )
                    .addCallback(TradeDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class TradeDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialTrades(database.tradeDao())
                    }
                }
            }
        }

        suspend fun populateInitialTrades(tradeDao: TradeDao) {
            val currentTime = System.currentTimeMillis()
            val hour = 3600 * 1000L
            val initialTrades = listOf(
                TradeEntity(
                    asset = "XAUUSD",
                    direction = "Long",
                    entryPrice = 2412.50,
                    stopLoss = 2408.00,
                    takeProfit = 2423.30,
                    exitPrice = 2423.30,
                    executionType = "Manual",
                    lotSize = 0.5,
                    profitOrLoss = 420.00,
                    rMultiple = 2.4,
                    strategy = "FVG",
                    timeframe = "M15",
                    poiNotes = "Asian low sweep into M15 bullish Fair Value Gap + NY Open confluence.",
                    timestamp = currentTime - (1 * hour)
                ),
                TradeEntity(
                    asset = "EURUSD",
                    direction = "Short",
                    entryPrice = 1.08950,
                    stopLoss = 1.09100,
                    takeProfit = 1.08500,
                    exitPrice = 1.09100,
                    executionType = "Manual",
                    lotSize = 1.2,
                    profitOrLoss = -150.00,
                    rMultiple = -1.0,
                    strategy = "OB",
                    timeframe = "H1",
                    poiNotes = "4H Bearish Order Block tap during London session.",
                    timestamp = currentTime - (4 * hour)
                ),
                TradeEntity(
                    asset = "BTCUSD",
                    direction = "Long",
                    entryPrice = 64200.00,
                    stopLoss = 63850.00,
                    takeProfit = 65880.00,
                    exitPrice = 65880.00,
                    executionType = "Algorithmic",
                    lotSize = 0.05,
                    profitOrLoss = 1120.50,
                    rMultiple = 4.8,
                    strategy = "Liq Sweep",
                    timeframe = "M5",
                    poiNotes = "Weekly low liquidity sweep followed by energetic displacement upward.",
                    timestamp = currentTime - (9 * hour)
                ),
                TradeEntity(
                    asset = "GBPUSD",
                    direction = "Long",
                    entryPrice = 1.29400,
                    stopLoss = 1.29200,
                    takeProfit = 1.29820,
                    exitPrice = 1.29820,
                    executionType = "Manual",
                    lotSize = 0.8,
                    profitOrLoss = 380.00,
                    rMultiple = 2.1,
                    strategy = "BOS",
                    timeframe = "M15",
                    poiNotes = "Break of structure on M15 with clean mitigation of internal order block.",
                    timestamp = currentTime - (24 * hour)
                ),
                TradeEntity(
                    asset = "US30",
                    direction = "Short",
                    entryPrice = 40500.0,
                    stopLoss = 40620.0,
                    takeProfit = 40080.0,
                    exitPrice = 40080.0,
                    executionType = "Manual",
                    lotSize = 1.0,
                    profitOrLoss = 850.00,
                    rMultiple = 3.5,
                    strategy = "Breaker",
                    timeframe = "M5",
                    poiNotes = "Judas swing high at NY 09:30 AM followed by rapid expansion into breaker block.",
                    timestamp = currentTime - (30 * hour)
                )
            )
            tradeDao.insertTrades(initialTrades)
        }
    }
}
