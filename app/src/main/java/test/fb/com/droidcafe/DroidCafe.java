package test.fb.com.droidcafe;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Toast;

import test.fb.com.BuildConfig;
import test.fb.com.R;

public class DroidCafe extends AppCompatActivity {
    public static final String EXTRA_ORDER = BuildConfig.APPLICATION_ID + ".order";
    private String mOrderMessage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_droid_cafe);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DroidCafe.this, OrderActivity.class);
                intent.putExtra(EXTRA_ORDER, mOrderMessage);
                startActivity(intent);
            }
        });
    }

    public void displayOrder(View view) {
        switch(view.getId()) {
            case R.id.image_donut:
                mOrderMessage = getString(R.string.donut_order_msg);
                Toast.makeText(this, R.string.donut_order_msg, Toast.LENGTH_SHORT).show();
                break;
            case R.id.image_icecream:
                mOrderMessage = getString(R.string.icecream_order_msg);
                Toast.makeText(this, R.string.icecream_order_msg, Toast.LENGTH_SHORT).show();
                break;
            case R.id.image_froyo:
                mOrderMessage = getString(R.string.froyo_order_msg);
                Toast.makeText(this, R.string.froyo_order_msg, Toast.LENGTH_SHORT).show();
                break;
            default:
                mOrderMessage = getString(R.string.unknown_order_msg);
                Toast.makeText(this, R.string.unknown_order_msg, Toast.LENGTH_SHORT).show();
        }
    }
}
