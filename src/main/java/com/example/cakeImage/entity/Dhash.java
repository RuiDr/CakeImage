package com.example.cakeImage.entity;

/**
 * @ Author     ：CrazyCake
 * @ Date       ：Created in 20:39 2019/5/24
 * @ Description：dhash算法实体类
 * @ Modified By：
 * @Version: 1.0$
 */
public class Dhash {
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
