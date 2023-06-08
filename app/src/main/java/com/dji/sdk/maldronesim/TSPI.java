package com.dji.sdk.maldronesim;

import java.util.Date;

public class TSPI {
    StringBuffer loggedTSPI;
    private String header;
    private Date timestamp;
    private String gpsSignalStrength;
    private int satelliteCount;

    private double homepointLatitude;
    private double homepointLongitude;
    private double homepointAltitude;

    private double currentLatitude;
    private double currentLongitude;
    private double currentAltitude;
    private double currentAltitude_seaTohome;

    private double pitch;
    private double yaw;
    private double roll;

    float vX;
    float vY;
    float vZ;
    float vXYZ;

    TSPI(){
        this.loggedTSPI = new StringBuffer();
        this.header = "Date,GPSSignalStrength,SatteliteCount,Altitude,Latitude,Longitude\n";
        this.loggedTSPI.append(header);
    }

    public void setTimestamp(Date time){this.timestamp = time;}
    public void setGpsSignalStrength(String GpsSignalStrength){this.gpsSignalStrength = GpsSignalStrength; }
    public void setSatelliteCount(int satelliteCount){this.satelliteCount = satelliteCount;}

    public void setHomepointLatitude(double homepointLatitude) {this.homepointLatitude = homepointLatitude;}
    public void setHomepointLongitude(double homepointLongitude) {this.homepointLongitude = homepointLongitude;}
    public void setHomepointAltitude(double homepointAltitude){this.homepointAltitude = homepointAltitude;}

    public void setCurrentLatitude(double latitude){
        this.currentLatitude = latitude;
    }
    public void setCurrentLongitude(double longitude){
        this.currentLongitude = longitude;
    }
    public void setCurrentAltitude(double altitude){
        this.currentAltitude = altitude;
    }

    public void setCurrentAltitude_seaTohome(double altitude_seaTohome){
        this.currentAltitude_seaTohome = altitude_seaTohome;
    }

    public void setPitch(double pitch){
        this.pitch = pitch;
    }
    public void setYaw(double yaw){
        this.yaw = yaw;
    }
    public void setRoll(double roll){
        this.roll = roll;
    }
    public Date getTimestamp(){
        return this.timestamp;
    }
    public String getGpsSignalStrength(){
        return gpsSignalStrength;
    }
    public double getCurrentLatitude(){
        return currentLatitude;
    }
    public double getCurrentLongitude(){
        return currentLongitude;
    }
    public double getCurrentAltitude(){
        return currentAltitude;
    }
    public double getPitch(){
        return pitch;
    }
    public double getYaw() {
        return yaw;
    }

    public String logResults(){
        if(loggedTSPI.length() == header.length()){
            loggedTSPI.append(timestamp).append(",");
            loggedTSPI.append(gpsSignalStrength).append(",");
            loggedTSPI.append(satelliteCount).append(",");
            loggedTSPI.append(currentAltitude).append(",");
            loggedTSPI.append(currentLatitude).append(",");
            loggedTSPI.append(currentLongitude).append("\n");
        } else {
            loggedTSPI.delete(0, loggedTSPI.length());
            loggedTSPI.append(timestamp).append(",");
            loggedTSPI.append(gpsSignalStrength).append(",");
            loggedTSPI.append(satelliteCount).append(",");
            loggedTSPI.append(currentAltitude).append(",");
            loggedTSPI.append(currentLatitude).append(",");
            loggedTSPI.append(currentLongitude).append("\n");
        }

        return String.valueOf(loggedTSPI);
    }
}