package test.fb.com.droidcafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import test.fb.com.R;

public class OrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        TextView textOrderDetail = (TextView) findViewById(R.id.text_order_detail);
        Intent intent = getIntent();
        textOrderDetail.setText("Order: " + intent.getStringExtra(DroidCafe.EXTRA_ORDER));
    }
}
