package me.kartikarora.aadharcardreader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.OnItemClickListener {

    private static final int SCAN_REQ_CODE = 1;
    private static final int CAMERA_PERM_CODE = 2;
    private static final String TAG = MainActivity.class.getName();
    private static final String BOTTOM_SHEET_STATE = "bottom_sheet_state";
    private static final String LAST_UID = "last_uid";
    private List<PrintLetterBarcodeData> mDataList = new ArrayList<>();
    private RealmResults<PrintLetterBarcodeData> mRealmResultList;
    private CoordinatorLayout mLayout;
    private RecyclerViewAdapter mAdapter;
    private RecyclerView mReadCardList;
    private Realm mRealm;
    private FloatingActionButton mReadCardFAB;
    private View mBottomSheetView;
    private BottomSheetBehavior behavior;
    private int bottomSheetState;
    private String lastUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRealm = Realm.getInstance(new RealmConfiguration.Builder(MainActivity.this).build());

        mLayout = (CoordinatorLayout) findViewById(R.id.layout);
        mReadCardList = (RecyclerView) findViewById(R.id.read_card_list);
        mReadCardFAB = (FloatingActionButton) findViewById(R.id.read_card_fab);
        mBottomSheetView = mLayout.findViewById(R.id.bottom_sheet_view);

        behavior = BottomSheetBehavior.from(mBottomSheetView);
        behavior.setHideable(true);

        mRealmResultList = mRealm.where(PrintLetterBarcodeData.class).findAll();
        mDataList.addAll(mRealmResultList);

        if (mReadCardList != null) {
            if (mDataList.size() == 0) {
                mReadCardList.setVisibility(View.GONE);
                findViewById(R.id.nothing_text_view).setVisibility(View.VISIBLE);
            }
            mAdapter = new RecyclerViewAdapter(getApplicationContext(), mDataList);
            mAdapter.setOnItemClickListener(this);

            mReadCardList.setAdapter(mAdapter);
            mReadCardList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        }

        if (mReadCardFAB != null) {
            mReadCardFAB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        startActivityForResult(new Intent(MainActivity.this, ScannerActivity.class), SCAN_REQ_CODE);
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA)) {
                            Snackbar.make(mLayout, "Camera Permission Required", Snackbar.LENGTH_INDEFINITE)
                                    .setAction(android.R.string.ok, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
                                        }
                                    }).show();
                        } else
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                bottomSheetState = newState;
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheet.setVisibility(View.GONE);
                    mReadCardFAB.show();
                    behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(BOTTOM_SHEET_STATE, bottomSheetState);
        outState.putString(LAST_UID, lastUID);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        bottomSheetState = savedInstanceState.getInt(BOTTOM_SHEET_STATE);
        if (bottomSheetState == BottomSheetBehavior.STATE_COLLAPSED) {
            mReadCardFAB.show();
            mBottomSheetView.setVisibility(View.GONE);
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else {
            lastUID = savedInstanceState.getString(LAST_UID);
            if (lastUID != null) {
                PrintLetterBarcodeData item = mRealm.where(PrintLetterBarcodeData.class).contains("uid", lastUID).findFirst();
                mReadCardFAB.hide();
                setBottomSheetData(mBottomSheetView, item);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                mBottomSheetView.setVisibility(View.VISIBLE);
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERM_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startActivityForResult(new Intent(MainActivity.this, ScannerActivity.class), SCAN_REQ_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == SCAN_REQ_CODE) {
            String contents = data.getStringExtra(ScannerActivity.SCAN_CONTENTS);
            String format = data.getStringExtra(ScannerActivity.SCAN_FORMAT);
            Log.d(TAG, format);
            Log.d(TAG, contents);


            XmlPullParserFactory xmlFactoryObject;
            try {
                xmlFactoryObject = XmlPullParserFactory.newInstance();
                XmlPullParser myparser = xmlFactoryObject.newPullParser();
                myparser.setInput(new StringReader(contents));
                int event = myparser.getEventType();
                while (event != XmlPullParser.END_DOCUMENT) {
                    String name = myparser.getName();
                    switch (event) {
                        case XmlPullParser.START_TAG:
                            break;

                        case XmlPullParser.END_TAG:
                            if (name.equals("PrintLetterBarcodeData")) {
                                if (!checkIfExists(myparser.getAttributeValue(null, "uid"))) {
                                    mRealm.beginTransaction();
                                    PrintLetterBarcodeData barcodeData = mRealm.createObject(PrintLetterBarcodeData.class);
                                    barcodeData.setUid(myparser.getAttributeValue(null, "uid"));
                                    barcodeData.setName(myparser.getAttributeValue(null, "name"));
                                    barcodeData.setCo(myparser.getAttributeValue(null, "co"));
                                    barcodeData.setDist(myparser.getAttributeValue(null, "dist"));
                                    barcodeData.setGender(myparser.getAttributeValue(null, "gender"));
                                    barcodeData.setHouse(myparser.getAttributeValue(null, "house"));
                                    barcodeData.setLoc(myparser.getAttributeValue(null, "loc"));
                                    barcodeData.setPc(myparser.getAttributeValue(null, "pc"));
                                    barcodeData.setState(myparser.getAttributeValue(null, "state"));
                                    barcodeData.setStreet(myparser.getAttributeValue(null, "street"));
                                    barcodeData.setVtc(myparser.getAttributeValue(null, "vtc"));
                                    barcodeData.setYob(myparser.getAttributeValue(null, "yob"));
                                    mRealm.commitTransaction();
                                } else
                                    Snackbar.make(mLayout, "Card already scanned", Snackbar.LENGTH_SHORT).show();
                            }
                            break;
                    }
                    event = myparser.next();
                }
            } catch (XmlPullParserException | IOException e) {
                Snackbar.make(mLayout, "Invalid QR Code", Snackbar.LENGTH_LONG).show();
                e.printStackTrace();
            }
            mDataList.clear();
            mRealmResultList = mRealm.where(PrintLetterBarcodeData.class).findAll();
            mDataList.addAll(mRealmResultList);
            mAdapter = new RecyclerViewAdapter(getApplicationContext(), mDataList);
            mAdapter.setOnItemClickListener(this);
            mReadCardList.setAdapter(mAdapter);
            mReadCardList.setVisibility(View.VISIBLE);
            findViewById(R.id.nothing_text_view).setVisibility(View.GONE);
        } else
            Snackbar.make(mLayout, "Unable to scan", Snackbar.LENGTH_SHORT).show();
    }


    public boolean checkIfExists(String uid) {
        RealmQuery<PrintLetterBarcodeData> query = mRealm.where(PrintLetterBarcodeData.class)
                .equalTo("uid", uid);
        return query.count() != 0;
    }

    @Override
    public void onItemClick(PrintLetterBarcodeData item) {
        if (item != null) {
            lastUID = item.getUid();
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            mBottomSheetView.setVisibility(View.VISIBLE);
            mReadCardFAB.hide();
            behavior.setHideable(true);
            TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                behavior.setPeekHeight(TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics()));
            }

            setBottomSheetData(mBottomSheetView, item);
        }
    }

    private void setBottomSheetData(View bottomSheetView, PrintLetterBarcodeData item) {
        TextView uidTextView = (TextView) bottomSheetView.findViewById(R.id.uid_text_view);
        TextView yobTextView = (TextView) bottomSheetView.findViewById(R.id.yob_text_view);
        TextView genderTextView = (TextView) bottomSheetView.findViewById(R.id.gender_text_view);
        TextView addressTextView = (TextView) bottomSheetView.findViewById(R.id.address_text_view);
        TextView nameTextView = (TextView) bottomSheetView.findViewById(R.id.name_text_view);

        String address = item.getCo() + ", " + item.getHouse()
                + ", " + item.getStreet() + ", " + item.getLoc()
                + ", " + item.getVtc() + ", " + item.getState()
                + ", " + item.getPc();
        uidTextView.setText("UID: " + item.getUid());
        yobTextView.setText("Year of Birth: " + item.getYob());
        genderTextView.setText("Gender: " + item.getGender());
        addressTextView.setText("Address: " + address);
        nameTextView.setText(item.getName());
    }
}

