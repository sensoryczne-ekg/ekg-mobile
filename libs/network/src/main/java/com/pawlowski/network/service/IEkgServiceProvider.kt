package com.pawlowski.network.service

import ElectrocardiogramGrpcKt

interface IEkgServiceProvider {
    operator fun invoke(): ElectrocardiogramGrpcKt.ElectrocardiogramCoroutineStub
}
