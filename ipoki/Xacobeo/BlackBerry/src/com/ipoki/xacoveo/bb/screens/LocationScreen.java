package com.ipoki.xacoveo.bb.screens;

import java.util.Vector;

import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.MapsArguments;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

import com.ipoki.xacoveo.bb.XacoVeoSettings;
import com.ipoki.xacoveo.bb.Friend;
import com.ipoki.xacoveo.bb.HttpRequestHelper;
import com.ipoki.xacoveo.bb.HttpRequester;
import com.ipoki.xacoveo.bb.LocationHandler;
import com.ipoki.xacoveo.bb.Utils;
import com.ipoki.xacoveo.bb.local.XacoveoLocalResource;

public class LocationScreen extends MainScreen implements FieldChangeListener, HttpRequester, XacoveoLocalResource {
	LabelField statusLabel;
	LabelField latitudeField;
	LabelField longitudeField;
	LabelField speedField;
	LabelField heightField;
	LabelField privacyLabel;
	LabelField recLabel;
	ButtonField connectButton;
	RadioButtonGroup recordGroup;
	RadioButtonField recordOnButton;
	RadioButtonField recordOffButton;
	RadioButtonGroup publicGroup;
	RadioButtonField publicOnButton;
	RadioButtonField publicOffButton;
	double longitude;
	double latitude;
	Friend[] friends;

	public LocationScreen() {
		super(NO_VERTICAL_SCROLL);

		String url = XacoVeoSettings.getFriendsUrl();
		
		HttpRequestHelper helper = new HttpRequestHelper(url, this);
		helper.start();

		final Bitmap logoBitmap = Bitmap.getBitmapResource("res/fondo.png");
		final int displayWidth = Display.getWidth();
		final int displayHeight = Display.getHeight();

		VerticalFieldManager mainManager = new VerticalFieldManager(
				VerticalFieldManager.USE_ALL_WIDTH | VerticalFieldManager.USE_ALL_HEIGHT | VerticalFieldManager.NO_VERTICAL_SCROLL){
		    // override pain method to create the background image
		    public void paint(Graphics graphics) {
		        // draw the background image
		        graphics.drawBitmap(0, 0, displayWidth, displayHeight, logoBitmap, 0, 0);
		        super.paint(graphics);
		    }            
		};

		statusLabel = new LabelField(XacoVeoSettings.XacoveoResource.getString(LOC_SCR_DISCONNECTED), Field.FIELD_HCENTER);
		mainManager.add(statusLabel);
		
		mainManager.add(new SeparatorField());

		VerticalFieldManager lonlatLabManager = new VerticalFieldManager();
		lonlatLabManager.add(new LabelField(XacoVeoSettings.XacoveoResource.getString(LOC_SCR_LATITUDE)));
		lonlatLabManager.add(new LabelField(XacoVeoSettings.XacoveoResource.getString(LOC_SCR_LONGITUDE)));
		
		VerticalFieldManager lonlatDataManager = new VerticalFieldManager();
		latitudeField = new LabelField("");
		latitudeField.setFont(getFont().derive(Font.ITALIC));
		longitudeField = new LabelField("");
		longitudeField.setFont(getFont().derive(Font.ITALIC));
		lonlatDataManager.add(latitudeField);
		lonlatDataManager.add(longitudeField);
		
		HorizontalFieldManager latlonManager = new HorizontalFieldManager();
		latlonManager.add(lonlatLabManager);
		latlonManager.add(lonlatDataManager);
		
		mainManager.add(latlonManager);
		
		mainManager.add(new SeparatorField());
		
		VerticalFieldManager shLabManager = new VerticalFieldManager();
		shLabManager.add(new LabelField(XacoVeoSettings.XacoveoResource.getString(LOC_SCR_SPEED)));
		shLabManager.add(new LabelField(XacoVeoSettings.XacoveoResource.getString(LOC_SCR_HEIGHT)));
		
		VerticalFieldManager shDataManager = new VerticalFieldManager();
		speedField = new LabelField("");
		heightField = new LabelField("");
		shDataManager.add(speedField);
		shDataManager.add(heightField);
		
		HorizontalFieldManager shManager = new HorizontalFieldManager();
		shManager.add(shLabManager);
		shManager.add(shDataManager);
		
		mainManager.add(shManager);

		mainManager.add(new SeparatorField());
		
		VerticalFieldManager recordManager = new VerticalFieldManager();
		recordGroup = new RadioButtonGroup();
		recordGroup.setChangeListener(this);
		recordOnButton = new RadioButtonField(XacoVeoSettings.XacoveoResource.getString(LOC_SCR_REC_ON), recordGroup, true);
		recordOffButton = new RadioButtonField(XacoVeoSettings.XacoveoResource.getString(LOC_SCR_REC_OFF), recordGroup, false);
		recordManager.add(recordOnButton);
		recordManager.add(recordOffButton);
		
		VerticalFieldManager publicManager = new VerticalFieldManager();
		publicGroup = new RadioButtonGroup();
		publicGroup.setChangeListener(this);
		publicOnButton = new RadioButtonField(XacoVeoSettings.XacoveoResource.getString(LOC_SCR_PUBLIC_POS), publicGroup, true);
		publicOffButton = new RadioButtonField(XacoVeoSettings.XacoveoResource.getString(LOC_SCR_PRIVATE_POS), publicGroup, false);
		publicManager.add(publicOnButton);
		publicManager.add(publicOffButton);
		
		HorizontalFieldManager radioManager = new HorizontalFieldManager();
		radioManager.add(recordManager);
		radioManager.add(publicManager);
		
		mainManager.add(radioManager);
		
		LocationHandler handler = new LocationHandler(this);
		handler.start();
		connectButton = new ButtonField(XacoVeoSettings.XacoveoResource.getString(LOC_SCR_BUT_CONNECT), ButtonField.CONSUME_CLICK);
		connectButton.setChangeListener(this);
		mainManager.add(connectButton);
		
		add(mainManager);
	}

	public void gettingLocation() {
		synchronized(UiApplication.getEventLock()) {
			latitudeField.setText(XacoVeoSettings.XacoveoResource.getString(LOC_SCR_GETTING_POS));
			longitudeField.setText(XacoVeoSettings.XacoveoResource.getString(LOC_SCR_GETTING_POS));
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
		menu.add(new MenuItem(XacoVeoSettings.XacoveoResource.getString(LOC_SCR_VIEW_MAP),10, 10) {
			public void run() {
				int latitude = (int)(100000 * LocationScreen.this.latitude);
				int longitude = (int)(100000 * LocationScreen.this.longitude);;
				String document = 
						"<lbs clear='ALL'><location-document>" +
							"<location lon='" + String.valueOf(longitude) + "' lat='" + String.valueOf(latitude) + "'" +
							" label='" + XacoVeoSettings.UserName + "' description='' zoom='4'/>" +
                "</location-document></lbs>";

				Invoke.invokeApplication(Invoke.APP_TYPE_MAPS,
                      new MapsArguments(
                      MapsArguments.ARG_LOCATION_DOCUMENT,document));
			}
		});
		menu.add(new MenuItem(XacoVeoSettings.XacoveoResource.getString(LOC_SCR_VIEW_FRIENDS),20, 10) {
			public void run() {
				UiApplication.getUiApplication().pushScreen(new FriendsListScreen(friends));
			}
		});
		menu.add(new MenuItem(XacoVeoSettings.XacoveoResource.getString(LOC_SCR_VIEW_CONF),30, 10) {
			public void run() {
				UiApplication.getUiApplication().pushScreen(new ConfigurationScreen());
			}
		});
		menu.add(new MenuItem(XacoVeoSettings.XacoveoResource.getString(LOC_SCR_VIEW_ABOUT),30, 10) {
			public void run() {
				UiApplication.getUiApplication().pushScreen(new AboutScreen());
			}
		});
	}
	
	public void fieldChanged(Field field, int context) {
		if (field == connectButton) {
			XacoVeoSettings.Lapse = 0;
			if (XacoVeoSettings.Connected) {
				XacoVeoSettings.Connected = false;
				statusLabel.setText(XacoVeoSettings.XacoveoResource.getString(LOC_SCR_DISCONNECTED));
				connectButton.setLabel(XacoVeoSettings.XacoveoResource.getString(LOC_SCR_BUT_CONNECT));
			}
			else {
				XacoVeoSettings.Connected = true;
				statusLabel.setText(XacoVeoSettings.XacoveoResource.getString(LOC_SCR_CONNECTED));
				connectButton.setLabel(XacoVeoSettings.XacoveoResource.getString(LOC_SCR_BUT_DISCONNECT));
			}
		}
		else if (field == publicOnButton) {
			String url = XacoVeoSettings.getPublicOnUrl();
			HttpRequestHelper helper = new HttpRequestHelper(url, this);
			helper.start();
		}
		else if (field == publicOffButton) {
			String url = XacoVeoSettings.getPublicOffUrl();
			HttpRequestHelper helper = new HttpRequestHelper(url, this);
			helper.start();
		}
		else if (field == recordOnButton) {
			String url = XacoVeoSettings.getRecordOnUrl();
			HttpRequestHelper helper = new HttpRequestHelper(url, this);
			helper.start();
		}
		else if (field == recordOffButton) {
			String url = XacoVeoSettings.getRecordOffUrl();
			HttpRequestHelper helper = new HttpRequestHelper(url, this);
			helper.start();
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
