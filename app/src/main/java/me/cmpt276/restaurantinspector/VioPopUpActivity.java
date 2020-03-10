package me.cmpt276.restaurantinspector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class VioPopUpActivity extends AppCompatActivity {

    private String receivedString = "full_description";
    private String receivedInteger = "violation_id";
    private int VIOLATION_ID;
    private String FULL_DESCRIPTION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vio_pop_up);

        DisplayMetrics ds = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(ds);

        int width = ds.widthPixels;
        int height = ds.heightPixels;

        getWindow().setLayout((int)(width*.7),(int)(height*.6));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);
        setUpOKButton();

        Intent intent = getIntent();
        FULL_DESCRIPTION = intent.getStringExtra(receivedString);
        VIOLATION_ID = intent.getIntExtra(receivedInteger,0);

        TextView description = (TextView) findViewById(R.id.full_detail);
        description.setText(FULL_DESCRIPTION);

        TextView id = (TextView) findViewById(R.id.violation_id);
        id.setText("" + VIOLATION_ID);
    }

    private void setUpOKButton() {
        Button btn = (Button) findViewById(R.id.finish_detail);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, VioPopUpActivity.class);
    }
}