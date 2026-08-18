package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

object SoundEffectManager {
    private const val SAMPLE_RATE = 22050
    private val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        // Silently swallow audio exceptions on headless or restricted devices
    }
    private val audioScope = CoroutineScope(Dispatchers.Default + exceptionHandler)

    fun playPop() {
        audioScope.launch {
            try {
                val durationMs = 90
                val numSamples = (SAMPLE_RATE * durationMs) / 1000
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / SAMPLE_RATE
                    val progress = i.toDouble() / numSamples
                    val freq = 400.0 + (500.0 * progress)
                    val envelope = 1.0 - progress
                    val sample = (sin(2.0 * Math.PI * freq * t) * envelope * Short.MAX_VALUE * 0.4).toInt()
                    buffer[i] = sample.toShort()
                }
                playBuffer(buffer)
            } catch (_: Throwable) {}
        }
    }

    fun playChime() {
        audioScope.launch {
            try {
                val durationMs = 280
                val numSamples = (SAMPLE_RATE * durationMs) / 1000
                val buffer = ShortArray(numSamples)

                val note1Samples = numSamples / 2
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / SAMPLE_RATE
                    val freq = if (i < note1Samples) 659.25 else 987.77
                    val noteProgress = if (i < note1Samples) {
                        i.toDouble() / note1Samples
                    } else {
                        (i - note1Samples).toDouble() / (numSamples - note1Samples)
                    }
                    val envelope = (1.0 - noteProgress) * 0.5
                    val sample = (sin(2.0 * Math.PI * freq * t) * envelope * Short.MAX_VALUE).toInt()
                    buffer[i] = sample.toShort()
                }
                playBuffer(buffer)
            } catch (_: Throwable) {}
        }
    }

    fun playCrack() {
        audioScope.launch {
            try {
                val durationMs = 120
                val numSamples = (SAMPLE_RATE * durationMs) / 1000
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / SAMPLE_RATE
                    val progress = i.toDouble() / numSamples
                    val noise = (Math.random() * 2.0 - 1.0)
                    val resonance = sin(2.0 * Math.PI * 320.0 * t)
                    val envelope = (1.0 - progress) * (1.0 - progress)
                    val sample = ((noise * 0.6 + resonance * 0.4) * envelope * Short.MAX_VALUE * 0.5).toInt()
                    buffer[i] = sample.toShort()
                }
                playBuffer(buffer)
            } catch (_: Throwable) {}
        }
    }

    fun playFanfare() {
        audioScope.launch {
            try {
                val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.50)
                val noteDurationMs = 130
                val totalDurationMs = noteDurationMs * notes.size + 200
                val numSamples = (SAMPLE_RATE * totalDurationMs) / 1000
                val buffer = ShortArray(numSamples)

                val samplesPerNote = (SAMPLE_RATE * noteDurationMs) / 1000

                for (i in 0 until numSamples) {
                    val noteIdx = (i / samplesPerNote).coerceAtMost(notes.size - 1)
                    val freq = notes[noteIdx]
                    val t = i.toDouble() / SAMPLE_RATE
                    val noteLocalIdx = i % samplesPerNote
                    val env = (1.0 - (noteLocalIdx.toDouble() / samplesPerNote)) * 0.6
                    val sample = (sin(2.0 * Math.PI * freq * t) * env * Short.MAX_VALUE).toInt()
                    buffer[i] = sample.toShort()
                }
                playBuffer(buffer)
            } catch (_: Throwable) {}
        }
    }

    private fun playBuffer(buffer: ShortArray) {
        try {
            val minBufferSize = AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSizeInBytes = if (minBufferSize > 0) {
                maxOf(minBufferSize, buffer.size * 2)
            } else {
                buffer.size * 2
            }

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSizeInBytes)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            val written = audioTrack.write(buffer, 0, buffer.size)
            if (written > 0 && audioTrack.state == AudioTrack.STATE_INITIALIZED) {
                audioTrack.play()
                audioTrack.setNotificationMarkerPosition(buffer.size)
                audioTrack.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
                    override fun onMarkerReached(track: AudioTrack?) {
                        try {
                            track?.release()
                        } catch (_: Throwable) {}
                    }
                    override fun onPeriodicNotification(track: AudioTrack?) {}
                })
            } else {
                try {
                    audioTrack.release()
                } catch (_: Throwable) {}
            }
        } catch (_: Throwable) {
            // Audio output device not available in certain emulator container configurations
        }
    }
}
