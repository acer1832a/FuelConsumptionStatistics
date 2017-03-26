package huang.mike.fuelconsumptionstatistics;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.text.DecimalFormat;
import java.util.Calendar;


public class AddRefuelDataActivity extends AppCompatActivity {
    private RefuelDBHelper dbHelper = null;
    private RadioGroup radioGroupAutoOrCustom = null;
    private RadioGroup radioGroupOilProvider = null;
    private RadioGroup radioGroupOilType = null;
    //private RadioButton radioButton = null;
    //private EditText editTextRefuelMileage = null;
    private EditText editTextOilPrice = null;
    private EditText editTextRefuelVolume = null;
    private EditText editTextTotalPrice = null;
    final String mURL = "http://m.gas.goodlife.tw/";
    private String[] CPC = null;
    private String[] FPCC = null;
    final int subStringIndex = 19;
    final int arrayMax = 4;
    final int priceIndex92 = 0;
    final int priceIndex95 = 1;
    final int priceIndex98 = 2;
    final int priceIndexDiesel = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.add_refuel_data);
        super.onCreate(savedInstanceState);
        this.setTitle(getString(R.string.add_refuel_data_title));
        radioGroupAutoOrCustom = (RadioGroup) findViewById(R.id.AutoOrCustom);
        radioGroupOilProvider = (RadioGroup) findViewById(R.id.OilProvider);
        radioGroupOilType = (RadioGroup) findViewById(R.id.OilType);
        //editTextRefuelMileage = (EditText)findViewById(R.id.editRefuelMileage);
        editTextOilPrice = (EditText) findViewById(R.id.editCustom);
        editTextRefuelVolume = (EditText) findViewById(R.id.editRefuelVolume);
        editTextTotalPrice = (EditText) findViewById(R.id.editTotal);


        //initiate refuel date
        Calendar calendar = Calendar.getInstance();
        TextView textView = (TextView) findViewById(R.id.textCurrentTime);
        textView.setText(calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH));
        if (calendar.get(Calendar.HOUR_OF_DAY) < 12)
            textView.append("AM" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
        else if (calendar.get(Calendar.HOUR_OF_DAY) < 13)
            textView.append("PM" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
        else
            textView.append("PM" + (calendar.get(Calendar.HOUR_OF_DAY) - 12) + ":" + calendar.get(Calendar.MINUTE));


        radioGroupAutoOrCustom.setOnCheckedChangeListener(new AutoOrCustomOnCheckedChangeListener());
        radioGroupOilProvider.setOnCheckedChangeListener(new OilProviderOnCheckedChangeListener());
        radioGroupOilType.setOnCheckedChangeListener(new OilTypeOnCheckedChangeListener());
        editTextOilPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocus) {
                if (isFocus == false && editTextOilPrice.getText().toString().trim().length() > 0) {
                    DecimalFormat format = new DecimalFormat("##.0");
                    String tempString = format.format(Double.parseDouble(editTextOilPrice.getText().toString()));
                    editTextOilPrice.setText(tempString);
                }
            }
        });
        editTextRefuelVolume.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocus) {
                if (isFocus == false) {
                    if((editTextRefuelVolume.getText().toString().trim().length() != 0) && (editTextOilPrice.getText().toString().trim().length() != 0)) {
                        DecimalFormat format = new DecimalFormat("##.00");
                        String tempString = format.format(Double.parseDouble(editTextRefuelVolume.getText().toString()));
                        editTextRefuelVolume.setText(tempString);
                        if ((editTextOilPrice.getText().toString().trim().length() != 0) && (editTextRefuelVolume.getText().toString().trim().length() != 0)) {
                            Double totalPrice = Double.parseDouble(tempString) * Double.parseDouble(editTextOilPrice.getText().toString());
                            editTextTotalPrice.setText(String.valueOf(totalPrice.intValue()));
                        }
                    }
                }
            }
        });
        editTextTotalPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocus) {
                if (isFocus == false) {
                    if ((editTextOilPrice.getText().toString().trim().length() != 0) && (editTextTotalPrice.getText().toString().length() != 0)) {
                        calculateRefuelVolume();
                    }
                }
            }
        });

        editTextTotalPrice.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {
                if ((actionID == EditorInfo.IME_ACTION_DONE) || (actionID == EditorInfo.IME_ACTION_NEXT)) {
                    if ((editTextOilPrice.getText().toString().trim().length() != 0) && (editTextTotalPrice.getText().toString().length() != 0)) {
                        calculateRefuelVolume();
                    }
                }
                return false;
            }
        });

        //get oil price information from http://m.gas.goodlife.tw/
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest getRequest = new StringRequest(Request.Method.GET, mURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String mHtml) {

                        Document document = Jsoup.parse(mHtml);
                        Elements elements = document.select("li");
                        CPC = new String[arrayMax];
                        FPCC = new String[arrayMax];
                        for (int i = 0; i < 4; i++)
                            CPC[i] = elements.get(i + 4).toString().substring(subStringIndex, subStringIndex + 4);

                        for (int i = 0; i < 4; i++)
                            FPCC[i] = elements.get(i + 9).toString().substring(subStringIndex, subStringIndex + 4);
                        Toast.makeText(getApplicationContext(), "油價資訊更新成功", Toast.LENGTH_SHORT).show();

                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getApplicationContext(), "無法取得油價資訊，請檢查網路是否有連線!", Toast.LENGTH_SHORT).show();
                    }
                });
        queue.add(getRequest);

        //initiate RefuelDBHelper and get vehicle name list
        dbHelper = new RefuelDBHelper(this);
        Spinner spinner = (Spinner) findViewById(R.id.vehicleNameSpinner);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_dropdown_item, dbHelper.getVehicleNameList());
        spinner.setAdapter(adapter);
    }

    private void calculateRefuelVolume() {
        Double refuelVolume = Double.parseDouble(editTextRefuelVolume.getText().toString());
        Double oilPrice = Double.parseDouble(editTextOilPrice.getText().toString());
        Double compareTotalPrice = refuelVolume * oilPrice;
        Integer totalPrice = Integer.parseInt(editTextTotalPrice.getText().toString());
        if(compareTotalPrice.intValue() != totalPrice) {
            refuelVolume = totalPrice.doubleValue() / oilPrice;
            DecimalFormat format = new DecimalFormat("##.00");
            String tempString = format.format(refuelVolume);
            editTextRefuelVolume.setText(tempString);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // Show check dialog when pressing "back" button,
            checkDialog();

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void checkDialog() {
        new AlertDialog.Builder(AddRefuelDataActivity.this)
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


    private class AutoOrCustomOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            EditText editText = (EditText) findViewById(R.id.editCustom);
            //radioButton = (RadioButton)findViewById(checkedId);
            switch (checkedId) {
                case R.id.radioAuto:
                    editText.setEnabled(false);
                    priceAutoUpdate();
                    break;
                case R.id.radioCustom:
                    editText.setEnabled(true);
                    AddRefuelDataActivity.this.getCurrentFocus().clearFocus();
                    editText.requestFocus();
                    break;

            }
        }
    }

    private class OilProviderOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            priceAutoUpdate();
        }
    }

    private class OilTypeOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            priceAutoUpdate();
        }
    }

    private void priceAutoUpdate() {
        int checkId;
        EditText editText = (EditText) findViewById(R.id.editCustom);
        checkId = radioGroupAutoOrCustom.getCheckedRadioButtonId();
        if (checkId == R.id.radioAuto) {
            checkId = radioGroupOilProvider.getCheckedRadioButtonId();
            if (checkId == R.id.radioButtonCPC) {
                checkId = radioGroupOilType.getCheckedRadioButtonId();
                switch (checkId) {
                    case R.id.radioButton92:
                        editText.setText(CPC[priceIndex92]);
                        break;
                    case R.id.radioButton95:
                        editText.setText(CPC[priceIndex95]);
                        break;
                    case R.id.radioButton98:
                        editText.setText(CPC[priceIndex98]);
                        break;
                    case R.id.radioButtonDiesel:
                        editText.setText(CPC[priceIndexDiesel]);
                        break;
                    default:
                        break;

                }
            } else if (checkId == R.id.radioButtonFPCC) {
                checkId = radioGroupOilType.getCheckedRadioButtonId();
                switch (checkId) {
                    case R.id.radioButton92:
                        editText.setText(FPCC[priceIndex92]);
                        break;
                    case R.id.radioButton95:
                        editText.setText(FPCC[priceIndex95]);
                        break;
                    case R.id.radioButton98:
                        editText.setText(FPCC[priceIndex98]);
                        break;
                    case R.id.radioButtonDiesel:
                        editText.setText(FPCC[priceIndexDiesel]);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public void clickCancel(View v) {
        checkDialog();
    }

    public void clickOK(View v) {
        if(checkEditTextHasData()){
            //create refuel data
            RefuelData refuelData = new RefuelData();
            Vehicle vehicle = dbHelper.getVehicleByPosition(((int) ((Spinner) findViewById(R.id.vehicleNameSpinner)).getSelectedItemId()));
            refuelData.setVehicleID(vehicle.getVehicleID());
            refuelData.setDateOfRefuel(((TextView)findViewById(R.id.textCurrentTime)).getText().toString());
            refuelData.setOilPrice(Double.parseDouble(editTextOilPrice.getText().toString()));
            refuelData.setRefuelVolume(Double.parseDouble(editTextRefuelVolume.getText().toString()));
            refuelData.setRefuelMileage(Integer.parseInt(((EditText)findViewById(R.id.editRefuelMileage)).getText().toString()));
            refuelData.setOilType(getOilTypeString());
            if(refuelData.getRefuelMileage() > vehicle.getCurrentMileage() && refuelData.getRefuelMileage() > vehicle.getOriginMileage()){
                vehicle.updateVehicleSummary(refuelData.getRefuelMileage(),refuelData.getRefuelVolume());
                dbHelper.addRefuelData(refuelData);
                dbHelper.updateVehicle(vehicle);
                setResult(RESULT_OK);
                dbHelper.close();
                finish();
            }else{
                new AlertDialog.Builder(AddRefuelDataActivity.this)
                        .setTitle(R.string.error_check_title)
                        .setMessage(R.string.refuel_mileage_error)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
            }
        }
    }

    public void clickTime(View v) {
        final Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        final TextView textView = (TextView) findViewById(R.id.textCurrentTime);
        textView.clearComposingText();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        new TimePickerDialog(AddRefuelDataActivity.this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (hourOfDay < 12)
                    textView.append("AM" + hourOfDay + ":" + minute);
                else if (hourOfDay < 13)
                    textView.append("PM" + hourOfDay + ":" + minute);
                else
                    textView.append("PM" + (hourOfDay - 12) + ":" + minute);
            }
        }, hour, minute, false).show();

        new DatePickerDialog(AddRefuelDataActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                String format = setDateFormat(year, month, day);
                textView.setText(format);
            }

        }, mYear, mMonth, mDay).show();


    }

    private String setDateFormat(int year, int monthOfYear, int dayOfMonth) {
        return String.valueOf(year) + "-"
                + String.valueOf(monthOfYear + 1) + "-"
                + String.valueOf(dayOfMonth);
    }

    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    private boolean checkEditTextHasData() {
        boolean result = true;
        EditText editText = (EditText) findViewById(R.id.editRefuelMileage);
        editText.setError(null);
        editTextOilPrice.setError(null);
        editTextRefuelVolume.setError(null);
        editTextTotalPrice.setError(null);
        if (isEmpty(editText)) {
            editText.setError("");
            result = false;
        }
        if (isEmpty(editTextOilPrice)) {
            editTextOilPrice.setError("");
            result = false;
        } else if(isEmpty(editTextRefuelVolume)){
            if(isEmpty(editTextTotalPrice)){
                editTextRefuelVolume.setError("");
                result = false;
            }else{
                Double refuelVolume = Double.parseDouble(editTextTotalPrice.getText().toString()) / Double.parseDouble(editTextOilPrice.getText().toString());
                DecimalFormat format = new DecimalFormat("##.00");
                String tempString = format.format(refuelVolume);
                editTextRefuelVolume.setText(tempString);
            }
        }else if(isEmpty(editTextTotalPrice)){
            String tempString = editTextRefuelVolume.getText().toString();
            Double totalPrice = Double.parseDouble(tempString) * Double.parseDouble(editTextOilPrice.getText().toString());
            editTextTotalPrice.setText(String.valueOf(totalPrice.intValue()));
        }

        return result;
    }
    private String getOilTypeString(){
        String string;
        int checkID = radioGroupOilType.getCheckedRadioButtonId();
        switch (checkID){
            case R.id.radioButton92:
                string = getString(R.string.oil_92);
                break;
            case R.id.radioButton95:
                string = getString(R.string.oil_95);
                break;
            case R.id.radioButton98:
                string = getString(R.string.oil_98);
                break;
            case R.id.radioButtonDiesel:
                string = getString(R.string.diesel);
                break;
            default:
                string = "";
                break;
        }
        return string;
    }

}
