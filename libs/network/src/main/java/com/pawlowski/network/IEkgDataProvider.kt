package com.pawlowski.network

import kotlinx.coroutines.flow.Flow

interface IEkgDataProvider {
    fun streamRecords(): Flow<EkgRecord>

    suspend fun getRecords(
        from: Long,
        to: Long,
    ): List<EkgRecord>
}
