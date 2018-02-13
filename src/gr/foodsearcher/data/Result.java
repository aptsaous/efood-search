package gr.foodsearcher.data;

import javafx.beans.property.SimpleStringProperty;

public class Result
{
    private final SimpleStringProperty foodName;
    private final SimpleStringProperty foodDescription;
    private final SimpleStringProperty foodPrice;
    private final SimpleStringProperty restaurantName;

    public Result( String foodName, String foodDescription, String foodPrice, String restaurantName )
    {
        this.foodName = new SimpleStringProperty( foodName );
        this.foodDescription = new SimpleStringProperty( foodDescription );
        this.foodPrice = new SimpleStringProperty( foodPrice );
        this.restaurantName = new SimpleStringProperty( restaurantName );
    }

    public String getFoodName()
    {
        return foodName.get();
    }


    public String getFoodDescription()
    {
        return foodDescription.get();
    }


    public String getFoodPrice()
    {
        return foodPrice.get();
    }

    public String getRestaurantName()
    {
        return restaurantName.get();
    }
}
