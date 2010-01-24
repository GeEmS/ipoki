package com.ipoki.xacoveo.javame.web;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Image;

import com.ipoki.xacoveo.javame.utils.State;
import com.ipoki.xacoveo.javame.utils.XacoveoAlert;

public class XacoveoWeb {
	private static final String urlConnect = "http://www.ipoki.com/alta.php";
	private static final String urlDisconnect = "http://www.ipoki.com/baja.php";
	private static final String urlPosition = "http://www.ipoki.com/oreja.php";

	public static int sendWebReg(String user, String pass) throws IOException
	{
		String url = urlConnect + "?user="+user+"&pass="+pass;
		String message = sendWebRequestString(url);
		int result = WebResult.UNKNOWN_MESSAGE_TYPE;

		String[] messages = parseMessage(message);

		if (messages.length == 0)
			return WebResult.BAD_RESPONSE;

		String messageType = messages[0];

		// Si el tipo es CODIGO, obtenemos el IdSeguro
		if (messageType.equals("CODIGO"))
		{
			State.secureId = messages[1];
			System.out.println("WebReg CODIGO: " + State.secureId);
			if (State.secureId.equals("ERROR"))
			{
				return WebResult.CODE_ERROR;
			}
			State.connected = true;
			result = WebResult.CODE_OK;
		}

		if (messageType.equals("AVISO"))
		{
			System.out.println("WebReg AVIS: " + messages[1]);
			if (messages.length != 8)
			{
				return WebResult.ALERT_ERROR;
			}

			XacoveoAlert.Text = messages[1];
			XacoveoAlert.Url = messages[2];
			XacoveoAlert.Latitude = messages[3];
			XacoveoAlert.Longitude = messages[4];
			XacoveoAlert.Distance = messages[5];
			XacoveoAlert.Login = messages[6];
			XacoveoAlert.IsPositional = messages[7].equals("S");
			result = WebResult.ALERT_OK;

		}

		return result;
	}

	public static Image sendWebRequestImage(String url) throws IOException
	{
		HttpConnection c = null;
		InputStream is = null;
		Image image = null;

		try
		{
			// Obtenemos la conexión con la dirección url indicada
			c = (HttpConnection)Connector.open(url);

			// Obtener el código de respuesta abrirá la conexión,
			// enviará la petición, y leerá las cabeceras de la respuesta HTTP.
			int rc = c.getResponseCode();
			if (rc != HttpConnection.HTTP_OK )
			{
				throw new IOException("Error HTTP:" + rc);
			}
			// Obtenemos el "stream" de datos
			is = c.openInputStream();

			image = Image.createImage(is);
		}
		catch (ClassCastException e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
			throw new IllegalArgumentException("Not an HTTP URL");
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
		finally
		{
			if ( is != null )
				is.close();
			if ( c != null )
				c.close();
		}

		return image;
	}

	private static String sendWebRequestString(String url) throws IOException
	{
		HttpConnection c = null;
		InputStream is = null;
		StringBuffer str = new StringBuffer(); // StringBuffer que almacenará la cadena de repuesta

		try
		{
			// Obtenemos la conexión con la dirección url indicada
			c = (HttpConnection)Connector.open(url);

			// Obtener el código de respuesta abrirá la conexión,
			// enviará la petición, y leerá las cabeceras de la respuesta HTTP.
			int rc = c.getResponseCode();
			if (rc != HttpConnection.HTTP_OK )
			{
				throw new IOException("Error HTTP:" + rc);
			}

			// Obtenemos el "stream" de datos
			is = c.openInputStream();


			// Leemos carácter a carácter hasta el final (-1)
			int actual = -1;
			while ((actual = is.read()) != -1)
			{
				str.append((char)actual);
			}

			// Si no se devuelven datos, devolvemos un error
			if (str.length() == 0)
				throw new IOException("Unexpected error: empty response.");
		}
		catch (ClassCastException e)
		{
			throw new IllegalArgumentException("Not an HTTP URL");
		}
		finally
		{
			if ( is != null )
				is.close();
			if ( c != null )
				c.close();
		}

		return str.toString();
	}

	public static void sendWebPos(String lat, String lon) throws IOException
	{
		String url = urlPosition + "?iduser=" + State.secureId + "&lat=" + lat + "&lon=" + lon;
		sendWebRequestString(url);
	}

	public static void sendWebDisconnection() throws IOException
	{
		String url = urlDisconnect + "?iduser=" + State.secureId;
		sendWebRequestString(url);
	}

	private static String[] parseMessage(String mensaje)
	{
		// Temporalmente almacenaremos los mensajes en un vector
		// (ya que nos abemos el número de elementos)
		java.util.Vector mensajes = new java.util.Vector();

		// Mientras haya un $$$ en la cadena
		while (mensaje.indexOf("$$$") != -1)
		{
			// Añadimos lo que hay hasta el $$$ al vector
			mensajes.addElement(mensaje.substring(0, mensaje.indexOf("$$$")));
			// Eliminamos lo ya añadido (incluído el $$$)
			mensaje = mensaje.substring(mensaje.indexOf("$$$") + 3);
		}

		// Copiamos el vector a un array de Strings
		String[] respuesta = new String[mensajes.size()];
		mensajes.copyInto(respuesta);
		return respuesta;
	}

}
