package test.fb.com.droidcafe;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import test.fb.com.BuildConfig;
import test.fb.com.R;

public class DroidCafe extends AppCompatActivity {
    private String mOrderMessage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_droid_cafe);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(
                v -> OrderActivity.startActivity(DroidCafe.this, mOrderMessage));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu, adding items to the appbar.
        getMenuInflater().inflate(R.menu.menu_droid_cafe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_order:
                OrderActivity.startActivity(this, mOrderMessage);
                break;
            case R.id.action_status:
                Toast.makeText(this, R.string.action_status_msg, Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_fav:
                Toast.makeText(this, R.string.action_fav_msg, Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_contact:
                Toast.makeText(this, R.string.action_contact_mg, Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
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
