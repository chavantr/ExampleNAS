package com.mywings.appschedulling;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.mywings.appschedulling.lanucher.AppModel;
import com.mywings.appschedulling.locally.AppSchedulingDatabaseHelper;
import com.mywings.appschedulling.locally.DbHelper;
import com.mywings.appschedulling.process.OnRegisterDeviceListener;
import com.mywings.appschedulling.process.RegisterDeviceAsync;
import com.mywings.appschedulling.stats.UserInfoHolder;
import com.mywings.appschedulling.stats.model.AppMetadata;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class ConfigureActivity extends Activity implements OnRegisterDeviceListener {

    private static final int REQUEST_CODE_PLACE = 10090;
    private Button btnFiles;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);
        btnFiles = findViewById(R.id.btnFiles);
        chkAll = findViewById(R.id.chkAll);
        chkImages = findViewById(R.id.chkImages);
        chkPdf = findViewById(R.id.chkPdf);
        chkOther = findViewById(R.id.chkOther);
        chkAudio = findViewById(R.id.chkAudio);
        lblPath = findViewById(R.id.lblPath);

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

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_PLACE) {
                AppModel appModel = UserInfoHolder.getInstance().getAppModel();
                String filePath = data.getData().getPath();
                File file = new File(filePath);
                lblPath.setText(file.getAbsolutePath());
                AppMetadata appMetadata = new AppMetadata();
                appMetadata.setName(appModel.getAppInfo().name);
            }
        }
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

    @Override
    public void onDeviceRegisteredSuccess(@Nullable String result) {

    }
}
