package util;

import javafx.scene.input.MouseEvent;

public interface MouseListener {
    boolean onMouseClicked(MouseEvent mouseEvent);
    boolean onMouseDragged(MouseEvent mouseEvent);
    boolean onMousePressed(MouseEvent mouseEvent);
}

