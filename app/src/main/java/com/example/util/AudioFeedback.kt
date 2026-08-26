package com.example.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Random
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin

/**
 * Authentic acoustic physical paper flip sound synthesizer.
 * Simulates real book page separation, air swoosh turbulence, and paper-on-stack landing slap.
 */
object AudioFeedback {
    private val scope = CoroutineScope(Dispatchers.Default)
    private val sampleRate = 44100
    private val random = Random()

    // Pre-synthesized variations of page flip sounds for zero-latency instant playback
    private val soundBuffers = mutableListOf<ShortArray>()

    init {
        // Generate a set of natural realistic variations
        try {
            for (variant in 0..3) {
                soundBuffers.add(generateRealPaperFlipPcm(variant))
            }
        } catch (e: Exception) {
            // Handled
        }
    }

    /**
     * Synthesizes high-fidelity 16-bit PCM for a real book page flip sound.
     */
    private fun generateRealPaperFlipPcm(variant: Int): ShortArray {
        val durationMs = 210
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(numSamples)

        val variationSpeed = 0.95 + (variant * 0.05)
        val liftDurationSamples = (sampleRate * 0.045 * variationSpeed).toInt() // 0..45ms (crisp lift)
        val swooshDurationSamples = (sampleRate * 0.120 * variationSpeed).toInt() // 45..165ms (air displacement)
        val slapStartSamples = liftDurationSamples + swooshDurationSamples // 165ms..end (paper landing)

        var hpPrevSample = 0.0
        var hpPrevInput = 0.0
        var lpSample = 0.0

        for (i in 0 until numSamples) {
            val tSec = i.toDouble() / sampleRate
            var sample = 0.0

            // 1. Phase 1: Crisp Paper Separation & Edge Friction (0 to 45ms)
            if (i < liftDurationSamples) {
                val liftProgress = i.toDouble() / liftDurationSamples
                val liftEnv = sin(liftProgress * PI * 0.5) * exp(-2.0 * liftProgress)
                val rawFriction = random.nextDouble() * 2.0 - 1.0

                // High-pass filter for paper grain rustle (~4000Hz)
                val rc = 1.0 / (2.0 * PI * 4200.0)
                val dt = 1.0 / sampleRate
                val alpha = rc / (rc + dt)
                hpPrevSample = alpha * (hpPrevSample + rawFriction - hpPrevInput)
                hpPrevInput = rawFriction

                val microClick = if (i % 70 < 4 && random.nextDouble() > 0.4) (random.nextDouble() - 0.5) * 1.5 else 0.0
                sample += (hpPrevSample * 0.85 + microClick * 0.5) * liftEnv * 1.3
            }

            // 2. Phase 2: Aerodynamic Sheet Swoosh & Flutter (35ms to 175ms)
            if (i in (liftDurationSamples - 200)..(slapStartSamples + 200)) {
                val swooshRel = (i - liftDurationSamples + 200).toDouble() / (swooshDurationSamples + 400)
                if (swooshRel in 0.0..1.0) {
                    val swooshEnv = sin(swooshRel * PI) * (1.0 - 0.3 * swooshRel)

                    // Sweeping band-pass center frequency from 950Hz down to 280Hz
                    val centerFreq = 950.0 - (swooshRel * 670.0)
                    val rawAir = (random.nextDouble() * 2.0 - 1.0)

                    // Low-pass resonance simulation
                    lpSample = lpSample * 0.78 + rawAir * 0.22

                    // Air turbulence modulation
                    val airMod = (1.0 + 0.35 * sin(2.0 * PI * (35.0 + variant * 5.0) * tSec))
                    sample += lpSample * swooshEnv * airMod * 0.95
                }
            }

            // 3. Phase 3: Paper Landing Slap / Settle on Open Stack (155ms onwards)
            if (i >= (slapStartSamples - 300)) {
                val slapRel = (i - (slapStartSamples - 300)).toDouble() / (numSamples - slapStartSamples + 300)
                if (slapRel in 0.0..1.0) {
                    val slapEnv = exp(-12.0 * slapRel) * sin(slapRel.coerceIn(0.0, 0.1) / 0.1 * PI * 0.5)

                    // Dual acoustic resonance of book block (180Hz body thud + 1400Hz crisp surface contact)
                    val slapThud = sin(2.0 * PI * 185.0 * tSec) * exp(-18.0 * slapRel) * 0.55
                    val slapCrack = (random.nextDouble() * 2.0 - 1.0) * exp(-28.0 * slapRel) * 0.45

                    sample += (slapThud + slapCrack) * slapEnv * 1.6
                }
            }

            // Master envelope tapering to prevent clicks
            val masterEnvelope = when {
                i < 100 -> i / 100.0
                i > numSamples - 200 -> (numSamples - i) / 200.0
                else -> 1.0
            }

            val finalSample = (sample * masterEnvelope).coerceIn(-1.0, 1.0)
            buffer[i] = (finalSample * 22000.0).toInt().toShort()
        }

        return buffer
    }

    /**
     * Plays the authentic physical book page turn sound.
     */
    fun playPageFlipSound(enabled: Boolean = true) {
        if (!enabled) return

        scope.launch {
            try {
                val variantIndex = random.nextInt(soundBuffers.size.coerceAtLeast(1))
                val buffer = if (soundBuffers.isNotEmpty()) soundBuffers[variantIndex] else generateRealPaperFlipPcm(0)

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()

                val duration = (buffer.size * 1000L) / sampleRate
                Thread.sleep(duration + 40)
                audioTrack.release()
            } catch (e: Exception) {
                // Handled gracefully
            }
        }
    }
}
