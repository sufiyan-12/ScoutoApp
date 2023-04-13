package com.example.scoutoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.scoutoapp.db.CarsDB;
import com.example.scoutoapp.pojo.GetModelFromMakeId;
import com.example.scoutoapp.pojo.Make;
import com.example.scoutoapp.pojo.MakeResult;
import com.example.scoutoapp.pojo.Result;
import com.example.scoutoapp.spinners.MakeSpinnerAdapter;
import com.example.scoutoapp.spinners.ModelSpinnerAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DashboardActivity extends AppCompatActivity implements OnCarItemClicked {

    private RecyclerView recyclerView;
    private DashboardRVAdapter dashboardAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private static final String BASE_URL = "https://vpic.nhtsa.dot.gov/api/vehicles/";
    private String[] arr;
    private Spinner makeSpinner, modelSpinner;
    private ArrayList<Make> makeArrayList;
    private ArrayList<Result> resultArrayList;
    private ArrayList<String> makeSpinnerItems;
    private ArrayList<String> modelSpinnerItems;
    private int selectedMakeSpinnerPosition;
    private int selectedModelSpinnerPosition;
    private Button addCarBtn, logoutBtn;
    private Retrofit retrofit;

    MakeSpinnerAdapter makeSpinnerAdapter;
    ModelSpinnerAdapter modelSpinnerAdapter;

    private CarsDB dbHandler;
    private CarItem carItemToUpdate;

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_GALLERY_PERMISSION = 101;
    private static final int REQUEST_IMAGE_CAPTURE = 200;
    private static final int REQUEST_IMAGE_GALLERY = 201;
    private static final int capturedImageCount = 1000;

    private SharedPreferences pref;
    private int USER_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initVariables();
        initRecyclerView();
        buildRetrofitObj();
        setSpinners();
        updateRecyclerView();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                getMakeData(); // to get care make date from first api end-point
            }
        });

        // car make dropdown list select item listener
        makeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedItem = adapterView.getItemAtPosition(position).toString();
                if(!selectedItem.equals(arr[0])) {
//                    Toast.makeText(DashboardActivity.this, "selectedItem: " + selectedItem + " position: " + position, Toast.LENGTH_SHORT).show();
                    selectedMakeSpinnerPosition = position-1;
                    makeSpinner.setSelection(position);
//                    Log.d("MyDashboardActivity", "make_id: "+makeArrayList.get(position-1).MAKE_ID+"\n make_name: "+makeArrayList.get(position-1).MAKE_NAME);
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            getModelData(makeArrayList.get(position-1).MAKE_ID);
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                makeSpinner.setSelection(0);
            }
        });

        // car model dropdown list select item listener
        modelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                selectedModelSpinnerPosition = position-1;
                modelSpinner.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                modelSpinner.setSelection(0);
            }
        });

        // add new car button click listener
        addCarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!resultArrayList.isEmpty()) {
                    Toast.makeText(DashboardActivity.this, "New Car Added Successfully!!", Toast.LENGTH_SHORT).show();
                    Result car = resultArrayList.get(selectedModelSpinnerPosition);
                    int make_id = car.Make_ID;
                    String make_name = car.Make_Name.trim();
                    String model_name = car.Model_Name.trim();
//                        int model_id = car.Model_ID;
//                    Log.d("dataSent", "car_make: " + make_name + "\n car_model: " + model_name);
                    dbHandler.addCar(new CarItem(make_id, make_name, model_name, "", new byte[0]), USER_ID);
                    modelSpinnerItems.clear();
                    modelSpinnerItems.add(arr[0]);
                    selectedMakeSpinnerPosition = 0;
                    selectedModelSpinnerPosition = 0;
                    makeSpinner.setSelection(selectedMakeSpinnerPosition);
                    modelSpinner.setSelection(selectedModelSpinnerPosition);
                    updateRecyclerView();
            }else {
//                Log.d("dataSent", "result array is empty!!");
                Toast.makeText(DashboardActivity.this, "Please select car make and car model..", Toast.LENGTH_SHORT).show();
                }
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                startActivity(new Intent(DashboardActivity.this, RegisterOrLoginActivity.class));
                editor.apply();
                finish();
            }
        });
    }

    private void updateRecyclerView(){
        ArrayList<CarItem> list = dbHandler.getAllCarsItems(USER_ID);
        ArrayList<CarItem> newList = new ArrayList<>();
        list.forEach(it -> {
            newList.add(new CarItem(it.getMake_id(), it.getCar_make(), it.getCar_model(), it.getImageUrl(), it.getImageBytes()));
//            Log.d("mydatabase", "car_make : " + it.getCar_make() + "\n car_model: " + it.getCar_model());
        });
//        Toast.makeText(this, "size: "+list.size(), Toast.LENGTH_SHORT).show();
        dashboardAdapter.updateList(newList);
    }
    private void setSpinners() {
        // make spinner to select make of car
        makeSpinnerAdapter = new MakeSpinnerAdapter(DashboardActivity.this, makeSpinnerItems);
        makeSpinner.setAdapter(makeSpinnerAdapter);

        // model spinner to select model of car
        modelSpinnerAdapter = new ModelSpinnerAdapter(DashboardActivity.this, modelSpinnerItems);
        modelSpinner.setAdapter(modelSpinnerAdapter);
    }

    private void getModelData(int make_id) {
        ApiServices apiServices = retrofit.create(ApiServices.class);
        Call<GetModelFromMakeId> secondCall = apiServices.getModelFromMakeId(make_id);
        secondCall.enqueue(new Callback<GetModelFromMakeId>() {
            @Override
            public void onResponse(Call<GetModelFromMakeId> call, Response<GetModelFromMakeId> response) {
                if (response.isSuccessful()){
                    GetModelFromMakeId model = response.body();
                    List<Result> resultList = model.Results;
                    Result res = resultList.get(0);
                    resultArrayList.clear();
                    resultArrayList.add(new Result(res.Make_ID, res.Make_Name, res.Model_ID, res.Model_Name));
                    modelSpinnerItems.clear();
                    modelSpinnerItems.add(arr[0]);
                    modelSpinnerItems.add(res.Model_Name.trim());
                    modelSpinnerAdapter.updateModelSpinnerList(DashboardActivity.this, modelSpinnerItems);
                    Log.d("MyDashboardActivity","make_id: "+res.Make_ID+" make_name: "+res.Make_Name+
                            " model_id: "+res.Model_ID+ " model_name: "+res.Model_Name);
                }else{
                    Log.d("MyDashboardActivity", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<GetModelFromMakeId> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void getMakeData() {
        if(!makeArrayList.isEmpty()){
            return;
        }
        ApiServices apiServices = retrofit.create(ApiServices.class);
        Call<MakeResult> firstCall = apiServices.getCarMakes();
        firstCall.enqueue(new Callback<MakeResult>() {
            @Override
            public void onResponse(Call<MakeResult> call, Response<MakeResult> response) {
                if(response.isSuccessful()){
                    MakeResult makeResult = response.body();
                    List<Make> list = makeResult.Results;
                    ArrayList<String> myList = new ArrayList<>();
                    list.forEach(make -> {
                        int make_id = make.MAKE_ID;
                        String make_name = make.MAKE_NAME.trim();
                        makeArrayList.add(new Make(make_id, make_name));
//                        Log.d("MyDashboardActivity", "make_id: "+make.MAKE_ID + " make_name: " + make.MAKE_NAME);
                        myList.add(make.MAKE_NAME.trim());
                    });
                    makeSpinnerItems.clear();
                    makeSpinnerItems.add(arr[0]);
                    makeSpinnerItems.addAll(myList);
                    makeSpinnerAdapter.updateMakeList(DashboardActivity.this, makeSpinnerItems);
                }else{
                    Log.d("MyDashboardActivity", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<MakeResult> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void buildRetrofitObj() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(dashboardAdapter);
    }

    private void initVariables() {
        arr = new String[]{"Select an options"};
        makeSpinner = findViewById(R.id.make_spinner);
        modelSpinner = findViewById(R.id.model_spinner);
        makeSpinnerItems = new ArrayList<>(Arrays.asList(arr));
        modelSpinnerItems = new ArrayList<>(Arrays.asList(arr));
        makeArrayList = new ArrayList<>();
        resultArrayList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        dashboardAdapter = new DashboardRVAdapter(this);
        selectedMakeSpinnerPosition = 0;
        selectedModelSpinnerPosition = 0;
        addCarBtn = findViewById(R.id.idAddCarBtn);
        dbHandler = CarsDB.getInstance(DashboardActivity.this);
        pref = getSharedPreferences("user_account", Context.MODE_PRIVATE);
        USER_ID = pref.getInt("user_id", 0);
        logoutBtn = findViewById(R.id.idLogoutBtn);
    }

    @Override
    public void onItemClicked(CarItem item) {
        Log.d("DashboardActivity", "data: " + item.getCar_make());
    }

    @Override
    public void onImageSet(CarItem item) {
        carItemToUpdate = item;
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Choose Image Source");
        dialog.setItems(new CharSequence[]{"Camera", "Gallery"},
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Check camera permission
                        if(i == 0) {
                            launchCamera();
                        }
                        // Check gallery permission
                        if(i == 1){
                            launchGallery();
                        }
                    }
                });
        dialog.show();
//        launchGallery();
    }
    private void launchCamera() {
        if (ContextCompat.checkSelfPermission(DashboardActivity.this,
                android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(DashboardActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        } else {
            ActivityCompat.requestPermissions(DashboardActivity.this,
                    new String[]{android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CAMERA_PERMISSION);
        }
    }

    private void launchGallery() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R){
            if (ContextCompat.checkSelfPermission(DashboardActivity.this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                Log.d("myActivity", "works");
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
            } else {
                ActivityCompat.requestPermissions(DashboardActivity.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_GALLERY_PERMISSION);
            }
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            if (ContextCompat.checkSelfPermission(DashboardActivity.this,
                    Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
//                Log.d("myActivity", "works");
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
            } else {
                ActivityCompat.requestPermissions(DashboardActivity.this,
                        new String[]{android.Manifest.permission.READ_MEDIA_IMAGES},
                        REQUEST_GALLERY_PERMISSION);
            }
        }

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchCamera();
                }
                break;
            case REQUEST_GALLERY_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchGallery();
                }
                break;
        }
    }
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if(requestCode == REQUEST_IMAGE_CAPTURE){
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                File rootDirectory = Environment.getExternalStorageDirectory();
                String fileName = "image_" + System.currentTimeMillis() + ".jpg"; // Unique file name
                File file = new File(rootDirectory, fileName);
                FileOutputStream fileOutputStream = null;
                try {
                    fileOutputStream = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Uri imageUri = FileProvider.getUriForFile(DashboardActivity.this, "com.example.scoutoapp.fileprovider", file);
                if(imageUri != null)
                    dbHandler.updateCar(carItemToUpdate, USER_ID, imageUri.toString(), new byte[0]);
                updateRecyclerView();
            }

            if(requestCode == REQUEST_IMAGE_GALLERY){
//                Log.d("getImageUri", "uri: "+data.getData());
                if(carItemToUpdate != null){
                    dbHandler.updateCar(carItemToUpdate, USER_ID, data.getData().toString(), new byte[0]);
                    updateRecyclerView();
                }
            }
            Toast.makeText(this, "Image updated successfully!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemDelete(CarItem item, int position) {
        if(dbHandler != null){
            dbHandler.deleteCar(item, USER_ID);
            updateRecyclerView();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
