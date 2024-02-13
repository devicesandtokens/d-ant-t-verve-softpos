package com.alcineo.bonappetit.domain.utils;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.media.AudioTrack.Builder;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Utils class for beep/sound
 */
public class SoundGenerator {

    private static final int        SAMPLE_RATE = 44100;  // Standard 44.1 KHz
    private static       AudioTrack audioTrack  = null;
    private static       float      volume      = 0.5f;

    public static void playSound(int frequencyTone, int durationMS, int intervalMs, int count) {
        ByteArrayOutputStream audioBuffer = new ByteArrayOutputStream();

        for (int i = 0; i < count; i++) {
            // first bip
            try {
                audioBuffer.write(generateWave(durationMS, frequencyTone));
                audioBuffer.write(generateWave(intervalMs, 0));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            int bufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE,
                                                         AudioFormat.CHANNEL_OUT_MONO,
                                                         AudioFormat.ENCODING_PCM_16BIT);

            if (VERSION.SDK_INT >= VERSION_CODES.M) {
                audioTrack = new Builder().setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION)
                                                                                                      .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                                                                                      .build())
                                                     .setAudioFormat(new AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                                                                              .setSampleRate(SAMPLE_RATE)
                                                                                              .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                                                                                              .build())
                                                     .setBufferSizeInBytes(bufferSize)
                                                     .build();
            }

            // Sanity Check for max volume, set after write method to handle issue in android
            float maxVolume = AudioTrack.getMaxVolume();

            if (volume > maxVolume) {
                volume = maxVolume;
            } else if (volume < 0) {
                volume = 0;
            }
            audioTrack.setVolume(volume);

            audioTrack.play(); // Play the track
            audioTrack.write(audioBuffer.toByteArray(), 0, audioBuffer.toByteArray().length);    // Load the track

        } catch (Exception e) {
            e.printStackTrace();
        }

        stopTone();
    }

    private static byte[] generateWave(double duration, int frequencyTone) {
        double dnumSamples = (duration / 1000) * SAMPLE_RATE;
        dnumSamples = Math.ceil(dnumSamples);
        int numSamples = (int) dnumSamples;
        double[] sample = new double[numSamples];
        byte[] generatedSnd = new byte[2 * numSamples];

        for (int i = 0; i < numSamples; ++i) {      // Fill the sample array
            sample[i] = Math.sin(frequencyTone * 2 * Math.PI * i / SAMPLE_RATE);
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalized.
        int idx = 0;
        int i;

        int ramp = numSamples / 20;  // Amplitude ramp as a percent of sample count

        for (i = 0; i < ramp; ++i) {  // Ramp amplitude up (to avoid clicks)
            // Ramp up to maximum
            final short val = (short) (sample[i] * 32767 * i / ramp);
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }

        for (i = ramp; i < numSamples - ramp; ++i) { // Max amplitude for most of the samples
            // scale to maximum amplitude
            final short val = (short) (sample[i] * 32767);
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }

        for (i = numSamples - ramp; i < numSamples; ++i) { // Ramp amplitude down
            // Ramp down to zero
            final short val = (short) (sample[i] * 32767 * (numSamples - i) / ramp);
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }
        return generatedSnd;
    }

    public static void stopTone() {
        if (audioTrack != null && audioTrack.getState() == AudioTrack.PLAYSTATE_PLAYING) {
            audioTrack.stop();
            audioTrack.release();
        }
    }

}
