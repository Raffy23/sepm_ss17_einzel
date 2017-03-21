package sepm.ss17.e1526280.gui.controller;

/**
 * Created by
 *
 * @author Raphael Ludwig
 * @version 20.03.17
 */

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;
import sepm.ss17.e1526280.dto.Box;
import sepm.ss17.e1526280.gui.dialogs.DialogUtil;
import sepm.ss17.e1526280.gui.dialogs.ImageDialog;
import sepm.ss17.e1526280.service.BoxDataService;
import sepm.ss17.e1526280.util.DataServiceManager;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;

public class StatisticEditController {

    
    @FXML private Label boxId;
    @FXML private Label boxSize;
    @FXML private Label boxPrice;
    @FXML private Label boxWindow;
    @FXML private Label boxIndoor;
    @FXML private TextField priceField;
    @FXML private RadioButton absPrice;
    @FXML private RadioButton perPrice;
    @FXML private Label boxCntLabel;
    @FXML private Button prevBtn;
    @FXML private Button nextBtn;
    @FXML private Button imgBtn;

    private final ToggleGroup toggleGroup = new ToggleGroup();

    private List<Box> data;
    private int curElement = 0;

    @FXML
    public void initialize() {
        absPrice.setToggleGroup(toggleGroup);
        perPrice.setToggleGroup(toggleGroup);
        absPrice.setSelected(true);

        priceField.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
    }


    private void setElementData() {
        final Box e = data.get(curElement);

        boxId.setText(String.valueOf(e.getBoxID()));
        boxSize.setText(String.valueOf(e.getSize()));
        boxPrice.setText(String.valueOf(e.getPrice()));
        boxWindow.setText(String.valueOf(e.isWindow()));
        boxIndoor.setText(String.valueOf(e.isIndoor()));

        if( e.getPhoto() == null && e.getPhoto().length() > 5)
            imgBtn.setDisable(true);
        else
            imgBtn.setDisable(false);
    }

    @FXML
    public void onBoxImage(ActionEvent event) {
        new ImageDialog(data.get(curElement), DataServiceManager.getService().getBoxDataService());
    }

    @FXML
    public void onNextBox(ActionEvent event) {
        curElement++;
        setElementData();

        if( curElement >= data.size()-1 )
            ((Button)event.getSource()).setDisable(true);

        prevBtn.setDisable(false);
    }


    @FXML
    public void onPrevBox(ActionEvent event) {
        curElement--;
        setElementData();

        if( curElement <= 0 )
            ((Button)event.getSource()).setDisable(true);

        nextBtn.setDisable(false);
    }


    @FXML
    public void onSave(ActionEvent event) {
        final BoxDataService service = DataServiceManager.getService().getBoxDataService();
        final double minPrice = data.stream().mapToDouble(Box::getPrice).min().orElseGet(() -> 0);

        try {
            final float price = NumberFormat.getInstance().parse(priceField.getText()).floatValue();

            // Check data and Update stuff
            if( absPrice.isSelected() ) {
                if( price < 0 && -price > minPrice ) {
                    //TODO: msg min price!
                    return;
                }

                System.out.println("abs");
                data.forEach(box -> box.setPrice(box.getPrice()+price));
            } else {
                if( price < -100 ) {
                    //TODO: -100% isn't possible
                    return;
                }

                System.out.println("%");
                data.forEach(box -> box.setPrice(box.getPrice()*price));
            }

            // Persist data
            data.forEach(box ->
                    service.update(box).thenRun(() -> {/* Log stuff */}).exceptionally(DialogUtil::onError)
            );

            ((Stage)((Button)event.getSource()).getScene().getWindow()).close();
        } catch (ParseException e) {
            DialogUtil.onError(e);
        }
    }

    public void setData(List<Box> data) {
        this.data = data;

        boxCntLabel.setText(String.valueOf(data.size()));
        setElementData();

        if( data.size() == 1 )
            nextBtn.setDisable(true);
    }
}
