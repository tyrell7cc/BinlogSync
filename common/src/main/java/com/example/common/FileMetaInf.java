package com.example.common;

import java.io.Serializable;

public class FileMetaInf implements Serializable {
    private long size;
    private String hash;


    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
