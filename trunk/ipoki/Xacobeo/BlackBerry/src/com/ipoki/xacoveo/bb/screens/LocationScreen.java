package com.ipoki.xacoveo.bb.screens;

import java.util.Vector;

import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.MapsArguments;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;

import com.ipoki.xacoveo.bb.EPeregrinoSettings;
import com.ipoki.xacoveo.bb.Friend;
import com.ipoki.xacoveo.bb.HttpRequestHelper;
import com.ipoki.xacoveo.bb.HttpRequester;
import com.ipoki.xacoveo.bb.LocationHandler;
import com.ipoki.xacoveo.bb.Utils;

public class LocationScreen extends MainScreen implements FieldChangeListener, HttpRequester {
	LabelField statusLabel;
	EditField latitudeField;
	EditField longitudeField;
	EditField speedField;
	EditField heightField;
	LabelField privacyLabel;
	LabelField recLabel;
	ButtonField connectButton;
	double longitude;
	double latitude;
	Friend[] friends;

	public LocationScreen() {
		String url = "http://www.ipoki.com/myfriends2.php?iduser=" + EPeregrinoSettings.UserKey;
		HttpRequestHelper helper = new HttpRequestHelper(url, this);
		helper.start();

		
		statusLabel = new LabelField("Disconnected");
		add(statusLabel);
		
		add(new SeparatorField());
		
		latitudeField = new EditField("Latitude: ", "");
		latitudeField.setEditable(false);
		longitudeField = new EditField("Longitude: ", "");
		longitudeField.setEditable(false);
		add(latitudeField);
		add(longitudeField);
		
		add(new SeparatorField());
		
		speedField = new EditField("Speed: ", "");
		speedField.setEditable(false);
		heightField = new EditField("Height: ", "");
		heightField.setEditable(false);
		add(speedField);
		add(heightField);

		add(new SeparatorField());
		
		privacyLabel = new LabelField("Posici�n p�blica");
		recLabel = new LabelField("Guardando ruta");
		add(privacyLabel);
		add(recLabel);

		connectButton = new ButtonField("Conectar", ButtonField.CONSUME_CLICK);
		connectButton.setChangeListener(this);
		add(connectButton);
		
		LocationHandler handler = new LocationHandler(this);
		handler.start();
	}

	public void gettingLocation() {
		synchronized(UiApplication.getEventLock()) {
			latitudeField.setText("Obteniendo posici�n...");
			longitudeField.setText("Obteniendo posici�n...");
		}
	}
	
	public void setLocationData(double longitude, double latitude, float speed, float height) {
		synchronized(UiApplication.getEventLock()) {
			this.longitude = longitude;
			this.latitude = latitude;
			latitudeField.setText(Double.toString(latitude));
			longitudeField.setText(Double.toString(longitude));
			speedField.setText(Float.toString(speed));
			heightField.setText(Float.toString(height));
		}
	}
	
	protected void makeMenu(Menu menu, int instance) {
		super.makeMenu(menu, instance);
		menu.add(new MenuItem("Ver mapa",10, 10) {
			public void run() {
				int latitude = (int)(100000 * LocationScreen.this.latitude);
				int longitude = (int)(100000 * LocationScreen.this.longitude);;
				String document = 
						"<lbs clear='ALL'><location-document>" +
							"<location lon='" + String.valueOf(longitude) + "' lat='" + String.valueOf(latitude) + "'" +
							" label='" + EPeregrinoSettings.UserName + "' description='' zoom='4'/>" +
                "</location-document></lbs>";

				Invoke.invokeApplication(Invoke.APP_TYPE_MAPS,
                      new MapsArguments(
                      MapsArguments.ARG_LOCATION_DOCUMENT,document));
			}
		});
		menu.add(new MenuItem("Ver amigos",20, 10) {
			public void run() {
				UiApplication.getUiApplication().pushScreen(new FriendsListScreen(friends));
			}
		});
		menu.add(new MenuItem("Configuraci�n",30, 10) {
			public void run() {
				UiApplication.getUiApplication().pushScreen(new ConfigurationScreen());
			}
		});
		menu.add(new MenuItem("Acerca de XacoVeo",30, 10) {
			public void run() {
				UiApplication.getUiApplication().pushScreen(new AboutScreen());
			}
		});
	}
	
	public void fieldChanged(Field field, int context) {
		if (field == connectButton) {
			EPeregrinoSettings.Lapse = 0;
			if (EPeregrinoSettings.Connected) {
				EPeregrinoSettings.Connected = false;
				statusLabel.setText("Desconectado");
				connectButton.setLabel("Conectar");
			}
			else {
				EPeregrinoSettings.Connected = true;
				statusLabel.setText("Conectado");
				connectButton.setLabel("Desconectar");
			}
		}
	}

	public void requestFailed(String message) {
		// TODO Auto-generated method stub
		
	}

	public void requestSucceeded(byte[] result, String contentType) {
		String message = new String(result, 0, result.length);
		Vector tokens = Utils.parseMessage(message);
		
    	int friendsNum = tokens.size() / 6;
    	Friend[] friends = new Friend[friendsNum];
    	for (int i = 0; i < friendsNum; i++) {
    		friends[i] = new Friend(String.valueOf(tokens.elementAt(6 * i + 1)), 
    				String.valueOf(tokens.elementAt(6 * i + 2)), 
    				String.valueOf(tokens.elementAt(6 * i + 3)), 
    				String.valueOf(tokens.elementAt(6 * i + 5)));
    	}
    	
    	this.friends = friends;
	}
}
