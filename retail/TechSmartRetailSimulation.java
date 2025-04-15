// package org.cloudbus.cloudsim.examples.retail;

// import java.text.DecimalFormat;
// import java.util.ArrayList;
// import java.util.Calendar;
// import java.util.LinkedList;
// import java.util.List;

// import org.cloudbus.cloudsim.Cloudlet;
// import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
// import org.cloudbus.cloudsim.Datacenter;
// import org.cloudbus.cloudsim.DatacenterBroker;
// import org.cloudbus.cloudsim.DatacenterCharacteristics;
// import org.cloudbus.cloudsim.Host;
// import org.cloudbus.cloudsim.Log;
// import org.cloudbus.cloudsim.Pe;
// import org.cloudbus.cloudsim.Storage;
// import org.cloudbus.cloudsim.UtilizationModel;
// import org.cloudbus.cloudsim.UtilizationModelFull;
// import org.cloudbus.cloudsim.Vm;
// import org.cloudbus.cloudsim.VmAllocationPolicySimple;
// import org.cloudbus.cloudsim.VmSchedulerTimeShared;
// import org.cloudbus.cloudsim.core.CloudSim;
// import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
// import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
// import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

// /**
//  * Enhanced TechSmart Retail IoT Simulation using CloudSim 5
//  */
// public class TechSmartRetailSimulation {

//     private static List<Cloudlet> cloudletList;
//     private static List<Vm> vmList;

//     // Sensor types constants
//     private static final int HEART_RATE_SENSOR = 0;
//     private static final int RFID_SENSOR = 1;
//     private static final int PROXIMITY_SENSOR = 2;
//     private static final int TEMPERATURE_SENSOR = 3;
//     private static final int CAMERA_SENSOR = 4;

//     // Sensor requirements
//     private static final int[] SENSOR_MIPS_REQUIREMENTS = {500, 300, 200, 100, 1000};
//     private static final long[] SENSOR_DATA_RATES = {100, 5000, 500, 50, 50000};
//     private static final String[] SENSOR_NAMES = {
//         "Heart Rate Monitor", "RFID Tag Scanner", 
//         "Proximity Sensor", "Temperature Sensor", "AI Camera"
//     };

//     public static void main(String[] args) {
//         Log.printLine("Starting Enhanced TechSmart Retail IoT Simulation...");

//         try {
//             // Initialize CloudSim
//             CloudSim.init(1, Calendar.getInstance(), false);

//             // Create Datacenters
//             Datacenter cloudDatacenter = createDatacenter("CloudDatacenter");
//             Datacenter fogDatacenter = createFogDatacenter("FogDatacenter");
//             Datacenter edgeDatacenter = createEdgeDatacenter("EdgeDatacenter");

//             // Verify datacenter creation
//             if (cloudDatacenter == null || fogDatacenter == null || edgeDatacenter == null) {
//                 Log.printLine("Error: Failed to create one or more datacenters");
//                 return;
//             }

//             // Create Broker
//             DatacenterBroker broker = createBroker();
//             if (broker == null) {
//                 Log.printLine("Error: Failed to create broker");
//                 return;
//             }
//             int brokerId = broker.getId();

//             // Create VMs
//             vmList = createVms(brokerId);
//             broker.submitVmList(vmList);

//             // Create Cloudlets
//             cloudletList = createCloudlets(brokerId);
//             broker.submitCloudletList(cloudletList);

//             // Start simulation
//             CloudSim.startSimulation();
//             CloudSim.stopSimulation();

//             // Print results
//             printResults(broker);
//             Log.printLine("Simulation completed successfully!");
//         } catch (Exception e) {
//             e.printStackTrace();
//             Log.printLine("Simulation failed due to an error!");
//         }
//     }

//     private static List<Vm> createVms(int brokerId) {
//         List<Vm> vms = new ArrayList<>();

//         // High-performance VMs (2) - For camera processing
//         for (int i = 0; i < 2; i++) {
//             vms.add(new Vm(i, brokerId, 2000, 2, 8192, 10000, 1000000, "Xen",
//                 new CloudletSchedulerTimeShared()));
//         }
        
//         // Medium-performance VMs (2) - For heart rate and RFID
//         for (int i = 2; i < 4; i++) {
//             vms.add(new Vm(i, brokerId, 1000, 1, 4096, 5000, 500000, "Xen",
//                 new CloudletSchedulerTimeShared()));
//         }
        
//         // Low-performance VMs (5) - For proximity and temperature
//         for (int i = 4; i < 9; i++) {
//             vms.add(new Vm(i, brokerId, 500, 1, 2048, 1000, 100000, "Xen",
//                 new CloudletSchedulerTimeShared()));
//         }

//         return vms;
//     }

//     private static List<Cloudlet> createCloudlets(int brokerId) {
//         List<Cloudlet> cloudlets = new ArrayList<>();
//         int id = 0;
//         long baseLength = 10000;
//         long baseFileSize = 300;
//         long baseOutputSize = 300;
//         UtilizationModel utilizationModel = new UtilizationModelFull();

//         // Create 5 cloudlets for each sensor type
//         for (int sensorType = 0; sensorType < 5; sensorType++) {
//             for (int i = 0; i < 5; i++) {
//                 long length = baseLength * (SENSOR_MIPS_REQUIREMENTS[sensorType] / 100);
//                 long fileSize = baseFileSize + SENSOR_DATA_RATES[sensorType];
//                 long outputSize = baseOutputSize + (SENSOR_DATA_RATES[sensorType] / 10);
                
//                 Cloudlet cloudlet = new Cloudlet(
//                     id++, length, 1, fileSize, outputSize,
//                     utilizationModel, utilizationModel, utilizationModel);
//                 cloudlet.setUserId(brokerId);
                
//                 // Assign to appropriate VM based on sensor type
//                 int vmId;
//                 if (sensorType == CAMERA_SENSOR) {
//                     vmId = id % 2; // First 2 VMs (0-1)
//                 } else if (sensorType == HEART_RATE_SENSOR || sensorType == RFID_SENSOR) {
//                     vmId = 2 + (id % 2); // Next 2 VMs (2-3)
//                 } else {
//                     vmId = 4 + (id % 5); // Last 5 VMs (4-8)
//                 }
                
//                 cloudlet.setVmId(vmId);
//                 cloudlets.add(cloudlet);
//             }
//         }
//         return cloudlets;
//     }

//     private static void printResults(DatacenterBroker broker) {
//         List<Cloudlet> finishedCloudlets = broker.getCloudletReceivedList();
//         DecimalFormat dft = new DecimalFormat("###.##");

//         Log.printLine("\n========== CLOUDLET EXECUTION RESULTS ==========");
//         Log.printLine("Cloudlet ID | Status | DC ID | VM ID | Time | Start Time | Finish Time | Sensor Type");
        
//         double[] totalTimes = new double[5];
//         int[] counts = new int[5];

//         for (Cloudlet cloudlet : finishedCloudlets) {
//             int sensorType = (cloudlet.getCloudletId() / 5) % 5;
//             counts[sensorType]++;
//             totalTimes[sensorType] += cloudlet.getActualCPUTime();

//             Log.printLine(String.format("%-10d | %-6s | %-5d | %-5d | %-5s | %-10s | %-11s | %s",
//                 cloudlet.getCloudletId(),
//                 "DONE",
//                 cloudlet.getResourceId(),
//                 cloudlet.getVmId(),
//                 dft.format(cloudlet.getActualCPUTime()),
//                 dft.format(cloudlet.getExecStartTime()),
//                 dft.format(cloudlet.getFinishTime()),
//                 SENSOR_NAMES[sensorType]));
//         }

//         Log.printLine("\n========== AVERAGE PROCESSING TIMES ==========");
//         for (int i = 0; i < 5; i++) {
//             if (counts[i] > 0) {
//                 Log.printLine(String.format("%-20s: %s seconds",
//                     SENSOR_NAMES[i], dft.format(totalTimes[i] / counts[i])));
//             }
//         }

//         Log.printLine("\n========== VM UTILIZATION SUMMARY ==========");
//         for (Vm vm : vmList) {
//             double totalMips = vm.getMips() * vm.getNumberOfPes();
//             double utilizedMips = 0;
//             int cloudletCount = 0;
            
//             for (Cloudlet cloudlet : finishedCloudlets) {
//                 if (cloudlet.getVmId() == vm.getId()) {
//                     int sensorType = (cloudlet.getCloudletId() / 5) % 5;
//                     utilizedMips += cloudlet.getActualCPUTime() * SENSOR_MIPS_REQUIREMENTS[sensorType];
//                     cloudletCount++;
//                 }
//             }
            
//             double utilization = (utilizedMips / (totalMips * CloudSim.clock())) * 100;
//             Log.printLine(String.format("VM %d: %d cloudlets | %.2f%% MIPS utilization",
//                 vm.getId(), cloudletCount, utilization));
//         }
//     }

//     private static Datacenter createDatacenter(String name) {
//         List<Host> hostList = new ArrayList<>();
//         int mips = 5000;

//         // Create 2 powerful cloud hosts
//         for (int hostId = 0; hostId < 2; hostId++) {
//             List<Pe> peList = new ArrayList<>();
//             for (int i = 0; i < 8; i++) {
//                 peList.add(new Pe(i, new PeProvisionerSimple(mips)));
//             }

//             hostList.add(new Host(
//                 hostId,
//                 new RamProvisionerSimple(65536), // 64GB RAM
//                 new BwProvisionerSimple(100000), // 100Gbps BW
//                 10000000, // 10TB storage
//                 peList,
//                 new VmSchedulerTimeShared(peList)
//             ));
//         }

//         DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
//             "x86", "Linux", "Xen", hostList, 10.0, 3.0, 0.05, 0.001, 0.1);

//         try {
//             return new Datacenter(name, characteristics, 
//                 new VmAllocationPolicySimple(hostList), new LinkedList<>(), 0);
//         } catch (Exception e) {
//             e.printStackTrace();
//             return null;
//         }
//     }

//     private static Datacenter createFogDatacenter(String name) {
//         List<Host> hostList = new ArrayList<>();
//         int mips = 3000;

//         // Create 2 fog hosts
//         for (int hostId = 2; hostId < 4; hostId++) {
//             List<Pe> peList = new ArrayList<>();
//             for (int i = 0; i < 4; i++) {
//                 peList.add(new Pe(i, new PeProvisionerSimple(mips)));
//             }

//             hostList.add(new Host(
//                 hostId,
//                 new RamProvisionerSimple(32768), // 32GB RAM
//                 new BwProvisionerSimple(10000), // 10Gbps BW
//                 1000000, // 1TB storage
//                 peList,
//                 new VmSchedulerTimeShared(peList)
//             ));
//         }

//         DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
//             "x86", "Linux", "Xen", hostList, 10.0, 2.0, 0.03, 0.0005, 0.05);

//         try {
//             return new Datacenter(name, characteristics,
//                 new VmAllocationPolicySimple(hostList), new LinkedList<>(), 0);
//         } catch (Exception e) {
//             e.printStackTrace();
//             return null;
//         }
//     }

//     private static Datacenter createEdgeDatacenter(String name) {
//         List<Host> hostList = new ArrayList<>();
//         int mips = 1500;

//         // Create 5 edge hosts (one per sensor type)
//         for (int hostId = 4; hostId < 9; hostId++) {
//             List<Pe> peList = new ArrayList<>();
//             for (int i = 0; i < 2; i++) {
//                 peList.add(new Pe(i, new PeProvisionerSimple(mips)));
//             }

//             hostList.add(new Host(
//                 hostId,
//                 new RamProvisionerSimple(8192), // 8GB RAM
//                 new BwProvisionerSimple(1000), // 1Gbps BW
//                 100000, // 100GB storage
//                 peList,
//                 new VmSchedulerTimeShared(peList)
//             ));
//         }

//         DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
//             "ARM", "Linux", "Xen", hostList, 10.0, 1.0, 0.01, 0.0001, 0.02);

//         try {
//             return new Datacenter(name, characteristics,
//                 new VmAllocationPolicySimple(hostList), new LinkedList<>(), 0);
//         } catch (Exception e) {
//             e.printStackTrace();
//             return null;
//         }
//     }

//     private static DatacenterBroker createBroker() {
//         try {
//             return new DatacenterBroker("Broker");
//         } catch (Exception e) {
//             e.printStackTrace();
//             return null;
//         }
//     }
// }



package org.cloudbus.cloudsim.examples.retail;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.cloudsim.examples.retail.Sensor;

/**
 * Enhanced TechSmart Retail IoT Simulation using CloudSim 5
 * Connects 5 different types of sensors and saves output for visualization
 */
public class TechSmartRetailSimulation {

    private static List<Cloudlet> cloudletList;
    private static List<Vm> vmList;
    private static List<Sensor> sensorList;
    
    // Output file paths for visualization
    private static final String CLOUDLET_OUTPUT_FILE = "results/cloudlet_results.csv";
    private static final String SENSOR_DATA_OUTPUT_FILE = "results/sensor_data.csv";
    private static final String VM_UTILIZATION_FILE = "results/vm_utilization.csv";
    
    // Map to store sensor data for visualization
    private static Map<Integer, List<Double>> sensorDataMap;

    // Sensor types constants
    private static final int HEART_RATE_SENSOR = 0;
    private static final int RFID_SENSOR = 1;
    private static final int PROXIMITY_SENSOR = 2;
    private static final int TEMPERATURE_SENSOR = 3;
    private static final int CAMERA_SENSOR = 4;

    // Sensor requirements
    private static final int[] SENSOR_MIPS_REQUIREMENTS = {500, 300, 200, 100, 1000};
    private static final long[] SENSOR_DATA_RATES = {100, 5000, 500, 50, 50000};
    private static final String[] SENSOR_NAMES = {
        "Heart Rate Monitor", "RFID Tag Scanner", 
        "Proximity Sensor", "Temperature Sensor", "AI Camera"
    };

    public static void main(String[] args) {
        Log.printLine("Starting Enhanced TechSmart Retail IoT Simulation...");

        try {
            // Initialize CloudSim
            int num_user = 1;
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false;
            CloudSim.init(num_user, calendar, trace_flag);

            // Initialize sensor data map
            sensorDataMap = new HashMap<>();
            
            // Create sensors
            sensorList = createSensors();
            
            // Create Datacenters
            Datacenter cloudDatacenter = createDatacenter("CloudDatacenter");
            Datacenter fogDatacenter = createFogDatacenter("FogDatacenter");
            Datacenter edgeDatacenter = createEdgeDatacenter("EdgeDatacenter");

            // Verify datacenter creation
            if (cloudDatacenter == null || fogDatacenter == null || edgeDatacenter == null) {
                Log.printLine("Error: Failed to create one or more datacenters");
                return;
            }

            // Create Broker
            DatacenterBroker broker = createBroker();
            if (broker == null) {
                Log.printLine("Error: Failed to create broker");
                return;
            }
            int brokerId = broker.getId();

            // Create VMs
            vmList = createVms(brokerId);
            broker.submitVmList(vmList);

            // Create Cloudlets based on sensor data
            cloudletList = createCloudlets(brokerId);
            broker.submitCloudletList(cloudletList);

            // Start simulation
            CloudSim.startSimulation();
            
            // Generate sensor data during simulation
            generateSensorData();
            
            CloudSim.stopSimulation();

            // Print and save results
            List<Cloudlet> finishedCloudlets = broker.getCloudletReceivedList();
            printResults(broker);
            saveResultsToFile(finishedCloudlets);
            saveSensorDataToFile();
            saveVmUtilizationToFile(finishedCloudlets);
            
            Log.printLine("Simulation completed successfully!");
            Log.printLine("Results saved to files in the 'results' directory.");
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("Simulation failed due to an error: " + e.getMessage());
        }
    }
    
    private static List<Sensor> createSensors() {
        List<Sensor> sensors = new ArrayList<>();
        
        // Create 5 instances of each sensor type
        for (int i = 0; i < 5; i++) {
            sensors.add(new HeartRateSensor(i));
            sensors.add(new RfidSensor(i + 5));
            sensors.add(new ProximitySensor(i + 10));
            sensors.add(new TemperatureSensor(i + 15));
            sensors.add(new CameraSensor(i + 20));
            
            // Initialize data arrays for each sensor
            sensorDataMap.put(i, new ArrayList<>());          // Heart Rate
            sensorDataMap.put(i + 5, new ArrayList<>());      // RFID
            sensorDataMap.put(i + 10, new ArrayList<>());     // Proximity
            sensorDataMap.put(i + 15, new ArrayList<>());     // Temperature
            sensorDataMap.put(i + 20, new ArrayList<>());     // Camera
        }
        
        return sensors;
    }

    private static List<Vm> createVms(int brokerId) {
        List<Vm> vms = new ArrayList<>();

        // High-performance VMs (2) - For camera processing
        for (int i = 0; i < 2; i++) {
            vms.add(new Vm(i, brokerId, 2000, 2, 8192, 10000, 1000000, "Xen",
                new CloudletSchedulerTimeShared()));
        }
        
        // Medium-performance VMs (2) - For heart rate and RFID
        for (int i = 2; i < 4; i++) {
            vms.add(new Vm(i, brokerId, 1000, 1, 4096, 5000, 500000, "Xen",
                new CloudletSchedulerTimeShared()));
        }
        
        // Low-performance VMs (5) - For proximity and temperature
        for (int i = 4; i < 9; i++) {
            vms.add(new Vm(i, brokerId, 500, 1, 2048, 1000, 100000, "Xen",
                new CloudletSchedulerTimeShared()));
        }

        return vms;
    }

    private static List<Cloudlet> createCloudlets(int brokerId) {
        List<Cloudlet> cloudlets = new ArrayList<>();
        int id = 0;
        long baseLength = 10000;
        long baseFileSize = 300;
        long baseOutputSize = 300;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        // Create cloudlets for each sensor
        for (Sensor sensor : sensorList) {
            int sensorType;
            if (sensor instanceof HeartRateSensor) {
                sensorType = HEART_RATE_SENSOR;
            } else if (sensor instanceof RfidSensor) {
                sensorType = RFID_SENSOR;
            } else if (sensor instanceof ProximitySensor) {
                sensorType = PROXIMITY_SENSOR;
            } else if (sensor instanceof TemperatureSensor) {
                sensorType = TEMPERATURE_SENSOR;
            } else { // Camera
                sensorType = CAMERA_SENSOR;
            }
            
            long length = baseLength * (SENSOR_MIPS_REQUIREMENTS[sensorType] / 100);
            long fileSize = baseFileSize + SENSOR_DATA_RATES[sensorType];
            long outputSize = baseOutputSize + (SENSOR_DATA_RATES[sensorType] / 10);
            
            Cloudlet cloudlet = new Cloudlet(
                id, length, 1, fileSize, outputSize,
                utilizationModel, utilizationModel, utilizationModel);
            cloudlet.setUserId(brokerId);
            
            // Assign to appropriate VM based on sensor type
            int vmId;
            if (sensorType == CAMERA_SENSOR) {
                vmId = id % 2; // First 2 VMs (0-1)
            } else if (sensorType == HEART_RATE_SENSOR || sensorType == RFID_SENSOR) {
                vmId = 2 + (id % 2); // Next 2 VMs (2-3)
            } else {
                vmId = 4 + (id % 5); // Last 5 VMs (4-8)
            }
            
            cloudlet.setVmId(vmId);
            cloudlets.add(cloudlet);
            id++;
        }
        return cloudlets;
    }
    
    private static void generateSensorData() {
        // Generate data for each sensor over simulation time
        for (int timeStep = 0; timeStep < 10; timeStep++) {
            for (Sensor sensor : sensorList) {
                double data = sensor.generateData();
                sensorDataMap.get(sensor.getId()).add(data);
            }
        }
    }

    private static void printResults(DatacenterBroker broker) {
        List<Cloudlet> finishedCloudlets = broker.getCloudletReceivedList();
        DecimalFormat dft = new DecimalFormat("###.##");

        Log.printLine("\n========== CLOUDLET EXECUTION RESULTS ==========");
        Log.printLine("Cloudlet ID | Status | DC ID | VM ID | Time | Start Time | Finish Time | Sensor Type");
        
        double[] totalTimes = new double[5];
        int[] counts = new int[5];

        for (Cloudlet cloudlet : finishedCloudlets) {
            int sensorId = cloudlet.getCloudletId();
            int sensorType = getSensorTypeFromId(sensorId);
            
            counts[sensorType]++;
            totalTimes[sensorType] += cloudlet.getActualCPUTime();

            Log.printLine(String.format("%-10d | %-6s | %-5d | %-5d | %-5s | %-10s | %-11s | %s",
                cloudlet.getCloudletId(),
                "DONE",
                cloudlet.getResourceId(),
                cloudlet.getVmId(),
                dft.format(cloudlet.getActualCPUTime()),
                dft.format(cloudlet.getExecStartTime()),
                dft.format(cloudlet.getFinishTime()),
                SENSOR_NAMES[sensorType]));
        }

        Log.printLine("\n========== AVERAGE PROCESSING TIMES ==========");
        for (int i = 0; i < 5; i++) {
            if (counts[i] > 0) {
                Log.printLine(String.format("%-20s: %s seconds",
                    SENSOR_NAMES[i], dft.format(totalTimes[i] / counts[i])));
            }
        }

        Log.printLine("\n========== VM UTILIZATION SUMMARY ==========");
        for (Vm vm : vmList) {
            double totalMips = vm.getMips() * vm.getNumberOfPes();
            double utilizedMips = 0;
            int cloudletCount = 0;
            
            for (Cloudlet cloudlet : finishedCloudlets) {
                if (cloudlet.getVmId() == vm.getId()) {
                    int sensorId = cloudlet.getCloudletId();
                    int sensorType = getSensorTypeFromId(sensorId);
                    utilizedMips += cloudlet.getActualCPUTime() * SENSOR_MIPS_REQUIREMENTS[sensorType];
                    cloudletCount++;
                }
            }
            
            double utilization = (utilizedMips / (totalMips * CloudSim.clock())) * 100;
            Log.printLine(String.format("VM %d: %d cloudlets | %.2f%% MIPS utilization",
                vm.getId(), cloudletCount, utilization));
        }
    }
    
    private static int getSensorTypeFromId(int sensorId) {
        if (sensorId < 5) {
            return HEART_RATE_SENSOR;
        } else if (sensorId < 10) {
            return RFID_SENSOR;
        } else if (sensorId < 15) {
            return PROXIMITY_SENSOR;
        } else if (sensorId < 20) {
            return TEMPERATURE_SENSOR;
        } else {
            return CAMERA_SENSOR;
        }
    }
    
    private static void saveResultsToFile(List<Cloudlet> finishedCloudlets) throws IOException {
        // Ensure directory exists
        new java.io.File("results").mkdirs();
        
        FileWriter writer = new FileWriter(CLOUDLET_OUTPUT_FILE);
        writer.write("CloudletID,SensorType,DCID,VMID,ExecTime,StartTime,FinishTime\n");
        
        for (Cloudlet cloudlet : finishedCloudlets) {
            int sensorId = cloudlet.getCloudletId();
            int sensorType = getSensorTypeFromId(sensorId);
            
            writer.write(String.format("%d,%s,%d,%d,%.2f,%.2f,%.2f\n",
                cloudlet.getCloudletId(),
                SENSOR_NAMES[sensorType],
                cloudlet.getResourceId(),
                cloudlet.getVmId(),
                cloudlet.getActualCPUTime(),
                cloudlet.getExecStartTime(),
                cloudlet.getFinishTime()));
        }
        
        writer.close();
    }
    
    private static void saveSensorDataToFile() throws IOException {
        FileWriter writer = new FileWriter(SENSOR_DATA_OUTPUT_FILE);
        writer.write("TimeStep,SensorID,SensorType,Value\n");
        
        for (Map.Entry<Integer, List<Double>> entry : sensorDataMap.entrySet()) {
            int sensorId = entry.getKey();
            int sensorType = getSensorTypeFromId(sensorId);
            List<Double> dataPoints = entry.getValue();
            
            for (int timeStep = 0; timeStep < dataPoints.size(); timeStep++) {
                writer.write(String.format("%d,%d,%s,%.2f\n",
                    timeStep,
                    sensorId,
                    SENSOR_NAMES[sensorType],
                    dataPoints.get(timeStep)));
            }
        }
        
        writer.close();
    }
    
    private static void saveVmUtilizationToFile(List<Cloudlet> finishedCloudlets) throws IOException {
        FileWriter writer = new FileWriter(VM_UTILIZATION_FILE);
        writer.write("VMID,CloudletCount,MIPSUtilization\n");
        
        for (Vm vm : vmList) {
            double totalMips = vm.getMips() * vm.getNumberOfPes();
            double utilizedMips = 0;
            int cloudletCount = 0;
            
            for (Cloudlet cloudlet : finishedCloudlets) {
                if (cloudlet.getVmId() == vm.getId()) {
                    int sensorId = cloudlet.getCloudletId();
                    int sensorType = getSensorTypeFromId(sensorId);
                    utilizedMips += cloudlet.getActualCPUTime() * SENSOR_MIPS_REQUIREMENTS[sensorType];
                    cloudletCount++;
                }
            }
            
            double utilization = (utilizedMips / (totalMips * CloudSim.clock())) * 100;
            writer.write(String.format("%d,%d,%.2f\n", vm.getId(), cloudletCount, utilization));
        }
        
        writer.close();
    }

    private static Datacenter createDatacenter(String name) {
        List<Host> hostList = new ArrayList<>();
        int mips = 5000;

        // Create 2 powerful cloud hosts
        for (int hostId = 0; hostId < 2; hostId++) {
            List<Pe> peList = new ArrayList<>();
            for (int i = 0; i < 8; i++) {
                peList.add(new Pe(i, new PeProvisionerSimple(mips)));
            }

            hostList.add(new Host(
                hostId,
                new RamProvisionerSimple(65536), // 64GB RAM
                new BwProvisionerSimple(100000), // 100Gbps BW
                10000000, // 10TB storage
                peList,
                new VmSchedulerTimeShared(peList)
            ));
        }

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
            "x86", "Linux", "Xen", hostList, 10.0, 3.0, 0.05, 0.001, 0.1);

        try {
            return new Datacenter(name, characteristics, 
                new VmAllocationPolicySimple(hostList), new LinkedList<>(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Datacenter createFogDatacenter(String name) {
        List<Host> hostList = new ArrayList<>();
        int mips = 3000;

        // Create 2 fog hosts
        for (int hostId = 2; hostId < 4; hostId++) {
            List<Pe> peList = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                peList.add(new Pe(i, new PeProvisionerSimple(mips)));
            }

            hostList.add(new Host(
                hostId,
                new RamProvisionerSimple(32768), // 32GB RAM
                new BwProvisionerSimple(10000), // 10Gbps BW
                1000000, // 1TB storage
                peList,
                new VmSchedulerTimeShared(peList)
            ));
        }

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
            "x86", "Linux", "Xen", hostList, 10.0, 2.0, 0.03, 0.0005, 0.05);

        try {
            return new Datacenter(name, characteristics,
                new VmAllocationPolicySimple(hostList), new LinkedList<>(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Datacenter createEdgeDatacenter(String name) {
        List<Host> hostList = new ArrayList<>();
        int mips = 1500;

        // Create 5 edge hosts (one per sensor type)
        for (int hostId = 4; hostId < 9; hostId++) {
            List<Pe> peList = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                peList.add(new Pe(i, new PeProvisionerSimple(mips)));
            }

            hostList.add(new Host(
                hostId,
                new RamProvisionerSimple(8192), // 8GB RAM
                new BwProvisionerSimple(1000), // 1Gbps BW
                100000, // 100GB storage
                peList,
                new VmSchedulerTimeShared(peList)
            ));
        }

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
            "ARM", "Linux", "Xen", hostList, 10.0, 1.0, 0.01, 0.0001, 0.02);

        try {
            return new Datacenter(name, characteristics,
                new VmAllocationPolicySimple(hostList), new LinkedList<>(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static DatacenterBroker createBroker() {
        try {
            return new DatacenterBroker("Broker");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}