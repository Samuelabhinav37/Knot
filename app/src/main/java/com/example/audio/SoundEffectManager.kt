package com.example.audio

import android.media.AudioManager
import android.media.ToneGenerator

object SoundEffectManager {
    private var toneGen: ToneGenerator? = null
    var isMuted: Boolean = false

    init {
        try {
            toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 60)
        } catch (_: Throwable) {
            toneGen = null
        }
    }

    fun playPop() {
        if (isMuted) return
        try {
            toneGen?.startTone(ToneGenerator.TONE_PROP_BEEP, 30)
        } catch (_: Throwable) {}
    }

    fun playTick() {
        if (isMuted) return
        try {
            toneGen?.startTone(ToneGenerator.TONE_PROP_BEEP2, 15)
        } catch (_: Throwable) {}
    }

    fun playChime() {
        if (isMuted) return
        try {
            toneGen?.startTone(ToneGenerator.TONE_PROP_ACK, 80)
        } catch (_: Throwable) {}
    }

    fun playCrack() {
        if (isMuted) return
        try {
            toneGen?.startTone(ToneGenerator.TONE_PROP_NACK, 50)
        } catch (_: Throwable) {}
    }

    fun playFanfare() {
        if (isMuted) return
        try {
            toneGen?.startTone(ToneGenerator.TONE_PROP_PROMPT, 100)
        } catch (_: Throwable) {}
    }
}
