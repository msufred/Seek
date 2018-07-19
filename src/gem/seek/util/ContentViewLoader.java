package gem.seek.util;

import javafx.fxml.FXMLLoader;

import java.io.IOException;

/**
 *
 * Created by RAFIS-FRED on 4/19/2017.
 */
public abstract class ContentViewLoader {

    protected FXMLLoader loadContentView(Object caller, Object controller, String resource) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(caller.getClass().getResource(resource));
        if(controller != null){
            loader.setController(controller);
        }
        loader.load();
        return loader;
    }

}
