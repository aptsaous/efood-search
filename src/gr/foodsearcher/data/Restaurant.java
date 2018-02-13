package gr.foodsearcher.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import static gr.foodsearcher.data.Restaurants.SOCKET_TIMEOUT;

public class Restaurant
{
    private String name;
    private String url;
    private ArrayList<Food> foods;

    public Restaurant( String name, String url )
    {
        this.name = name;
        this.url = url;
        this.foods = new ArrayList<>();
    }

    public boolean hasFood( String foodName )
    {
        try
        {
            Document doc = Jsoup.connect( url )
                    .userAgent( "Mozilla" )
                    .timeout( SOCKET_TIMEOUT )
                    .get();

            Elements JSON = doc.getElementsByAttributeValue( "type", "application/ld+json" );

            try
            {
                JSONObject data = new JSONObject( JSON.get( 1 ).data() );
                JSONArray graph = data.getJSONArray( "@graph" );
                JSONArray foodList = graph.getJSONArray( 1 );

                for ( int i = 0; i < foodList.length(); i++ )
                {
                    String name = foodList.getJSONObject( i ).getString( "name" );
                    String description = foodList.getJSONObject( i ).getString( "description" );
                    double price = foodList.getJSONObject( i ).getJSONObject( "offers" ).getDouble( "price" );

                    if ( name.toLowerCase().contains( foodName ) || description.contains( foodName ) )
                    {
                        boolean isAvailable = true;

                        Elements select = doc.select( "li[class*=menu-item disabled]" );
                        for ( Element element : select )
                        {
                            Elements itemName = element.select( "[class=\"menu-item-name\"]" );
                            Elements itemDescription = element.select( "[class=\"menu-item-description\"]" );

                            if ( ( itemName.text().contains( name ) ) && ( itemDescription.text().contains( description ) ) )
                            {
                                isAvailable = false;
                                break;
                            }
                        }
                        if ( isAvailable )
                            foods.add( new Food( name, description, String.valueOf( price ) ) );
                    }
                }

            }
            catch ( JSONException e )
            {
                e.printStackTrace();
            }

        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }

        return true;
    }

    public ArrayList<Food> getFoods()
    {
        return foods;
    }

    public String getName()
    {
        return name;
    }

    public String getUrl()
    {
        return url;
    }
}