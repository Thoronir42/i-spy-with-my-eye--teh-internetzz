package cz.zcu.kiv.nlp.cdNemovitosti;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Estate implements Serializable {
    private String url;
    private String title;

    private String evidenceNumber;
    private String pricePerSquareMeter;

    private String region;
    private String district;
    private String city;
    private String catasterZone;
    private String surface;
    private String water;
    private String electricity;
    private String canalization;

    private List<String> picturesUrls = new ArrayList<>();

    public Estate() {
    }

    public String getUrl() {
        return url;
    }

    public Estate setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Estate setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getEvidenceNumber() {
        return evidenceNumber;
    }

    public Estate setEvidenceNumber(String evidenceNumber) {
        this.evidenceNumber = evidenceNumber;
        return this;
    }

    public String getPricePerSquareMeter() {
        return pricePerSquareMeter;
    }

    public Estate setPricePerSquareMeter(String pricePerSquareMeter) {
        this.pricePerSquareMeter = pricePerSquareMeter;
        return this;
    }

    public String getRegion() {
        return region;
    }

    public Estate setRegion(String region) {
        this.region = region;
        return this;
    }

    public String getDistrict() {
        return district;
    }

    public Estate setDistrict(String district) {
        this.district = district;
        return this;
    }

    public String getCity() {
        return city;
    }

    public Estate setCity(String city) {
        this.city = city;
        return this;
    }

    public String getCatasterZone() {
        return catasterZone;
    }

    public Estate setCatasterZone(String catasterZone) {
        this.catasterZone = catasterZone;
        return this;
    }

    public String getSurface() {
        return surface;
    }

    public Estate setSurface(String surface) {
        this.surface = surface;
        return this;
    }

    public String getWater() {
        return water;
    }

    public Estate setWater(String water) {
        this.water = water;
        return this;
    }

    public String getElectricity() {
        return electricity;
    }

    public Estate setElectricity(String electricity) {
        this.electricity = electricity;
        return this;
    }

    public String getCanalization() {
        return canalization;
    }

    public Estate setCanalization(String canalization) {
        this.canalization = canalization;
        return this;
    }

    public List<String> getPicturesUrls() {
        return picturesUrls;
    }

    public Estate setPicturesUrls(List<String> picturesUrls) {
        this.picturesUrls = picturesUrls;
        return this;
    }
}
