package huang.mike.fuelconsumptionstatistics;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class AddVehicleActivity extends AppCompatActivity {
    private RefuelDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_vehicle);
        this.setTitle(getString(R.string.add_vehicle_title));

        dbHelper = new RefuelDBHelper(this);

        Spinner spinner = (Spinner) findViewById(R.id.vehicleTypeSpanner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.vehicle_type_array,
                android.R.layout.simple_spinner_item);
        //調用setDropDownViewResource方法，以XML的方式定義下拉菜單要顯示的样式
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //为spinner設置适配器
        spinner.setAdapter(adapter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            // Show check dialog when pressing "back" button,
            checkDialog();

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void checkDialog(){
        new AlertDialog.Builder(AddVehicleActivity.this)
                .setTitle(R.string.check_Title)
                .setMessage(R.string.check_Message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(RESULT_CANCELED);
                        dbHelper.close();
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    public void clickCancel(View view){
        checkDialog();
    }

    public void clickOK(View view){
        if(checkEditTextHasData()) {
            Vehicle vehicle = new Vehicle();
            EditText editText = (EditText)findViewById(R.id.editVehicleName);
            vehicle.setVehicleName(editText.getText().toString());
            editText = (EditText) findViewById(R.id.editAirCapacity);
            vehicle.setAirCapacity(Integer.parseInt(editText.getText().toString()));
            editText = (EditText) findViewById(R.id.editCurrentMileage);
            vehicle.setOriginMileage(Integer.parseInt(editText.getText().toString()));
            Spinner spinner = (Spinner) findViewById(R.id.vehicleTypeSpanner);
            vehicle.setVehicleType(spinner.getSelectedItem().toString());
            dbHelper.addVehicle(vehicle);
            setResult(RESULT_OK);
            dbHelper.close();
            finish();
        }
    }

    private boolean isEmpty(EditText editText){
        return editText.getText().toString().trim().length() == 0;
    }

    private boolean checkEditTextHasData(){
        boolean result = true;
        EditText editText = (EditText)findViewById(R.id.editVehicleName);
        if(isEmpty(editText)){
            editText.setError(getString(R.string.error_message_vehicle_name));
            result = false;
        }
        editText = (EditText)findViewById(R.id.editAirCapacity);
        if(isEmpty(editText)){
            editText.setError(getString(R.string.error_message_air_capacity));
            result = false;
        }
        editText = (EditText)findViewById(R.id.editCurrentMileage);
        if(isEmpty(editText)){
            editText.setError(getString(R.string.error_message_origin_mileage));
            result = false;
        }
        return result;
    }
}
