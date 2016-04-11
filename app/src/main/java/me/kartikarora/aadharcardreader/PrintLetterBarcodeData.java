package me.kartikarora.aadharcardreader;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Developer: chipset
 * Package : me.kartikarora.aadharcardreader
 * Project : Aadhar Card Reader
 * Date : 9/4/16
 */
public class PrintLetterBarcodeData extends RealmObject {
    @PrimaryKey
    private String uid;
    private String co;
    private String name;
    private String street;
    private String pc;
    private String yob;
    private String state;
    private String gender;
    private String loc;
    private String house;
    private String vtc;
    private String dist;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCo() {
        return co;
    }

    public void setCo(String co) {
        this.co = co;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPc() {
        return pc;
    }

    public void setPc(String pc) {
        this.pc = pc;
    }

    public String getYob() {
        return yob;
    }

    public void setYob(String yob) {
        this.yob = yob;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getVtc() {
        return vtc;
    }

    public void setVtc(String vtc) {
        this.vtc = vtc;
    }

    public String getDist() {
        return dist;
    }

    public void setDist(String dist) {
        this.dist = dist;
    }

    @Override
    public String toString() {
        return "ClassPojo [uid = " + uid + ", co = " + co + ", name = " + name + ", street = " + street + ", pc = " + pc + ", yob = " + yob + ", state = " + state + ", gender = " + gender + ", loc = " + loc + ", house = " + house + ", vtc = " + vtc + ", dist = " + dist + "]";
    }
}
