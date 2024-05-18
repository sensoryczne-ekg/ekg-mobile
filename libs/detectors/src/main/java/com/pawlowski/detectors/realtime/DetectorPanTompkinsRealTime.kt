package com.pawlowski.detectors.realtime

import com.pawlowski.detectors.handlers.Filters
import com.pawlowski.detectors.handlers.MWA
import com.pawlowski.detectors.handlers.MoveDifference
import com.pawlowski.detectors.handlers.PanPeakDetector
import uk.me.berndporr.iirj.Butterworth
import kotlin.math.pow

/**
 * Jiapu Pan and Willis J. Tompkins.
 * A Real-Time QRS Detection Algorithm.
 * In: IEEE Transactions on Biomedical Engineering
 * BME-32.3 (1985), pp. 230–236.
 */

class DetectorPanTompkinsRealTime(fs: Double) : RealTimeDetector(fs) {
    private val bandPassFilter: Butterworth = Filters.initBandPassFilter(fs, 1, 5.0, 15.0)
    private val differenceHandler: MoveDifference = MoveDifference(2)
    private val mwa: MWA = MWA((0.12 * fs).toInt())
    private val panPeakDetector: PanPeakDetector = PanPeakDetector(fs)

    private var skipped = 0
    private var ready = false

    override fun processSample(sample: Double): Int {
        return if (!ready) {
            var filteredSample = bandPassFilter.filter(sample)
            filteredSample = differenceHandler.passValue(filteredSample).pow(2)
            if (filteredSample.isNaN()) {
                skipped++
            } else {
                ready = true
            }
            -1
        } else {
            var filteredSample = bandPassFilter.filter(sample)
            filteredSample = differenceHandler.passValue(filteredSample).pow(2)
            filteredSample = mwa.passValue(filteredSample)
            panPeakDetector.passValue(filteredSample).toInt() + skipped
        }
    }

    override fun reset() {
        differenceHandler.reset()
        mwa.reset()
        panPeakDetector.reset()
        skipped = 0
        ready = false
    }
}
