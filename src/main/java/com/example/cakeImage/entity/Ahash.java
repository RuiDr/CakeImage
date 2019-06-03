package com.example.cakeImage.entity;

public class Ahash {
    public String id;
    public String address;
    public String finger;
    public int distancec;


    @Override
    public String toString() {
        return "Ahash{" +
                "id='" + id + '\'' +
                ", address='" + address + '\'' +
                ", finger='" + finger + '\'' +
                ", distancec=" + distancec +
                '}';
    }

    public int getDistancec() {
        return distancec;
    }

    public void setDistancec(int distancec) {
        this.distancec = distancec;
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
