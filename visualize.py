import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import os

def visualize_sensor_data():
    """Visualize the sensor data collected during simulation"""
    # Create results directory if it doesn't exist
    if not os.path.exists('visualizations'):
        os.makedirs('visualizations')
    
    # Load the CSV data
    sensor_data = pd.read_csv('results/sensor_data.csv')
    cloudlet_results = pd.read_csv('results/cloudlet_results.csv')
    vm_utilization = pd.read_csv('results/vm_utilization.csv')
    
    # Set seaborn style
    sns.set(style="whitegrid")
    
    # 1. Visualize sensor data over time
    plt.figure(figsize=(12, 8))
    
    # Group by sensor type and timestep
    sensor_time_data = sensor_data.groupby(['TimeStep', 'SensorType'])['Value'].mean().reset_index()
    
    # Plot each sensor type
    for sensor_type in sensor_time_data['SensorType'].unique():
        subset = sensor_time_data[sensor_time_data['SensorType'] == sensor_type]
        plt.plot(subset['TimeStep'], subset['Value'], marker='o', label=sensor_type)
    
    plt.title('Average Sensor Readings Over Time')
    plt.xlabel('Time Step')
    plt.ylabel('Sensor Value')
    plt.legend()
    plt.grid(True)
    plt.savefig('visualizations/sensor_readings_over_time.png')
    
    # 2. Execution times by sensor type
    plt.figure(figsize=(10, 6))
    sns.boxplot(x='SensorType', y='ExecTime', data=cloudlet_results)
    plt.title('Execution Time by Sensor Type')
    plt.xlabel('Sensor Type')
    plt.ylabel('Execution Time (s)')
    plt.xticks(rotation=45)
    plt.tight_layout()
    plt.savefig('visualizations/exec_time_by_sensor.png')
    
    # 3. VM Utilization
    plt.figure(figsize=(8, 6))
    sns.barplot(x='VMID', y='MIPSUtilization', data=vm_utilization)
    plt.title('VM Utilization (% MIPS)')
    plt.xlabel('VM ID')
    plt.ylabel('MIPS Utilization (%)')
    plt.savefig('visualizations/vm_utilization.png')
    
    # 4. Distribution of sensor values
    plt.figure(figsize=(15, 10))
    
    for i, sensor_type in enumerate(sensor_data['SensorType'].unique(), 1):
        plt.subplot(2, 3, i)
        subset = sensor_data[sensor_data['SensorType'] == sensor_type]
        sns.histplot(subset['Value'], kde=True)
        plt.title(f'Distribution of {sensor_type} Values')
        plt.xlabel('Value')
        plt.ylabel('Frequency')
    
    plt.tight_layout()
    plt.savefig('visualizations/sensor_value_distributions.png')
    
    # 5. Heatmap of sensor values by time and sensor ID
    plt.figure(figsize=(14, 10))
    
    for i, sensor_type in enumerate(sensor_data['SensorType'].unique(), 1):
        plt.subplot(2, 3, i)
        subset = sensor_data[sensor_data['SensorType'] == sensor_type].pivot(
            index='TimeStep', columns='SensorID', values='Value')
        sns.heatmap(subset, cmap='viridis', annot=True, fmt='.1f', cbar_kws={'label': 'Value'})
        plt.title(f'{sensor_type} Values Over Time')
        plt.xlabel('Sensor ID')
        plt.ylabel('Time Step')
    
    plt.tight_layout()
    plt.savefig('visualizations/sensor_heatmaps.png')
    
    print("Visualizations created successfully in the 'visualizations' directory!")

if __name__ == "__main__":
    visualize_sensor_data()