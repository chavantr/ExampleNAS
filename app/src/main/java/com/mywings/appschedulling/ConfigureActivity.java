package com.mywings.appschedulling;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.io.File;

public class ConfigureActivity extends Activity {

    private static final int REQUEST_CODE_PLACE = 10090;
    private Button btnFiles;
    private CheckBox chkAll;
    private CheckBox chkImages;
    private CheckBox chkPdf;
    private CheckBox chkOther;
    private CheckBox chkAudio;
    private TextView lblPath;
    private static final int EXTERNAL_REQUEST = 1000;


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
                // Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                // intent.setType("*/*");
                // intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                // startActivityForResult(intent, REQUEST_CODE_PLACE);

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(Intent.createChooser(intent, "Choose directory for place 1"), REQUEST_CODE_PLACE);
            }
        });

       /* if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    EXTERNAL_REQUEST);


        }*/

        /*UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        long start = calendar.getTimeInMillis();
        long end = System.currentTimeMillis();

        List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end);

        Log.e("error", "stat" + stats.size());*/

        /*if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            long currentTime = System.currentTimeMillis();
            List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_WEEKLY, currentTime - 100 * 1000, currentTime);
            if (stats != null) {
                long lastUsedAppTime = 0;
                for (UsageStats usageStats : stats) {
                    if (usageStats.getLastTimeUsed() > lastUsedAppTime) {
                        String foregroundApp = usageStats.getPackageName();
                        lastUsedAppTime = usageStats.getLastTimeUsed();
                    }
                }
            }
        }*/


    }

   /* private boolean checkForPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, Process.myUid(), context.getPackageName());
        return mode == MODE_ALLOWED;
    }*/

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case EXTERNAL_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                break;
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_PLACE) {
                String filePath = data.getData().getPath();
                File file = new File(filePath);
                lblPath.setText(file.getAbsolutePath());
            }
        }
    }
}
