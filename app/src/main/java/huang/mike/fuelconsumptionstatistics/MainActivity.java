package huang.mike.fuelconsumptionstatistics;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final int ADD_REFUEL_DATA_REQUEST = 0;
    private static final int ADD_VEHICLE_DATA_REQUEST = 1;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                listDialog();

            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent intent){
        super.onActivityResult(requestCode,resultCode,intent);
        if(resultCode == RESULT_OK){
            mSectionsPagerAdapter.getItem(mViewPager.getCurrentItem()).onActivityResult(requestCode,resultCode,intent);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            // Show check dialog when pressing "back" button,
            leaveDialog();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void leaveDialog(){
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.check_Title)
                .setMessage(R.string.leave_Check)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    private void listDialog(){
        final ArrayList<String> addChoose = new ArrayList<>();
        addChoose.add(getString(R.string.add_refuel_data));
        addChoose.add(getString(R.string.add_vehicle));
        new AlertDialog.Builder(MainActivity.this)
                .setItems(addChoose.toArray(new String[addChoose.size()]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choose) {
                        //String name = addChoose.get(choose);
                        //Toast.makeText(getApplicationContext(), getString(R.string.u_choose) + name, Toast.LENGTH_SHORT).show();
                        if(choose == 0){
                            RefuelDBHelper dbHelper = new RefuelDBHelper(MainActivity.this);
                            if(dbHelper.getVehicleCount() == 0){
                                noVehicleDataDialog();
                                dialog.dismiss();
                            }else{
                                Intent intent = new Intent(getApplicationContext(),AddRefuelDataActivity.class);
                                //Bundle bundle = new Bundle();
                                //bundle.putString("mode","add");
                                //bundle.putString("result",null);
                                //intent.putExtras(bundle);
                                startActivityForResult(intent,ADD_REFUEL_DATA_REQUEST);
                            }
                        }
                        if(choose == 1){
                            Intent intent = new Intent(getApplicationContext(),AddVehicleActivity.class);
                            //Bundle bundle = new Bundle();
                            //bundle.putString("mode","add");
                            //bundle.putString("result",null);
                            //intent.putExtras(bundle);
                            startActivityForResult(intent,ADD_VEHICLE_DATA_REQUEST);
                        }
                    }
                })
                .show();
    }

    private void noVehicleDataDialog(){
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.no_vehicle_data_title)
                .setMessage(R.string.no_vehicle_data_content)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dialog.dismiss();
                    }
                })
                .show();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static ArrayAdapter refuelArrayAdapter = null;
        private static ArrayAdapter vehicleArrayAdapter = null;
        private static ArrayList<RefuelData> refuelDataArrayList = null;
        private static ArrayList<Vehicle> vehicleArrayList = null;
        private static RefuelDBHelper dbHelper = null;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            dbHelper = new RefuelDBHelper(getContext());
            View rootView1;
            if(dbHelper.getVehicleCount() == 0){
                rootView1 = inflater.inflate(R.layout.state_empty_layout, container, false);
            }else{
                rootView1 = inflater.inflate(R.layout.state_layout, container, false);
                initiateRootView1(rootView1);
            }
            View rootView2 = inflater.inflate(R.layout.refuel_data_list, container, false);
            View rootView3 = inflater.inflate(R.layout.vehicle_management, container, false);
            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                    case 1:
                        return rootView1;
                    case 2:
                        refuelDataArrayList = dbHelper.getRefuelDataArrayList();
                        ListView listViewRefuelData = (ListView) rootView2.findViewById(R.id.refuel_data_list);
                        refuelArrayAdapter = new ArrayAdapter<RefuelData>(getContext(),
                                android.R.layout.simple_list_item_2,
                                android.R.id.text1,
                                refuelDataArrayList) {
                            @Override
                            public View getView(int pos, View convert, ViewGroup group) {
                                View view = super.getView(pos, convert, group);
                                TextView textView1 = (TextView) view.findViewById(android.R.id.text1);
                                TextView textView2 = (TextView) view.findViewById(android.R.id.text2);
                                textView1.setText(getItem(pos).getDateOfRefuel());
                                textView2.setText(getItem(pos).getOilType() + " × " + getItem(pos).getRefuelVolume() + "公升");
                                return view;
                            }
                        };
                        listViewRefuelData.setAdapter(refuelArrayAdapter);
                        listViewRefuelData.setEmptyView(rootView2.findViewById(R.id.refuel_empty));
                        return rootView2;

                    case 3:
                        vehicleArrayList = dbHelper.getVehicleArrayList();
                        ListView listViewVehicle = (ListView) rootView3.findViewById(R.id.vehicle_list);
                        vehicleArrayAdapter = new ArrayAdapter<Vehicle>(getContext(),
                                android.R.layout.simple_list_item_2,
                                android.R.id.text1,
                                vehicleArrayList) {
                            @Override
                            public View getView(int pos, View convert, ViewGroup group) {
                                View view = super.getView(pos, convert, group);
                                DecimalFormat format = new DecimalFormat("#0.000");
                                TextView textView1 = (TextView) view.findViewById(android.R.id.text1);
                                TextView textView2 = (TextView) view.findViewById(android.R.id.text2);
                                textView1.setText(getItem(pos).getVehicleName() + " 里程數：" + getItem(pos).getCurrentMileage() + "公里");
                                textView2.setText("平均油耗：" + format.format(getItem(pos).getFuelConsumptionKMPerLiter()) + "公里/公升");
                                return view;
                            }
                        };
                        listViewVehicle.setAdapter(vehicleArrayAdapter);
                        listViewVehicle.setEmptyView(rootView3.findViewById(R.id.vehicleEmpty));
                        return rootView3;
                    default:
                        return rootView1;
                }
        }
        @Override
        public void onActivityResult(int requestCode,int resultCode,Intent intent){
            Log.d("fragment","onActivityResult");
            super.onActivityResult(requestCode,resultCode,intent);
            switch (requestCode){
                case ADD_REFUEL_DATA_REQUEST:
                    updateRefuelListView();
                    updateVehicleListView();
                    break;
                case ADD_VEHICLE_DATA_REQUEST:
                    updateVehicleListView();
                    break;
                default:
                    break;
            }
        }

        private void updateVehicleListView() {
            vehicleArrayList = dbHelper.getVehicleArrayList();
            vehicleArrayAdapter.clear();
            vehicleArrayAdapter.addAll(vehicleArrayList);
        }

        private void updateRefuelListView(){
            refuelDataArrayList = dbHelper.getRefuelDataArrayList();
            refuelArrayAdapter.clear();
            refuelArrayAdapter.addAll(refuelDataArrayList);
        }

        private void initiateRootView1(View view){
            HashMap<String,Double> doubleHashMap = dbHelper.getTotalVolumeStatics();
            HashMap<String,TextView> textViewHashMap = new HashMap<>();
            textViewHashMap.put(getString(R.string.oil_92),(TextView)view.findViewById(R.id.textViewVolume92));
            textViewHashMap.put(getString(R.string.oil_95),(TextView)view.findViewById(R.id.textViewVolume95));
            textViewHashMap.put(getString(R.string.oil_98),(TextView)view.findViewById(R.id.textViewVolume98));
            textViewHashMap.put(getString(R.string.diesel),(TextView)view.findViewById(R.id.textViewVolumeDiesel));
            initiateTextView(textViewHashMap,doubleHashMap,getString(R.string.liter));

            doubleHashMap = dbHelper.getVehicleFuelConsumption(getString(R.string.car));
            textViewHashMap.put(getString(R.string.oil_92),(TextView)view.findViewById(R.id.textViewCarConsumption92));
            textViewHashMap.put(getString(R.string.oil_95),(TextView)view.findViewById(R.id.textViewCarConsumption95));
            textViewHashMap.put(getString(R.string.oil_98),(TextView)view.findViewById(R.id.textViewCarConsumption98));
            textViewHashMap.put(getString(R.string.diesel),(TextView)view.findViewById(R.id.textViewCarConsumptionDiesel));
            initiateTextView(textViewHashMap,doubleHashMap,getString(R.string.kilometers_per_liter));

            doubleHashMap = dbHelper.getVehicleFuelConsumption(getString(R.string.scooter));
            textViewHashMap.put(getString(R.string.oil_92),(TextView)view.findViewById(R.id.textViewScooterConsumption92));
            textViewHashMap.put(getString(R.string.oil_95),(TextView)view.findViewById(R.id.textViewScooterConsumption95));
            textViewHashMap.put(getString(R.string.oil_98),(TextView)view.findViewById(R.id.textViewScooterConsumption98));
            textViewHashMap.put(getString(R.string.diesel),(TextView)view.findViewById(R.id.textViewScooterConsumptionDiesel));
            initiateTextView(textViewHashMap,doubleHashMap,getString(R.string.kilometers_per_liter));

        }
        private void initiateTextView(HashMap<String,TextView> textViewHashMap, HashMap<String,Double> doubleHashMap, String unitString){
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
            Double tempDouble;
            final String colon = ":";
            final double zeroString = 0.0;
            String tempString;

            if((tempDouble = doubleHashMap.get(getString(R.string.oil_92))) == null){
                tempString = getString(R.string.oil_92) + colon + decimalFormat.format(zeroString);
                textViewHashMap.get(getString(R.string.oil_92)).setText(tempString + unitString);
            }else{
                tempString = getString(R.string.oil_92) + colon + decimalFormat.format(tempDouble);
                textViewHashMap.get(getString(R.string.oil_92)).setText(tempString + unitString);
            }

            if((tempDouble = doubleHashMap.get(getString(R.string.oil_95))) == null){
                tempString = getString(R.string.oil_95) + colon + decimalFormat.format(zeroString);
                textViewHashMap.get(getString(R.string.oil_95)).setText(tempString + unitString);
            }else{
                tempString = getString(R.string.oil_95) + colon + decimalFormat.format(tempDouble);
                textViewHashMap.get(getString(R.string.oil_95)).setText(tempString + unitString);
            }

            if((tempDouble = doubleHashMap.get(getString(R.string.oil_98))) == null){
                tempString = getString(R.string.oil_98) + colon + decimalFormat.format(zeroString);
                textViewHashMap.get(getString(R.string.oil_98)).setText(tempString + unitString);
            }else{
                tempString = getString(R.string.oil_98) + colon + decimalFormat.format(tempDouble);
                textViewHashMap.get(getString(R.string.oil_98)).setText(tempString + unitString);
            }

            if((tempDouble = doubleHashMap.get(getString(R.string.diesel))) == null){
                tempString = getString(R.string.diesel) + colon + decimalFormat.format(zeroString);
                textViewHashMap.get(getString(R.string.diesel)).setText(tempString + unitString);
            }else{
                tempString = getString(R.string.diesel) + colon + decimalFormat.format(tempDouble);
                textViewHashMap.get(getString(R.string.diesel)).setText(tempString + unitString);
            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.fuel_consumption_state);
                case 1:
                    return getString(R.string.refuel_history);
                case 2:
                    return getString(R.string.vehicle_management);
            }
            return null;
        }
    }
}
