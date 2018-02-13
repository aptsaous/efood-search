package gr.foodsearcher;

import gr.foodsearcher.data.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.util.ArrayList;
import java.util.prefs.Preferences;

public class MainScreenController
{
    @FXML
    private TextField streetNameField;

    @FXML
    private TextField streetNumberField;

    @FXML
    private TextField postalCodeField;

    @FXML
    private TextField areaField;

    @FXML
    private TextField cityField;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Result> resultsTable;

    @FXML
    private TableColumn foodColumn;

    @FXML
    private TableColumn foodDescription;

    @FXML
    private TableColumn foodPrice;

    @FXML
    private TableColumn restaurantName;

    private final static int NOTIFICATION_DELAY = 5;
    private static boolean needsSaving = false;
    private GeoLocation geoLocation;

    @FXML
    void onTextChanged( KeyEvent event )
    {
        if ( ( event.getCode() == KeyCode.ENTER ) || ( event.getCode() == KeyCode.TAB ) || ( event.getCode().isArrowKey() ) )
            return;

        needsSaving = true;
    }

    @FXML
    void onKeyPressedSearch( KeyEvent event )
    {
        if ( event.getCode() == KeyCode.ENTER )
        {
            if ( needsSaving )
            {
                showNotification( "[FoodSearcher]: Error when trying to search for food", "Please save your location settings before searching", "ERROR" );
            }
            else
            {
                searchButtonClicked( null );
            }

        }
    }

    @FXML
    void searchButtonClicked( MouseEvent event )
    {
        if ( needsSaving )
        {
            showNotification( "[FoodSearcher]: Error when trying to search for food", "Please save your location settings before searching", "ERROR" );
        }
        else
        {
            String query = searchField.getText();

            if ( query.trim().isEmpty() )
                return;

            if ( !query.toLowerCase().matches( "([\\w\\sα-ωΑ-Ω'ήάόίέώύϊΐ.]+)" ) )
            {
                showNotification( "[FoodSearcher]: Error when trying to search for food", "Invalid search character: " + query, "ERROR" );
                return;
            }

            Platform.runLater( () -> {
                Restaurants restaurants = new Restaurants( geoLocation );
                restaurants.fetchRestaurants( query );

                ObservableList<Result> data = FXCollections.observableArrayList();
                ArrayList<Restaurant> restaurantsList = restaurants.getRestaurantsList();

                for ( Restaurant restaurant : restaurantsList )
                {
                    for ( Food food : restaurant.getFoods() )
                    {
                        data.add( new Result( food.getName(), food.getDescription(), food.getPrice(), restaurant.getName() ) );
                    }
                }

                resultsTable.setItems( data );
            } );
        }

    }

    @FXML
    void saveButtonClicked( MouseEvent event )
    {
        Preferences prefs = Preferences.userNodeForPackage( FoodSearcher.class );

        String streetName = streetNameField.getText();
        String streetNumber = streetNumberField.getText();
        String postalCode = postalCodeField.getText();
        String area = areaField.getText();
        String city = cityField.getText();

        if ( streetName.equals( "" ) || streetNumber.equals( "" ) || postalCode.equals( "" ) || area.equals( "" ) || city.equals( "" ) )
        {
            showNotification( "[FoodSearcher]: Error when saving location data", "Please fill in all fields", "ERROR" );
        }
        else
        {
            if ( !needsSaving )
                return;

            // TODO: validate input before assigning to vars

            prefs.put( "streetName", streetName );
            prefs.put( "streetNumber", streetNumber );
            prefs.put( "postalCode", postalCode );
            prefs.put( "area", area );
            prefs.put( "city", city );

            geoLocation = new GeoLocation( streetName, streetNumber, city, postalCode, area );
            geoLocation.setGeoLocation();
            double latitude = geoLocation.getLatitude();
            double longitude = geoLocation.getLongitude();

            if ( ( latitude == Double.NaN ) || ( longitude == Double.NaN ) )
            {
                showNotification( "[FoodSearcher]: GeoLocation failed", "Please verify that the location data are valid", "ERROR" );
                return;
            }

            prefs.put( "latitude", String.valueOf( latitude ) );
            prefs.put( "longitude", String.valueOf( longitude ) );

            showNotification( "[FoodSearcher]: Saving location data", "Settings were saved successfully", "INFO" );

            needsSaving = false;
        }

    }

    private static void showNotification( String title, String message, String notificationType )
    {
        Notifications notification = Notifications.create()
                .title( title )
                .text( message )
                .position( Pos.BOTTOM_RIGHT )
                .hideAfter( Duration.seconds( NOTIFICATION_DELAY ) );

        switch ( notificationType )
        {
            case "INFO":
                notification.showInformation();
                break;
            case "ERROR":
                notification.showError();
                break;
            case "WARNING":
                notification.showWarning();
                break;
            default:
                notification.show();
                break;
        }

    }

    public void initialize()
    {
        Preferences prefs = Preferences.userNodeForPackage( FoodSearcher.class );

        String streetName = prefs.get( "streetName", null );
        String streetNumber = prefs.get( "streetNumber", null );
        String postalCode = prefs.get( "postalCode", null );
        String area = prefs.get( "area", null );
        String city = prefs.get( "city", null );

        streetNameField.setText( ( streetName == null ) ? "" : streetName );
        streetNumberField.setText( ( streetNumber == null ) ? "" : streetNumber );
        postalCodeField.setText( ( postalCode == null ) ? "" : postalCode );
        areaField.setText( ( area == null ) ? "" : area );
        cityField.setText( ( city == null ) ? "" : city );

        resultsTable.setPlaceholder( new Label( "" ) );

        String lat = prefs.get( "latitude", null );
        String lng = prefs.get( "longitude", null );

        if ( ( lat == null ) || ( lng == null ) )
        {
            needsSaving = true;
            geoLocation = new GeoLocation( streetName, streetNumber, city, postalCode, area );
        }
        else
            geoLocation = new GeoLocation( streetName, streetNumber, city, postalCode, area, Double.parseDouble( lat ), Double.parseDouble( lng ) );

        foodColumn.setCellValueFactory( new PropertyValueFactory<Result, String>( "foodName" ) );
        foodDescription.setCellValueFactory( new PropertyValueFactory<Result, String>( "foodDescription" ) );
        foodPrice.setCellValueFactory( new PropertyValueFactory<Result, String>( "foodPrice" ) );
        restaurantName.setCellValueFactory( new PropertyValueFactory<Result, String>( "restaurantName" ) );

    }
}