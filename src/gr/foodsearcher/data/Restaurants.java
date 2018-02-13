package gr.foodsearcher.data;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Restaurants
{
    static final String eFoodURL = "https://www.e-food.gr";
    static final int SOCKET_TIMEOUT = 10_000;
    private GeoLocation geoLocation;
    private ArrayList<Restaurant> restaurantsList;

    public Restaurants( GeoLocation geoLocation )
    {
        this.geoLocation = geoLocation;
        this.restaurantsList = new ArrayList<>();
    }

    public void fetchRestaurants( String query )
    {
        try {
            Document doc = Jsoup.connect( eFoodURL + "/shops" )
                    .userAgent( "Mozilla" )
                    .data( "address", String.format( "%s %s, %s %s, %s", geoLocation.getStreetName(), geoLocation.getStreetNumber(), geoLocation.getPostalCode(), geoLocation.getArea(), geoLocation.getCity() ) )
                    .data( "city", geoLocation.getCity() )
                    .data( "county", geoLocation.getArea() )
                    .data( "latitude", String.valueOf( geoLocation.getLatitude() ) )
                    .data( "longitude", String.valueOf( geoLocation.getLongitude() ) )
                    .data( "nomap", "0" )
                    .data( "street", geoLocation.getStreetName() )
                    .data( "street_no", geoLocation.getStreetNumber() )
                    .data( "zip", geoLocation.getPostalCode() )
                    .timeout( SOCKET_TIMEOUT )
                    .get();

            Elements shops = doc.getElementsByClass( "shop-open" );

            for ( Element shop : shops )
            {
                String shopName = shop.attr( "data-shop-name" );
                String shopLink = shop.getElementsByClass( "shop-info" ).select("a[href]" ).get( 0 ).attr( "href" );

                Restaurant restaurant = new Restaurant( shopName, eFoodURL + shopLink );

                if ( restaurant.hasFood( query.toLowerCase() ) )
                    restaurantsList.add( restaurant );

            }


        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }


    }

    public ArrayList<Restaurant> getRestaurantsList()
    {
        return restaurantsList;
    }

    private String getURL()
    {
        String address = String.format( "address=%s+%s,+%s+%s,+%s", geoLocation.getStreetName(), geoLocation.getStreetNumber(), geoLocation.getPostalCode(),
                geoLocation.getArea(), geoLocation.getCity() );

        String city = String.format( "city=%s", geoLocation.getCity() );
        String area = String.format( "county=%s", geoLocation.getArea() );
        String latitude = String.format( "latitude=%f", geoLocation.getLatitude() );
        String longitude = String.format( "longitude=%f", geoLocation.getLongitude() );
        String nomap = "nomap=0";
        String streetName = String.format( "street=%s", geoLocation.getStreetName() );
        String streetNumber = String.format( "street_no=%s", geoLocation.getStreetNumber() );
        String postalCode = String.format( "zip=%s", geoLocation.getPostalCode() );
        String slug = "slug=%2f";
        String deliveryType = "deliverytype=0";

        return String.format( "%s%s&%s&%s&%s&%s&%s&%s&%s&%s&%s&%s", eFoodURL, address, city, area, latitude, longitude, nomap, streetName, streetNumber, postalCode, slug, deliveryType );
    }
}
