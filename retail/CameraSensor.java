package org.cloudbus.cloudsim.examples.retail;

import org.cloudbus.cloudsim.examples.retail.Sensor;

public class CameraSensor extends Sensor {
    private static final int MIPS_REQ = 1000;  // High MIPS for video processing
    private static final double DATA_RATE = 50000;  // KB/s (50MB/s)

    public CameraSensor(int id) {
        super(id, MIPS_REQ, DATA_RATE);
    }

    @Override
    public double generateData() {
        // Simulate frame processing (dummy confidence score 0-1)
        return Math.random(); 
    }

    @Override
    public String toString() {
        return "CameraSensor#" + getId();
    }
}