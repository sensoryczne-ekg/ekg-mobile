package com.pawlowski.detectors.handlers

class MWA(private val size: Int) : HelperInterface {
    private val values: ArrayList<Double> = ArrayList()

    override fun passValue(sample: Double): Double {
        values.add(sample)
        return if (values.size != size) {
            values.sum() / values.size
        } else {
            val result = values.sum() / size
            values.removeFirst()
            result
        }
    }

    override fun reset() {
        values.clear()
    }
}
