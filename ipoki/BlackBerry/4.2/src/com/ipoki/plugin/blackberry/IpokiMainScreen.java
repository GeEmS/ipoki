/*
 * MainScreen.java
 *
 * � <your company here>, 2003-2007
 * Confidential and proprietary.
 */

package com.ipoki.plugin.blackberry;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.i18n.*;
import com.ipoki.plugin.blackberry.resource.*;

final class IpokiMainScreen extends MainScreen implements IpokiResource
{
    static ResourceBundle _resources = ResourceBundle.getBundle(BUNDLE_ID, BUNDLE_NAME);
    
    Ipoki _app; 
    

    public IpokiMainScreen()
    {
        super(DEFAULT_MENU | DEFAULT_CLOSE);
        setTitle(_resources.getString(APP_TITLE));
        _app = (Ipoki)UiApplication.getUiApplication();
        
        
        _app._lblUser = new LabelField(_resources.getString(LBL_USER) + ": " + Ipoki._user);
        int fontSize = _app._lblUser.getFont().getHeight() * 3 / 4;
        Font font = _app._lblUser.getFont();
        Font newFont = font.derive(Font.BOLD);
        _app._lblUser.setFont(newFont);
        add(_app._lblUser);
        
        _app._lblStatus = new LabelField(_resources.getString(LBL_DISCONNECTED));
        add(_app._lblStatus);
        
        SeparatorField sep = new SeparatorField();
        add(sep);
        
        LabelField lblTextLongitude = new LabelField(_resources.getString(LBL_LONGITUDE));
        font = lblTextLongitude.getFont();
        newFont = font.derive(Font.ITALIC, fontSize);
        lblTextLongitude.setFont(newFont);
        add(lblTextLongitude);
        
        _app._lblLongitude = new LabelField(_resources.getString(LBL_NOLOC));
        font = _app._lblLongitude.getFont();
        newFont = font.derive(Font.BOLD, fontSize);
        _app._lblLongitude.setFont(newFont);
        add(_app._lblLongitude);

        LabelField lblTextLatitude = new LabelField(_resources.getString(LBL_LATITUDE));
        font = lblTextLatitude.getFont();
        newFont = font.derive(Font.ITALIC, fontSize);
        lblTextLatitude.setFont(newFont);
        add(lblTextLatitude);
        
        _app._lblLatitude = new LabelField(_resources.getString(LBL_NOLOC));
        font = _app._lblLatitude.getFont();
        newFont = font.derive(Font.BOLD, fontSize);
        _app._lblLatitude.setFont(newFont);
        add(_app._lblLatitude);

        LabelField lblTextAltSpeed = new LabelField(_resources.getString(LBL_ALTSPEED));
        font = lblTextAltSpeed.getFont();
        newFont = font.derive(Font.ITALIC, fontSize);
        lblTextAltSpeed.setFont(newFont);
        add(lblTextAltSpeed);
        
        _app._lblAltSpeed = new LabelField(_resources.getString(LBL_NODATA));
        font = _app._lblAltSpeed.getFont();
        newFont = font.derive(Font.BOLD, fontSize);
        _app._lblAltSpeed.setFont(newFont);
        add(_app._lblAltSpeed);

        LabelField lblTextCommentSent = new LabelField(_resources.getString(LBL_MESSAGE_RECEIVED));
        font = lblTextCommentSent.getFont();
        newFont = font.derive(Font.ITALIC, fontSize);
        lblTextCommentSent.setFont(newFont);
        add(lblTextCommentSent);
        
        _app._lblCommentSent = new LabelField("");
        font = _app._lblCommentSent.getFont();
        newFont = font.derive(Font.BOLD, fontSize);
        _app._lblCommentSent.setFont(newFont);
        add(_app._lblCommentSent);
        
        NullField nf = new NullField(Field.FOCUSABLE);
        add(nf);
    }
    
    public void makeMenu(Menu menu, int instance)
    {
        menu.add(invokeConnect);
        menu.add(invokeDisconnect);
        menu.addSeparator();
        menu.add(invokeSendMessage);
        menu.add(invokeMap);
        //menu.add(invokeFriends);
        //menu.addSeparator();
        //menu.add(invokePositionLog);
        menu.add(invokeOptions);
        menu.add(invokeAbout);
        menu.addSeparator();
        menu.add(invokeClose);
    }
    
    MenuItem invokeConnect  = new MenuItem(_resources.getString(MNU_CONNECT),0,0)
    {
        public void run()
        {
            _app.connect();
            
        }        
    };    
    
    MenuItem invokeDisconnect  = new MenuItem(_resources.getString(MNU_DISCONNECT),0,0)
    {
        public void run()
        {
            _app.disconnect();
        }        
    };    

    MenuItem invokeSendMessage  = new MenuItem(_resources.getString(MNU_MESSAGE),0,0)
    {
        public void run()
        {
            _app.sendMessage();
        }        
    };    

    MenuItem invokeMap  = new MenuItem(_resources.getString(MNU_MAP),0,0)
    {
        public void run()
        {
            if (Ipoki._isConnected)
                _app.showMap();
        }        
    };    

    MenuItem invokeFriends  = new MenuItem(_resources.getString(MNU_FRIENDS),0,0)
    {
        public void run()
        {
        }        
    };    

    MenuItem invokePositionLog  = new MenuItem(_resources.getString(MNU_POSITION_LOG_ON),0,0)
    {
        public void run()
        {
        }        
    };    

    MenuItem invokeOptions  = new MenuItem(_resources.getString(MNU_OPTIONS),0,0)
    {
        public void run()
        {
            _app.viewOptions();
        }        
    };    
    
    MenuItem invokeAbout  = new MenuItem(_resources.getString(MNU_ABOUT),0,0)
    {
        public void run()
        {
            _app.showAbout();
        }        
    };    

    MenuItem invokeClose = new MenuItem(_resources.getString(MNU_CLOSE),0,0)
    {
        public void run()
        {
        	_app._connectionThread.stop();
        	_app._listenThread.stop();
            onClose();
        }        
    };    
}

