package com.zoho.atlas.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ForecastData {
    String cod;
    int message,cnt;
    ArrayList<forecastlist> list;

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public int getMessage() {
        return message;
    }

    public void setMessage(int message) {
        this.message = message;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public ArrayList<forecastlist> getList() {
        return list;
    }

    public void setList(ArrayList<forecastlist> list) {
        this.list = list;
    }

    public class forecastlist {
        @SerializedName("main")
        @Expose
        private Main main;
        private String dt_txt;
        int dt;
        List<weatherlist> weather;
        public int getDt() {
            return dt;
        }

        public void setDt(int dt) {
            this.dt = dt;
        }

        public Main getMain() {
            return main;
        }

        public void setMain(Main main) {
            this.main = main;
        }

        public String getDt_txt() {
            return dt_txt;
        }

        public void setDt_txt(String dt_txt) {
            this.dt_txt = dt_txt;
        }

        public class Main {

            @SerializedName("temp")
            @Expose
            private Double temp;

            public Double getTemp() {
                return temp;
            }

            public void setTemp(Double temp) {
                this.temp = temp;
            }
        }

        public List<weatherlist> getWeather() {
            return weather;
        }

        public void setWeather(List<weatherlist> weather) {
            this.weather = weather;
        }

        public class weatherlist {

            private String description,main,icon;

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getMain() {
                return main;
            }

            public void setMain(String main) {
                this.main = main;
            }

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }
        }
    }

}
