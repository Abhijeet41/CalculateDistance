package com.abhi41.calculatedistance;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.abhi41.calculatedistance.databinding.ActivityMainBinding;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    String key = "";
    String sType;
    double lat1=0, long1=0,lat2=0,long2=0;
    int flag =0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        Places.initialize(getApplicationContext(),key);
        binding.source.setFocusable(false);
        binding.source.setOnClickListener(v -> {
            sType = "source";
            //intialize place field list
            List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS,Place.Field.LAT_LNG);
            Intent intent = new Autocomplete
                    .IntentBuilder(AutocompleteActivityMode.OVERLAY,fields)
                    .build(MainActivity.this);
            startActivityForResult(intent,100);


        });

        binding.destination.setFocusable(false);
        binding.destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sType = "destination";
                List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS,Place.Field.LAT_LNG);
                Intent intent = new Autocomplete
                        .IntentBuilder(AutocompleteActivityMode.OVERLAY,fields)
                        .build(MainActivity.this);
                startActivityForResult(intent,100);
            }
        });

        binding.txtDistance.setText("0.0 kilometers");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK)
        {
            Place place = Autocomplete.getPlaceFromIntent(data);
            //intialize place
            if (sType.equals("source"))
            {
                //increase flag
                flag++;

                //set address on edit text
                binding.source.setText(place.getAddress());

                //get lat lng
                String sSource = String.valueOf(place.getLatLng());
                sSource = sSource.replaceAll("lat/lng:","");
                sSource = sSource.replace("(","");
                sSource = sSource.replace(")","");
                String[] split = sSource.split(",");

                try {
                    lat1 = Double.parseDouble(split[0].replaceAll(",",""));
                    long1 = Double.parseDouble(split[1].replaceAll(",",""));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

            }else {
                //when type is destination
                //increase flag value
                flag++;
                //set address on edit text
                binding.destination.setText(place.getAddress());

                //get lat lng
                String sDestination = String.valueOf(place.getLatLng());
                sDestination = sDestination.replaceAll("lat/lng:","");
                sDestination = sDestination.replace("(","");
                sDestination = sDestination.replace(")","");
                String[] split = sDestination.split(",");

                lat2 = Double.parseDouble(split[0].replaceAll(",",""));
                long2 = Double.parseDouble(split[1].replaceAll(",",""));
            }

            //check condition
            if (flag >= 2)
            {
                //when flag is greaterthan and qual to 2

                //calculate distance
                distance(lat1,long1,lat2,long2);
            }else if (requestCode == AutocompleteActivity.RESULT_ERROR)
            {
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void distance(double lat1, double long1, double lat2, double long2) {
        double longDiff = long1 -long2;
        //calculate distance
        double distance = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                +Math.cos(deg2rad(lat1))
                *Math.cos(deg2rad(lat2))
                *Math.cos(deg2rad(longDiff));

        distance = Math.acos(distance);
        //convert distance radian to degree
        distance = rad2degress(distance);

        //distance in miles
        distance = distance * 60 *1.1515;

        //distance in kilimeters
        distance = distance * 1.609344;

        //set distance on textview
        binding.txtDistance.setText(String.format(Locale.US,"%.2f Kilometers",distance));
    }

    //convert degree to radian
    private double deg2rad(double lat1) {
        return (lat1*Math.PI/180.0);
    }

    //convert radian to degress
    private double rad2degress(double distance) {
        return (distance * 180.0/Math.PI);
    }

}