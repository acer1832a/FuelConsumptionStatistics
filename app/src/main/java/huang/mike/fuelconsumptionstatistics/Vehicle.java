package huang.mike.fuelconsumptionstatistics;

/**
 * Created by User on 2017/2/15.
 */

class Vehicle {
    private int vehicleID;
    private String vehicleName;
    private String vehicleType;
    private int airCapacity;
    private int originMileage;
    private int currentMileage;
    private int lastTimeRefuelMileage;
    private double currentRefuelVolume;
    private double totalRefuelVolume;

    public Vehicle(){
    }

    public Vehicle(String vehicleName, String vehicleType, int airCapacity, int originMileage){
        this.vehicleName = vehicleName;
        this.vehicleType = vehicleType;
        this.airCapacity = airCapacity;
        this.originMileage = originMileage;
    }

    public void setVehicleName(String vehicleName){ this.vehicleName = vehicleName;}
    public void setVehicleType(String vehicleType){ this.vehicleType = vehicleType;}
    public void setAirCapacity(int airCapacity){ this.airCapacity = airCapacity;}
    public void setOriginMileage(int originMileage){ this.originMileage = originMileage;}
    public void setCurrentMileage(int currentMileage){ this.currentMileage = currentMileage;}
    public void setLastTimeRefuelMileage(int lastTimeRefuelMileage){ this.lastTimeRefuelMileage = lastTimeRefuelMileage;}
    public void setCurrentRefuelVolume(double currentRefuelVolume){ this.currentRefuelVolume = currentRefuelVolume;}
    public void setTotalRefuelVolume(double totalRefuelVolume){ this.totalRefuelVolume = totalRefuelVolume;}
    public void setVehicleID(int vehicleID){ this.vehicleID = vehicleID;}


    public void updateVehicleSummary(int mileage, double refuelVolume){
        this.lastTimeRefuelMileage = this.currentMileage;
        this.currentMileage = mileage;
        this.currentRefuelVolume = refuelVolume;
        this.totalRefuelVolume += refuelVolume;
    }

    public double getFuelConsumptionKMPerLiter(){
        if(lastTimeRefuelMileage == 0) {
            return 0;
        }else{
        return (lastTimeRefuelMileage - originMileage) / totalRefuelVolume;
        }
    }

    public double getFuelConsumptionLiterPer100KM(){
        return (totalRefuelVolume/(lastTimeRefuelMileage - originMileage))*100;
    }

    public int getVehicleID(){ return vehicleID; }
    public String getVehicleName(){
        return vehicleName;
    }
    public String getVehicleType(){
        return vehicleType;
    }
    public int getAirCapacity(){
        return airCapacity;
    }
    public int getOriginMileage(){
        return originMileage;
    }
    public int getCurrentMileage(){
        if(currentMileage > 0) {
            return currentMileage;
        }else {
            return originMileage;
        }
    }
    public double getCurrentRefuelVolume(){ return currentRefuelVolume;}
    public double getTotalRefuelVolume(){
        return totalRefuelVolume;
    }
    public int getLastTimeRefuelMileage(){
        return lastTimeRefuelMileage;
    }

}
