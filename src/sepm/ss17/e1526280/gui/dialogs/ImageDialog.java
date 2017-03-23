package sepm.ss17.e1526280.gui.dialogs;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import sepm.ss17.e1526280.dto.Box;
import sepm.ss17.e1526280.service.BoxDataService;

import java.io.File;

/**
 * A Simple Dialog which only displays the Image for a Box
 *
 * @author Raphael Ludwig
 * @version 09.03.17
 */
public class ImageDialog {

    public ImageDialog(Box box, BoxDataService service) {
        final Stage stage = new Stage();

        final BorderPane pane = new BorderPane();
        final Scene scene = new Scene(pane);

        stage.setTitle("Bild von der Box (" + box.getBoxID() + ")");

        final String resource = service.resolveImage(box).toURI().getPath();
        final Image img = new Image("file:"+resource);
        final ImageView imageView = new ImageView();

        pane.setCenter(imageView);

        imageView.setImage(img);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(600);

        stage.setScene(scene);
        stage.showAndWait();
    }
}
