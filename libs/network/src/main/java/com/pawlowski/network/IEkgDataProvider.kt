package com.pawlowski.network

import kotlinx.coroutines.flow.Flow

interface IEkgDataProvider {
    fun streamRecords(): Flow<Record>
}
