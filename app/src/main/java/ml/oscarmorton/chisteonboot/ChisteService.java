package ml.oscarmorton.chisteonboot;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.LongDef;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class ChisteService extends Service {

    public static final int CHISTE_CODE = 101;
    public static final int CODE_END = 1;
    private final String CHANNEL_ID = "id";
    public Chiste chiste;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d("testing_chiste_service", " Servicio working ");


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        final ResultReceiver receiver = intent.getParcelableExtra("receiver");

        // This thread parsed the xml and sends the bundle
        new Thread(new Runnable() {
            @Override
            public void run() {
                chiste = new Chiste();
                String text;
                try {
                    text = "";


                    // Getting the input stream from the url
                    URL url = new URL("http://chistes.germangascon.com/aleatorio.php");
                    URLConnection urlConnection = url.openConnection();
                    InputStream stream = urlConnection.getInputStream();
                    Log.d("testing_chiste_service", "testing URL");

                    // Making the xml parser from the input stream
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    XmlPullParser parser = factory.newPullParser();
                    parser.setInput(stream, null);

                    int eventType = parser.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        String tagname = parser.getName();

                        switch (eventType) {

                            case XmlPullParser.START_TAG:
                                if (tagname.equalsIgnoreCase("chiste")) {
                                    // create a new instance of employee
                                    chiste = new Chiste();
                                }
                                break;

                            case XmlPullParser.TEXT:

                                text = parser.getText();
                                break;
                            case XmlPullParser.END_TAG:
                                if (tagname.equalsIgnoreCase("texto")) {
                                    chiste.setTexto(text);

                                    Log.d("testing_chiste_service", "Joke found" + chiste.getTexto());
                                }
                                break;
                            default:
                                break;
                        }
                        // next tag of the XML
                        eventType = parser.next();

                    }

                } catch (IOException | XmlPullParserException e) {
                    Log.d("testing_chiste_service", e.getMessage());

                }
                // Making the bundle to send the joke
                Bundle bundle = new Bundle();
                bundle.putString("chiste", chiste.getTexto());

                // sending the bundle
                receiver.send(CHISTE_CODE, bundle);

                // After finding the joke, I make the notification
                showNotification();

            }

        }).start();


        return Service.START_NOT_STICKY;
    }


    /**
     * Shows the notification
     */
    private void showNotification() {
        makeNotificationChannel();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(getBaseContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Chiste del dia")
                .setContentText(chiste.getTexto())
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .build();
        notificationManager.notify(0, notification);


    }


    private void makeNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence nombre = "My Channel";
            String description = "Mi channel description ";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, nombre, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}
