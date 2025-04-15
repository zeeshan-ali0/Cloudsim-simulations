package org.cloudbus.cloudsim.examples.retail;

import org.cloudbus.cloudsim.examples.retail.Sensor;

public class TemperatureSensor extends Sensor {
    private static final int MIPS_REQ = 100;
    private static final double DATA_RATE = 50;  // Low data rate

    public TemperatureSensor(int id) {
        super(id, MIPS_REQ, DATA_RATE);
    }

    @Override
    public double generateData() {
        // Simulate retail store temperature (15-30°C)
        return 15 + Math.random() * 15; 
    }

    @Override
    public String toString() {
        return "TemperatureSensor#" + getId();
    }
}