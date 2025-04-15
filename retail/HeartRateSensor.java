package org.cloudbus.cloudsim.examples.retail;

import org.cloudbus.cloudsim.examples.retail.Sensor;
import java.util.Random;

public class HeartRateSensor extends Sensor {
    private static final int MIPS_REQ = 500;  // MIPS needed for processing
    private static final double DATA_RATE = 100;  // KB/s
    private final Random rand;

    public HeartRateSensor(int id) {
        super(id, MIPS_REQ, DATA_RATE);
        this.rand = new Random();
    }

    @Override
    public double generateData() {
        return 60 + rand.nextInt(40); 
    }

    @Override
    public String toString() {
        return "HeartRateSensor#" + getId();
    }
}