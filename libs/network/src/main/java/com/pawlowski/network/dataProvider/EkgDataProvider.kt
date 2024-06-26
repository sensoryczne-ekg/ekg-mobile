package com.pawlowski.network.dataProvider

import Api
import ElectrocardiogramGrpcKt
import com.pawlowski.network.Classification
import com.pawlowski.network.EkgRecord
import com.pawlowski.network.IEkgDataProvider
import com.pawlowski.network.service.IEkgServiceProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class EkgDataProvider
    @Inject
    constructor(
        private val ekgServiceProvider: IEkgServiceProvider,
    ) : IEkgDataProvider {
        override fun streamRecords(): Flow<EkgRecord> =
            withStreamService(
                method = ElectrocardiogramGrpcKt.ElectrocardiogramCoroutineStub::streamRecords,
                request = Api.Empty.getDefaultInstance(),
            ).map {
                it.toDomain()
            }

        override suspend fun getRecords(
            from: Long,
            to: Long,
        ): List<EkgRecord> =
            withUnaryService(
                method = ElectrocardiogramGrpcKt.ElectrocardiogramCoroutineStub::listRecords,
                request = buildFilterRequest(from, to),
            ).recordsList.map {
                it.toDomain()
            }

        override suspend fun classify(
            from: Long,
            to: Long,
        ): Classification =
            withUnaryService(
                method = ElectrocardiogramGrpcKt.ElectrocardiogramCoroutineStub::classify,
                request = buildFilterRequest(from, to),
            ).let {
                Classification(
                    n = it.n,
                    s = it.s,
                    v = it.v,
                    f = it.f,
                    q = it.q,
                )
            }

        private fun buildFilterRequest(
            from: Long,
            to: Long,
        ): Api.Filter =
            Api.Filter.newBuilder()
                .setStart(from * 1000L)
                .setEnd(to * 1000L)
                .build()

        private fun Api.Record.toDomain(): EkgRecord =
            EkgRecord(
                id = id,
                value = value,
                timestamp = timestamp,
            )

        private fun <REQ : Any, RESP : Any> withStreamService(
            method: ElectrocardiogramGrpcKt.ElectrocardiogramCoroutineStub.(REQ) -> Flow<RESP>,
            request: REQ,
        ): Flow<RESP> =
            flow {
                emitAll(
                    ekgServiceProvider
                        .invoke()
                        .method(request),
                )
            }

        private suspend fun <REQ : Any, RESP : Any> withUnaryService(
            method: suspend ElectrocardiogramGrpcKt.ElectrocardiogramCoroutineStub.(REQ) -> RESP,
            request: REQ,
        ): RESP =
            ekgServiceProvider
                .invoke()
                .method(request)
    }
