package gr.foodsearcher;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;

public class FoodSearcher extends Application
{
    public static void main( String[] args )
    {
        launch( args );
    }

    @Override
    public void start( Stage primaryStage )
    {
        try
        {
            FXMLLoader loader = new FXMLLoader( getClass().getResource( "MainScreen.fxml" ) );
            Parent root = loader.load();

            Scene scene = new Scene( root ); // attach scene graph to scene
            primaryStage.setTitle( "FoodSearcher" ); // displayed in window's title bar
            primaryStage.setScene( scene ); // attach scene to stage
            primaryStage.setResizable( false );
            primaryStage.show(); // display the stage
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }

    }
}