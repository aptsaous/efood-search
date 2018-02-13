package gr.foodsearcher.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;

public class GeoLocation
{
    static final int SOCKET_TIMEOUT = 10_000;

    private String streetName;
    private String streetNumber;
    private String postalCode;
    private String area;
    private String city;
    private double latitude;
    private double longitude;

    private final String googleAPIKeyFileName = "authKey";

    public GeoLocation( String streetName, String streetNumber, String city, String postalCode, String area  )
    {
        this( streetName, streetNumber, city, postalCode, area, Double.NaN, Double.NaN );
    }

    public GeoLocation( String streetName, String streetNumber, String city, String postalCode, String area, double latitude, double longitude )
    {
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.postalCode = postalCode;
        this.area = area;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setGeoLocation()
    {
        URL authKeyURL = GeoLocation.class.getClassLoader().getResource( googleAPIKeyFileName );

        if ( authKeyURL == null )
        {
            System.err.println( "AuthKey resource not found" );
            return;
        }

        File authKey = new File( authKeyURL.getPath() );

        try
        {
            ArrayList<String> key = ( ArrayList<String> ) Files.readAllLines( authKey.toPath() );

            if ( key.isEmpty() )
                return;

            String address = String.format( "%s %s, %s, %s", streetName, streetNumber, city, postalCode );
            String url = "https://maps.googleapis.com/maps/api/geocode/json";

            String JSONData = Jsoup.connect( url )
                .userAgent( "Mozilla" )
                .ignoreContentType( true )
                .data( "address", address )
                .data( "key", key.get( 0 ) )
                .timeout( SOCKET_TIMEOUT )
                .get().text();

            if ( JSONData != null )
            {
                setGeoDataFromJSON( JSONData );
            }
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    private void setGeoDataFromJSON( String JSON )
    {
        try
        {
            JSONObject data = new JSONObject( JSON );
            JSONArray results = data.getJSONArray( "results" );
            JSONObject location = results.getJSONObject( 0 ).getJSONObject( "geometry" ).getJSONObject( "location" );

            latitude = location.getDouble( "lat" );
            longitude = location.getDouble( "lng" );

        }
        catch ( JSONException e )
        {
            System.err.println( "Error while parsing Google's GeoLocation Data" );
            e.printStackTrace();
        }

    }

    public String getStreetName()
    {
        return streetName;
    }

    public String getStreetNumber()
    {
        return streetNumber;
    }

    public String getPostalCode()
    {
        return postalCode;
    }

    public String getArea()
    {
        return area;
    }

    public String getCity()
    {
        return city;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }
}
