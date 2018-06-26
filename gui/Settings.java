package gui;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

class Settings {
    long duration;
    int nbIterations;

    boolean changeTapesCells;
    String tapesCellsDescription;

    Settings(long duration, int nbIterations) {
        this.duration = duration;
        this.nbIterations = nbIterations;
    }

    static Dialog<Settings> getDialog(
            long duration,
            int nbIterations,
            String tapesCellsDescription) {
        Dialog<Settings> dialog = new Dialog<Settings>();

        dialog.setTitle("Options");
        dialog.setHeaderText(null);

        DialogPane dialogPane = dialog.getDialogPane();

        VBox vbox = new VBox();

        HBox hbox = new HBox();

        Label durationLabel = new Label("Animation duration (ms) : ");
        durationLabel.setAlignment(Pos.CENTER_LEFT);
        TextField durationTextField = new TextField(String.valueOf(duration));

        durationTextField.textProperty().addListener((observableValue, old, value) -> {
            if(!value.matches("\\d*"))
                durationTextField.setText(old);
            }
        );
        hbox.getChildren().addAll(durationLabel, durationTextField);

        HBox hbox3 = new HBox();

        Label nbIterationsLabel = new Label("Build max nb iteration : ");
        nbIterationsLabel.setAlignment(Pos.CENTER_LEFT);
        TextField nbIterationsTextField = new TextField(String.valueOf(nbIterations));

        nbIterationsTextField.textProperty().addListener((observableValue, old, value) -> {
            if(!value.matches("\\d*"))
                durationTextField.setText(old);
            }
        );
        hbox3.getChildren().addAll(nbIterationsLabel, nbIterationsTextField);


        VBox vbox2 = new VBox();
        HBox hbox2 = new HBox();

        TextArea tapeEditTextArea = new TextArea();
        tapeEditTextArea.setDisable(true);
        tapeEditTextArea.setText(tapesCellsDescription);
        tapeEditTextArea.setFont(Font.font("monospace"));

        CheckBox tapeEditCheckBox = new CheckBox("Edit tape manually.");
        tapeEditCheckBox.setOnMouseClicked(mouseEvent -> {
            tapeEditTextArea.setDisable(!tapeEditCheckBox.isSelected());
        });

        Label tapeEditHelp = new Label("(?)");

        hbox2.getChildren().addAll(tapeEditCheckBox, tapeEditHelp);


        vbox2.getChildren().addAll(hbox2, tapeEditTextArea);
        vbox.getChildren().addAll(hbox, hbox3, vbox2);

        dialogPane.setContent(vbox);

        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if(buttonType == ButtonType.OK){
                long nduration = 0L;
                if(!durationTextField.getText().equals(""))
                    nduration = Integer.valueOf(durationTextField.getText());

                int nnbIterations = 1;
                if(!durationTextField.getText().equals(""))
                    nnbIterations = Integer.valueOf(nbIterationsTextField.getText());
                if(nnbIterations == 0)
                    nnbIterations++;

                Settings settings = new Settings(nduration, nnbIterations);
                settings.changeTapesCells = tapeEditCheckBox.isSelected();
                settings.tapesCellsDescription = tapeEditTextArea.getText();
                return settings;
            }
            return null;
        });

        return dialog;
    }
}
