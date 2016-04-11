package me.kartikarora.aadharcardreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Developer: chipset
 * Package : me.kartikarora.aadharcardreader
 * Project : Aadhar Card Reader
 * Date : 9/4/16
 */
public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    static final String SCAN_CONTENTS = "content";
    static final String SCAN_FORMAT = "format";

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.setAutoFocus(true);
        List<BarcodeFormat> barcodeFormats = new ArrayList<>();
        barcodeFormats.add(BarcodeFormat.QR_CODE);
        mScannerView.setFormats(barcodeFormats);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        Intent intent = new Intent();
        intent.putExtra(SCAN_CONTENTS, rawResult.getText());
        intent.putExtra(SCAN_FORMAT, rawResult.getBarcodeFormat().toString());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
