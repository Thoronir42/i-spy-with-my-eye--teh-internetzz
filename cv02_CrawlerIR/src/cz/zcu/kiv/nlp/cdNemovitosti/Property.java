package cz.zcu.kiv.nlp.cdNemovitosti;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Property implements Serializable {
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

    public Property() {
    }

    public String getUrl() {
        return url;
    }

    public Property setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Property setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getEvidenceNumber() {
        return evidenceNumber;
    }

    public Property setEvidenceNumber(String evidenceNumber) {
        this.evidenceNumber = evidenceNumber;
        return this;
    }

    public String getPricePerSquareMeter() {
        return pricePerSquareMeter;
    }

    public Property setPricePerSquareMeter(String pricePerSquareMeter) {
        this.pricePerSquareMeter = pricePerSquareMeter;
        return this;
    }

    public String getRegion() {
        return region;
    }

    public Property setRegion(String region) {
        this.region = region;
        return this;
    }

    public String getDistrict() {
        return district;
    }

    public Property setDistrict(String district) {
        this.district = district;
        return this;
    }

    public String getCity() {
        return city;
    }

    public Property setCity(String city) {
        this.city = city;
        return this;
    }

    public String getCatasterZone() {
        return catasterZone;
    }

    public Property setCatasterZone(String catasterZone) {
        this.catasterZone = catasterZone;
        return this;
    }

    public String getSurface() {
        return surface;
    }

    public Property setSurface(String surface) {
        this.surface = surface;
        return this;
    }

    public String getWater() {
        return water;
    }

    public Property setWater(String water) {
        this.water = water;
        return this;
    }

    public String getElectricity() {
        return electricity;
    }

    public Property setElectricity(String electricity) {
        this.electricity = electricity;
        return this;
    }

    public String getCanalization() {
        return canalization;
    }

    public Property setCanalization(String canalization) {
        this.canalization = canalization;
        return this;
    }

    public List<String> getPicturesUrls() {
        return picturesUrls;
    }

    public Property setPicturesUrls(List<String> picturesUrls) {
        this.picturesUrls = picturesUrls;
        return this;
    }
}
