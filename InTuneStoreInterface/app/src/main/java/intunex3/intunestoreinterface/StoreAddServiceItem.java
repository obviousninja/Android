package intunex3.intunestoreinterface;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Randy on 10/15/2015.
 */
public class StoreAddServiceItem extends AppCompatActivity{

    static final int ADDSERVICEOPTIONREQUES = 10;
    Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_add_service_item);
        mContext= getApplicationContext();

        View nextSlideButton = findViewById(R.id.store_item_next);
        nextSlideButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent serviceOptionIntent = new Intent(mContext, StoreAddServiceOption.class);

                //getting service name and put it in intent extra
                EditText service_name_string = (EditText) findViewById(R.id.store_service_item_name);
                String serviceName = service_name_string.getText().toString();

                EditText service_descr_string = (EditText) findViewById(R.id.store_service_item_desc);
                String serviceDesc = service_descr_string.getText().toString();

                EditText service_price_string = (EditText) findViewById(R.id.store_service_item_price);
                String serviceprice = service_price_string.getText().toString();

                EditText service_duration_string = (EditText) findViewById(R.id.store_add_service_item_service_duration);
                String serviceDuration = service_duration_string.getText().toString();

                //check if any of them are null, none of them are to be null
                if(serviceName == null || serviceName.compareTo("") == 0|| serviceDesc == null || serviceDesc.compareTo("") == 0 || serviceprice == null || serviceprice.compareTo("") == 0 ){
                    //if any of those are true
                    Toast.makeText(mContext, "All Fields Must Not Be Empty", Toast.LENGTH_SHORT).show();
                    return;
                }


                serviceOptionIntent.putExtra(getString(R.string.service_name), serviceName);
                serviceOptionIntent.putExtra(getString(R.string.service_description), serviceDesc);
                serviceOptionIntent.putExtra(getString(R.string.service_price), serviceprice);
                serviceOptionIntent.putExtra(getString(R.string.service_duration), serviceDuration);
                startActivityForResult(serviceOptionIntent, ADDSERVICEOPTIONREQUES);

            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ADDSERVICEOPTIONREQUES:
                //resultcode is the same as the requestcode, we designate this as finish()
                if(resultCode== 10){
                    finish();
                    break;
                }

                break;
            default:
                break;

        }
    }
}
