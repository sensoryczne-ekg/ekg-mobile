package com.pawlowski.detectors.handlers

class MoveDifference(private val size: Int) : HelperInterface {
    private val values: ArrayList<Double> = ArrayList()

    override fun passValue(sample: Double): Double {
        values.add(sample)
        return if (values.size == this.size) {
            val first = values.first()
            values.removeFirst()
            sample - first
        } else {
            Double.NaN
        }
    }

    override fun reset() {
        values.clear()
    }
}
