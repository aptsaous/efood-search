package gr.foodsearcher.data;

import java.text.NumberFormat;
import java.util.Locale;

public class Food
{
    private String name;
    private String description;
    private String price;

    public Food( String name, String description, String price )
    {
        this.name = name;
        this.description = description;
        this.price = NumberFormat.getCurrencyInstance( Locale.GERMANY ).format( Double.parseDouble( price ) );
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public String getPrice()
    {
        return price;
    }
}
