package com.woogleFX.file.fileImport;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PositionableInputStream {

    private final byte[] data;
    public PositionableInputStream(byte[] data) {
        this.data = data;
    }


    private int m_ptr = 0;
    public void setPtr(int ptr) {
        this.m_ptr = ptr;
    }


    public byte[] read(int length) {
        byte[] sub_data = new byte[length];
        System.arraycopy(data, m_ptr, sub_data, 0, length);
        m_ptr += length;
        return sub_data;
    }


    public int readInt() {
        return ByteBuffer.wrap(read(4)).order(ByteOrder.LITTLE_ENDIAN).getInt(0);
    }

    public float readFloat() {
        return ByteBuffer.wrap(read(4)).order(ByteOrder.LITTLE_ENDIAN).getFloat(0);
    }

}
