package gui;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

class Settings {
    int duration;

    boolean changeTapesCells;
    String tapesCellsDescription;

    Settings(int duration) {
        this.duration = duration;
    }

    static Dialog<Settings> getDialog(
            long duration,
            String tapesCellsDescription) {
        Dialog<Settings> dialog = new Dialog<Settings>();

        dialog.setTitle("Options");
        dialog.setHeaderText(null);

        DialogPane dialogPane = dialog.getDialogPane();

        VBox vbox = new VBox();

        HBox hbox = new HBox();

        Label durationLabel = new Label("Animation duration (ms) : ");
        TextField durationTextField = new TextField(String.valueOf(duration));

        durationTextField.textProperty().addListener((observableValue, old, value) -> {
            if(value.equals(""))
                durationTextField.setText("0");
            else if(!value.matches("\\d+"))
                durationTextField.setText(old);
            }
        );
        hbox.getChildren().addAll(durationLabel, durationTextField);


        VBox vbox2 = new VBox();
        HBox hbox2 = new HBox();

        TextArea tapeEditTextArea = new TextArea();
        tapeEditTextArea.setDisable(true);
        tapeEditTextArea.setText(tapesCellsDescription);

        CheckBox tapeEditCheckBox = new CheckBox("Edit tape manually.");
        tapeEditCheckBox.setOnMouseClicked(mouseEvent -> {
            tapeEditTextArea.setDisable(!tapeEditCheckBox.isSelected());
        });

        Label tapeEditHelp = new Label("(?)");

        hbox2.getChildren().addAll(tapeEditCheckBox, tapeEditHelp);


        vbox2.getChildren().addAll(hbox2, tapeEditTextArea);
        vbox.getChildren().addAll(hbox, vbox2);

        dialogPane.setContent(vbox);

        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if(buttonType == ButtonType.OK){
                Settings settings = new Settings(Integer.valueOf(durationTextField.getText()));
                settings.changeTapesCells = tapeEditCheckBox.isSelected();
                settings.tapesCellsDescription = tapeEditTextArea.getText();
                return settings;
            }
            return null;
        });

        return dialog;
    }
}
