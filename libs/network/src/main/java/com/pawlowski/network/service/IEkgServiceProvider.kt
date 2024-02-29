package com.pawlowski.network.service

import com.ekg.proto.ElectrocardiogramGrpcKt

interface IEkgServiceProvider {
    operator fun invoke(): ElectrocardiogramGrpcKt.ElectrocardiogramCoroutineStub
}
