package com.zoho.atlas.model;

import java.util.ArrayList;
import java.util.List;

public class CountryData {

        private String name,capital,region;
        private int population;
        private List<Double> latlng = new ArrayList<Double>();
        private String flag;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCapital() {
            return capital;
        }

        public void setCapital(String capital) {
            this.capital = capital;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public int getPopulation() {
            return population;
        }

        public void setPopulation(int population) {
            this.population = population;
        }

        public List<Double> getLatlng() {
            return latlng;
        }

        public void setLatlng(List<Double> latlng) {
            this.latlng = latlng;
        }

        public String getFlag() {
            return flag;
        }

        public void setFlag(String flag) {
            this.flag = flag;
        }
}

