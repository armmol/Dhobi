package com.example.dhobi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Test extends AppCompatActivity {

    private Button btn_test;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_4testupi);
        btn_test = findViewById (R.id.button);

        btn_test.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                startActivity (new Intent (Test.this, MapsActivity.class));
            }
        });
    }

    private void payusingGPay () {
        Uri uri = Uri.parse ("upi://pay").buildUpon ()
                .appendQueryParameter ("pa","")
                .appendQueryParameter ("pn","")
                .appendQueryParameter ("tn","Payment just for testing")
                .appendQueryParameter ("am","1")
                .appendQueryParameter ("cu","INR").build ();
        Intent upiintent = new Intent(Intent.ACTION_VIEW);
        upiintent.setData (uri);
        Intent chooser = Intent.createChooser (upiintent, "Pay with");
        try {
            startActivityForResult (upiintent,101);
        }catch (Exception e){
            Toast.makeText (this,"GPay unavailable", Toast.LENGTH_SHORT).show ();
            e.printStackTrace ();;
        }
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        if(requestCode==101){
            if(resultCode==RESULT_OK){
                if(data!=null){
                    String val = data.getStringExtra ("response");
                    ArrayList<String> list = new ArrayList<> ();
                    list.add(val);
                    getstatus(list.get(0));
                    Toast.makeText (this,"Payment Success", Toast.LENGTH_SHORT).show ();
                }
            }else{

                Toast.makeText (this,"Payment failed", Toast.LENGTH_SHORT).show ();
            }
        }
    }

    private void getstatus (String data) {
        boolean iscanceled=false;
        boolean isdone=false;
        boolean isdailed=false;
        String value[] = data.split ("&");
        for(int i =0;i<= value.length;i++)
        {
            String copy[] = value[i].split ("=");
            if(copy.length>=2){
                if(copy[0].toLowerCase ().equals ("status")){
                    isdone=true;
                }
            }else{
                iscanceled=true;
            }
        }
        if(isdone){
            Toast.makeText (this,"Payment Success", Toast.LENGTH_SHORT).show ();
        }else if(iscanceled){
            Toast.makeText (this,"Payment canceled", Toast.LENGTH_SHORT).show ();
        }else {
            Toast.makeText (this,"Payment failed", Toast.LENGTH_SHORT).show ();
        }
    }
}
