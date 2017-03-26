package huang.mike.fuelconsumptionstatistics;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by User on 2017/2/5.
 */

class RefuelDBHelper extends SQLiteOpenHelper {
    // Database Name
    private static final String DATABASE_NAME = "refuelDataManager";
    private static final int DATABASE_VERSION = 2;

    //Table Name
    private static final String TABLE_VEHICLE = "vehicle";
    private static final String TABLE_REFUEL_DATA = "refuel";
    private static final String KEY_ID = "_id"; //primary key

    //Vehicle Table - columns name
    private static final String KEY_VEHICLE_NAME = "vehicle_name";
    private static final String KEY_VEHICLE_TYPE = "vehicle_type";
    private static final String KEY_AIR_CAPACITY = "ari_capacity";
    private static final String KEY_ORIGIN_MILEAGE = "origin_mileage";
    private static final String KEY_CURRENT_MILEAGE = "current_mileage";
    private static final String KEY_LAST_TIME_REFUEL_MILEAGE = "last_time_refuel_mileage";
    private static final String KEY_CURRENT_REFUEL_VOLUME = "current_refuel_volume";
    private static final String KEY_TOTAL_REFUEL_VOLUME = "total_refuel_volume";

    //Refuel data Table - columns name
    private static final String KEY_VEHICLE_ID = "vehicle_id";
    private static final String KEY_REFUEL_DATE = "refuel_date";
    private static final String KEY_REFUEL_MILEAGE = "refuel_mileage";
    private static final String KEY_REFUEL_VOLUME = "refuel_volume";
    private static final String KEY_OIL_TYPE = "oil_type";
    private static final String KEY_OIL_PRICE = "oil_price";

    //Table Create Statements
    //vehicle table create statement
    private static final String CREATE_TABLE_VEHICLE = "CREATE TABLE "
            + TABLE_VEHICLE + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_VEHICLE_NAME + " TEXT NOT NULL,"
            + KEY_VEHICLE_TYPE + " TEXT NOT NULL," + KEY_AIR_CAPACITY + " INTEGER NOT NULL,"
            + KEY_ORIGIN_MILEAGE + " INTEGER NOT NULL," + KEY_CURRENT_MILEAGE + " INTEGER,"
            + KEY_LAST_TIME_REFUEL_MILEAGE + " INTEGER," + KEY_CURRENT_REFUEL_VOLUME + " INTEGER,"
            + KEY_TOTAL_REFUEL_VOLUME + " REAL)";
    //refuel data table create statement
    private static final String CREATE_TABLE_REFUEL_DATA = "CREATE TABLE "
            + TABLE_REFUEL_DATA + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_VEHICLE_ID + " INTEGER NOT NULL,"
            + KEY_REFUEL_DATE + " TEXT NOT NULL," + KEY_REFUEL_MILEAGE + " INTEGER NOT NULL,"
            + KEY_REFUEL_VOLUME + " REAL NOT NULL," + KEY_OIL_TYPE + " TEXT NOT NULL,"
            + KEY_OIL_PRICE + " REAL NOT NULL)";

    public RefuelDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_VEHICLE);
        sqLiteDatabase.execSQL(CREATE_TABLE_REFUEL_DATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if(oldVersion == 1) {
            try {
                sqLiteDatabase.beginTransaction();
                String sql = "DROP TABLE IF EXISTS " + TABLE_REFUEL_DATA;
                sqLiteDatabase.execSQL(sql);
                sql = "DROP TABLE IF EXISTS " + TABLE_VEHICLE;
                sqLiteDatabase.execSQL(sql);
                sqLiteDatabase.setTransactionSuccessful();
            } catch (Exception e) {
                throw (e);
            } finally {
                sqLiteDatabase.endTransaction();
            }
            onCreate(sqLiteDatabase);
        }


    }

    //get single refuel data
    public RefuelData getRefuelDataByID(long dataId){
        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT * FROM " + TABLE_REFUEL_DATA + " WHERE "
                + KEY_ID + " = " + dataId;
        Cursor cursor = db.rawQuery(queryString,null);
        RefuelData refuelData = new RefuelData();
        if (cursor.moveToFirst()) {
            getRefuelDataFromDatabase(refuelData,cursor);
            cursor.close();
        }
        return refuelData;
    }
    public ArrayList<RefuelData> getRefuelDataArrayList(){
        ArrayList<RefuelData> refuelDataArrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT * FROM " + TABLE_REFUEL_DATA + " ORDER BY "
                + KEY_ID + " DESC";
        Cursor cursor = db.rawQuery(queryString,null);
        if(cursor.moveToFirst()){
            do {
                RefuelData refuelData = new RefuelData();
                getRefuelDataFromDatabase(refuelData,cursor);
                refuelDataArrayList.add(refuelData);
            }while(cursor.moveToNext());
            cursor.close();
        }

        return refuelDataArrayList;
    }
    private void getRefuelDataFromDatabase(RefuelData refuelData, Cursor cursor){
        refuelData.setDataId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
        Log.d("refuel data id",String.valueOf(refuelData.getDataId()));
        refuelData.setDateOfRefuel(cursor.getString(cursor.getColumnIndex(KEY_REFUEL_DATE)));
        refuelData.setOilType(cursor.getString(cursor.getColumnIndex(KEY_OIL_TYPE)));
        refuelData.setVehicleID(cursor.getLong(cursor.getColumnIndex(KEY_VEHICLE_ID)));
        refuelData.setRefuelVolume(cursor.getDouble(cursor.getColumnIndex(KEY_REFUEL_VOLUME)));
        refuelData.setOilPrice(cursor.getDouble(cursor.getColumnIndex(KEY_OIL_PRICE)));
        refuelData.setRefuelMileage(cursor.getInt(cursor.getColumnIndex(KEY_REFUEL_MILEAGE)));
    }
    //get single vehicle
    public Vehicle getVehicleByID(long vehicleID){
        Vehicle vehicle = new Vehicle();
        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT * FROM " + TABLE_VEHICLE + " WHERE "
                + KEY_ID + " = " + vehicleID;
        Cursor cursor = db.rawQuery(queryString,null);
        if (cursor.moveToFirst()){
            getVehicleFromDatabase(vehicle, cursor);
            cursor.close();
        }
        return vehicle;
    }

    private void getVehicleFromDatabase(Vehicle vehicle, Cursor cursor) {
        vehicle.setVehicleID(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
        Log.d("Vehicle id:",String.valueOf(vehicle.getVehicleID()));
        vehicle.setVehicleName(cursor.getString(cursor.getColumnIndex(KEY_VEHICLE_NAME)));
        vehicle.setVehicleType(cursor.getString(cursor.getColumnIndex(KEY_VEHICLE_TYPE)));
        vehicle.setAirCapacity(cursor.getInt(cursor.getColumnIndex(KEY_AIR_CAPACITY)));
        vehicle.setOriginMileage(cursor.getInt(cursor.getColumnIndex(KEY_ORIGIN_MILEAGE)));
        if (!cursor.isNull(cursor.getColumnIndex(KEY_CURRENT_REFUEL_VOLUME))){
            vehicle.setCurrentRefuelVolume(cursor.getDouble(cursor.getColumnIndex(KEY_CURRENT_REFUEL_VOLUME)));
            vehicle.setCurrentMileage(cursor.getInt(cursor.getColumnIndex(KEY_CURRENT_MILEAGE)));
            vehicle.setTotalRefuelVolume(cursor.getDouble(cursor.getColumnIndex(KEY_TOTAL_REFUEL_VOLUME)));
            if(!cursor.isNull(cursor.getColumnIndex(KEY_LAST_TIME_REFUEL_MILEAGE)))
                vehicle.setLastTimeRefuelMileage(cursor.getInt(cursor.getColumnIndex(KEY_LAST_TIME_REFUEL_MILEAGE)));
        }
    }

    //取得第某筆車輛資訊，而非使用ID搜尋
    public Vehicle getVehicleByPosition(int position){
        Vehicle result = new Vehicle();
        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT * FROM " + TABLE_VEHICLE;
        Cursor cursor = db.rawQuery(queryString,null);
        if(cursor.moveToFirst()){
            cursor.moveToPosition(position);
            getVehicleFromDatabase(result,cursor);
            cursor.close();
            Log.d("vehicle id is",String.valueOf(result.getVehicleID()));
        }
        return result;
    }

    public String[] getVehicleNameList(){
        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT " + KEY_VEHICLE_NAME + " FROM "
                + TABLE_VEHICLE;
        Cursor cursor = db.rawQuery(queryString,null);
        String[] nameStrings = new String[cursor.getCount()];
        int arrayIndex = 0;
        if(cursor.moveToFirst()){
            do {
                nameStrings[arrayIndex] = cursor.getString(cursor.getColumnIndex(KEY_VEHICLE_NAME));
                arrayIndex++;
            }while(cursor.moveToNext());
            cursor.close();
        }
        return nameStrings;
    }

    public int getVehicleCount(){
        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT " + KEY_VEHICLE_NAME + " FROM "
                + TABLE_VEHICLE;
        Cursor cursor = db.rawQuery(queryString,null);
        int vehicleCount = cursor.getCount();
        cursor.close();
        return vehicleCount;
    }

    public ArrayList<Vehicle> getVehicleArrayList(){
        ArrayList<Vehicle> queryResult = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT * FROM " + TABLE_VEHICLE;
        Cursor cursor = db.rawQuery(queryString,null);
        if(cursor.moveToFirst()){
            do {
                Vehicle vehicle = new Vehicle();
                getVehicleFromDatabase(vehicle,cursor);
                queryResult.add(vehicle);
            }while(cursor.moveToNext());
        }
        return queryResult;
    }

    public long addRefuelData(RefuelData refuelData){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_VEHICLE_ID,refuelData.getVehicleID());
        values.put(KEY_REFUEL_VOLUME,refuelData.getRefuelVolume());
        values.put(KEY_REFUEL_MILEAGE,refuelData.getRefuelMileage());
        values.put(KEY_REFUEL_DATE,refuelData.getDateOfRefuel());
        values.put(KEY_OIL_TYPE,refuelData.getOilType());
        values.put(KEY_OIL_PRICE,refuelData.getOilPrice());
        return db.insert(TABLE_REFUEL_DATA,null,values);
    }

    public long addVehicle(Vehicle vehicle){
        Log.d("ADD","addVehicle");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_VEHICLE_NAME,vehicle.getVehicleName());
        values.put(KEY_AIR_CAPACITY,vehicle.getAirCapacity());
        values.put(KEY_ORIGIN_MILEAGE,vehicle.getOriginMileage());
        values.put(KEY_VEHICLE_TYPE,vehicle.getVehicleType());
        return db.insert(TABLE_VEHICLE,null,values);
    }

    public void updateRefuelData(RefuelData refuelData){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_VEHICLE_ID,refuelData.getVehicleID());
        values.put(KEY_REFUEL_VOLUME,refuelData.getRefuelVolume());
        values.put(KEY_REFUEL_MILEAGE,refuelData.getRefuelMileage());
        values.put(KEY_REFUEL_DATE,refuelData.getDateOfRefuel());
        values.put(KEY_OIL_TYPE,refuelData.getOilType());
        values.put(KEY_OIL_PRICE,refuelData.getOilPrice());
        db.update(TABLE_REFUEL_DATA,values,KEY_ID + " = " + refuelData.getDataId(),
                null);
    }

    public void updateVehicle(Vehicle vehicle){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_VEHICLE_NAME,vehicle.getVehicleName());
        values.put(KEY_AIR_CAPACITY,vehicle.getAirCapacity());
        values.put(KEY_ORIGIN_MILEAGE,vehicle.getOriginMileage());
        values.put(KEY_VEHICLE_TYPE,vehicle.getVehicleType());
        values.put(KEY_LAST_TIME_REFUEL_MILEAGE,vehicle.getLastTimeRefuelMileage());
        values.put(KEY_CURRENT_REFUEL_VOLUME,vehicle.getCurrentRefuelVolume());
        values.put(KEY_TOTAL_REFUEL_VOLUME,vehicle.getTotalRefuelVolume());
        values.put(KEY_CURRENT_MILEAGE,vehicle.getCurrentMileage());
        db.update(TABLE_VEHICLE,values,KEY_ID + " = " + vehicle.getVehicleID(),
                null);
    }

    public void deleteRefuelData(long dataID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REFUEL_DATA, KEY_ID + " = ?",
                new String[]{String.valueOf(dataID)});
    }

    public void deleteVehicle(String vehicleName){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_VEHICLE, KEY_VEHICLE_NAME + " = ?",
                new String[]{vehicleName});
    }

    public HashMap<String,Double> getTotalVolumeStatics(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String queryString = "SELECT " + KEY_REFUEL_VOLUME + ", "
                + KEY_OIL_TYPE + " FROM " + TABLE_REFUEL_DATA
                + " GROUP BY " + KEY_OIL_TYPE;
        Cursor cursor = sqLiteDatabase.rawQuery(queryString,null);

        HashMap<String,Double> hashMap = new HashMap<>();
        if(cursor.moveToFirst()){
            do {
                hashMap.put(cursor.getString(cursor.getColumnIndex(KEY_OIL_TYPE)),cursor.getDouble(cursor.getColumnIndex(KEY_REFUEL_VOLUME)));
            }while (cursor.moveToNext());
            cursor.close();
        }
        return hashMap;
    }
    public HashMap<String,Double> getVehicleFuelConsumption(String vehicleType){
        HashMap<String,Double> hashMap = new HashMap<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String queryString = "SELECT " + KEY_OIL_TYPE + ", " + KEY_REFUEL_VOLUME
                + ", " + KEY_REFUEL_MILEAGE + ", " + KEY_VEHICLE_ID
                + ", " + KEY_VEHICLE_TYPE + " FROM " + TABLE_REFUEL_DATA
                + " JOIN " + TABLE_VEHICLE + " ON " + TABLE_REFUEL_DATA + "."
                + KEY_VEHICLE_ID + " = " + TABLE_VEHICLE + "." + KEY_ID
                + " WHERE " + KEY_VEHICLE_TYPE + " = '"
                + vehicleType + "'";
        Cursor cursor = sqLiteDatabase.rawQuery(queryString,null);
        final String oil92 = "92無鉛汽油";
        final String oil95 = "95無鉛汽油";
        final String oil98 = "98無鉛汽油";
        final String diesel = "超級柴油";
        final int MAX = 4;
        double[] volume = new double[MAX];
        int[] originMileage = new int[MAX];
        int[] lastMileage = new int[MAX];
        int mileage;

        if(cursor.moveToFirst()){
            do {
            switch (cursor.getString(cursor.getColumnIndex(KEY_OIL_TYPE))){
                case oil92:
                    volume[0] += cursor.getDouble(cursor.getColumnIndex(KEY_REFUEL_VOLUME));
                    mileage = cursor.getInt(cursor.getColumnIndex(KEY_REFUEL_MILEAGE));
                    if(originMileage[0] == 0){
                        originMileage[0] = mileage;
                        lastMileage[0] = mileage;
                    }else if(mileage < originMileage[0]){
                        originMileage[0] = mileage;
                    }else if(mileage > lastMileage[0]){
                        lastMileage[0] = mileage;
                    }
                    break;
                case oil95:
                    volume[1] += cursor.getDouble(cursor.getColumnIndex(KEY_REFUEL_VOLUME));
                    mileage = cursor.getInt(cursor.getColumnIndex(KEY_REFUEL_MILEAGE));
                    if(originMileage[1] == 0){
                        originMileage[1] = mileage;
                        lastMileage[1] = mileage;
                    }else if(mileage < originMileage[1]){
                        originMileage[1] = mileage;
                    }else if(mileage > lastMileage[1]){
                        lastMileage[1] = mileage;
                    }
                    break;
                case oil98:
                    volume[2] += cursor.getDouble(cursor.getColumnIndex(KEY_REFUEL_VOLUME));
                    mileage = cursor.getInt(cursor.getColumnIndex(KEY_REFUEL_MILEAGE));
                    if(originMileage[2] == 0){
                        originMileage[2] = mileage;
                        lastMileage[2] = mileage;
                    }else if(mileage < originMileage[2]){
                        originMileage[2] = mileage;
                    }else if(mileage > lastMileage[2]){
                        lastMileage[2] = mileage;
                    }
                    break;
                case diesel:
                    mileage = cursor.getInt(cursor.getColumnIndex(KEY_REFUEL_MILEAGE));
                    if(originMileage[3] == 0){
                        originMileage[3] = mileage;
                        lastMileage[3] = mileage;
                    }else if(mileage < originMileage[3]){
                        originMileage[3] = mileage;
                    }else if(mileage > lastMileage[3]){
                        lastMileage[3] = mileage;
                    }
                    break;
                default:
                    break;
            }
            }while (cursor.moveToNext());
            cursor.close();
        }
        hashMap.put(oil92, getFuelConsumption(volume[0],originMileage[0],lastMileage[0]));
        hashMap.put(oil95, getFuelConsumption(volume[1],originMileage[1],lastMileage[1]));
        hashMap.put(oil98, getFuelConsumption(volume[2],originMileage[2],lastMileage[2]));
        hashMap.put(diesel, getFuelConsumption(volume[3],originMileage[3],lastMileage[3]));

        return hashMap;
    }
    private double getFuelConsumption(double volume, int originMileage, int lastMileage){
        if(volume == 0.0){
            return 0.0;
        }else{
            return (lastMileage-originMileage)/volume;
        }
    }

}
