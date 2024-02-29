package com.pawlowski.network.dataProvider

import com.ekg.proto.Api
import com.ekg.proto.ElectrocardiogramGrpcKt
import com.pawlowski.network.IEkgDataProvider
import com.pawlowski.network.Record
import com.pawlowski.network.service.IEkgServiceProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class EkgDataProvider
    @Inject
    constructor(
        private val ekgServiceProvider: IEkgServiceProvider,
    ) : IEkgDataProvider {
        override fun streamRecords(): Flow<Record> =
            withStreamService(
                method = ElectrocardiogramGrpcKt.ElectrocardiogramCoroutineStub::streamRecords,
                request = Api.Empty.getDefaultInstance(),
            ).map {
                Record(
                    id = it.id,
                    value = it.value,
                    timestamp = it.timestamp,
                )
            }

        private fun <REQ : Any, RESP : Any> withStreamService(
            method: ElectrocardiogramGrpcKt.ElectrocardiogramCoroutineStub.(REQ) -> Flow<RESP>,
            request: REQ,
        ): Flow<RESP> =
            ekgServiceProvider
                .invoke()
                .method(request)
    }
