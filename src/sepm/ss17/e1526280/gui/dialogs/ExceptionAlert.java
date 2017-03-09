package sepm.ss17.e1526280.gui.dialogs;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * A simple class which does create a new Alert with a StackTrace in it.
 *
 * @author Raphael Ludwig
 * @version 08.03.17
 */
public class ExceptionAlert extends Alert {

    public ExceptionAlert(Throwable ex) {
        super(AlertType.ERROR);

        //Set contents of the Alert Window
        this.setTitle("Error");
        this.getDialogPane().setPrefSize(640,300);

        //Create some Objects for the expandable Area
        final Label exceptionLabel = new Label("Die aufgetretene Ausnahme war:");
        final TextArea textArea = new TextArea(stacktraceToString(ex));

        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        final GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(exceptionLabel, 0, 0);
        expContent.add(textArea, 0, 1);

        this.getDialogPane().setExpandableContent(expContent);
    }

    private static String stacktraceToString(Throwable ex) {
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringWriter);
        ex.printStackTrace(printWriter);

        return stringWriter.toString();
    }
}
