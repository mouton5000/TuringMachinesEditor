/*
 * Copyright (c) 2018 Dimitri Watel
 */

package util;

import javafx.scene.input.MouseEvent;

/**
 * Interface used by all the nodes listening to a mouse event.
 */
public interface MouseListener {
    boolean onMouseClicked(MouseEvent mouseEvent);
    boolean onMouseDragged(MouseEvent mouseEvent);
    boolean onMousePressed(MouseEvent mouseEvent);
}

