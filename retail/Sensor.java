package org.cloudbus.cloudsim.examples.retail;

/**
 * Base class for all sensor implementations in retail IoT simulation
 */
public abstract class Sensor {
    private int id;
    private int mipsRequirement;
    private double dataRate;
    
    /**
     * Constructor for Sensor
     * 
     * @param id Unique sensor identifier
     * @param mipsRequirement Processing power required (in MIPS)
     * @param dataRate Data generation rate (in KB/s)
     */
    public Sensor(int id, int mipsRequirement, double dataRate) {
        this.id = id;
        this.mipsRequirement = mipsRequirement;
        this.dataRate = dataRate;
    }
    
    /**
     * Generate sensor data
     * Each sensor implementation should override this to produce
     * appropriate simulated data values
     * 
     * @return The generated sensor data value
     */
    public abstract double generateData();
    
    /**
     * Get the sensor ID
     * 
     * @return The sensor ID
     */
    public int getId() {
        return id;
    }
    
    /**
     * Get the MIPS requirement for this sensor
     * 
     * @return MIPS required for processing
     */
    public int getMipsRequirement() {
        return mipsRequirement;
    }
    
    /**
     * Get the data generation rate
     * 
     * @return Data rate in KB/s
     */
    public double getDataRate() {
        return dataRate;
    }
    
    /**
     * String representation of the sensor
     */
    @Override
    public abstract String toString();
}