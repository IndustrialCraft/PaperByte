package com.github.industrialcraft.paperbyte.common.util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class NonClosingInputStreamWrapper extends InputStream {
    private final InputStream parent;
    public NonClosingInputStreamWrapper(InputStream parent) {
        this.parent = parent;
    }
    @Override
    public int read(@NotNull byte[] b) throws IOException {
        return parent.read(b);
    }
    @Override
    public int read(@NotNull byte[] b, int off, int len) throws IOException {
        return parent.read(b, off, len);
    }
    @Override
    public byte[] readAllBytes() throws IOException {
        return parent.readAllBytes();
    }
    @Override
    public byte[] readNBytes(int len) throws IOException {
        return parent.readNBytes(len);
    }
    @Override
    public int readNBytes(byte[] b, int off, int len) throws IOException {
        return parent.readNBytes(b, off, len);
    }
    @Override
    public long skip(long n) throws IOException {
        return parent.skip(n);
    }
    @Override
    public void skipNBytes(long n) throws IOException {
        parent.skipNBytes(n);
    }
    @Override
    public int available() throws IOException {
        return parent.available();
    }
    @Override
    public void close() throws IOException {

    }
    @Override
    public synchronized void mark(int readlimit) {
        parent.mark(readlimit);
    }
    @Override
    public synchronized void reset() throws IOException {
        parent.reset();
    }
    @Override
    public boolean markSupported() {
        return parent.markSupported();
    }
    @Override
    public long transferTo(OutputStream out) throws IOException {
        return parent.transferTo(out);
    }
    @Override
    public int read() throws IOException {
        return parent.read();
    }
}
