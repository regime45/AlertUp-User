package com.android.alertup_user;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class user_id extends AppCompatActivity {
    ImageView ivOutput, urlcontact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_id);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4b88a2")));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>User ID</font>"));

        ivOutput=  (findViewById(R.id.iv_output));
        updateViews();
    }

    public void updateViews() {

        @SuppressLint("WrongConstant") SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_APPEND);
        String sText = sharedPreferences.getString("id", "");


        // switch1.setChecked(switchOnOff);

        // String str= ""+newstring+ "" +text ;


        // String sText = ""+text+""+ numbers+ "" +barangays;

        MultiFormatWriter writer = new MultiFormatWriter();

        try {
            BitMatrix matrix= writer.encode(sText, BarcodeFormat.QR_CODE
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