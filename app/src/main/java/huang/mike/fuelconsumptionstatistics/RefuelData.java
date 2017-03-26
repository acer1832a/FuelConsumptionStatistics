package huang.mike.fuelconsumptionstatistics;

/**
 * Created by User on 2017/2/15.
 */

class RefuelData {
    private String dateOfRefuel;
    private double refuelVolume;
    private int refuelMileage;
    private long vehicleID;
    private String oilType;
    private double oilPrice;
    private int dataId;

    public RefuelData(){

    }
    public RefuelData(int vehicleID, String dateOfRefuel, int refuelMileage, String oilType, double oilPrice, double refuelVolume){
        this.vehicleID = vehicleID;
        this.dateOfRefuel = dateOfRefuel;
        this.refuelMileage = refuelMileage;
        this.oilType = oilType;
        this.oilPrice = oilPrice;
        this.refuelVolume = refuelVolume;
    }

    public void setVehicleID(long vehicleID){ this.vehicleID = vehicleID;}
    public void setDateOfRefuel(String dateOfRefuel){ this.dateOfRefuel = dateOfRefuel;}
    public void setRefuelVolume(double refuelVolume){ this.refuelVolume = refuelVolume;}
    public void setRefuelMileage(int refuelMileage){ this.refuelMileage = refuelMileage;}
    public void setOilType(String oilType){ this.oilType = oilType;}
    public void setOilPrice(double oilPrice){ this.oilPrice = oilPrice;}
    public void setDataId(int dataId){ this.dataId = dataId;}

    public long getVehicleID(){
        return vehicleID;
    }
    public String getOilType(){
        return oilType;
    }
    public double getRefuelVolume(){
        return refuelVolume;
    }
    public int getDataId(){return dataId;}
    public double getOilPrice(){
        return oilPrice;
    }
    public int getRefuelMileage(){
        return refuelMileage;
    }
    public String getDateOfRefuel(){
        return dateOfRefuel;
    }
    public int getTotalPrice(){
        Double totalPrice = oilPrice * refuelVolume;
        return totalPrice.intValue();
    }
}
