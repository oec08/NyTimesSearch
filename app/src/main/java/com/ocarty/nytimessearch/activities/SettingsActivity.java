package com.ocarty.nytimessearch.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.ocarty.nytimessearch.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by ocarty on 10/21/2016.
 */
public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Button saveButton;
    private Button setDateButton;
    private Spinner sortOrderSpinner;
    private RadioGroup newsDeskRadioGroup;
    private boolean isArtsChecked;
    private boolean isFashionAndDesignChecked;
    private boolean isSportsChecked;
    private TextView tvDateSet;
    private Calendar calendar;
    private String selectedSpinnerValue = "";
    private CheckBox artCheckbox;
    private CheckBox fashionCheckbox;
    private CheckBox sportsCheckbox;
    private int year;
    private int month;
    private int day;
    public static final String MONTH_DATE_YEAR_FORMAT = "%04d%02d%02d";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setDateButton = (Button)findViewById(R.id.setDateButton);
        saveButton = (Button)findViewById(R.id.saveSettingsBtn);
        sortOrderSpinner = (Spinner)findViewById(R.id.sortOrderSpinner);
        newsDeskRadioGroup = (RadioGroup)findViewById(R.id.newsDeskRadioGroup);
        tvDateSet = (TextView)findViewById(R.id.tvDateSet);
        setUpSpinner();
        setCheckBoxValues();
        setUpIntialCalendar();


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("selectedSpinnerValue",selectedSpinnerValue);
                intent.putExtra("isArtsChecked", isArtsChecked);
                intent.putExtra("isFashionAndDesignChecked", isFashionAndDesignChecked);
                intent.putExtra("isSportsChecked", isSportsChecked);
                intent.putExtra("date", String.format(Locale.US, MONTH_DATE_YEAR_FORMAT, year, month, day));
                setResult(RESULT_OK, intent);
                finish();


            }
        });

        setDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(10);
            }
        });


    }
    private void setUpSpinner() {
        sortOrderSpinner.setOnItemSelectedListener(this);

        List<String> sortOrder = new ArrayList<String>();
        sortOrder.add("Newest");
        sortOrder.add("Oldest");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sortOrder);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sortOrderSpinner.setAdapter(dataAdapter);
    }
    private void setUpIntialCalendar() {
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month, day);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if(id == 10) {
            return new DatePickerDialog(this, dateListener, year, month-1, day);
        }
        return null;
     }
    private DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            year = arg1;
            month = arg2 + 1; // Accounts for months starting at zero
            day = arg3;
            showDate(year, month, day);
        }
    };

    private void showDate(int year, int month, int day) {
        tvDateSet.setText(new StringBuilder().append("Begin Date: ").append(month).append("/")
                .append(day).append("/").append(year));
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedSpinnerValue = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void setCheckBoxValues() {
        artCheckbox=(CheckBox)findViewById(R.id.artCheckBox);
        fashionCheckbox=(CheckBox)findViewById(R.id.fashionCheckBox);
        sportsCheckbox=(CheckBox)findViewById(R.id.sportsCheckBox);

        artCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isArtsChecked = isChecked;
            }
        });

        fashionCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isFashionAndDesignChecked = isChecked;
            }
        });

        sportsCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isSportsChecked = isChecked;
            }
        });

    }
}
