package com.pawlowski.network.service

import ElectrocardiogramGrpcKt

interface IEkgServiceProvider {
    suspend operator fun invoke(): ElectrocardiogramGrpcKt.ElectrocardiogramCoroutineStub
}
