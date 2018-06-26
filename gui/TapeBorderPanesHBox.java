package gui;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;
import turingmachines.Tape;
import turingmachines.TuringMachine;
import util.Subscriber;

import java.util.*;

/**
 * Created by dimitri.watel on 07/06/18.
 */
class TapeBorderPanesHBox extends HBox{

    TuringMachineDrawer drawer;
    private Map<Tape, TapeBorderPane> tapes;

    TapeBorderPanesHBox(TuringMachineDrawer drawer){
        this.drawer = drawer;
        this.tapes = new HashMap<>();
        this.setAlignment(Pos.CENTER_LEFT);

        this.layoutBoundsProperty().addListener((obs, oldVal, newVal) ->
            resizeChildren(newVal.getWidth(), newVal.getHeight())
        );

        Subscriber s = new Subscriber() {
            @Override
            public void read(String msg, Object... parameters) {
                Tape tape = (Tape) parameters[1];
                switch (msg) {
                    case TuringMachine.SUBSCRIBER_MSG_HEAD_INITIAL_POSITION_CHANGED: {
                        Integer head = (Integer) parameters[2];
                        Integer line = (Integer) parameters[3];
                        Integer column = (Integer) parameters[4];
                        moveHeadFromMachine(tape, line, column, head);
                    }
                        break;
                    case TuringMachine.SUBSCRIBER_MSG_INPUT_CHANGED: {
                        Integer line = (Integer) parameters[2];
                        Integer column = (Integer) parameters[3];
                        String symbol = (String) parameters[4];
                        setInputSymbolFromMachine(tape, line, column, symbol);
                    }
                        break;
                    case TuringMachine.SUBSCRIBER_MSG_TAPE_LEFT_CHANGED:
                        Integer left = (Integer) parameters[2];
                        setLeftFromMachine(tape, left);
                        break;
                    case TuringMachine.SUBSCRIBER_MSG_TAPE_RIGHT_CHANGED:
                        Integer right = (Integer) parameters[2];
                        setRightFromMachine(tape, right);
                        break;
                    case TuringMachine.SUBSCRIBER_MSG_TAPE_BOTTOM_CHANGED:
                        Integer bottom = (Integer) parameters[2];
                        setBottomFromMachine(tape, bottom);
                        break;
                    case TuringMachine.SUBSCRIBER_MSG_TAPE_TOP_CHANGED:
                        Integer top = (Integer) parameters[2];
                        setTopFromMachine(tape, top);
                        break;
                }
            }
        };

        s.subscribe(TuringMachine.SUBSCRIBER_MSG_HEAD_INITIAL_POSITION_CHANGED);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_INPUT_CHANGED);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_TAPE_LEFT_CHANGED);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_TAPE_RIGHT_CHANGED);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_TAPE_BOTTOM_CHANGED);
        s.subscribe(TuringMachine.SUBSCRIBER_MSG_TAPE_TOP_CHANGED);

    }

    void resizeChildren(double width, double height){
        if(this.getChildren().size() == 1){
            Node child = this.getChildren().get(0);

            this.setSpacing(0);
            TapeBorderPane tapeBorderPane = (TapeBorderPane) child;
            tapeBorderPane.setMinHeight(height);
            tapeBorderPane.setMaxHeight(height);
            tapeBorderPane.setMinWidth(width);
            tapeBorderPane.setMaxWidth(width);
        }
        else {

            double leavingSpace = width * (1 - TuringMachineDrawer.TAPE_WIDTH_RATIO) / 2;
            this.setSpacing((leavingSpace - TuringMachineDrawer.TAPE_HOBX_ARROW_WIDTH) * 2 / 3);

            for (Node child : this.getChildren()) {
                if(!(child instanceof TapeBorderPane))
                    continue;
                TapeBorderPane tapeBorderPane = (TapeBorderPane) child;
                tapeBorderPane.setMinHeight(height);
                tapeBorderPane.setMaxHeight(height);
                tapeBorderPane.setMinWidth(width * TuringMachineDrawer.TAPE_WIDTH_RATIO);
                tapeBorderPane.setMaxWidth(width * TuringMachineDrawer.TAPE_WIDTH_RATIO);
            }
        }
    }

    void addSymbol(String symbol){
        for(TapeBorderPane tapeBorderPane: tapes.values())
            tapeBorderPane.tapePane.addSymbol(symbol);
    }

    void editSymbol(int index, String previousSymbol, String symbol){
        for(TapeBorderPane tapeBorderPane: tapes.values()) {
            tapeBorderPane.tapePane.editSymbol(index, previousSymbol, symbol);
        }
    }

    void removeSymbol(int index, String symbol) {
        for(TapeBorderPane tapeBorderPane: tapes.values()) {
            tapeBorderPane.tapePane.removeSymbol(index, symbol);
        }
    }

    void addTape(Tape tape){
        TapeBorderPane tapeBorderPane = new TapeBorderPane(this.drawer, tape);

        tapeBorderPane.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 1, 0, 0))));

        tapes.put(tape, tapeBorderPane);

        if(this.getChildren().size() != 0){
            TapeBorderPane previousTapeBorderPane = (TapeBorderPane) this.getChildren().get(
                    this.getChildren().size() - 1);
            for(Node child : this.getChildren())
                child.setTranslateX(0);
            this.getChildren().addAll(
                    new RightArrow(this.drawer, this, tapeBorderPane),
                    new LeftArrow(this.drawer, this, previousTapeBorderPane)
            );
        }

        this.getChildren().add(tapeBorderPane);
        this.resizeChildren(this.getMaxWidth(), this.getMaxHeight());
    }

    void removeTape(Tape tape){
        TapeBorderPane tapeBorderPane = tapes.get(tape);
        int index = this.getChildren().indexOf(tapeBorderPane);
        this.getChildren().remove(index);
        int size = this.getChildren().size();

        if(index == 0){
            if(size > 1) {
                this.getChildren().remove(0);
                this.getChildren().remove(0);
            }
        }
        else if(size > 1) {
            this.getChildren().remove(index - 1);
            this.getChildren().remove(index - 2);

            if(size > index){
                ((TranslateTapesArrow)this.getChildren().get(index - 1)).tapeBorderPane =
                        (TapeBorderPane) this.getChildren().get(index - 3);
            }
        }

        this.resizeChildren(this.getMaxWidth(), this.getMaxHeight());
        if(size != 0) {
            if(index == 0)
                this.centerOn(((TapeBorderPane) this.getChildren().get(index)).tape);
            else
                this.centerOn(((TapeBorderPane) this.getChildren().get(index - 3)).tape);
        }
    }

    private void moveHeadFromMachine(Tape tape, int line, int column, int head){
        TapeBorderPane tapeBorderPane = tapes.get(tape);
        tapeBorderPane.tapePane.moveHead(line, column, head);
    }

    void addHead(Tape tape, int line, int column, Color color){
        TapeBorderPane tapeBorderPane = tapes.get(tape);
        tapeBorderPane.tapePane.addHead(line, column, color);
    }

    void editHeadColor(Tape tape, int head, Color color) {
        TapeBorderPane tapeBorderPane = tapes.get(tape);
        tapeBorderPane.tapePane.editHeadColor(head, color);
    }

    void removeHead(Tape tape, int head){
        TapeBorderPane tapeBorderPane = tapes.get(tape);
        tapeBorderPane.tapePane.removeHead(head);
    }

    private void setInputSymbolFromMachine(Tape tape, int line, int column, String symbol){
        TapeBorderPane tapeBorderPane = tapes.get(tape);
        tapeBorderPane.tapePane.drawSymbol(line, column, symbol);
    }

    private void setLeftFromMachine(Tape tape, Integer left){
        TapeBorderPane tapeBorderPane = tapes.get(tape);
        tapeBorderPane.setTapeLeftBound(left);
    }

    private void setRightFromMachine(Tape tape, Integer right){
        TapeBorderPane tapeBorderPane = tapes.get(tape);
        tapeBorderPane.setTapeRightBound(right);
    }

    private void setBottomFromMachine(Tape tape, Integer bottom){
        TapeBorderPane tapeBorderPane = tapes.get(tape);
        tapeBorderPane.setTapeBottomBound(bottom);
    }

    private void setTopFromMachine(Tape tape, Integer top){
        TapeBorderPane tapeBorderPane = tapes.get(tape);
        tapeBorderPane.setTapeTopBound(top);
    }

    void centerOn(Tape tape, int head) {
        TapeBorderPane tapeBorderPane = tapes.get(tape);
        this.drawer.tapesPane.centerOn(tape);
        tapeBorderPane.centerOn(head);
    }

    void centerOn(Tape tape){
        TapeBorderPane tapeBorderPane = tapes.get(tape);
        double x = tapeBorderPane.getTranslateX();

        int index = this.getChildren().indexOf(tapeBorderPane) / 3;
        double leavingSpace =
                (this.getChildren().size() == 1)?0:this.getMaxWidth() * ((1 - TuringMachineDrawer.TAPE_WIDTH_RATIO) / 2);

        double targetX = -(index * this.getMaxWidth()) + leavingSpace;


        this.translate(targetX - x);
    }

    private void translate(double dx){
        if(dx == 0)
            return;

        for(Node child : this.getChildren())
            child.setTranslateX(child.getTranslateX() + dx);
    }

    void closeAllOptionRectangle() {
        for(TapeBorderPane tapeBorderPane : tapes.values())
            tapeBorderPane.closeAllOptionRectangle();
    }

    KeyFrame getHeadWriteKeyFrame(Tape tape, Integer head) {
        return tapes.get(tape).getHeadWriteKeyFrame(head);
    }

    KeyFrame getMoveHeadKeyFrame(Tape tape, Integer head, Integer line, Integer column) {
        return tapes.get(tape).getMoveHeadKeyFrame(head, line, column);
    }

    Timeline getWriteSymbolTimeline(Tape tape, Integer line, Integer column, String symbol) {
        return tapes.get(tape).getWriteSymbolTimeline(line, column, symbol);
    }

    JSONArray getJSON() {
        JSONArray jsonArray = new JSONArray();
        for(Node child: this.getChildren()) {
            if(!(child instanceof TapeBorderPane))
                continue;
            jsonArray.put(((TapeBorderPane)child).getJSON());
        }
        return jsonArray;
    }

    void clear() {
        closeAllOptionRectangle();
        for(TapeBorderPane tapeBorderPane : tapes.values())
            tapeBorderPane.clear();
        tapes.clear();
    }

    void eraseTapes(String tapesCellsDescription) {
        String[] tapesCellsDescriptionAr = tapesCellsDescription.split(";");

        if(tapesCellsDescriptionAr.length != tapes.size())
            return;


        int i = 0;
        for(Node child: this.getChildren()) {
            if(!(child instanceof TapeBorderPane))
                continue;

            ((TapeBorderPane)child).eraseTape(tapesCellsDescriptionAr[i]);
            i++;
        }

    }

    String getTapesString() {
        StringBuilder sb = new StringBuilder();

        for(Node child: this.getChildren()) {
            if(!(child instanceof TapeBorderPane))
                continue;

            sb.append(((TapeBorderPane)child).getTapeString());
            sb.append(';');
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
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

    Tape tape;

    TapeBorderPane(TuringMachineDrawer drawer, Tape tape) {
        this.drawer = drawer;
        this.tape = tape;

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
        tapePane.checkLinesAndColumns(maxWidth, maxHeight, true);
        tapePane.tapeOptionRectangle.reset();
    }

    void setTapeRightBound(Integer right) {
        this.right = right;
        tapePane.checkLinesAndColumns(maxWidth, maxHeight, true);
        tapePane.tapeOptionRectangle.reset();
    }

    void setTapeBottomBound(Integer bottom) {
        this.bottom = bottom;
        tapePane.checkLinesAndColumns(maxWidth, maxHeight, true);
        tapePane.tapeOptionRectangle.reset();
    }

    void setTapeTopBound(Integer top) {
        this.top = top;
        tapePane.checkLinesAndColumns(maxWidth, maxHeight, true);
        tapePane.tapeOptionRectangle.reset();
    }

    void translate(double dx, double dy) {
        offsetX -= dx;
        offsetY -= dy;

        double width = Math.max(Math.abs(-2 * offsetX - this.getMaxWidth()), -2 * offsetX + this.getMaxWidth());
        double height = Math.max(Math.abs(-2 * offsetY - this.getMaxHeight()), -2 * offsetY + this.getMaxHeight());

        tapePane.translate(dx, dy);
        horizontalCoordinates.translate(dx);
        verticalCoordinates.translate(dy);

        tapePane.checkLinesAndColumns(width, height, false);
        horizontalCoordinates.checkColumn(width);
        verticalCoordinates.checkLines(height);
    }

    void centerOn(int head){
        int line = tapePane.lineOf(head);
        int column = tapePane.columnOf(head);

        double x = getXOf(column);
        double y = getYOf(line);

        translate(-x + offsetX, -y + offsetY);
    }

    void checkLinesAndColumns(double width, double height){
        tapePane.checkLinesAndColumns(width, height, false);
        horizontalCoordinates.checkColumn(width);
        verticalCoordinates.checkLines(height);
        maxWidth = Math.max(maxWidth, width);
        maxHeight = Math.max(maxHeight, height);
    }

    double getXOf(int column) {
        return column * TuringMachineDrawer.TAPE_CELL_WIDTH;
    }

    double getYOf(int line) {
        return -line * TuringMachineDrawer.TAPE_CELL_WIDTH;
    }

    void closeAllOptionRectangle() {
        tapePane.closeAllOptionRectangle();
    }

    KeyFrame getHeadWriteKeyFrame(Integer head) {
        return tapePane.getHeadWriteKeyFrame(head);
    }

    KeyFrame getMoveHeadKeyFrame(Integer head, Integer line, Integer column) {
        return tapePane.getMoveHeadKeyFrame(head, line, column);
    }

    Timeline getWriteSymbolTimeline(Integer line, Integer column, String symbol) {
        return tapePane.getWriteSymbolTimeline(line, column, symbol);
    }

    JSONObject getJSON() {
        return tapePane.getJSON()
                .put("leftBound", (left == null)?"inf":left)
                .put("rightBound", (right == null)?"inf":right)
                .put("bottomBound", (bottom == null)?"inf":bottom)
                .put("topBound", (top == null)?"inf":top);
    }

    void clear() {
        tapePane.clear();
    }

    void eraseTape(String tapeCellsDescription) {
        try {
            tapeCellsDescription = tapeCellsDescription.trim();
            String[] tapeCellsDescriptionAr = tapeCellsDescription.split("\n");

            String[] lineColumn = tapeCellsDescriptionAr[0].trim().split(" ");
            int line = Integer.valueOf(lineColumn[0]);
            int firstColumn = Integer.valueOf(lineColumn[1]);

            tapePane.clear();
            int column;

            for(int i = 1; i < tapeCellsDescriptionAr.length; i++){
                column = firstColumn;
                for(int j = 0; j < tapeCellsDescriptionAr[i].length(); j++){
                    char c = tapeCellsDescriptionAr[i].charAt(j);
                    if(c == ' '){
                        column++;
                        continue;
                    }
                    String symbol = String.valueOf(c);
                    if((bottom == null || line >= bottom) && (top == null || line <= top)
                            && (left == null || column >= left) && (right == null || column <= right)
                            && (symbol == null || drawer.machine.hasSymbol(symbol)))
                        tape.writeInput(line, column, symbol);
                    column++;
                }
                line++;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    String getTapeString() {
        return tapePane.getTapeString();
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
    TapeOptionRectangle tapeOptionRectangle;

    TapeBorderPane tapeBorderPane;

    private Map<Integer, Map<Integer, Label>> cellLabels;
    private List<Rectangle> heads;
    private Map<Rectangle, Integer> headsColumns;
    private Map<Rectangle, Integer> headsLines;

    private Timeline timeline;
    boolean animating;
    private Rectangle animatedRectangle;

    TapePane(TuringMachineDrawer drawer, TapeBorderPane tapeBorderPane) {
        this.tapeBorderPane = tapeBorderPane;
        this.drawer = drawer;
        this.cellLabels = new HashMap<>();
        this.heads = new ArrayList<>();
        this.headsColumns = new HashMap<>();
        this.headsLines = new HashMap<>();

        timeline = new Timeline();
        timeline.setOnFinished(actionEvent -> animating = false);
        animating = false;

        lines = new ArrayList<>();
        columns = new ArrayList<>();
        this.setOnMousePressed(drawer.tapesMouseHandler);
        this.setOnMouseDragged(drawer.tapesMouseHandler);
        this.setOnMouseClicked(drawer.tapesMouseHandler);

        tapeLinesGroup = new Group();

        cellOptionRectangle = new CellOptionRectangle(drawer, this);
        cellOptionRectangle.setVisible(false);
        cellOptionRectangle.setOnMouseClicked(drawer.tapesMouseHandler);

        tapeOptionRectangle = new TapeOptionRectangle(drawer, this.tapeBorderPane);
        tapeOptionRectangle.setVisible(false);
        tapeOptionRectangle.setOnMouseClicked(drawer.tapesMouseHandler);

        animatedRectangle = new Rectangle(0, 0,
                TuringMachineDrawer.TAPE_CELL_WIDTH, TuringMachineDrawer.TAPE_CELL_WIDTH);
        animatedRectangle.setFill(TuringMachineDrawer.TAPE_MENU_DEFAULT_FILL_COLOR);
        animatedRectangle.setStroke(Color.BLACK);
        animatedRectangle.setVisible(false);

        tapeLinesGroup.getChildren().addAll(cellOptionRectangle, tapeOptionRectangle, animatedRectangle);

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

    void openCellOptionRectangle(Integer line, Integer column) {
        if (line == null || column == null)
            return;
        cellOptionRectangle.setLayoutX(tapeBorderPane.getXOf(column));
        cellOptionRectangle.setLayoutY(tapeBorderPane.getYOf(line) - TuringMachineDrawer.TAPE_CELL_WIDTH / 2
                - TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT / 2);
        cellOptionRectangle.setLineAndColumn(line, column);
        cellOptionRectangle.toFront();
        cellOptionRectangle.setVisible(true);
        cellOptionRectangle.maximize();
    }

    void closeCellOptionRectangle(){
        closeCellOptionRectangle(true);
    }
    void closeCellOptionRectangle(boolean animate){
        cellOptionRectangle.minimize(animate);
    }

    void openTapeOptionRectangle(Integer line, Integer column) {
        if (line == null || column == null)
            return;
        tapeOptionRectangle.setLayoutX(tapeBorderPane.getXOf(column));
        tapeOptionRectangle.setLayoutY(tapeBorderPane.getYOf(line) - TuringMachineDrawer.TAPE_CELL_WIDTH / 2
                - TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT / 2);
        tapeOptionRectangle.setLineAndColumn(line, column);
        tapeOptionRectangle.toFront();
        tapeOptionRectangle.setVisible(true);
        tapeOptionRectangle.maximize();
    }

    void closeTapeOptionRectangle(){
        closeTapeOptionRectangle(true);
    }
    void closeTapeOptionRectangle(boolean animate){
        tapeOptionRectangle.minimize(animate);
    }


    void closeAllOptionRectangle() {
        closeTapeOptionRectangle(false);
        closeCellOptionRectangle(false);
    }

    void checkLinesAndColumns(double width, double height, boolean forceChange) {
        ObservableList<Node> children = tapeLinesGroup.getChildren();

        int nbLines = 2 * (int) ((height / (TuringMachineDrawer.TAPE_CELL_WIDTH * 2) - 0.5) + 2);
        int prevNbLines = lines.size();

        int nbColumns = 2 * (int) ((width / (TuringMachineDrawer.TAPE_CELL_WIDTH * 2) - 0.5) + 2);
        int prevNbColumns = columns.size();

        double h, w;
        double leftWidth = (tapeBorderPane.left == null) ? -width : (TuringMachineDrawer.TAPE_CELL_WIDTH * (tapeBorderPane.left - 0.5));
        double rightWidth = (tapeBorderPane.right == null) ? width : (TuringMachineDrawer.TAPE_CELL_WIDTH * (tapeBorderPane.right + 0.5));

        if (forceChange || tapeBorderPane.maxWidth != width) {
            for (Line line : lines) {
                line.setStartX(leftWidth);
                line.setEndX(rightWidth);
            }
        }

        for (int i = lines.size() / 2; i < nbLines / 2; i++) {
            h = TuringMachineDrawer.TAPE_CELL_WIDTH * (0.5 + i);
            Line linePos = new Line(leftWidth, -h, rightWidth, -h);
            Line lineNeg = new Line(leftWidth, h, rightWidth, h);

            lines.add(linePos);
            lines.add(0, lineNeg);

            children.add(linePos);
            children.add(lineNeg);
        }

        if (forceChange || prevNbLines < nbLines) {
            int zeroIndex = lines.size() / 2;
            for (int i = 0; i < lines.size(); i++) {
                int index = i - zeroIndex;
                Line line = lines.get(i);
                line.setVisible(
                        (tapeBorderPane.top == null || index <= tapeBorderPane.top)
                        && (tapeBorderPane.bottom == null || index + 1 >= tapeBorderPane.bottom)
                );
                line.toBack();
            }
        }

        double bottomHeight = (tapeBorderPane.bottom == null) ? height : TuringMachineDrawer.TAPE_CELL_WIDTH * (-tapeBorderPane.bottom + 0.5);
        double topHeight = (tapeBorderPane.top == null) ? -height : TuringMachineDrawer.TAPE_CELL_WIDTH * (- tapeBorderPane.top - 0.5);


        if (forceChange || tapeBorderPane.maxHeight != height) {
            for (Line column : columns) {
                column.setStartY(bottomHeight);
                column.setEndY(topHeight);
            }
        }

        for (int i = columns.size() / 2; i < nbColumns / 2; i++) {
            w = TuringMachineDrawer.TAPE_CELL_WIDTH * (0.5 + i);
            Line columnPos = new Line(w, -bottomHeight, w, topHeight);
            Line columnNeg = new Line(-w, -bottomHeight, -w, topHeight);

            columns.add(columnPos);
            columns.add(0, columnNeg);

            children.add(columnPos);
            children.add(columnNeg);
        }

        if (forceChange || prevNbColumns < nbColumns) {
            int zeroIndex = columns.size() / 2;
            for(int i = 0; i < columns.size(); i++){
                int index = i - zeroIndex;
                Line column = columns.get(i);
                column.setVisible(
                        (tapeBorderPane.right == null || index <= tapeBorderPane.right)
                                && (tapeBorderPane.left == null || index + 1 >= tapeBorderPane.left)
                );
                column.toBack();
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
        return (tapeBorderPane.right == null || column <= tapeBorderPane.right)
                && (tapeBorderPane.left == null || column >= tapeBorderPane.left) ? column : null;
    }

    Integer getLine(double y) {
        y += tapeBorderPane.offsetY;
        y -= this.getMaxHeight() / 2;
        y += TuringMachineDrawer.TAPE_CELL_WIDTH / 2;
        if (y < 0)
            y -= TuringMachineDrawer.TAPE_CELL_WIDTH;
        int line = (int) (-y / TuringMachineDrawer.TAPE_CELL_WIDTH);
        return (tapeBorderPane.top == null || line <= tapeBorderPane.top)
                && (tapeBorderPane.bottom == null || line >= tapeBorderPane.bottom) ? line : null;
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
                cellLabel.setFont(Font.font(TuringMachineDrawer.SYMBOL_FONT_NAME,
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

    void editHeadColor(int head, Color color) {
        Rectangle headRectangle = heads.get(head);
        headRectangle.setStroke(color);

        cellOptionRectangle.editHeadColor(head, color);
    }

    void removeHead(int head) {
        Rectangle headRectangle = heads.remove(head);
        tapeLinesGroup.getChildren().remove(headRectangle);
        headsLines.remove(headRectangle);
        headsColumns.remove(headRectangle);

        cellOptionRectangle.removeHead(head);
    }

    void addSymbol(String symbol){
        cellOptionRectangle.addSymbol(symbol);
    }

    void editSymbol(int index, String previousSymbol, String symbol){
        cellOptionRectangle.editSymbol(index, symbol);

        for(Map<Integer, Label> column : cellLabels.values()){
            for(Label label : column.values()){
                if(label.getText().equals(previousSymbol))
                    label.setText(symbol);
            }
        }
    }

    void removeSymbol(int index, String symbol) {
        cellOptionRectangle.removeSymbol(index);

        Iterator<Map.Entry<Integer, Map<Integer, Label>>> itColumns = cellLabels.entrySet().iterator();

        while (itColumns.hasNext()){
            Map.Entry<Integer, Map<Integer, Label>> entry = itColumns.next();
            Iterator<Map.Entry<Integer, Label>> itLines = entry.getValue().entrySet().iterator();
            while(itLines.hasNext()){
                Map.Entry<Integer, Label> entry2 = itLines.next();
                if(entry2.getValue().getText().equals(symbol)) {
                    tapeLinesGroup.getChildren().remove(entry2.getValue());
                    itLines.remove();
                }
            }
            if(entry.getValue().isEmpty())
                itColumns.remove();
        }

    }

    int lineOf(int head){
        return headsLines.get(heads.get(head));
    }

    int columnOf(int head){
        return headsColumns.get(heads.get(head));
    }

    void startTimeline(Integer line, Integer column){
        if(line == null || column == null)
            return;

        animatedRectangle.toBack();
        animatedRectangle.setLayoutX(tapeBorderPane.getXOf(column)  - TuringMachineDrawer.TAPE_CELL_WIDTH / 2);
        animatedRectangle.setLayoutY(tapeBorderPane.getYOf(line) - TuringMachineDrawer.TAPE_CELL_WIDTH / 2);
        animatedRectangle.setVisible(true);

        animating = true;
        timeline.getKeyFrames().clear();
        KeyValue kfill = new KeyValue(this.animatedRectangle.fillProperty(),
                TuringMachineDrawer.EDIT_PRESS_COLOR,
                Interpolator.EASE_BOTH);
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(TuringMachineDrawer.EDIT_PRESS_DURATION), kfill)
        );
        timeline.play();
    }

    void stopTimeline(){
        timeline.stop();
        animatedRectangle.setFill(TuringMachineDrawer.TAPE_MENU_DEFAULT_FILL_COLOR);
        animatedRectangle.setVisible(false);
        animating = false;
    }

    KeyFrame getHeadWriteKeyFrame(Integer head) {
        Rectangle headRectangle = heads.get(head);

        KeyValue kStrokeWidth = new KeyValue(headRectangle.strokeWidthProperty(),
                TuringMachineDrawer.HEAD_WRITE_STROKE_WIDTH,
                Interpolator.EASE_BOTH);

        return new KeyFrame(Duration.millis(TuringMachineDrawer.ANIMATION_DURATION / 2), kStrokeWidth);
    }

    KeyFrame getMoveHeadKeyFrame(Integer head, Integer line, Integer column) {
        Rectangle headRectangle = heads.get(head);

        KeyValue kX = new KeyValue(headRectangle.layoutXProperty(),
                tapeBorderPane.getXOf(column) - TuringMachineDrawer.TAPE_CELL_HEAD_SIZE / 2,
                Interpolator.EASE_BOTH);

        KeyValue kY = new KeyValue(headRectangle.layoutYProperty(),
                tapeBorderPane.getYOf(line) - TuringMachineDrawer.TAPE_CELL_HEAD_SIZE / 2,
                Interpolator.EASE_BOTH);

        return new KeyFrame(Duration.millis(TuringMachineDrawer.ANIMATION_DURATION), kX, kY);
    }

    Timeline getWriteSymbolTimeline(Integer line, Integer column, String symbol) {
        Map<Integer, Label> h = cellLabels.computeIfAbsent(line, k -> new HashMap<>());
        Label cellLabel = h.get(column);
        if(cellLabel == null){
            cellLabel = new Label(null);
            cellLabel.setFont(Font.font(TuringMachineDrawer.SYMBOL_FONT_NAME,
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

        final Label cellLabel2 = cellLabel;
        Timeline timeline = new Timeline();

        KeyValue ktransp = new KeyValue(cellLabel2.opacityProperty(),0 );
        KeyFrame kftransp = new KeyFrame(Duration.millis(TuringMachineDrawer.ANIMATION_DURATION / 2),
                actionEvent -> {
                    cellLabel2.setText(symbol);
                }, ktransp);

        KeyValue kopa= new KeyValue(cellLabel2.opacityProperty(),1 );
        KeyFrame kfopa = new KeyFrame(Duration.millis(TuringMachineDrawer.ANIMATION_DURATION),
                kopa);

        timeline.getKeyFrames().addAll(kftransp, kfopa);
        return timeline;

    }

    JSONObject getJSON() {
        JSONArray jsonHeads = new JSONArray();
        for(Rectangle headRectangle: heads){
            jsonHeads.put(
                    new JSONObject()
                            .put("line", headsLines.get(headRectangle))
                            .put("column", headsColumns.get(headRectangle))
                            .put("color", headRectangle.getStroke())
            );
        }

        JSONArray jsonCells = new JSONArray();
        for(Map.Entry<Integer, Map<Integer, Label>> entry: cellLabels.entrySet()){
            int line = entry.getKey();
            for(Map.Entry<Integer, Label> entry2: entry.getValue().entrySet()){
                if(entry2.getValue().getText() == null)
                    continue;
                int column = entry2.getKey();
                String symbol = entry2.getValue().getText();
                if(symbol != null && symbol.equals(""))
                    symbol = null;

                if(symbol == null)
                    continue;

                jsonCells.put(new JSONObject()
                .put("line", line)
                .put("column", column)
                .put("symbol", symbol)
                );
            }
        }

        return new JSONObject()
                .put("heads", jsonHeads)
                .put("cells", jsonCells);
    }

    void clear() {
        for(Map.Entry<Integer, Map<Integer, Label>> entry : new HashSet<>(cellLabels.entrySet()))
            for(Map.Entry<Integer, Label> entry2 : new HashSet<>(entry.getValue().entrySet()))
                tapeBorderPane.tape.writeInput(entry.getKey(), entry2.getKey(), null);

        cellLabels.clear();
        cellOptionRectangle.clear();
        tapeOptionRectangle.clear();
    }

    String getTapeString() {
        int minLine = Integer.MAX_VALUE;
        int minColumn = Integer.MAX_VALUE;
        int maxLine = Integer.MIN_VALUE;
        int maxColumn = Integer.MIN_VALUE;

        for(Map.Entry<Integer, Map<Integer, Label>> entry : cellLabels.entrySet()) {
            minLine = Math.min(minLine, entry.getKey());
            maxLine = Math.max(maxLine, entry.getKey());
            for (Map.Entry<Integer, Label> entry2 : entry.getValue().entrySet()) {
                minColumn = Math.min(minColumn, entry2.getKey());
                maxColumn = Math.max(maxColumn, entry2.getKey());
            }
        }


        StringBuilder sb = new StringBuilder();
        sb.append(minLine);
        sb.append(' ');
        sb.append(minColumn);
        sb.append('\n');

        for(int line = minLine; line <= maxLine; line++){
            Map<Integer, Label> v1 = cellLabels.get(line);
            for(int column = minColumn; column <= maxColumn; column++){
                if(v1 == null) {
                    sb.append(' ');
                    continue;
                }
                Label label = v1.get(column);
                if(label == null)
                    sb.append(' ');
                else
                    sb.append(label.getText());
            }
            sb.append('\n');
        }


        return sb.toString();

    }
}

abstract class TranslateTapesArrow extends Polygon{
    TapeBorderPanesHBox tapeBorderPanesHBox;
    TapeBorderPane tapeBorderPane;
    TranslateTapesArrow(TuringMachineDrawer drawer, TapeBorderPanesHBox tapeBorderPanesHBox,
                        TapeBorderPane tapeBorderPane) {
        this.tapeBorderPanesHBox = tapeBorderPanesHBox;
        this.tapeBorderPane = tapeBorderPane;
        this.setOnMouseClicked(drawer.tapesMouseHandler);
    }
}

class RightArrow extends TranslateTapesArrow{

    RightArrow(TuringMachineDrawer drawer, TapeBorderPanesHBox tapeBorderPanesHBox, TapeBorderPane tapeBorderPane) {
        super(drawer, tapeBorderPanesHBox, tapeBorderPane);

        this.getPoints().addAll(
                0D, 0D,
                TuringMachineDrawer.TAPE_HOBX_ARROW_WIDTH,
                TuringMachineDrawer.TAPE_HOBX_ARROW_HEIGHT / 2,
                0D, TuringMachineDrawer.TAPE_HOBX_ARROW_HEIGHT);
    }
}

class LeftArrow extends TranslateTapesArrow{

    LeftArrow(TuringMachineDrawer drawer, TapeBorderPanesHBox tapeBorderPanesHBox, TapeBorderPane tapeBorderPane){
        super(drawer, tapeBorderPanesHBox, tapeBorderPane);

        this.getPoints().addAll(
                TuringMachineDrawer.TAPE_HOBX_ARROW_WIDTH, 0D,
                0D,
                TuringMachineDrawer.TAPE_HOBX_ARROW_HEIGHT / 2,
                TuringMachineDrawer.TAPE_HOBX_ARROW_WIDTH, TuringMachineDrawer.TAPE_HOBX_ARROW_HEIGHT);
    }
}