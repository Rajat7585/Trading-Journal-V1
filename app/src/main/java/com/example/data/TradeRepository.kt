package com.example.data

import kotlinx.coroutines.flow.Flow

class TradeRepository(private val tradeDao: TradeDao) {
    val allTrades: Flow<List<TradeEntity>> = tradeDao.getAllTrades()

    suspend fun insert(trade: TradeEntity): Long = tradeDao.insertTrade(trade)

    suspend fun update(trade: TradeEntity) = tradeDao.updateTrade(trade)

    suspend fun delete(trade: TradeEntity) = tradeDao.deleteTrade(trade)

    suspend fun deleteById(id: Long) = tradeDao.deleteTradeById(id)

    suspend fun resetWithSampleData() {
        tradeDao.deleteAllTrades()
        AppDatabase.populateInitialTrades(tradeDao)
    }
}
