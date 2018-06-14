package gui;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.util.*;

/**
 * Created by dimitri.watel on 07/06/18.
 */
class TapeBorderPanesHBox extends HBox{

    TapeBorderPanesHBox(TuringMachineDrawer drawer){
        this.getChildren().add(new TapeBorderPane(drawer));

        this.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            double width = newVal.getWidth();
            double height = newVal.getHeight();
            for(Node child: this.getChildren()){
                TapeBorderPane tapePane = (TapeBorderPane) child;
                tapePane.setMinHeight(height);
                tapePane.setMaxHeight(height);
                tapePane.setMinWidth(width);
                tapePane.setMaxWidth(width);
            }
        });

    }

    void moveHead(int tape, int line, int column, int head){
        TapeBorderPane tapeBorderPane = (TapeBorderPane) this.getChildren().get(tape);
        tapeBorderPane.tapePane.moveHead(line, column, head);
    }

    void addHead(int tape, int line, int column, Color color){
        TapeBorderPane tapeBorderPane = (TapeBorderPane) this.getChildren().get(tape);
        tapeBorderPane.tapePane.addHead(line, column, color);
    }

    public void translateTo(int tape, int head) {
        TapeBorderPane tapeBorderPane = (TapeBorderPane) this.getChildren().get(tape);
        tapeBorderPane.translateTo(head);
    }

    public void editHeadColor(int tape, int head, Color color) {
        TapeBorderPane tapeBorderPane = (TapeBorderPane) this.getChildren().get(tape);
        tapeBorderPane.tapePane.editHeadColor(head, color);
    }
}

class TapeBorderPane extends BorderPane {
    TuringMachineDrawer drawer;

    Integer bottom;
    Integer top;
    Integer left;
    Integer right;

    double offsetX;
    double offsetY;

    TapePane tapePane;
    private HorizontalCoordinates horizontalCoordinates;
    private VerticalCoordinates verticalCoordinates;

    double maxWidth;
    double maxHeight;

    TapeBorderPane(TuringMachineDrawer drawer) {
        this.drawer = drawer;

        horizontalCoordinates = new HorizontalCoordinates(drawer, this);
        horizontalCoordinates.setMinHeight(TuringMachineDrawer.TAPE_COORDINATES_WIDTH);
        horizontalCoordinates.setMaxHeight(TuringMachineDrawer.TAPE_COORDINATES_WIDTH);
        horizontalCoordinates.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        horizontalCoordinates.setTranslateX(TuringMachineDrawer.TAPE_COORDINATES_WIDTH);

        verticalCoordinates = new VerticalCoordinates(drawer, this);
        verticalCoordinates.setMinWidth(TuringMachineDrawer.TAPE_COORDINATES_WIDTH);
        verticalCoordinates.setMaxWidth(TuringMachineDrawer.TAPE_COORDINATES_WIDTH);
        verticalCoordinates.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        tapePane = new TapePane(drawer, this);

        this.setOnMousePressed(drawer.tapesMouseHandler);
        this.setOnMouseDragged(drawer.tapesMouseHandler);

        this.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            double width = newVal.getWidth()
                    - TuringMachineDrawer.TAPE_COORDINATES_WIDTH;
            double height = newVal.getHeight()
                    - TuringMachineDrawer.TAPE_COORDINATES_WIDTH;
            tapePane.setMinHeight(height);
            tapePane.setMaxHeight(height);
            tapePane.setMinWidth(width);
            tapePane.setMaxWidth(width);
            horizontalCoordinates.setMinWidth(width);
            horizontalCoordinates.setMaxWidth(width);
            verticalCoordinates.setMinHeight(height);
            verticalCoordinates.setMaxHeight(height);


            this.checkLinesAndColumns(width, height);
        });

        this.setTop(horizontalCoordinates);
        this.setLeft(verticalCoordinates);
        this.setCenter(tapePane);

        setTapeLeftBound(TuringMachineDrawer.TAPE_DEFAULT_LEFT);
        setTapeRightBound(TuringMachineDrawer.TAPE_DEFAULT_RIGHT);
        setTapeBottomBound(TuringMachineDrawer.TAPE_DEFAULT_BOTTOM);
        setTapeTopBound(TuringMachineDrawer.TAPE_DEFAULT_TOP);

    }

    void setTapeLeftBound(Integer left) {
        this.left = left;
        this.tapePane.setTapeLeftBound(left);
    }

    void setTapeRightBound(Integer right) {
        this.right = right;
        this.tapePane.setTapeRightBound(right);
    }

    void setTapeBottomBound(Integer bottom) {
        this.bottom = bottom;
        this.tapePane.setTapeBottomBound(bottom);
    }

    void setTapeTopBound(Integer top) {
        this.top = top;
        this.tapePane.setTapeTopBound(top);
    }

    void translate(double dx, double dy) {
        offsetX -= dx;
        offsetY -= dy;

        double width = Math.max(Math.abs(-2 * offsetX - this.getMaxWidth()), -2 * offsetX + this.getMaxWidth());
        double height = Math.max(Math.abs(-2 * offsetY - this.getMaxHeight()), -2 * offsetY + this.getMaxHeight());

        tapePane.translate(dx, dy);
        horizontalCoordinates.translate(dx);
        verticalCoordinates.translate(dy);

        tapePane.checkLinesAndColumns(width, height);
        horizontalCoordinates.checkColumn(width);
        verticalCoordinates.checkLines(height);
    }

    void translateTo(int head){
        int line = tapePane.lineOf(head);
        int column = tapePane.columnOf(head);

        double x = getXOf(column);
        double y = getYOf(line);

        translate(-x + offsetX, -y + offsetY);
    }

    void checkLinesAndColumns(double width, double height){
        tapePane.checkLinesAndColumns(width, height);
        horizontalCoordinates.checkColumn(width);
        verticalCoordinates.checkLines(height);
        maxWidth = Math.max(maxWidth, width);
        maxHeight = Math.max(maxHeight, height);
    }

    boolean checkDirection(int index, Integer direction){
        return direction == null || index <= direction;
    }

    double getXOf(int column) {
        return column * TuringMachineDrawer.TAPE_CELL_WIDTH;
    }

    double getYOf(int line) {
        return -line * TuringMachineDrawer.TAPE_CELL_WIDTH;
    }
}

class HorizontalCoordinates extends Pane {
    private Group coordinatesGroup;

    private List<Line> columns;
    private List<Label> abscissae;
    private TapeBorderPane tapeBorderPane;

    HorizontalCoordinates(TuringMachineDrawer drawer, TapeBorderPane tapeBorderPane) {
        coordinatesGroup = new Group();
        this.tapeBorderPane = tapeBorderPane;

        columns = new ArrayList<>();
        abscissae = new ArrayList<>();

        this.getChildren().add(coordinatesGroup);

        Rectangle coordinatesClip = new Rectangle();
        this.setClip(coordinatesClip);

        this.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            double width = this.getMaxWidth();
            double height = this.getMaxHeight();
            coordinatesGroup.setLayoutX(width / 2);
            coordinatesGroup.setLayoutY(height / 2);
            coordinatesClip.setWidth(width);
            coordinatesClip.setHeight(height);
        });

    }

    void translate(double dx) {
        coordinatesGroup.setTranslateX(coordinatesGroup.getTranslateX() + dx);
    }

    void checkColumn(double width) {
        ObservableList<Node> children = coordinatesGroup.getChildren();

        int nbColumns = 2 * (int) ((width / (TuringMachineDrawer.TAPE_CELL_WIDTH * 2) - 0.5) + 2);
        int prevNbColumns = columns.size();

        double w;

        if (prevNbColumns < nbColumns) {
            for (int i = columns.size() / 2; i < nbColumns / 2; i++) {
                w = TuringMachineDrawer.TAPE_CELL_WIDTH * (0.5 + i);
                Line columnPos = new Line(w, -TuringMachineDrawer.TAPE_COORDINATES_WIDTH / 2,
                        w, TuringMachineDrawer.TAPE_COORDINATES_WIDTH / 2);
                Line columnNeg = new Line(-w, -TuringMachineDrawer.TAPE_COORDINATES_WIDTH / 2,
                        -w, TuringMachineDrawer.TAPE_COORDINATES_WIDTH / 2);

                columns.add(columnPos);
                columns.add(0, columnNeg);

                children.add(columnPos);
                children.add(columnNeg);

                Label labelPos = new Label(String.valueOf(i));
                labelPos.setLayoutX(w - TuringMachineDrawer.TAPE_CELL_WIDTH);
                labelPos.setLayoutY(- TuringMachineDrawer.TAPE_COORDINATES_WIDTH / 2);
                labelPos.setMinWidth(TuringMachineDrawer.TAPE_CELL_WIDTH);
                labelPos.setMaxWidth(TuringMachineDrawer.TAPE_CELL_WIDTH);
                labelPos.setMinHeight(TuringMachineDrawer.TAPE_COORDINATES_WIDTH);
                labelPos.setMaxHeight(TuringMachineDrawer.TAPE_COORDINATES_WIDTH);
                labelPos.setAlignment(Pos.CENTER);
                abscissae.add(labelPos);
                children.add(labelPos);

                if(i != 0){
                    Label labelNeg = new Label(String.valueOf(-i));
                    labelNeg.setLayoutX(-w);
                    labelNeg.setLayoutY(- TuringMachineDrawer.TAPE_COORDINATES_WIDTH / 2);
                    labelNeg.setMinWidth(TuringMachineDrawer.TAPE_CELL_WIDTH);
                    labelNeg.setMaxWidth(TuringMachineDrawer.TAPE_CELL_WIDTH);
                    labelNeg.setMinHeight(TuringMachineDrawer.TAPE_COORDINATES_WIDTH);
                    labelNeg.setMaxHeight(TuringMachineDrawer.TAPE_COORDINATES_WIDTH);
                    labelNeg.setAlignment(Pos.CENTER);
                    abscissae.add(labelNeg);
                    children.add(labelNeg);
                }
            }
        }

    }
}

class VerticalCoordinates extends Pane {
    private Group coordinatesGroup;

    private List<Line> lines;
    private List<Label> ordinates;
    private TapeBorderPane tapeBorderPane;

    VerticalCoordinates(TuringMachineDrawer drawer, TapeBorderPane tapeBorderPane) {
        coordinatesGroup = new Group();
        this.tapeBorderPane = tapeBorderPane;

        lines = new ArrayList<>();
        ordinates = new ArrayList<>();

        this.getChildren().add(coordinatesGroup);

        Rectangle coordinatesClip = new Rectangle();
        this.setClip(coordinatesClip);

        this.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            double width = this.getMaxWidth();
            double height = this.getMaxHeight();
            coordinatesGroup.setLayoutX(width / 2);
            coordinatesGroup.setLayoutY(height / 2);
            coordinatesClip.setWidth(width);
            coordinatesClip.setHeight(height);
        });

    }

    void translate(double dy) {
        coordinatesGroup.setTranslateY(coordinatesGroup.getTranslateY() + dy);
    }

    void checkLines(double height) {
        ObservableList<Node> children = coordinatesGroup.getChildren();

        int nbLines = 2 * (int) ((height / (TuringMachineDrawer.TAPE_CELL_WIDTH * 2) - 0.5) + 2);
        int prevNbLines = lines.size();

        double h;

        if (prevNbLines < nbLines) {
            for (int i = lines.size() / 2; i < nbLines / 2; i++) {
                h = TuringMachineDrawer.TAPE_CELL_WIDTH * (0.5 + i);
                Line linePos = new Line(-TuringMachineDrawer.TAPE_COORDINATES_WIDTH / 2, h,
                        TuringMachineDrawer.TAPE_COORDINATES_WIDTH / 2, h);
                Line lineNeg = new Line(-TuringMachineDrawer.TAPE_COORDINATES_WIDTH / 2, -h,
                        TuringMachineDrawer.TAPE_COORDINATES_WIDTH / 2, -h);

                lines.add(linePos);
                lines.add(0, lineNeg);

                children.add(linePos);
                children.add(lineNeg);

                Label labelPos = new Label(String.valueOf(i));
                labelPos.setLayoutX(- TuringMachineDrawer.TAPE_COORDINATES_WIDTH / 2);
                labelPos.setLayoutY(-h );
                labelPos.setMinWidth(TuringMachineDrawer.TAPE_COORDINATES_WIDTH);
                labelPos.setMaxWidth(TuringMachineDrawer.TAPE_COORDINATES_WIDTH);
                labelPos.setMinHeight(TuringMachineDrawer.TAPE_CELL_WIDTH);
                labelPos.setMaxHeight(TuringMachineDrawer.TAPE_CELL_WIDTH);
                labelPos.setAlignment(Pos.CENTER);
                ordinates.add(labelPos);
                children.add(labelPos);

                if(i != 0){
                    Label labelNeg = new Label(String.valueOf(-i));
                    labelNeg.setLayoutX(- TuringMachineDrawer.TAPE_COORDINATES_WIDTH / 2);
                    labelNeg.setLayoutY(h - TuringMachineDrawer.TAPE_CELL_WIDTH);
                    labelNeg.setMinWidth(TuringMachineDrawer.TAPE_COORDINATES_WIDTH);
                    labelNeg.setMaxWidth(TuringMachineDrawer.TAPE_COORDINATES_WIDTH);
                    labelNeg.setMinHeight(TuringMachineDrawer.TAPE_CELL_WIDTH);
                    labelNeg.setMaxHeight(TuringMachineDrawer.TAPE_CELL_WIDTH);
                    labelNeg.setAlignment(Pos.CENTER);
                    ordinates.add(labelNeg);
                    children.add(labelNeg);
                }
            }
        }

    }
}

class TapePane extends Pane {

    TuringMachineDrawer drawer;
    private Group tapeLinesGroup;

    private List<Line> lines;
    private List<Line> columns;

    CellOptionRectangle cellOptionRectangle;

    private TapeBorderPane tapeBorderPane;

    private Map<Integer, Map<Integer, Label>> cellLabels;
    private List<Rectangle> heads;
    private Map<Rectangle, Integer> headsColumns;
    private Map<Rectangle, Integer> headsLines;

    TapePane(TuringMachineDrawer drawer, TapeBorderPane tapeBorderPane) {
        this.tapeBorderPane = tapeBorderPane;
        this.drawer = drawer;
        this.cellLabels = new HashMap<>();
        this.heads = new ArrayList<>();
        this.headsColumns = new HashMap<>();
        this.headsLines = new HashMap<>();

        lines = new ArrayList<>();
        columns = new ArrayList<>();
        this.setOnMouseClicked(drawer.tapesMouseHandler);

        tapeLinesGroup = new Group();

        cellOptionRectangle = new CellOptionRectangle(drawer, this);
        cellOptionRectangle.setVisible(false);
        cellOptionRectangle.setOnMouseClicked(drawer.tapesMouseHandler);
        tapeLinesGroup.getChildren().add(cellOptionRectangle);

        this.getChildren().addAll(tapeLinesGroup);

        Rectangle tapesClip = new Rectangle();
        this.setClip(tapesClip);

        this.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            double width = this.getMaxWidth();
            double height = this.getMaxHeight();
            tapeLinesGroup.setLayoutX(width / 2);
            tapeLinesGroup.setLayoutY(height / 2);
            tapesClip.setWidth(width);
            tapesClip.setHeight(height);
        });

    }

    void translate(double dx, double dy) {
        tapeLinesGroup.setTranslateX(tapeLinesGroup.getTranslateX() + dx);
        tapeLinesGroup.setTranslateY(tapeLinesGroup.getTranslateY() + dy);
    }

    void openCellOptionRectangle(int line, int column) {
        cellOptionRectangle.setLayoutX(tapeBorderPane.getXOf(column));
        cellOptionRectangle.setLayoutY(tapeBorderPane.getYOf(line) - TuringMachineDrawer.TAPE_CELL_WIDTH / 2
                - TuringMachineDrawer.STATE_OPTION_RECTANGLE_MINIMIZED_HEIGHT / 2);
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

    void setTapeLeftBound(Integer left) {
        int zeroIndex = columns.size() / 2;
        for (int i = 0; i <= zeroIndex - 1; i++)
            columns.get(i).setVisible(tapeBorderPane.checkDirection(zeroIndex - 1 - i, left));
    }

    void setTapeRightBound(Integer right) {
        int zeroIndex = columns.size() / 2;
        for (int i = zeroIndex; i < columns.size(); i++)
            columns.get(i).setVisible(tapeBorderPane.checkDirection(i - zeroIndex, right));
    }

    void setTapeBottomBound(Integer bottom) {
        int zeroIndex = lines.size() / 2;
        for (int i = 0; i <= zeroIndex - 1; i++)
            lines.get(i).setVisible(tapeBorderPane.checkDirection(zeroIndex - 1 - i, bottom));
    }

    void setTapeTopBound(Integer top) {
        int zeroIndex = lines.size() / 2;
        for (int i = zeroIndex; i < lines.size(); i++)
            lines.get(i).setVisible(tapeBorderPane.checkDirection(i - zeroIndex, top));
    }

    void checkLinesAndColumns(double width, double height) {
        ObservableList<Node> children = tapeLinesGroup.getChildren();

        int nbLines = 2 * (int) ((height / (TuringMachineDrawer.TAPE_CELL_WIDTH * 2) - 0.5) + 2);
        int prevNbLines = lines.size();

        int nbColumns = 2 * (int) ((width / (TuringMachineDrawer.TAPE_CELL_WIDTH * 2) - 0.5) + 2);
        int prevNbColumns = columns.size();

        double h, w;
        double leftWidth = (tapeBorderPane.left == null) ? width : (TuringMachineDrawer.TAPE_CELL_WIDTH * (tapeBorderPane.left + 0.5));
        double rightWidth = (tapeBorderPane.right == null) ? width : (TuringMachineDrawer.TAPE_CELL_WIDTH * (tapeBorderPane.right + 0.5));

        if (tapeBorderPane.maxWidth < width) {
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

                linePos.setVisible(tapeBorderPane.checkDirection(i, tapeBorderPane.top));
                lineNeg.setVisible(tapeBorderPane.checkDirection(i, tapeBorderPane.bottom));

                lines.add(linePos);
                lines.add(0, lineNeg);
                children.add(lineNeg);
                children.add(linePos);
            }
        }

        double bottomHeight = (tapeBorderPane.bottom == null) ? height : TuringMachineDrawer.TAPE_CELL_WIDTH * (tapeBorderPane.bottom + 0.5);
        double topHeight = (tapeBorderPane.top == null) ? height : TuringMachineDrawer.TAPE_CELL_WIDTH * (tapeBorderPane.top + 0.5);

        if (tapeBorderPane.maxHeight < height) {
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

                columnPos.setVisible(tapeBorderPane.checkDirection(i, tapeBorderPane.right));
                columnNeg.setVisible(tapeBorderPane.checkDirection(i, tapeBorderPane.left));

                columns.add(columnPos);
                columns.add(0, columnNeg);

                children.add(columnPos);
                children.add(columnNeg);
            }
        }

    }

    Integer getColumn(double x) {
        x += tapeBorderPane.offsetX;
        x -= this.getMaxWidth() / 2;
        x += TuringMachineDrawer.TAPE_CELL_WIDTH / 2;
        if (x < 0)
            x -= TuringMachineDrawer.TAPE_CELL_WIDTH;
        int column = (int) (x / TuringMachineDrawer.TAPE_CELL_WIDTH);
        return (column >= 0?tapeBorderPane.checkDirection(column, tapeBorderPane.right)
                :tapeBorderPane.checkDirection(column, tapeBorderPane.left)) ? column : null;
    }

    Integer getLine(double y) {
        y += tapeBorderPane.offsetY;
        y -= this.getMaxHeight() / 2;
        y += TuringMachineDrawer.TAPE_CELL_WIDTH / 2;
        if (y < 0)
            y -= TuringMachineDrawer.TAPE_CELL_WIDTH;
        int line = (int) (-y / TuringMachineDrawer.TAPE_CELL_WIDTH);
        return (line >= 0?tapeBorderPane.checkDirection(line, tapeBorderPane.top):
                tapeBorderPane.checkDirection(line, tapeBorderPane.bottom)) ? line : null;
    }


    void drawSymbol(int line, int column, String symbol) {
        Map<Integer, Label> h = cellLabels.get(line);
        if(symbol == null) { // White symbol
            if (h == null) // All the column is white
                return;
            Label cellLabel = h.get(column);
            if (cellLabel != null) {
                tapeLinesGroup.getChildren().remove(cellLabel);
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

                tapeLinesGroup.getChildren().add(cellLabel);
                cellLabel.setLayoutX(tapeBorderPane.getXOf(column)- cellLabel.getMinWidth() / 2);
                cellLabel.setLayoutY(tapeBorderPane.getYOf(line)- cellLabel.getMinHeight() / 2);

                h.put(column, cellLabel);
            }
            else
                cellLabel.setText(symbol);
        }

    }

    void moveHead(int line, int column, int head) {
        Rectangle headRectangle = heads.get(head);
        headRectangle.setLayoutX(tapeBorderPane.getXOf(column) - TuringMachineDrawer.TAPE_CELL_HEAD_SIZE / 2);
        headRectangle.setLayoutY(tapeBorderPane.getYOf(line) - TuringMachineDrawer.TAPE_CELL_HEAD_SIZE / 2);

        headsLines.put(headRectangle, line);
        headsColumns.put(headRectangle, column);
    }

    void addHead(int line, int column, Color color) {
        Rectangle headRectangle = new Rectangle(0, 0,
            TuringMachineDrawer.TAPE_CELL_HEAD_SIZE, TuringMachineDrawer.TAPE_CELL_HEAD_SIZE);
        headRectangle.setFill(Color.TRANSPARENT);
        headRectangle.setStroke(color);
        headRectangle.setStrokeWidth(TuringMachineDrawer.TAPE_CELL_HEAD_STROKE_WIDTH);
        heads.add(headRectangle);

        cellOptionRectangle.addHead(color);

        headRectangle.setVisible(true);
        tapeLinesGroup.getChildren().add(headRectangle);
        this.moveHead(line, column, heads.size() - 1);

        headsLines.put(headRectangle, line);
        headsColumns.put(headRectangle, column);
    }

    public void editHeadColor(int head, Color color) {
        Rectangle headRectangle = heads.get(head);
        headRectangle.setStroke(color);

        cellOptionRectangle.editHeadColor(head, color);
    }

    int lineOf(int head){
        return headsLines.get(heads.get(head));
    }

    int columnOf(int head){
        return headsColumns.get(heads.get(head));
    }
}

class Cell extends Rectangle {

    int i;
    int j;

}
