package sepm.ss17.e1526280.gui.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import sepm.ss17.e1526280.dto.Reservation;
import sepm.ss17.e1526280.gui.dialogs.CustomDialog;
import sepm.ss17.e1526280.gui.dialogs.DialogUtil;
import sepm.ss17.e1526280.service.ReservationDataService;
import sepm.ss17.e1526280.util.DataServiceManager;
import sepm.ss17.e1526280.util.GlobalSettings;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Created by
 *
 * @author Raphael Ludwig
 * @version 11.03.17
 */
public class ReservationTableViewController {


    public static class ReservationWrapper  {
        final String name;
        final List<Reservation> boxes;
        final Date start;
        final Date end;

        ReservationWrapper(List<Reservation> boxes) {
            this.boxes = boxes;
            this.name = boxes.get(0).getCustomer();
            this.start = boxes.get(0).getStart();
            this.end = boxes.get(0).getEnd();
        }

        public float getPrice() {
            final long days = TimeUnit.DAYS.convert(end.getTime()-start.getTime(),TimeUnit.MILLISECONDS);
            return (float) boxes.stream().mapToDouble(reservation -> reservation.getPrice() * days).sum();
        }

        public int getCount() {
            return boxes.size();
        }

        public String getName() {
            return name;
        }

        public String getStartString() {
            return fmt.format(start);
        }

        public String getEndString() {
            return fmt.format(end);
        }
    }

    private static final SimpleDateFormat fmt = new SimpleDateFormat("dd.MM.yyyy");
    private final ReservationDataService dataService = DataServiceManager.getService().getReservationDataService();
    private final ObservableList<ReservationWrapper> dataList = FXCollections.observableList(new ArrayList<>());

    @FXML private TableView<ReservationWrapper> tableView;
    @FXML private TableColumn<ReservationWrapper,String> customerCol;
    @FXML private TableColumn<ReservationWrapper,String> boxCountColl;
    @FXML private TableColumn<ReservationWrapper,Void> boxDetailCol;
    @FXML private TableColumn<ReservationWrapper,String> priceCol;
    @FXML private TableColumn<ReservationWrapper,Void> deleteCol;
    @FXML private TableColumn<ReservationWrapper,Void> invoiceCol;
    @FXML private TableColumn<ReservationWrapper,String> startCol;
    @FXML private TableColumn<ReservationWrapper,String> endCol;

    @FXML
    public void initialize() {
        customerCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        boxCountColl.setCellValueFactory(new PropertyValueFactory<>("count"));
        boxDetailCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        deleteCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        invoiceCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("startString"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("endString"));

        boxDetailCol.setCellFactory((TableColumn<ReservationWrapper, Void> boxStringTableColumn) -> new TableCell<ReservationWrapper, Void>() {
            final Button detailBtn = new Button("Details");

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setGraphic(detailBtn);
                    setText(null);

                    final ReservationWrapper curObj = getTableView().getItems().get(getIndex());

                    detailBtn.setOnAction(event -> {
                        final Stage parentStage = (Stage) ((Button)event.getSource()).getScene().getWindow();
                        final String fxml = GlobalSettings.FXML_ROOT+"/"+"reservation_detailview.fxml";

                        final CustomDialog<ReservationDetailController> dialog = new CustomDialog<ReservationDetailController>(parentStage,GlobalSettings.APP_TITLE + " Reservierungsdetails",fxml) {};
                        final ReservationDetailController controller = dialog.getController();
                        controller.init(curObj.boxes);
                        dialog.show();

                    });
                }
            }
        });

        deleteCol.setCellFactory((TableColumn<ReservationWrapper, Void> boxStringTableColumn) -> new TableCell<ReservationWrapper, Void>() {
            final Button deleteBtn = new Button("Löschen");

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setGraphic(deleteBtn);
                    setText(null);

                    final ReservationWrapper curObj = getTableView().getItems().get(getIndex());

                    deleteBtn.setOnAction(event -> {
                        final Alert question = new Alert(Alert.AlertType.CONFIRMATION);
                        question.setTitle("Confirmation Dialog");
                        question.setHeaderText("Deletion of a Reservation");
                        question.setContentText("You are going to delete a Reservation permanently.\nAre you sure you want to continue?");

                        Optional<ButtonType> result = question.showAndWait();
                        if (result.get() == ButtonType.OK) {
                            dataService.delete(curObj.boxes)
                                       .thenRun(() -> dataList.remove(curObj))
                                       .exceptionally(DialogUtil::onError);
                        }
                    });
                }
            }
        });

        invoiceCol.setCellFactory((TableColumn<ReservationWrapper, Void> boxStringTableColumn) -> new TableCell<ReservationWrapper, Void>() {
            final Button invoiceBtn = new Button(">");

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setGraphic(invoiceBtn);
                    setText(null);

                    final ReservationWrapper curObj = getTableView().getItems().get(getIndex());

                    invoiceBtn.setOnAction(event -> {
                        System.out.println("Create Invoice from: " + curObj);
                    });
                }
            }
        });


        dataService.queryGrouped()
                   .thenApply(lists -> {
                       Platform.runLater(() ->
                           lists.forEach(reservations ->
                               dataList.add(new ReservationWrapper(reservations))
                           )
                       );

                       return null;
                   })
                .exceptionally(DialogUtil::onFatal);

        tableView.setItems(dataList);
    }

    @FXML
    public void onNewReservation(ActionEvent event) {
        final Stage parentStage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        final String fxml = GlobalSettings.FXML_ROOT+"/"+"reservation_boxchooser.fxml";

        CustomDialog<ReservationChooserController> dialog = new CustomDialog<ReservationChooserController>(parentStage,GlobalSettings.APP_TITLE + ": Boxen auswählen",fxml) {};
        ReservationChooserController controller = dialog.getController();

        controller.setOkBtnEventHandler(event1 -> {
            if( !controller.validate() ) {
                showOnValidationError();
                return;
            }

            dataService.persist(controller.getReservations())
                       .thenAccept(this::addData)
                       .exceptionally(DialogUtil::onError);

            ((Stage) ((Button)event1.getSource()).getScene().getWindow()).close();
        });


        dialog.show();
    }

    private void addData(List<Reservation> rs) {
        Platform.runLater(() -> dataList.add(new ReservationWrapper(rs)));
    }

    private static void showOnValidationError() {
        final Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle("Warning: " + GlobalSettings.APP_TITLE);
        a.setHeaderText("Es ist ein Fehler bei der Verarbeitung aufgetreten!");
        a.setContentText("Überprüfen Sie alle eingegeben Werte!");
        a.show();
    }
}
