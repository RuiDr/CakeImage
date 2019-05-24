package com.example.cakeImage.entity;

public class Ahash {
    public String id;
    public String address;
    public String finger;

    @Override
    public String toString() {
        return "ImagesInfo{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", finger='" + finger + '\'' +
                '}';
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFinger() {
        return finger;
    }

    public void setFinger(String finger) {
        this.finger = finger;
    }
}
