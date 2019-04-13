package com.mywings.appschedulling;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.provider.DocumentFile;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.view.View;
import android.widget.*;
import com.mywings.appschedulling.lanucher.AppModel;
import com.mywings.appschedulling.locally.AppSchedulingDatabaseHelper;
import com.mywings.appschedulling.locally.DbHelper;
import com.mywings.appschedulling.process.*;
import com.mywings.appschedulling.stats.UserInfoHolder;
import com.mywings.appschedulling.stats.model.AppMetadata;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class ConfigureActivity extends Activity implements OnRegisterDeviceListener, OnAppMetadataListener {

    private static final int REQUEST_CODE_PLACE = 10090;
    private Button btnFiles;
    private Button btnConfig;
    private CheckBox chkAll;
    private CheckBox chkImages;
    private CheckBox chkPdf;
    private CheckBox chkOther;
    private CheckBox chkAudio;
    private TextView lblPath;
    private static final int EXTERNAL_REQUEST = 1000;
    private AppSchedulingDatabaseHelper appSchedulingDatabaseHelper;
    private String strDeviceName = "";
    private String strImeiNumber = "";
    private AppMetadata appMetadata;
    private ProgressDialogUtil progressDialogUtil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);
        btnFiles = findViewById(R.id.btnFiles);
        btnConfig = findViewById(R.id.btnConfig);
        chkAll = findViewById(R.id.chkAll);
        chkImages = findViewById(R.id.chkImages);
        chkPdf = findViewById(R.id.chkPdf);
        chkOther = findViewById(R.id.chkOther);
        chkAudio = findViewById(R.id.chkAudio);
        lblPath = findViewById(R.id.lblPath);
        progressDialogUtil = new ProgressDialogUtil(this);

        chkAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    chkImages.setEnabled(false);
                    chkPdf.setEnabled(false);
                    chkOther.setEnabled(false);
                    chkAudio.setEnabled(false);

                    chkAudio.setChecked(true);
                    chkImages.setChecked(true);
                    chkPdf.setChecked(true);
                    chkOther.setChecked(true);

                } else {
                    chkImages.setEnabled(true);
                    chkPdf.setEnabled(true);
                    chkOther.setEnabled(true);
                    chkAudio.setEnabled(true);
                }
            }
        });

        btnFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(Intent.createChooser(intent, "Choose directory for place 1"), REQUEST_CODE_PLACE);
            }
        });

        strDeviceName = android.os.Build.MODEL;

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            strImeiNumber = telephonyManager.getImei();
        } else {
            strImeiNumber = telephonyManager.getDeviceId();
        }


        appSchedulingDatabaseHelper = new AppSchedulingDatabaseHelper(ConfigureActivity.this, DbHelper.DB_NAME, null, DbHelper.DB_VERSION);

        if (!strImeiNumber.isEmpty() && !strDeviceName.isEmpty()) {
            try {
                init();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    initConfiguration(appMetadata);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_PLACE) {
                AppModel appModel = UserInfoHolder.getInstance().getAppModel();
                //String filePath = data.getData().getPath();
                Uri uri = data.getData();
                DocumentFile fileProvider = DocumentFile.fromTreeUri(this, uri);
                int numOfFiles = 0;
                int sizesOfFile = 0;
                fileProvider.getUri();
                if (fileProvider.isDirectory()) {
                    for (DocumentFile file : fileProvider.listFiles()) {
                        if (file.isDirectory()) {
                            for (DocumentFile iFile : file.listFiles()) {
                                if (iFile.isDirectory()) {
                                    numOfFiles = numOfFiles + 1;
                                    sizesOfFile = (int) (sizesOfFile + iFile.length());
                                } else
                                    numOfFiles = numOfFiles + 1;
                                sizesOfFile = (int) (sizesOfFile + iFile.length());
                            }
                        } else {
                            numOfFiles = numOfFiles + 1;
                            sizesOfFile = (int) (sizesOfFile + file.length());
                        }
                    }
                } else {
                    numOfFiles = 1;
                    sizesOfFile = (int) (sizesOfFile + fileProvider.length());
                }
                appMetadata = new AppMetadata();
                appMetadata.setName(appModel.getLabel());
                appMetadata.setPackageName(appModel.getAppInfo().packageName);
                appMetadata.setDrawable(appModel.getIcon());
                appMetadata.setServerUrl("");
                appMetadata.setShow(true);
                appMetadata.setLocalDirectory(fileProvider.getUri().getPath());
                appMetadata.setUpload(true);
                appMetadata.setSize(String.valueOf(sizesOfFile));
                appMetadata.setNumOfFiles(String.valueOf(numOfFiles));
                appMetadata.setImei(strImeiNumber);
                appMetadata.setImageIcon(convertBitmapToString(getBitmap(appMetadata.getDrawable())));
            }
        }
    }

    private String convertBitmapToString(Bitmap bitmap) {
        if (null != bitmap) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        }
        return "";
    }

    private Bitmap getBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (null != bitmapDrawable.getBitmap()) {
                return bitmapDrawable.getBitmap();
            }
        }
        return null;
    }

    private void init() throws JSONException {
        RegisterDeviceAsync registerDeviceAsync = new RegisterDeviceAsync();
        JSONObject jRequest = new JSONObject();
        JSONObject param = new JSONObject();
        param.put("IMEINumber", strImeiNumber);
        param.put("DeviceSecureId", "");
        param.put("DeviceName", strDeviceName);
        jRequest.put("request", param);
        registerDeviceAsync.setOnRegisterDeviceListener(this, jRequest);
    }

    private void initConfiguration(AppMetadata appMetadata) throws JSONException {
        progressDialogUtil.show();
        JSONObject jRequest = new JSONObject();
        JSONObject param = new JSONObject();
        param.put("Name", appMetadata.getName());
        param.put("PackageName", appMetadata.getPackageName());
        param.put("LocalDirector", appMetadata.getLocalDirectory());
        param.put("NumOfFiles", appMetadata.getNumOfFiles());
        param.put("Size", appMetadata.getSize());
        param.put("Show", appMetadata.getShow());
        param.put("Synced", appMetadata.getSynced());
        param.put("Upload", appMetadata.getUpload());
        param.put("ServerUrl", appMetadata.getServerUrl());
        param.put("ImageIcon", appMetadata.getImageIcon());
        param.put("Imei", appMetadata.getImei());
        jRequest.put("request", param);
        UploadMetadataAsync uploadMetadataAsync = new UploadMetadataAsync();
        uploadMetadataAsync.setOnMetadataListener(this, jRequest);
    }

    @Override
    public void onDeviceRegisteredSuccess(@Nullable String result) {

    }

    private void notifyUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Sync data")
                .setMessage("You have requested back up data an application to cloud, It will start shortly")
                .setCancelable(true)
                .setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onAppMetadataSuccess(@Nullable String result) {
        progressDialogUtil.hide();
        if (null != result && result.equalsIgnoreCase("1")) {
            notifyUser();
        } else {
            Toast.makeText(this, "Something went wrong in configuration setting or application already registered.", Toast.LENGTH_LONG).show();
        }
    }

    public long fileSize(File root) {
        if (root == null) {
            return 0;
        }
        if (root.isFile()) {
            return root.length();
        }
        try {
            if (isSymlink(root)) {
                return 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }

        long length = 0;
        File[] files = root.listFiles();
        if (files == null) {
            return 0;
        }
        for (File file : files) {
            length += fileSize(file);
        }

        return length;
    }

    private boolean isSymlink(File file) throws IOException {
        File canon;
        if (file.getParent() == null) {
            canon = file;
        } else {
            File canonDir = file.getParentFile().getCanonicalFile();
            canon = new File(canonDir, file.getName());
        }
        return !canon.getCanonicalFile().equals(canon.getAbsoluteFile());
    }
}
