package com.ivoaz.darbuka.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * http://ccrma.stanford.edu/courses/422/projects/WaveFormat
 */
public class Wave {
    private static final String CHUNK_ID = "RIFF";
    private static final String FORMAT = "WAVE";
    private static final String SUBCHUNK1_ID = "fmt ";
    private static final String SUBCHUNK2_ID = "data";
    private static final short SUBCHUNK1_SIZE = 16;
    private static final short AUDIO_FORMAT_PCM = 1;

    private short numChannels;
    private int sampleRate;
    private int byteRate;
    private short blockAlign;
    private short bitsPerSample;
    private int subchunk2Size;
    private short[] data;

    public Wave() { }

    public Wave(InputStream stream) throws IOException {
        this.read(stream);
    }

    public Wave(short numChannels, int sampleRate, short bitsPerSample) {
        this.setNumChannels(numChannels);
        this.setSampleRate(sampleRate);
        this.setBitsPerSample(bitsPerSample);
        this.setByteRate(sampleRate * numChannels * bitsPerSample / 8);
        this.setBlockAlign((short) (numChannels * bitsPerSample / 8));
    }

    public Wave(short numChannels, int sampleRate, short bitsPerSample, short[] data) {
        this(numChannels, sampleRate, bitsPerSample);
        this.setData(data);
    }

    public short getNumChannels() {
        return numChannels;
    }

    public void setNumChannels(short numChannels) {
        this.numChannels = numChannels;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public int getByteRate() {
        return byteRate;
    }

    public void setByteRate(int byteRate) {
        this.byteRate = byteRate;
    }

    public short getBlockAlign() {
        return blockAlign;
    }

    public void setBlockAlign(short blockAlign) {
        this.blockAlign = blockAlign;
    }

    public short getBitsPerSample() {
        return bitsPerSample;
    }

    public void setBitsPerSample(short bitsPerSample) {
        this.bitsPerSample = bitsPerSample;
    }

    public int getSubchunk2Size() {
        return subchunk2Size;
    }

    public void setSubchunk2Size(int subchunk2Size) {
        this.subchunk2Size = subchunk2Size;
    }

    public short[] getData() {
        return data;
    }

    public void setData(short[] data) {
        this.data = data;
        this.setSubchunk2Size(data.length * 2);
    }

    public void read(InputStream stream) throws IOException {
        readString(stream, CHUNK_ID);
        readInt(stream);
        readString(stream, FORMAT);

        readString(stream, SUBCHUNK1_ID);
        readInt(stream, SUBCHUNK1_SIZE);

        readShort(stream, AUDIO_FORMAT_PCM);

        this.setNumChannels(readShort(stream));
        this.setSampleRate(readInt(stream));
        this.setByteRate(readInt(stream));
        this.setBlockAlign(readShort(stream));
        this.setBitsPerSample(readShort(stream));

        readString(stream, SUBCHUNK2_ID);

        this.setSubchunk2Size(readInt(stream));
        this.setData(bytesToShorts(readData(stream, this.getSubchunk2Size())));
    }

    private static void readString(InputStream stream, String string) throws IOException {
        for (int i = 0; i < string.length(); ++i) {
            if (string.charAt(i) != stream.read()) {
                throw new IOException("Unsupported wave file, could not read " + string + " tag");
            }
        }
    }

    private static int readInt(InputStream stream) throws IOException {
        return stream.read() | (stream.read() << 8) | (stream.read() << 16) | (stream.read() << 24);
    }

    private static void readInt(InputStream stream, int value) throws IOException {
        int read = readInt(stream);

        if (read != value) {
            throw new IOException("Unsupported wave file, could not read value of " + value);
        }
    }

    private static short readShort(InputStream stream) throws IOException {
        return (short)(stream.read() | (stream.read() << 8));
    }

    private static void readShort(InputStream stream, short value) throws IOException {
        short read = readShort(stream);

        if (read != value) {
            throw new IOException("Unsupported wave file, could not read value of " + value);
        }
    }

    private static byte[] readData(InputStream stream, int size) throws IOException {
        byte[] data = new byte[size];

        stream.read(data);

        return data;
    }

    public void write(OutputStream stream) throws IOException {
        writeString(stream, CHUNK_ID);
        writeInt(stream, 36 + this.getSubchunk2Size());
        writeString(stream, FORMAT);

        writeString(stream, SUBCHUNK1_ID);
        writeInt(stream, SUBCHUNK1_SIZE);

        writeShort(stream, AUDIO_FORMAT_PCM);
        writeShort(stream, this.getNumChannels());
        writeInt(stream, this.getSampleRate());
        writeInt(stream, this.getByteRate());
        writeShort(stream, this.getBlockAlign());
        writeShort(stream, this.getBitsPerSample());

        writeString(stream, SUBCHUNK2_ID);
        writeInt(stream, this.getSubchunk2Size());

        writeData(stream, shortsToBytes(this.getData()));
    }

    private static void writeString(OutputStream stream, String string) throws IOException {
        for (int i = 0; i < string.length(); ++i) {
            stream.write(string.charAt(i));
        }
    }

    private static void writeInt(OutputStream stream, int value) throws IOException {
        stream.write(value >> 0);
        stream.write(value >> 8);
        stream.write(value >> 16);
        stream.write(value >> 24);
    }

    private static void writeShort(OutputStream stream, short value) throws IOException {
        stream.write(value >> 0);
        stream.write(value >> 8);
    }

    private static void writeData(OutputStream stream, byte[] data) throws IOException {
        stream.write(data);
    }

    private static byte[] shortsToBytes(short[] data) {
        byte[] bytes = new byte[data.length * 2];

        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(data);

        return bytes;
    }

    private static short[] bytesToShorts(byte[] data) {
        short[] shorts = new short[data.length / 2];

        ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);

        return shorts;
    }

    @Override
    public String toString() {
        return "Wave{" +
                "numChannels=" + numChannels +
                ", sampleRate=" + sampleRate +
                ", byteRate=" + byteRate +
                ", blockAlign=" + blockAlign +
                ", bitsPerSample=" + bitsPerSample +
                ", subchunk2Size=" + subchunk2Size +
                '}';
    }
}
