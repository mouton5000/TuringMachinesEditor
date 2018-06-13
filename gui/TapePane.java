package gui;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dimitri.watel on 07/06/18.
 */
class TapePane extends Pane {

    TuringMachineDrawer drawer;
    HBox tapeHBox;

    TapeLinesGroup tapeLinesGroup;

    double offsetX;
    double offsetY;

    CellOptionRectangle cellOptionRectangle;

    private Map<Integer, Map<Integer, Label>> cellLabels;
    private Map<CellOptionRectangleHeadRectangle, Rectangle> heads;

    TapePane(TuringMachineDrawer drawer, HBox tapeHBox) {
        this.drawer = drawer;
        this.tapeHBox = tapeHBox;
        this.cellLabels = new HashMap<>();
        this.heads = new HashMap<>();

        this.setOnMousePressed(drawer.tapesMouseHandler);
        this.setOnMouseClicked(drawer.tapesMouseHandler);
        this.setOnMouseDragged(drawer.tapesMouseHandler);

        tapeLinesGroup = new TapeLinesGroup(this);
        cellOptionRectangle = new CellOptionRectangle(drawer, this);
        cellOptionRectangle.setVisible(false);

        this.getChildren().addAll(tapeLinesGroup, cellOptionRectangle);

        this.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            double width = this.getMaxWidth();
            double height = this.getMaxHeight();
            tapeLinesGroup.setLayoutX(width / 2);
            tapeLinesGroup.setLayoutY(height / 2);
            tapeLinesGroup.checkLinesAndColumns(width, height);
        });

    }

    void translate(double dx, double dy) {

        offsetX -= dx;
        offsetY -= dy;

        tapeLinesGroup.setTranslateX(tapeLinesGroup.getTranslateX() + dx);
        tapeLinesGroup.setTranslateY(tapeLinesGroup.getTranslateY() + dy);
        cellOptionRectangle.setTranslateX(cellOptionRectangle.getTranslateX() + dx);
        cellOptionRectangle.setTranslateY(cellOptionRectangle.getTranslateY() + dy);

        double width = Math.max(Math.abs(-2 * offsetX - this.getMaxWidth()), -2 * offsetX + this.getMaxWidth());
        double height = Math.max(Math.abs(-2 * offsetY - this.getMaxHeight()), -2 * offsetY + this.getMaxHeight());

        tapeLinesGroup.checkLinesAndColumns(width, height);
    }

    Integer getColumn(double x) {
        x += offsetX;
        x -= this.getMaxWidth() / 2;
        x += TuringMachineDrawer.TAPE_CELL_WIDTH / 2;
        if (x < 0)
            x -= TuringMachineDrawer.TAPE_CELL_WIDTH;
        int column = (int) (x / TuringMachineDrawer.TAPE_CELL_WIDTH);
        return (tapeLinesGroup.checkColumn(column)) ? column : null;
    }

    Integer getLine(double y) {
        y += offsetY;
        y -= this.getMaxHeight() / 2;
        y += TuringMachineDrawer.TAPE_CELL_WIDTH / 2;
        if (y < 0)
            y -= TuringMachineDrawer.TAPE_CELL_WIDTH;
        int line = (int) (-y / TuringMachineDrawer.TAPE_CELL_WIDTH);
        return (tapeLinesGroup.checkLine(line)) ? line : null;
    }

    double getX(int column) {
        return -offsetX + this.getMaxWidth() / 2 + column * TuringMachineDrawer.TAPE_CELL_WIDTH;
    }

    double getY(int line) {
        return -offsetY + this.getMaxHeight() / 2 - line * TuringMachineDrawer.TAPE_CELL_WIDTH;
    }

    void openCellOptionRectangle(int line, int column) {
        cellOptionRectangle.setLayoutX(this.getX(column));
        cellOptionRectangle.setLayoutY(this.getY(line) - TuringMachineDrawer.TAPE_CELL_WIDTH / 2
                - TuringMachineDrawer.STATE_OPTION_RECTANGLE_MINIMIZED_HEIGHT / 2);
        cellOptionRectangle.setTranslateX(0);
        cellOptionRectangle.setTranslateY(0);
        cellOptionRectangle.setLineAndColumn(line, column);
        cellOptionRectangle.setVisible(true);
        cellOptionRectangle.maximize();
    }

    void closeCellOptionRectangle(){
        EventHandler<ActionEvent> handler = cellOptionRectangle.timeline.getOnFinished();
        cellOptionRectangle.timeline.setOnFinished(actionEvent -> {
            handler.handle(actionEvent);
            cellOptionRectangle.setVisible(false);
            cellOptionRectangle.timeline.setOnFinished(handler);
        });
        cellOptionRectangle.minimize(true);
    }

    void drawSymbol(int line, int column, String symbol) {
        Map<Integer, Label> h = cellLabels.get(line);
        if(symbol == null) { // White symbol
            if (h == null) // All the column is white
                return;
            Label cellLabel = h.get(column);
            if (cellLabel != null) {
                this.getChildren().remove(cellLabel);
                h.remove(column);
                if (h.size() == 0)
                    cellLabels.remove(line);
            }
        }
        else{
            if (h == null) {
                h = new HashMap<>();
                cellLabels.put(line, h);
            }
            Label cellLabel = h.get(column);

            if(cellLabel == null) {
                cellLabel = new Label(symbol);
                cellLabel.setFont(Font.font(TuringMachineDrawer.TAPE_CELL_OPTION_RECTANGLE_SYMBOL_FONT_NAME,
                        TuringMachineDrawer.TAPE_CELL_SYMBOL_FONT_SIZE));

                cellLabel.setMinWidth(TuringMachineDrawer.TAPE_CELL_WIDTH);
                cellLabel.setMaxWidth(TuringMachineDrawer.TAPE_CELL_WIDTH);
                cellLabel.setMinHeight(TuringMachineDrawer.TAPE_CELL_WIDTH);
                cellLabel.setMaxHeight(TuringMachineDrawer.TAPE_CELL_WIDTH);
                cellLabel.setAlignment(Pos.CENTER);

                this.getChildren().add(cellLabel);
                cellLabel.setLayoutX(getX(column)- cellLabel.getMinWidth() / 2);
                cellLabel.setLayoutY(getY(line)- cellLabel.getMinHeight() / 2);

                h.put(column, cellLabel);
            }
            else
                cellLabel.setText(symbol);
        }

    }

    public void moveHead(int line, int column, CellOptionRectangleHeadRectangle head) {

        Rectangle headRectangle = heads.get(head);
        if(headRectangle == null){
            headRectangle = new Rectangle(0, 0,
                    TuringMachineDrawer.TAPE_CELL_HEAD_SIZE, TuringMachineDrawer.TAPE_CELL_HEAD_SIZE);
            headRectangle.setFill(Color.TRANSPARENT);
            headRectangle.setStroke(head.getStroke());
            headRectangle.setStrokeWidth(TuringMachineDrawer.TAPE_CELL_HEAD_STROKE_WIDTH);
            heads.put(head, headRectangle);
            this.getChildren().add(headRectangle);
        }
        headRectangle.setLayoutX(getX(column) - TuringMachineDrawer.TAPE_CELL_HEAD_SIZE / 2);
        headRectangle.setLayoutY(getY(line) - TuringMachineDrawer.TAPE_CELL_HEAD_SIZE / 2);
    }
}

class TapeLinesGroup extends Group {

    private TapePane pane;

    private ArrayList<Line> lines;
    private ArrayList<Line> columns;

    Integer bottom;
    Integer top;
    Integer left;
    Integer right;

    private double maxWidth;
    private double maxHeight;

    TapeLinesGroup(TapePane pane) {
        this.pane = pane;
        this.maxWidth = 0;
        this.maxHeight = 0;

        lines = new ArrayList<>();
        columns = new ArrayList<>();

        setLeft(TuringMachineDrawer.TAPE_DEFAULT_LEFT);
        setRight(TuringMachineDrawer.TAPE_DEFAULT_RIGHT);
        setBottom(TuringMachineDrawer.TAPE_DEFAULT_BOTTOM);
        setTop(TuringMachineDrawer.TAPE_DEFAULT_TOP);

    }

    void setBottom(Integer bottom) {
        this.bottom = bottom;
        int zeroIndex = lines.size() / 2;
        for (int i = 0; i <= zeroIndex - 1; i++)
            lines.get(i).setVisible(checkBottom(zeroIndex - 1 - i));
    }

    void setTop(Integer top) {
        this.top = top;
        int zeroIndex = lines.size() / 2;
        for (int i = zeroIndex; i < lines.size(); i++)
            lines.get(i).setVisible(checkTop(i - zeroIndex));
    }

    void setLeft(Integer left) {
        this.left = left;
        int zeroIndex = columns.size() / 2;
        for (int i = 0; i <= zeroIndex - 1; i++)
            columns.get(i).setVisible(checkLeft(zeroIndex - 1 - i));
    }

    void setRight(Integer right) {
        this.right = right;
        int zeroIndex = columns.size() / 2;
        for (int i = zeroIndex; i < columns.size(); i++)
            columns.get(i).setVisible(checkRight(i - zeroIndex));
    }

    boolean checkColumn(int i) {
        return checkLeft(-i) && checkRight(i);
    }

    boolean checkLine(int i) {
        return checkBottom(-i) && checkTop(i);
    }

    private boolean checkBottom(int i) {
        return checkDirection(i, bottom);
    }

    private boolean checkTop(int i) {
        return checkDirection(i, top);
    }

    private boolean checkLeft(int i) {
        return checkDirection(i, left);
    }

    private boolean checkRight(int i) {
        return checkDirection(i, right);
    }

    private boolean checkDirection(int i, Integer directionBound) {
        return directionBound == null || i <= directionBound;
    }

    void checkLinesAndColumns(double width, double height) {
        ObservableList<Node> children = this.getChildren();

        int nbLines = 2 * (int) ((height / (TuringMachineDrawer.TAPE_CELL_WIDTH * 2) - 0.5) + 2);
        int prevNbLines = lines.size();

        int nbColumns = 2 * (int) ((width / (TuringMachineDrawer.TAPE_CELL_WIDTH * 2) - 0.5) + 2);
        int prevNbColumns = columns.size();

        double h, w;
        double leftWidth = (left == null) ? width : (TuringMachineDrawer.TAPE_CELL_WIDTH * (left + 0.5));
        double rightWidth = (right == null) ? width : (TuringMachineDrawer.TAPE_CELL_WIDTH * (right + 0.5));

        if (maxWidth < width) {
            for (Line line : lines) {
                line.setStartX(-leftWidth);
                line.setEndX(rightWidth);
            }
        }

        if (prevNbLines < nbLines) {
            for (int i = lines.size() / 2; i < nbLines / 2; i++) {
                h = TuringMachineDrawer.TAPE_CELL_WIDTH * (0.5 + i);
                Line linePos = new Line(-leftWidth, h, rightWidth, h);
                Line lineNeg = new Line(-leftWidth, -h, rightWidth, -h);

                linePos.setVisible(checkTop(i));
                lineNeg.setVisible(checkBottom(i));

                lines.add(linePos);
                lines.add(0, lineNeg);
                children.add(lineNeg);
                children.add(linePos);
            }
        }

        double bottomHeight = (bottom == null) ? height : TuringMachineDrawer.TAPE_CELL_WIDTH * (bottom + 0.5);
        double topHeight = (top == null) ? height : TuringMachineDrawer.TAPE_CELL_WIDTH * (top + 0.5);

        if (maxHeight < height) {
            for (Line column : columns) {
                column.setStartY(-bottomHeight);
                column.setEndY(topHeight);
            }
        }

        if (prevNbColumns < nbColumns) {
            for (int i = columns.size() / 2; i < nbColumns / 2; i++) {
                w = TuringMachineDrawer.TAPE_CELL_WIDTH * (0.5 + i);
                Line columnPos = new Line(w, -bottomHeight, w, topHeight);
                Line columnNeg = new Line(-w, -bottomHeight, -w, topHeight);

                columnPos.setVisible(checkRight(i));
                columnNeg.setVisible(checkLeft(i));

                columns.add(columnPos);
                columns.add(0, columnNeg);

                children.add(columnPos);
                children.add(columnNeg);
            }
        }

        maxWidth = Math.max(maxWidth, width);
        maxHeight = Math.max(maxHeight, height);

    }
}

class Cell extends Rectangle {

    int i;
    int j;

}
