package com.test.ekdk636.healthcheck;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.tsengvn.typekit.Typekit;
import com.tsengvn.typekit.TypekitContextWrapper;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ActionBar actionBar = getSupportActionBar();
        //actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#78909C")));

        Button b = (Button)findViewById(R.id.btn1);
        b.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), HealthBodyInfoActivity.class);
                startActivity(intent);
            }
        });

        Button b2 = (Button)findViewById(R.id.calc1);
        b2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), MyBodyInfoListActivity.class);
                startActivity(intent);
            }
        });

        Button b3 = (Button)findViewById(R.id.calc2);
        b3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), NormalWeightCheckActivity.class);

                intent.putExtra("waist", "");
                intent.putExtra("hip", "");
                intent.putExtra("sex", "1");

                startActivity(intent);
            }
        });

        Button b4 = (Button)findViewById(R.id.calc3);
        b4.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), BodyBmiCheckActivity.class);

                intent.putExtra("height", "");
                intent.putExtra("weight", "");

                startActivity(intent);
            }
        });

        Button b5 = (Button)findViewById(R.id.calc4);
        b5.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), BodyWhrCheckActivity.class);

                intent.putExtra("height", "");
                intent.putExtra("weight", "");
                intent.putExtra("sex", "1");

                startActivity(intent);
            }
        });

        Button b6 = (Button)findViewById(R.id.calc5);
        b6.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), BodyFatCheckActivity.class);
                startActivity(intent);
            }
        });

        Button btnClose = (Button) findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                finish();;
            }
        });
    }

    /*
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
    */
}
