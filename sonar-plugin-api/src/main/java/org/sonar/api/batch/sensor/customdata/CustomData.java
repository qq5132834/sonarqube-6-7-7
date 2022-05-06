package org.sonar.api.batch.sensor.customdata;

public interface CustomData {

    CustomData setData(String customData);

    String getData();

    void save();
}
