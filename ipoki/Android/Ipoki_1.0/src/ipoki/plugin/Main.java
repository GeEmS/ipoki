package ipoki.plugin;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity {
	
	private LocationManager mLocationManager=null;
	private LocationListener mLocationListener=null;
	static double mLatitude;
	static double mLongitude;
	static float mSpeed;
	static double mHigh;
	static double mTo;
	
	private ProgressDialog pd;
	
	public TextView outlat;
	public TextView outlon;
	public TextView outspeed;
	public TextView outhigh;
	
	public boolean mOn = false;
	static String mUserKey = null;
	private String mServer = "http://www.ipoki.com";
	private String mVer = "AND-1.0";
	
	private double oldLatitude = 0;
	private double oldLongitude = 0;
	private long lastTime = 0;
	
	private String mUser = null;
	private String mPass = null;
	private String mIntervalo = null;
	
	private String wprivate = "0";
	private String wrec = "0";

	/* ARRANCAR el MAIN */

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
		pd = ProgressDialog.show(this, "Ipoki for Android", "Waiting GPS signal");
		
	    // los textbox
        outlat = (TextView) findViewById(R.id.txtlat);
        outlon = (TextView) findViewById(R.id.txtlon);
        outspeed = (TextView) findViewById(R.id.txtspeed);
        outhigh = (TextView) findViewById(R.id.txth);
        
        // los oidores de los botones
        ImageView btna = (ImageView) findViewById(R.id.img00);
        btna.setOnClickListener(pulsabtna);
        ImageView btnb = (ImageView) findViewById(R.id.img01);
        btnb.setOnClickListener(pulsabtnb);
        ImageView redbtnon = (ImageView) findViewById(R.id.img20);
        redbtnon.setOnClickListener(pulsaredbtnon);
        ImageView redbtnoff = (ImageView) findViewById(R.id.img21);
        redbtnoff.setOnClickListener(pulsaredbtnoff);
        ImageView btnon = (ImageView) findViewById(R.id.img30);
        btnon.setOnTouchListener (tocabtnon);
        btnon.setOnClickListener(pulsabtnon);
        ImageView btnoff = (ImageView) findViewById(R.id.img31);
        btnoff.setOnClickListener(pulsabtnoff);
        
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationListener = new MyLocationListener();
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 1, mLocationListener);
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 100, mLocationListener);

		// leer preferencias la primera vez
		PreferenceManager.setDefaultValues(this, R.xml.setup, false);
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
		mUser = p.getString("user", "value_default_98732");
		// si no hay user grabado, va a preferencias para pedirlo
		if (mUser.equals("value_default_98732")) {
			ShowWelcome();
		}
     }
	
	 public void ShowWelcome() {
		AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
 	    builder.setTitle("Welcome to Ipoki");
 	    builder.setIcon(R.drawable.icon);
 	    builder.setMessage("Welcome to Ipoki service.\n" + 
 	    		"To use the program and share your position you need a Ipoki account.\n" +
 	    		"Visit our web site in: \n\n" +
 	    		"http://www.ipoki.com\n\n" + 
 	    		"and sign up, it's free!!");
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // va al setup
            	ShowSetup();
            }
        });
        builder.show();
	}

	 public void ShowLoc() {    	 
/*    	 // oculta el mensaje inicial,
		 if (pd.isShowing()) pd.hide();

      	 // pintar la posicion, velocidad y altura
    	 String work = new String();
    	 work = String.valueOf(mLongitude);
    	 outlon.setText(work.substring(0,9));
    	 work = String.valueOf(mLatitude);
    	 outlat.setText(work.substring(0,9));
    	 work = String.format("%.1f",mSpeed*3.6);
    	 outspeed.setText(work);
    	 work = String.format("%.1f",mHigh);
    	 outhigh.setText(work);
    	 
    	 if (mOn) {
    		// si esta conectado se manda la posicion 
    		// solo si ha pasado el intervalo correspondiente (MINIMO=3)
    		int iwork = 0;
 			try {
 	    		iwork = Integer.parseInt(mIntervalo);
			} catch (Exception e) {
				//e.printStackTrace();
			}
    		if (iwork < 3) iwork = 3;
    		long mNow = System.currentTimeMillis(); 
    		if ((mNow - lastTime) > (iwork * 1000)) {
        	 	SendLoc();
        	 	lastTime = System.currentTimeMillis();
    		}
    	 }*/
     }

     public void SendLoc() {
    	// controla si ha habido cambio de posicion
    	String mChange = "0"; 
 	 	if (oldLatitude != mLatitude) {
 	 		mChange = "1";
 	 	}
 	 	if (oldLongitude != mLongitude) {
 	 		mChange="1";
 	 	}
    	Comunica(mServer + "/ear.php?iduser=" + mUserKey + "&lat=" + String.valueOf(mLatitude).substring(0,10) + "&lon=" + String.valueOf(mLongitude).substring(0,10) + "&h=" + String.valueOf(mHigh) + "&speed=" + String.valueOf(mSpeed*3.6) + "&to=" + String.valueOf(mTo) + "&comment=&action=0&change=" + mChange);
	 	oldLatitude = mLatitude;
	 	oldLongitude = mLongitude;
     }
    
     private void PowerOn(){
    	String work = new String();
    	String[] resultData = null;

		// leer preferencias 
		PreferenceManager.setDefaultValues(this, R.xml.setup, false);
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
		mUser = p.getString("user", "value_default_98732");
    	mPass = p.getString("pass", "value_default");
    	mIntervalo = p.getString("intervalo", "3");

    	// hace la llamada a la web
	 	work = Comunica(mServer + "/signin.php?user=" + mUser + "&pass=" + mPass + "&ver=" + mVer);
	 	if (work.substring(0,14).equals("CODIGO$$$ERROR")) {
	 		// Log.w("Ipoki", "Error: " + work);
	 	    AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
	 	    builder.setTitle("Ipoki Connection");
	 	    builder.setIcon(R.drawable.alert_dialog_icon);
	 	    builder.setMessage("The user or password is not ok, modify the configuration");
	        builder.show();
	 	} else {
	    	resultData = work.substring(3).split("\\${3}");
	    	if (resultData.length != 0) {
		 		mUserKey = resultData[1];
		 		mServer = resultData[2];
		 		wrec = resultData[4];
		 		wprivate = resultData[5];
	    		//Log.w("Ipoki", work + " <---> " + mUserKey + mServer + " WR:" + wrec + " WP:" + wprivate);
		 		mOn = true;
	    	}
	 	}
     }

     private void PowerOff(){
    	// hace la llamada a la web
      	Comunica(mServer + "/signout.php?iduser=" + mUserKey);
      	mOn = false;
     }
     
     private void SetPublic() {
 	    // hace la llamada a la web
    	Comunica(mServer + "/set_p_off.php?iduser=" + mUserKey);
     }

     private void SetPrivate() {
  	    // hace la llamada a la web
     	Comunica(mServer + "/set_p_on.php?iduser=" + mUserKey);
     }

     private void ActiveRec(){
  	    // hace la llamada a la web
     	Comunica(mServer + "/set_h_on.php?iduser=" + mUserKey);
     }

     private void InactiveRec(){
  	    // hace la llamada a la web
     	Comunica(mServer + "/set_h_off.php?iduser=" + mUserKey);
     }

     public void ShowExit() {
    	 // si esta conectado pregunta si se borra la posicion
    	 if (mOn){
    		PideFin();
    	 } else {
   	    	// Acabar
    		mLocationManager.removeUpdates(mLocationListener);
    		finish();
    	 }
     }

     public void PideFin() {
    	 // si esta conectado pregunta si se borra la posicion
 	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
 	    builder.setTitle("Ipoki Connection");
 	    builder.setMessage("You are connected.\n" +
 	    		"Do you want to keep the position on Ipoki after exit?");
 	    builder.setIcon(R.drawable.alert_dialog_icon);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
       	    	// Acabar
        		mLocationManager.removeUpdates(mLocationListener);
            	finish();
            }
        });
 	   builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
         	    // hace la llamada a la web
        		Comunica(mServer + "/signout.php?iduser=" + mUserKey);      		
       	    	// Acabar
        		mLocationManager.removeUpdates(mLocationListener);
            	finish();
            }
        });         
        builder.show();
     }

     public void ShowMap() {
     	// Muestra el mapa del usuario
     	Intent intent = new Intent(); 
    	intent.setClass(Main.this, Mymap.class); 
    	startActivity(intent); 
     }
     
     public void ShowFriends() {
    	 if (!mOn){
 	 	    AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
	 	    builder.setTitle("Ipoki Connection");
	 	    builder.setIcon(R.drawable.alert_dialog_icon);
	 	    builder.setMessage("You are not connected.");
	        builder.show();
     	 } else {
	     	// Muestra los amigos
			Toast.makeText(getBaseContext(), 
					   "Loading friends...", 
					   Toast.LENGTH_LONG).show();
	      	Intent intent = new Intent(); 
	     	intent.setClass(Main.this, FriendsView.class); 
	     	startActivity(intent);
     	 }
     }

     public void ShowSetup() {
     	// La configuracion
         Intent launchPreferencesIntent = new Intent().setClass(this, Setup.class);
         startActivityForResult(launchPreferencesIntent, 1);
     }
    
     public class LoadSetup extends PreferenceActivity {

    	    @Override
    	    protected void onCreate(Bundle savedInstanceState) {
    	        super.onCreate(savedInstanceState);

    	        // Load the preferences from an XML resource
    	        addPreferencesFromResource(R.xml.setup);
    	    }
    	}
     
    private OnClickListener pulsabtna = new OnClickListener(){

		@Override
		public void onClick(View arg0) {
			Toast.makeText(getBaseContext(), 
					   "Set private position", 
					   Toast.LENGTH_LONG).show();
			
			ImageView image = (ImageView)findViewById(R.id.img00); 
		    image.setVisibility(View.INVISIBLE); 
		    image = (ImageView)findViewById(R.id.img01); 
		    image.setVisibility(View.VISIBLE); 

		    // llama a SetPrivate
		    SetPrivate();
			}
		};

    private OnClickListener pulsabtnb = new OnClickListener(){

		@Override
		public void onClick(View arg0) {
			Toast.makeText(getBaseContext(), 
					   "Set public position", 
					   Toast.LENGTH_LONG).show();
			
			ImageView image = (ImageView)findViewById(R.id.img01); 
		    image.setVisibility(View.INVISIBLE); 
		    image = (ImageView)findViewById(R.id.img00); 
		    image.setVisibility(View.VISIBLE);
		    
		    // llama a SetPublic
		    SetPublic();
		}
    };

    private OnClickListener pulsaredbtnon = new OnClickListener(){

		@Override
		public void onClick(View arg0) {
			Toast.makeText(getBaseContext(), 
					   "Track route on", 
					   Toast.LENGTH_LONG).show();
			
			ImageView image = (ImageView)findViewById(R.id.img20); 
		    image.setVisibility(View.INVISIBLE); 
		    image = (ImageView)findViewById(R.id.img21); 
		    image.setVisibility(View.VISIBLE); 
		    image = (ImageView)findViewById(R.id.img11); 
		    image.setVisibility(View.VISIBLE); 
		    image = (ImageView)findViewById(R.id.img10); 
		    image.setVisibility(View.INVISIBLE);
		    
		    //llamar a ActiveRec
		    ActiveRec();
		}
    };

    private OnClickListener pulsaredbtnoff = new OnClickListener(){

		@Override
		public void onClick(View arg0) {
			Toast.makeText(getBaseContext(), 
						"Track route off", 
					   Toast.LENGTH_LONG).show();

			ImageView image = (ImageView)findViewById(R.id.img21); 
		    image.setVisibility(View.INVISIBLE); 
		    image = (ImageView)findViewById(R.id.img20); 
		    image.setVisibility(View.VISIBLE); 
		    image = (ImageView)findViewById(R.id.img10); 
		    image.setVisibility(View.VISIBLE); 
		    image = (ImageView)findViewById(R.id.img11); 
		    image.setVisibility(View.INVISIBLE); 

		    // llamar a InactiveRec
		    InactiveRec();
		}
    };

    private OnTouchListener tocabtnon = new OnTouchListener(){
    	
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// hace que esto salga inmediatamente al pulsar !!!
			ImageView image = (ImageView)findViewById(R.id.img30); 
		    image.setVisibility(View.INVISIBLE); 
		    image = (ImageView)findViewById(R.id.img31); 
		    image.setVisibility(View.VISIBLE);
		    return false;
		}
    };

    private OnClickListener pulsabtnon = new OnClickListener(){
    	
		@Override
		public void onClick(View arg0) {
			// mensaje      
			Toast.makeText(getBaseContext(), 
					   "Connecting to server", 
					   Toast.LENGTH_LONG).show();

			// llamar a PowerOn
		    PowerOn();
		    PonerBotones();
		}
    };

    private OnClickListener pulsabtnoff = new OnClickListener(){

		@Override
		public void onClick(View arg0) {
			Toast.makeText(getBaseContext(), 
					   "Disconnecting", 
					   Toast.LENGTH_LONG).show();
			ImageView image = (ImageView)findViewById(R.id.img31); 
		    image.setVisibility(View.INVISIBLE); 
		    image = (ImageView)findViewById(R.id.img30); 
		    image.setVisibility(View.VISIBLE);
		    
		    // llama a PowerOff
	        PowerOff();		    
		    QuitarBotones();
		}
    };

    private void QuitarBotones(){
    	TextView lab = (TextView)findViewById(R.id.lab3); 
    	lab.setVisibility(View.INVISIBLE); 
    	lab = (TextView)findViewById(R.id.lab1); 
    	lab.setVisibility(View.INVISIBLE); 
    	lab = (TextView)findViewById(R.id.lab2); 
    	lab.setVisibility(View.INVISIBLE); 

    	ImageView image = (ImageView)findViewById(R.id.img00); 
	    image.setVisibility(View.INVISIBLE); 
	    image = (ImageView)findViewById(R.id.img01); 
	    image.setVisibility(View.INVISIBLE); 
		image = (ImageView)findViewById(R.id.img20); 
	    image.setVisibility(View.INVISIBLE); 
	    image = (ImageView)findViewById(R.id.img21); 
	    image.setVisibility(View.INVISIBLE); 
	    image = (ImageView)findViewById(R.id.img11); 
	    image.setVisibility(View.INVISIBLE); 
	    image = (ImageView)findViewById(R.id.img10); 
	    image.setVisibility(View.INVISIBLE);
    }
    
    private void PonerBotones(){
    	TextView lab = (TextView)findViewById(R.id.lab3); 
    	lab.setVisibility(View.VISIBLE); 
    	lab = (TextView)findViewById(R.id.labtit); 
    	lab.setVisibility(View.INVISIBLE); 
    	lab = (TextView)findViewById(R.id.lab1); 
    	lab.setVisibility(View.VISIBLE); 
    	lab = (TextView)findViewById(R.id.lab2); 
    	lab.setVisibility(View.VISIBLE); 

    	// activar los controles de REC y Privado segun la configuracion
 		if (wrec.equals("2")) {
			ImageView image = (ImageView)findViewById(R.id.img11); 
		    image.setVisibility(View.VISIBLE); 
		    image = (ImageView)findViewById(R.id.img21); 
		    image.setVisibility(View.VISIBLE);
 		} else {
 			ImageView image = (ImageView)findViewById(R.id.img10); 
 		    image.setVisibility(View.VISIBLE); 
 		    image = (ImageView)findViewById(R.id.img20); 
 		    image.setVisibility(View.VISIBLE); 
 		}
 		if (wprivate.equals("2")) {
			ImageView image = (ImageView)findViewById(R.id.img00); 
		    image.setVisibility(View.VISIBLE);
 		} else {
 	    	ImageView image = (ImageView)findViewById(R.id.img01); 
 		    image.setVisibility(View.VISIBLE); 
 		}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
        
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mymap:
                //mymap            	
            	ShowMap();
            	break;            
            case R.id.myfriends:
                //myfriends
            	ShowFriends();
            	break;            
            case R.id.setup:
                //settings
            	ShowSetup();
            	break;            
            case R.id.exit:
                //exit
            	ShowExit();
            	break;            
        }
        return false;
    }

    private class MyLocationListener implements LocationListener {
    	
		@Override
		public void onLocationChanged(Location location) {
			if (location != null) {
				mLatitude = location.getLatitude();
				mLongitude = location.getLongitude();
				mSpeed = location.getSpeed();
				mHigh = location.getAltitude();
				mTo = location.getBearing();
				// lo pinta
				ShowLoc();
			}			
		}
	
		@Override
		public void onProviderDisabled(String provider) {
		}
	
		@Override
		public void onProviderEnabled(String provider) {
		}
	
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}
        
    private String Comunica(String userURL){
    	String resultado=null;
    	URL url;
		try {
			url = new URL(userURL);
    		HttpURLConnection urlConnection = null;
			try {
				urlConnection = (HttpURLConnection)url.openConnection();
			} catch (IOException e) {
				//e.printStackTrace();
			}
    		int responseCode = 0;
			try {
				responseCode = urlConnection.getResponseCode();
			} catch (IOException e) {
				//e.printStackTrace();
			}
    		if (responseCode == HttpURLConnection.HTTP_OK) {
    			InputStream is = null;
				try {
					is = urlConnection.getInputStream();
				} catch (IOException e) {
					//e.printStackTrace();
				}
    			BufferedReader br = new BufferedReader(new InputStreamReader(is));
    	    	try {
    	    		// coge el resultado de la llamada pero no hace nada.
					resultado = br.readLine();
				} catch (IOException e) {
					//e.printStackTrace();
				}
    		}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} 
		return resultado;
    }

    @Override
    protected void onDestroy() {
    	super.onDestroy();
		mLocationManager.removeUpdates(mLocationListener);
    }    
}