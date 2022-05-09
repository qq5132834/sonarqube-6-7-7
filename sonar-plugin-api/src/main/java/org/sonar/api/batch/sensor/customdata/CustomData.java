package org.sonar.api.batch.sensor.customdata;

public interface CustomData {

    CustomData setFileName(String filename);

    String getFileName();

    CustomData setData(String customData);

    String getData();

    void save();
}
