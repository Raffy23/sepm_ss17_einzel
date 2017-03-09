package sepm.ss17.e1526280.gui.dialogs;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import sepm.ss17.e1526280.dto.Box;
import sepm.ss17.e1526280.service.DataService;

import java.io.File;

/**
 * TODO: Comments
 * @author Raphael Ludwig
 * @version 09.03.17
 */
public class ImageDialog {

    public ImageDialog(Box box, DataService service) {
        final Stage stage = new Stage();

        final BorderPane pane = new BorderPane();
        final Scene scene = new Scene(pane);

        stage.setTitle("Image of Box (" + box.getBoxID() + ")");

        final String resource = service.resolveImage(box).getAbsolutePath();
        final String rPath = new File(resource).toURI().getPath();
        final Image img = new Image("file:"+rPath);
        final ImageView imageView = new ImageView();

        pane.setCenter(imageView);

        imageView.setImage(img);
        imageView.setFitHeight(800);
        imageView.setFitWidth(640);

        stage.setScene(scene);
        stage.showAndWait();
    }
}
