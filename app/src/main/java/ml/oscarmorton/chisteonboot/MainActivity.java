package ml.oscarmorton.chisteonboot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private ResultReceiver receiver;


    private TextView tvChiste;

    private String chiste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        receiver = new MiResultReceiver(null);

        tvChiste = findViewById(R.id.tvChiste);

        chiste = "CHISTE";


        Intent serviceIntent = new Intent(this, ChisteService.class);
        serviceIntent.putExtra("receiver", receiver);

        startService(serviceIntent);




    }





    /**
     * This class receives information from the service
     */
    public class MiResultReceiver extends ResultReceiver {

        public MiResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            switch (resultCode) {


                case ChisteService.CHISTE_CODE:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvChiste.setText(resultData.getString("chiste", "ERROR"));
                            chiste = resultData.getString("chiste");


                        }
                    });
                    break;
                case ChisteService.CODE_END:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });

                    break;
            }
        }
    }


}