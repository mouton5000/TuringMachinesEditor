/*
 * Copyright (c) 2018 Dimitri Watel
 */

package gui;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.Arrays;
import java.util.List;

class Settings {
    long duration;
    int maximumNbIterationsAuto;
    int maximumNbIterationsManual;

    boolean changeTapesCells;
    String tapesCellsDescription;

    Settings(long duration, int maximumNbIterationsAuto, int maximumNbIterationsManual) {
        this.duration = duration;
        this.maximumNbIterationsAuto = maximumNbIterationsAuto;
        this.maximumNbIterationsManual = maximumNbIterationsManual;
    }

    static Dialog<Settings> getDialog(
            long duration,
            int nbIterationsAuto,
            int nbIterationsManual,
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

        Label nbIterationsAutoLabel = new Label("Max iterations (auto) : ");
        nbIterationsAutoLabel.setAlignment(Pos.CENTER_LEFT);
        TextField nbIterationsAutoTextField = new TextField(String.valueOf(nbIterationsAuto));

        nbIterationsAutoTextField.textProperty().addListener((observableValue, old, value) -> {
            if(!value.matches("\\d*"))
                durationTextField.setText(old);
            }
        );
        hbox3.getChildren().addAll(nbIterationsAutoLabel, nbIterationsAutoTextField);

        HBox hbox4 = new HBox();

        Label nbIterationsManualLabel = new Label("Max iterations (manual) : ");
        nbIterationsManualLabel.setAlignment(Pos.CENTER_LEFT);
        TextField nbIterationsManualTextField = new TextField(String.valueOf(nbIterationsManual));

        nbIterationsManualTextField.textProperty().addListener((observableValue, old, value) -> {
                    if(!value.matches("\\d*"))
                        durationTextField.setText(old);
                }
        );
        hbox4.getChildren().addAll(nbIterationsManualLabel, nbIterationsManualTextField);


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


        hbox2.getChildren().addAll(tapeEditCheckBox);

        Label errorLabel = new Label();
        errorLabel.setWrapText(true);
        errorLabel.setMinHeight(TuringMachineDrawer.SETTINGS_ERROR_LABEL_HEIGHT);

        vbox2.getChildren().addAll(hbox2, tapeEditTextArea, errorLabel);
        vbox.getChildren().addAll(hbox, hbox3, hbox4, vbox2);

        dialogPane.setContent(vbox);

        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        final Button btOk = (Button) dialogPane.lookupButton(ButtonType.OK);
        btOk.addEventFilter(
                ActionEvent.ACTION,
                event -> {
                    if (!tapeEditCheckBox.isSelected())
                        return;

                    String tapesDescription = tapeEditTextArea.getText();

                    String[] lines = tapesDescription.split("\n");
                    int size = lines.length;

                    String symbolsRegex = "([A-Z0-9] )*[A-Z0-9]?";
                    if(size <= 0 || !lines[0].matches(symbolsRegex)) {
                        errorLabel.setText("The first line should contain only valid symbols (0, 1, ..., 9, " +
                                "A, B, ..., Z) separated by a space.");
                        event.consume();
                        return;
                    }

                    List<String> symbols = Arrays.asList(lines[0].split(" "));

                    String tapeBoundRegex = "((-?\\d+|I) ){3}(-?\\d+|I)";
                    String tapeNbHeadsRegex = "\\d+";
                    String tapeHeadDescriptionRegex = "-?\\d+ -?\\d+ \\d+ \\d+ \\d+";
                    String tapeInputCoordinatesRegex = "-?\\d+ -?\\d+";

                    String tapeInputRegex = "(" + String.join("|", symbols) + "| )*";


                    int i = 1;
                    while(true){
                        if(size <= i || !lines[i].matches(tapeBoundRegex)){
                            errorLabel.setText("Line " + (i + 1) + " should contain only four (positive or negative) " +
                                    "integers or the symbol I separated by a space.");
                            event.consume();
                            return;
                        }
                        if(size <= i + 1 || !lines[i + 1].matches(tapeNbHeadsRegex)) {
                            errorLabel.setText("Line " + (i + 1) + " should contain only one positive integer.");
                            event.consume();
                            return;
                        }
                        Integer nbHeads = Integer.valueOf(lines[i + 1]);
                        for(int j = 0; j < nbHeads; j++){
                            if(size <= i + 2 + j || !lines[i + 2 + j].matches(tapeHeadDescriptionRegex)) {
                                errorLabel.setText("Line " + (i + 2 + j + 1) + " should contain only five integers " +
                                        "separated by a space.");
                                event.consume();
                                return;
                            }
                            String[] x = lines[i + 2 + j].split(" ");
                            for(int xi = 2; xi < 5; xi++){
                                int xx = Integer.valueOf(x[xi]);
                                if(xx < 0 || xx > 255){
                                    errorLabel.setText("The three last integers of line " + (i + 2 + j + 1) + " " +
                                            "should be between 0 and 255.");
                                    event.consume();
                                    return;
                                }
                            }
                        }
                        i = i + 2 + nbHeads;
                        if(size <= i || !lines[i].matches(tapeInputCoordinatesRegex)) {
                            errorLabel.setText("Line " + (i + 1) + " should contain only two (positive or negative " +
                                    "integers.");
                            event.consume();
                            return;
                        }

                        i++;
                        while(i < size && !lines[i].equals(";")){
                            if(!lines[i].matches(tapeInputRegex)) {
                                errorLabel.setText("Line " + (i + 1) + " should contain only the symbols given on the" +
                                        " first line.");
                                event.consume();
                                return;
                            }
                            i++;
                        }

                        if(i >= size)
                            break;
                        i++;
                    }
                    errorLabel.setText("");

                }
        );

        dialog.setResultConverter(buttonType -> {
            if(buttonType == ButtonType.OK){
                long nduration = 0L;
                if(!durationTextField.getText().equals(""))
                    nduration = Integer.valueOf(durationTextField.getText());

                int nnbIterationsAuto = 1;
                if(!durationTextField.getText().equals(""))
                    nnbIterationsAuto = Integer.valueOf(nbIterationsAutoTextField.getText());
                if(nnbIterationsAuto == 0)
                    nnbIterationsAuto++;

                int nnbIterationsManual = 1;
                if(!durationTextField.getText().equals(""))
                    nnbIterationsManual = Integer.valueOf(nbIterationsManualTextField.getText());
                if(nnbIterationsManual == 0)
                    nnbIterationsManual++;

                Settings settings = new Settings(nduration, nnbIterationsAuto, nnbIterationsManual);
                settings.changeTapesCells = tapeEditCheckBox.isSelected();
                settings.tapesCellsDescription = tapeEditTextArea.getText();
                return settings;
            }
            return null;
        });

        return dialog;
    }
}
