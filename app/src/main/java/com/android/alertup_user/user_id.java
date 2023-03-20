package com.android.alertup_user;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.jakewharton.processphoenix.ProcessPhoenix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class user_id extends AppCompatActivity {
    ImageView ivOutput, urlcontact;
    Button ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_id);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4b88a2")));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>User ID</font>"));

        ivOutput=  (findViewById(R.id.iv_output));
        ok= findViewById(R.id.ok);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProcessPhoenix.triggerRebirth(getApplicationContext());
            }
        });


                updateViews();
    }

    public void updateViews() {

        @SuppressLint("WrongConstant") SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_APPEND);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String m_androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        editor.putString("ids", String.valueOf(m_androidId));
        editor.commit();



        // switch1.setChecked(switchOnOff);

        // String str= ""+newstring+ "" +text ;


        // String sText = ""+text+""+ numbers+ "" +barangays;

        MultiFormatWriter writer = new MultiFormatWriter();

        try {
            BitMatrix matrix= writer.encode(m_androidId, BarcodeFormat.QR_CODE
                    ,300, 300);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);

            ivOutput.setImageBitmap(bitmap);
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            //hidekeyboard
//            manager.hideSoftInputFromWindow(textView.getApplicationWindowToken()
            //      ,0);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }


}