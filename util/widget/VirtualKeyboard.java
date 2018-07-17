package util.widget;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * Widget for JavaFX 8 consisting in a dialog box with a text field and a keyboard with all the capital latin french
 * letters from A to Z and the numbers from 0 to 9. Pressing a button add the corresponding symbol to the end of the
 * text field. A backspace button is also given with the keyboard to remove the last letter. The text field can also
 * be used with a real keyboard. The virtual keyboard is used for machine that do not have a mouse (as interactive
 * boards).
 *
 * Two constructor are given: {@link #VirtualKeyboard()} and {@link #VirtualKeyboard(String)}. If the first one is
 * used or if the second one is used with the null parameter, only one letter is expected as output. The backward
 * button is not provided. Pressing any letter or number closes the window and returns the pressed symbol. On the
 * contrary, if the second constructor is used with a non null parameter, a longer word is expected, the backward
 * button can be used and an OK button is given to close the dialog and return the result.
 *
 * A CANCEL button is also provided. Pressing it return a null result.
 */
public class VirtualKeyboard extends Dialog<String> {

    private boolean longWord;
    private TextField text;
    private String value;

    private static final int BUTTON_WIDTH = 32;
    private static final int WIDTH = 10 * BUTTON_WIDTH + 30;

    public VirtualKeyboard() {
        this(null);
    }

    public VirtualKeyboard(String s) {

        this.setWidth(VirtualKeyboard.WIDTH);

        this.longWord = (s != null);

        if(longWord)
            text = new TextField(s);

        GridPane grid = new GridPane();
        value = null;

        String keys[][] = {
                {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"},
                {"A", "Z", "E", "R", "T", "Y", "U", "I", "O", "P"},
                {"Q", "S", "D", "F", "G", "H", "J", "K", "L", "M"},
                {"W", "X", "C", "V", "B", "N"}
        };

        this.setResultConverter(buttonType -> {
            if(longWord){
                if(buttonType == ButtonType.OK)
                    return text.getText();
                else
                    return null;
            }
            else{
                return value;
            }
        });

        int line = 0;
        for(String[] keysLine: keys){
            int column = 0;
            for(String key : keysLine){
                Button b = new Button(key);
                b.setMinWidth(VirtualKeyboard.BUTTON_WIDTH);
                b.setMaxWidth(VirtualKeyboard.BUTTON_WIDTH);
                grid.add(b, column, line);

                b.setOnMouseClicked(mouseEvent -> {
                    if(longWord)
                        this.text.setText(this.text.getText() + b.getText());
                    else {
                        this.value = b.getText();
                        this.close();
                    }
                });

                column++;
            }

            if(longWord && line == 0){
                Button b = new Button("\u2190");
                b.setMinWidth(VirtualKeyboard.BUTTON_WIDTH);
                b.setMaxWidth(VirtualKeyboard.BUTTON_WIDTH);
                grid.add(b, column, line);

                b.setOnMouseClicked(mouseEvent -> {
                    String t = this.text.getText();
                    if(t.length() != 0)
                        this.text.setText(t.substring(0, t.length() - 1));
                });
            }


            line++;
        }

        if(longWord){
            VBox vBox = new VBox(text, grid);
            this.getDialogPane().setContent(vBox);
        }
        else
            this.getDialogPane().setContent(grid);

        if(longWord)
            this.getDialogPane().getButtonTypes().add(ButtonType.OK);
        this.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
    }
}