package org.cloudbus.cloudsim.examples.retail;

import org.cloudbus.cloudsim.examples.retail.Sensor;

public class ProximitySensor extends Sensor {
    private static final int MIPS_REQ = 200;
    private static final double DATA_RATE = 500;  // KB/s

    public ProximitySensor(int id) {
        super(id, MIPS_REQ, DATA_RATE);
    }

    @Override
    public double generateData() {
        // Simulate distance in cm (0-200cm range)
        return Math.random() * 200; 
    }

    @Override
    public String toString() {
        return "ProximitySensor#" + getId();
    }
}