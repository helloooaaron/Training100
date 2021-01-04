package test.fb.com.droidcafe;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import test.fb.com.BuildConfig;
import test.fb.com.R;

public class OrderActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static final String EXTRA_ORDER = BuildConfig.APPLICATION_ID + ".order";
    private static final String LOG_TAG = OrderActivity.class.getSimpleName();
    private String mSpinnerPhone;

    public static void startActivity(Context context, String orderMsg) {
        Intent intent = new Intent(context, OrderActivity.class);
        intent.putExtra(EXTRA_ORDER, orderMsg);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        TextView textOrderDetail = (TextView) findViewById(R.id.text_order_detail);
        Intent intent = getIntent();
        textOrderDetail.setText("Order: " + intent.getStringExtra(EXTRA_ORDER));

        // Set Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                (this, R.array.phone_array, android.R.layout.simple_spinner_item); // Layout of selected value w/o dropdown
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Layout of dropdown box
        Spinner spinner = (Spinner) findViewById(R.id.spinner_phone);
        if (spinner != null) {
            spinner.setAdapter(adapter); // Spinner needs an adapter
            spinner.setOnItemSelectedListener(this);
        }

        // Perform action directly from EditText's IME (keyboard)
        EditText editPhone = (EditText) findViewById(R.id.edit_phone);
        editPhone.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) { // android:imeOptions="actionSend"
                // Create implicit intent to start DIAL activity
                Intent imeIntent = new Intent(Intent.ACTION_DIAL);
                imeIntent.setData(Uri.parse("tel:" + textView.getText().toString()));
                if (imeIntent.resolveActivity(getPackageManager()) != null) { // Verify receiving activity exists
                    startActivity(imeIntent);
                } else {
                    Log.i(LOG_TAG, "Can't handle this intent");
                }
                return true;
            }
            return false;
        });
    }

    public void selectDeliveryMethod(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        if (checked) {
            switch(view.getId()) {
                case R.id.radio_sameday:
                    Toast.makeText(this, getString(R.string.radio_sameday), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.radio_nextday:
                    Toast.makeText(this, getString(R.string.radio_nextday), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.radio_pickup:
                    Toast.makeText(this, getString(R.string.radio_pickup), Toast.LENGTH_SHORT).show();
                    break;
                default:
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
        // Spinner callback
        mSpinnerPhone = adapterView.getItemAtPosition(pos).toString();
        Toast.makeText(this, mSpinnerPhone, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Spinner callback
    }
}
