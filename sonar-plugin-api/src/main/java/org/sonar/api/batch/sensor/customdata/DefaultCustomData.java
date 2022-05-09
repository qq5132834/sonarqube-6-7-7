package org.sonar.api.batch.sensor.customdata;

import org.sonar.api.batch.sensor.internal.DefaultStorable;
import org.sonar.api.batch.sensor.internal.SensorStorage;

public class DefaultCustomData extends DefaultStorable implements CustomData {

    public DefaultCustomData() {
        super(null);
    }

    public DefaultCustomData(SensorStorage storage) {
        super(storage);
    }

    private String jsonData;

    private String fileName;

    @Override
    protected void doSave() {
        super.storage.store(this);
    }

    @Override
    public CustomData setFileName(String filename) {
        this.fileName = filename;
        return this;
    }

    @Override
    public String getFileName() { return this.fileName; }

    @Override
    public CustomData setData(String customData) {
        this.jsonData = customData;
        return this;
    }

    @Override
    public String getData() {
        return this.jsonData;
    }
}
