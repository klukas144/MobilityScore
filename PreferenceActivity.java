package de.hka.projekt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

/**
 * @author Lukas Heupel
 * @author Simon Schreckenberg
 * @author Alexander Beck
 * @author Cosimo Schostok
 * @author Kilian Langlinderer
 * @version 1.0
 */
public class PreferenceActivity extends AppCompatActivity {

    private Switch switch_license;
    private Switch switch_car;
    private Switch switch_bike;
    private Switch switch_pt;
    private AppCompatRadioButton rbChecked;
    private AppCompatRadioButton rbSetCheck;
    private boolean b;


    /**
     * Layout definieren; Views einbinden; Buttons mit Aktionen versehen
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        switch_license = this.findViewById(R.id.switch_license);
        switch_car = this.findViewById(R.id.switch_car);
        switch_bike = this.findViewById(R.id.switch_bike);
        switch_pt = this.findViewById(R.id.switch_pt);
        Button btnSave = this.findViewById(R.id.btnSavePreferences);
        EditText txtBikeDistance = this.findViewById(R.id.txtBikeDistance);
        EditText txtCarDistance = this.findViewById(R.id.txtCarDistance);
        RelativeLayout rlCarDist = this.findViewById(R.id.rl_carDistance);
        RelativeLayout rlBikeDist = this.findViewById(R.id.rl_bikeDistance);
        rbChecked = this.findViewById(R.id.radioButton2);
        RadioGroup group = this.findViewById(R.id.prefl9);
        String[] reqString;
        Intent intent = this.getIntent();
        reqString = intent.getStringArrayExtra("reqList");
        b = intent.getBooleanExtra("status", true);

        if (!reqString[0].equals("-1")) {
            if (reqString[0].equals("1")) {
                switch_license.setChecked(true);
            } else {
                switch_license.setChecked(false);
            }
            if (reqString[1].equals("1")) {
                switch_car.setChecked(true);
            } else {
                switch_car.setChecked(false);
            }
            if (!reqString[2].equals("-1")) {
                txtCarDistance.setText(reqString[2]);
            }
            if (reqString[3].equals("1")) {
                switch_bike.setChecked(true);
            } else {
                switch_bike.setChecked(false);
            }
            if (!reqString[4].equals("-1")) {
                txtBikeDistance.setText(reqString[4]);
            }
            if (reqString[5].equals("1")) {
                switch_pt.setChecked(true);
            } else {
                switch_pt.setChecked(false);
            }
            switch (reqString[6]) {
                case "MIV":
                    rbSetCheck = this.findViewById(R.id.radioButton);
                    rbSetCheck.setChecked(true);
                    break;
                case "ÖV":
                    rbSetCheck = this.findViewById(R.id.radioButton2);
                    rbSetCheck.setChecked(true);
                    break;
                case "Fahrrad":
                    rbSetCheck = this.findViewById(R.id.radioButton3);
                    rbSetCheck.setChecked(true);
                    break;
                case "Fuß":
                    rbSetCheck = this.findViewById(R.id.radioButton4);
                    rbSetCheck.setChecked(true);
                    break;
                default:
                    break;
            }
        }

        group.setOnCheckedChangeListener((group1, checkedId) -> {
            rbChecked = findViewById(checkedId);
        });

        btnSave.setOnClickListener(view -> {
            Intent replyIntent = new Intent();
            String[] saved = new String[7];
            if (switch_license.isChecked()) {
                saved[0] = "1";
            } else {
                saved[0] = "0";
            }
            if (switch_car.isChecked()) {
                saved[1] = "1";
            } else {
                saved[1] = "0";
            }
            if (!TextUtils.isEmpty(txtCarDistance.getText())) {
                saved[2] = txtCarDistance.getText().toString();
            } else {
                saved[2] = "-1";
            }
            if (switch_bike.isChecked()) {
                saved[3] = "1";
            } else {
                saved[3] = "0";
            }
            if (!TextUtils.isEmpty(txtBikeDistance.getText())) {
                saved[4] = txtBikeDistance.getText().toString();
            } else {
                saved[4] = "-1";
            }
            if (switch_pt.isChecked()) {
                saved[5] = "1";
            } else {
                saved[5] = "0";
            }
            saved[6] = rbChecked.getText().toString();
            replyIntent.putExtra("saved", saved);
            setResult(RESULT_OK, replyIntent);
            finish();
        });

        switch_license.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d("Switch State=", "" + isChecked);
            Drawable d = switch_license.getThumbDrawable();
            if (isChecked) {
                d.setTint(getResources().getColor(R.color.green));
                if (switch_car.isChecked()) {
                    rlCarDist.setVisibility(View.VISIBLE);
                }
            } else {
                d.setTint(getResources().getColor(R.color.red));
                rlCarDist.setVisibility(View.GONE);
            }
        });
        if (switch_license.isChecked()) {
            Drawable d = switch_license.getThumbDrawable();
            d.setTint(getResources().getColor(R.color.green));
            if (switch_car.isChecked()) {
                rlCarDist.setVisibility(View.VISIBLE);
            }
        } else {
            Drawable d = switch_license.getThumbDrawable();
            d.setTint(getResources().getColor(R.color.red));
            rlCarDist.setVisibility(View.GONE);
        }
        switch_car.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d("Switch State=", "" + isChecked);
            Drawable d = switch_car.getThumbDrawable();
            if (isChecked) {
                d.setTint(getResources().getColor(R.color.green));
                if (switch_license.isChecked()) {
                    rlCarDist.setVisibility(View.VISIBLE);
                }
            } else {
                d.setTint(getResources().getColor(R.color.red));
                rlCarDist.setVisibility(View.GONE);
            }
        });
        if (switch_car.isChecked()) {
            Drawable d = switch_car.getThumbDrawable();
            d.setTint(getResources().getColor(R.color.green));
            if (switch_license.isChecked()) {
                rlCarDist.setVisibility(View.VISIBLE);
            }
        } else {
            Drawable d = switch_car.getThumbDrawable();
            d.setTint(getResources().getColor(R.color.red));
            rlCarDist.setVisibility(View.GONE);
        }
        switch_bike.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d("Switch State=", "" + isChecked);
            Drawable d = switch_bike.getThumbDrawable();
            if (isChecked) {
                d.setTint(getResources().getColor(R.color.green));
                rlBikeDist.setVisibility(View.VISIBLE);
            } else {
                d.setTint(getResources().getColor(R.color.red));
                rlBikeDist.setVisibility(View.GONE);
            }
        });
        if (switch_bike.isChecked()) {
            Drawable d = switch_bike.getThumbDrawable();
            d.setTint(getResources().getColor(R.color.green));
            rlBikeDist.setVisibility(View.VISIBLE);
        } else {
            Drawable d = switch_bike.getThumbDrawable();
            d.setTint(getResources().getColor(R.color.red));
            rlBikeDist.setVisibility(View.GONE);
        }
        switch_pt.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d("Switch State=", "" + isChecked);
            Drawable d = switch_pt.getThumbDrawable();
            if (isChecked) {
                d.setTint(getResources().getColor(R.color.green));
            } else {
                d.setTint(getResources().getColor(R.color.red));
            }
        });
        if (switch_pt.isChecked()) {
            Drawable d = switch_pt.getThumbDrawable();
            d.setTint(getResources().getColor(R.color.green));
        } else {
            Drawable d = switch_pt.getThumbDrawable();
            d.setTint(getResources().getColor(R.color.red));
        }
    }


    /**
     * bei gewissen Aufrufen Zurücktaste blockieren
     */
    public void onBackPressed() {
        if (b) {
            super.onBackPressed();
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "Bitte erst speichern!",
                    Toast.LENGTH_LONG).show();
        }
    }

}