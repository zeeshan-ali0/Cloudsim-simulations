package org.cloudbus.cloudsim.examples.retail;

import org.cloudbus.cloudsim.examples.retail.Sensor;

public class RfidSensor extends Sensor {
    private static final int MIPS_REQ = 300;
    private static final double DATA_RATE = 5000;  // High data rate for bulk scans

    public RfidSensor(int id) {
        super(id, MIPS_REQ, DATA_RATE);
    }

    @Override
    public double generateData() {
        // Simulate RFID tag reads (dummy product ID)
        return Math.floor(Math.random() * 10000); 
    }

    @Override
    public String toString() {
        return "RfidSensor#" + getId();
    }
}