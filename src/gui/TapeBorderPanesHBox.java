/*
 * Copyright (c) 2018 Dimitri Watel
 */

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
import javafx.scene.input.MouseEvent;
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
import util.MouseListener;
import util.Pair;

import java.util.*;

/**
 * Created by dimitri.watel on 07/06/18.
 */
class TapeBorderPanesHBox extends HBox{

    private Map<Tape, TapeBorderPane> tapes;

    TapeBorderPanesHBox(){
        this.tapes = new HashMap<>();
        this.setAlignment(Pos.CENTER_LEFT);

        this.layoutBoundsProperty().addListener((obs, oldVal, newVal) ->
            resizeChildren(newVal.getWidth(), newVal.getHeight())
        );

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
        TapeBorderPane tapeBorderPane = new TapeBorderPane(tape);

        tapeBorderPane.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 1, 0, 0))));

        tapes.put(tape, tapeBorderPane);

        if(this.getChildren().size() != 0){
            TapeBorderPane previousTapeBorderPane = (TapeBorderPane) this.getChildren().get(
                    this.getChildren().size() - 1);
            for(Node child : this.getChildren())
                child.setTranslateX(0);
            this.getChildren().addAll(
                    new RightArrow(this, tapeBorderPane),
                    new LeftArrow(this, previousTapeBorderPane)
            );
        }

        this.getChildren().add(tapeBorderPane);
        this.resizeChildren(this.getMaxWidth(), this.getMaxHeight());
    }

    void removeTape(Tape tape){
        TapeBorderPane tapeBorderPane = tapes.remove(tape);
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


    void addHead(Tape tape, int line, int column, Color color){
        TapeBorderPane tapeBorderPane = tapes.get(tape);
        tapeBorderPane.tapePane.addHead(line, column, color);
    }

    void editHeadColor(Tape tape, int head, Color color) {
        TapeBorderPane tapeBorderPane = tapes.get(tape);
        tapeBorderPane.tapePane.editHeadColor(head, color);
    }

    void moveHead(Tape tape, int line, int column, int head) {
        TapeBorderPane tapeBorderPane = tapes.get(tape);
        tapeBorderPane.tapePane.moveHead(line, column, head);
    }

    void removeHead(Tape tape, int head){
        TapeBorderPane tapeBorderPane = tapes.get(tape);
        tapeBorderPane.tapePane.removeHead(head);
    }

    void setInputSymbol(Tape tape, int line, int column, String symbol){
        TapeBorderPane tapeBorderPane = tapes.get(tape);
        tapeBorderPane.tapePane.drawSymbol(line, column, symbol);
    }

    void setTapeLeftBound(Tape tape, Integer left){
        TapeBorderPane tapeBorderPane = tapes.get(tape);
        tapeBorderPane.setTapeLeftBound(left);
    }

    void setTapeRightBound(Tape tape, Integer right){
        TapeBorderPane tapeBorderPane = tapes.get(tape);
        tapeBorderPane.setTapeRightBound(right);
    }

    void setTapeBottomBound(Tape tape, Integer bottom){
        TapeBorderPane tapeBorderPane = tapes.get(tape);
        tapeBorderPane.setTapeBottomBound(bottom);
    }

    void setTapeTopBound(Tape tape, Integer top){
        TapeBorderPane tapeBorderPane = tapes.get(tape);
        tapeBorderPane.setTapeTopBound(top);
    }

    void centerOn(Tape tape, int head) {
        TapeBorderPane tapeBorderPane = tapes.get(tape);
        TuringMachineDrawer.getInstance().tapesPane.centerOn(tape);
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

    void closeAllSettingsRectangle() {
        for(TapeBorderPane tapeBorderPane : tapes.values())
            tapeBorderPane.closeAllSettingsRectangle();
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
        closeAllSettingsRectangle();
        for(TapeBorderPane tapeBorderPane : tapes.values())
            tapeBorderPane.clear();
        tapes.clear();
    }

    void eraseTapes(String tapesCellsDescription) {
        String[] tapesCellsDescriptionAr = tapesCellsDescription.split(";");

        if(tapes.size() > tapesCellsDescriptionAr.length) {
            int s = tapesCellsDescriptionAr.length;
            for(Node child: new LinkedList<>(this.getChildren())) {
                if(!(child instanceof TapeBorderPane)) {
                    continue;
                }
                if(s-- > 0)
                    continue;
                TuringMachineDrawer.getInstance().removeTape(((TapeBorderPane) child).tape, false);
            }
        }
        else while(tapes.size() < tapesCellsDescriptionAr.length) {
            TuringMachineDrawer.getInstance().addTape();
        }

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
            sb.append('\n');
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}

class TapeBorderPane extends BorderPane implements MouseListener {

    Integer bottom;
    Integer top;
    Integer left;
    Integer right;

    double offsetX;
    double offsetY;

    Double dragX;
    Double dragY;

    TapePane tapePane;
    private HorizontalCoordinates horizontalCoordinates;
    private VerticalCoordinates verticalCoordinates;

    double maxWidth;
    double maxHeight;

    Tape tape;

    TapeBorderPane(Tape tape) {
        this.tape = tape;

        horizontalCoordinates = new HorizontalCoordinates(this);
        horizontalCoordinates.setMinHeight(TuringMachineDrawer.TAPE_COORDINATES_WIDTH);
        horizontalCoordinates.setMaxHeight(TuringMachineDrawer.TAPE_COORDINATES_WIDTH);
        horizontalCoordinates.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        horizontalCoordinates.setTranslateX(TuringMachineDrawer.TAPE_COORDINATES_WIDTH);

        verticalCoordinates = new VerticalCoordinates(this);
        verticalCoordinates.setMinWidth(TuringMachineDrawer.TAPE_COORDINATES_WIDTH);
        verticalCoordinates.setMaxWidth(TuringMachineDrawer.TAPE_COORDINATES_WIDTH);
        verticalCoordinates.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        tapePane = new TapePane(this);

        this.setOnMousePressed(TuringMachineDrawer.getInstance().mouseHandler);
        this.setOnMouseDragged(TuringMachineDrawer.getInstance().mouseHandler);

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


            this.checkLinesAndColumns(width, height, false);
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
        tapePane.tapeSettingsRectangle.reset();
    }

    void setTapeRightBound(Integer right) {
        this.right = right;
        tapePane.checkLinesAndColumns(maxWidth, maxHeight, true);
        tapePane.tapeSettingsRectangle.reset();
    }

    void setTapeBottomBound(Integer bottom) {
        this.bottom = bottom;
        tapePane.checkLinesAndColumns(maxWidth, maxHeight, true);
        tapePane.tapeSettingsRectangle.reset();
    }

    void setTapeTopBound(Integer top) {
        this.top = top;
        tapePane.checkLinesAndColumns(maxWidth, maxHeight, true);
        tapePane.tapeSettingsRectangle.reset();
    }

    void translate(double dx, double dy) {
        offsetX -= dx;
        offsetY -= dy;

        double width = Math.max(Math.abs(-2 * offsetX - this.getMaxWidth()), Math.abs(-2 * offsetX + this.getMaxWidth()));
        double height = Math.max(Math.abs(-2 * offsetY - this.getMaxHeight()), Math.abs(-2 * offsetY + this.getMaxHeight()));

        tapePane.translate(dx, dy);
        horizontalCoordinates.translate(dx);
        verticalCoordinates.translate(dy);

        this.checkLinesAndColumns(width, height, true);
    }

    void centerOn(int head){

        double x = tapePane.currentHeadX(head);
        double y = tapePane.currentHeadY(head);

        translate(-x + offsetX, -y + offsetY);
    }

    void checkLinesAndColumns(double width, double height, boolean forceChange){
        tapePane.checkLinesAndColumns(width, height, forceChange);
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

    void closeAllSettingsRectangle() {
        tapePane.closeAllSettingsRectangle();
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

    void eraseTape(String tapeDescription) {
        try {
            TuringMachineDrawer drawer = TuringMachineDrawer.getInstance();
            tapeDescription = tapeDescription.trim();
            String[] tapeCellsDescriptionAr = tapeDescription.split("\n");

            String[] bounds = tapeCellsDescriptionAr[0].trim().split(" ");
            Integer left = bounds[0].equals("I")?null:Integer.valueOf(bounds[0]);
            Integer right = bounds[1].equals("I")?null:Integer.valueOf(bounds[1]);
            Integer bottom = bounds[2].equals("I")?null:Integer.valueOf(bounds[2]);
            Integer top = bounds[3].equals("I")?null:Integer.valueOf(bounds[3]);

            if(left != null && right != null && left > right)
                right = left;
            if(bottom != null && top != null && bottom > top)
                top = bottom;

            if(right != null && right < 0) {
                drawer.setTapeLeftBound(tape, left);
                drawer.setTapeRightBound(tape, right);
            }
            else{
                drawer.setTapeRightBound(tape, right);
                drawer.setTapeLeftBound(tape, left);
            }

            if(top != null && top < 0) {
                drawer.setTapeBottomBound(tape, bottom);
                drawer.setTapeTopBound(tape, top);
            }
            else{
                drawer.setTapeTopBound(tape, top);
                drawer.setTapeBottomBound(tape, bottom);
            }

            int nbHeads = Integer.valueOf(tapeCellsDescriptionAr[1].trim());
            int prevNbHead = tape.getNbHeads();

            for(int i = 2; i < nbHeads + 2; i++){
                String[] headInfo = tapeCellsDescriptionAr[i].trim().split(" ");
                if(headInfo.length != 5){
                    nbHeads = i - 2;
                    break;
                }
                int line = Integer.valueOf(headInfo[0]);
                int column = Integer.valueOf(headInfo[1]);
                int red = Integer.valueOf(headInfo[2]);
                int green = Integer.valueOf(headInfo[3]);
                int blue = Integer.valueOf(headInfo[4]);

                Color color = Color.rgb(red, green, blue);

                if(drawer.isAvailable(color)) {
                    if(prevNbHead >= i - 1) {
                        drawer.editHeadColor(tape, i - 2, color);
                        drawer.moveHead(tape, line, column, i - 2);
                    }
                    else
                        drawer.addHead(tape, line, column, color);
                }
                else {
                    Pair<Tape, Integer> pair = drawer.getHead(color);
                    if(tape == pair.first && pair.second.equals(i - 2)) {
                        drawer.moveHead(tape, line, column, i - 2);
                        continue;
                    }
                    if(prevNbHead < i - 1){
                        Color c2 = drawer.getAvailableColor();
                        drawer.addHead(tape, line, column, c2);
                    }
                    drawer.swapHeadColor(tape, i - 2, pair.first, pair.second);
                }
            }

            this.clear();

            String[] lineColumn = tapeCellsDescriptionAr[nbHeads + 2].trim().split(" ");
            int line = Integer.valueOf(lineColumn[0]) + tapeCellsDescriptionAr.length - 4 - nbHeads;
            int firstColumn = Integer.valueOf(lineColumn[1]);

            int column;

            for(int i = nbHeads + 3; i < tapeCellsDescriptionAr.length; i++){
                column = firstColumn;
                for(int j = 0; j < tapeCellsDescriptionAr[i].length(); j++){
                    char c = tapeCellsDescriptionAr[i].charAt(j);
                    if(c == ' '){
                        column++;
                        continue;
                    }
                    String symbol = String.valueOf(c);
                    if((this.bottom == null || line >= this.bottom) && (this.top == null || line <= this.top)
                            && (this.left == null || column >= this.left) && (this.right == null || column <= this.right)
                            && (TuringMachineDrawer.getInstance().machine.hasSymbol(symbol)))
                        TuringMachineDrawer.getInstance().setInputSymbol(tape, line, column, symbol);
                    column++;
                }
                line--;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    String getTapeString() {
        return tapePane.getTapeString();
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        return false;
    }

    @Override
    public boolean onMouseDragged(MouseEvent mouseEvent) {
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();
        if(dragX == null){
            dragX = x;
            dragY = y;
        }
        else {
            this.translate(x - dragX, y - dragY);
            dragX = x;
            dragY = y;
        }
        return true;
    }

    @Override
    public boolean onMousePressed(MouseEvent mouseEvent) {
        dragX = mouseEvent.getX();
        dragY = mouseEvent.getY();
        return true;
    }
}

class HorizontalCoordinates extends Pane {
    private Group coordinatesGroup;

    private List<Line> columns;
    private List<Label> abscissae;
    private TapeBorderPane tapeBorderPane;

    HorizontalCoordinates( TapeBorderPane tapeBorderPane) {
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

    VerticalCoordinates(TapeBorderPane tapeBorderPane) {
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

class TapePane extends Pane implements MouseListener{

    private Group tapeLinesGroup;

    private List<Line> lines;
    private List<Line> columns;

    CellSettingsRectangle cellSettingsRectangle;
    TapeSettingsRectangle tapeSettingsRectangle;

    TapeBorderPane tapeBorderPane;

    private Map<Integer, Map<Integer, Label>> cellLabels;
    private List<Rectangle> heads;
    private Map<Rectangle, Integer> headsColumns;
    private Map<Rectangle, Integer> headsLines;

    private Timeline timeline;
    boolean animating;
    private Rectangle animatedRectangle;

    TapePane(TapeBorderPane tapeBorderPane) {
        this.tapeBorderPane = tapeBorderPane;
        this.cellLabels = new HashMap<>();
        this.heads = new ArrayList<>();
        this.headsColumns = new HashMap<>();
        this.headsLines = new HashMap<>();

        timeline = new Timeline();
        timeline.setOnFinished(actionEvent -> animating = false);
        animating = false;

        lines = new ArrayList<>();
        columns = new ArrayList<>();
        this.setOnMousePressed(TuringMachineDrawer.getInstance().mouseHandler);
        this.setOnMouseDragged(TuringMachineDrawer.getInstance().mouseHandler);
        this.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);

        tapeLinesGroup = new Group();

        cellSettingsRectangle = new CellSettingsRectangle( this);
        cellSettingsRectangle.setVisible(false);
        cellSettingsRectangle.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);

        tapeSettingsRectangle = new TapeSettingsRectangle(this.tapeBorderPane);
        tapeSettingsRectangle.setVisible(false);
        tapeSettingsRectangle.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);

        animatedRectangle = new Rectangle(0, 0,
                TuringMachineDrawer.TAPE_CELL_WIDTH, TuringMachineDrawer.TAPE_CELL_WIDTH);
        animatedRectangle.setFill(TuringMachineDrawer.TAPE_MENU_DEFAULT_FILL_COLOR);
        animatedRectangle.setStroke(Color.BLACK);
        animatedRectangle.setVisible(false);

        tapeLinesGroup.getChildren().addAll(cellSettingsRectangle, tapeSettingsRectangle, animatedRectangle);

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

    void openCellSettingsRectangle(Integer line, Integer column) {
        if (line == null || column == null)
            return;
        cellSettingsRectangle.setLayoutX(tapeBorderPane.getXOf(column));
        cellSettingsRectangle.setLayoutY(tapeBorderPane.getYOf(line) - TuringMachineDrawer.TAPE_CELL_WIDTH / 2
                - TuringMachineDrawer.SETTINGS_RECTANGLE_MINIMIZED_HEIGHT / 2);
        cellSettingsRectangle.setLineAndColumn(line, column);
        cellSettingsRectangle.toFront();
        cellSettingsRectangle.setVisible(true);
        cellSettingsRectangle.maximize();
    }

    void closeCellSettingsRectangle(){
        closeCellSettingsRectangle(true);
    }
    void closeCellSettingsRectangle(boolean animate){
        cellSettingsRectangle.minimize(animate);
    }

    void openTapeSettingsRectangle(Integer line, Integer column) {
        if (line == null || column == null)
            return;
        tapeSettingsRectangle.setLayoutX(tapeBorderPane.getXOf(column));
        tapeSettingsRectangle.setLayoutY(tapeBorderPane.getYOf(line) - TuringMachineDrawer.TAPE_CELL_WIDTH / 2
                - TuringMachineDrawer.SETTINGS_RECTANGLE_MINIMIZED_HEIGHT / 2);
        tapeSettingsRectangle.setLineAndColumn(line, column);
        tapeSettingsRectangle.toFront();
        tapeSettingsRectangle.setVisible(true);
        tapeSettingsRectangle.maximize();
    }

    void closeTapeSettingsRectangle(){
        closeTapeSettingsRectangle(true);
    }
    void closeTapeSettingsRectangle(boolean animate){
        tapeSettingsRectangle.minimize(animate);
    }


    void closeAllSettingsRectangle() {
        closeTapeSettingsRectangle(false);
        closeCellSettingsRectangle(false);
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
            Line columnPos = new Line(w, bottomHeight, w, topHeight);
            Line columnNeg = new Line(-w, bottomHeight, -w, topHeight);

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

        cellSettingsRectangle.addHead(color);

        headRectangle.setVisible(true);
        tapeLinesGroup.getChildren().add(headRectangle);
        this.moveHead(line, column, heads.size() - 1);

        headsLines.put(headRectangle, line);
        headsColumns.put(headRectangle, column);
    }

    void editHeadColor(int head, Color color) {
        Rectangle headRectangle = heads.get(head);
        headRectangle.setStroke(color);

        cellSettingsRectangle.editHeadColor(head, color);
    }

    void removeHead(int head) {
        Rectangle headRectangle = heads.remove(head);
        tapeLinesGroup.getChildren().remove(headRectangle);
        headsLines.remove(headRectangle);
        headsColumns.remove(headRectangle);

        cellSettingsRectangle.removeHead(head);
    }

    void addSymbol(String symbol){
        cellSettingsRectangle.addSymbol(symbol);
    }

    void editSymbol(int index, String previousSymbol, String symbol){
        cellSettingsRectangle.editSymbol(index, symbol);

        for(Map<Integer, Label> column : cellLabels.values()){
            for(Label label : column.values()){
                if(label.getText().equals(previousSymbol))
                    label.setText(symbol);
            }
        }
    }

    void removeSymbol(int index, String symbol) {
        cellSettingsRectangle.removeSymbol(index);

        Iterator<Map.Entry<Integer, Map<Integer, Label>>> itColumns = cellLabels.entrySet().iterator();

        while (itColumns.hasNext()){
            Map.Entry<Integer, Map<Integer, Label>> entry = itColumns.next();
            Iterator<Map.Entry<Integer, Label>> itLines = entry.getValue().entrySet().iterator();
            while(itLines.hasNext()){
                Map.Entry<Integer, Label> entry2 = itLines.next();
                if((entry2.getValue().getText() == symbol) || (entry2.getValue().getText() != null &&
                        entry2.getValue().getText().equals(symbol)  )) {
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
                new KeyFrame(Duration.millis(TuringMachineDrawer.SETTINGS_PRESS_DURATION), kfill)
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

    double currentHeadX(int head){
        return heads.get(head).getLayoutX();
    }

    double currentHeadY(int head){
        return heads.get(head).getLayoutY();
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
                TuringMachineDrawer.getInstance().setInputSymbol(
                        tapeBorderPane.tape, entry.getKey(), entry2.getKey(),null);

        cellLabels.clear();
        cellSettingsRectangle.clear();
        tapeSettingsRectangle.clear();
    }

    String getTapeString() {
        StringBuilder sb = new StringBuilder();

        if(this.tapeBorderPane.left==null)
            sb.append('I');
        else
            sb.append(this.tapeBorderPane.left);
        sb.append(' ');

        if(this.tapeBorderPane.right==null)
            sb.append('I');
        else
            sb.append(this.tapeBorderPane.right);
        sb.append(' ');

        if(this.tapeBorderPane.bottom==null)
            sb.append('I');
        else
            sb.append(this.tapeBorderPane.bottom);
        sb.append(' ');

        if(this.tapeBorderPane.top==null)
            sb.append('I');
        else
            sb.append(this.tapeBorderPane.top);
        sb.append('\n');

        int nbHead = this.heads.size();
        sb.append(nbHead);
        sb.append('\n');

        for(int head = 0; head < nbHead; head++){
            Color color = TuringMachineDrawer.getInstance().getColorOfHead(tapeBorderPane.tape, head);
            sb.append(this.lineOf(head));
            sb.append(' ');
            sb.append(this.columnOf(head));
            sb.append(' ');
            sb.append((int)(color.getRed() * 255));
            sb.append(' ');
            sb.append((int)(color.getGreen() * 255));
            sb.append(' ');
            sb.append((int)(color.getBlue() * 255));
            sb.append('\n');
        }

        int minLine = Integer.MAX_VALUE;
        int minColumn = Integer.MAX_VALUE;
        int maxLine = Integer.MIN_VALUE;
        int maxColumn = Integer.MIN_VALUE;

        boolean changed = false;

        for(Map.Entry<Integer, Map<Integer, Label>> entry : cellLabels.entrySet()) {
            minLine = Math.min(minLine, entry.getKey());
            maxLine = Math.max(maxLine, entry.getKey());
            for (Map.Entry<Integer, Label> entry2 : entry.getValue().entrySet()) {
                changed = true;
                minColumn = Math.min(minColumn, entry2.getKey());
                maxColumn = Math.max(maxColumn, entry2.getKey());
            }
        }

        if(!changed){
            if(this.tapeBorderPane.left == null)
                minColumn = 0;
            else
                minColumn = this.tapeBorderPane.left;

            if(this.tapeBorderPane.bottom == null)
                minLine = 0;
            else
                minLine = this.tapeBorderPane.bottom;
        }

        sb.append(minLine);
        sb.append(' ');
        sb.append(minColumn);
        sb.append('\n');

        for(int line = maxLine; line >= minLine; line--){
            Map<Integer, Label> v1 = cellLabels.get(line);
            for(int column = minColumn; column <= maxColumn; column++){
                if(v1 == null) {
                    sb.append(' ');
                    continue;
                }
                Label label = v1.get(column);
                if(label == null || label.getText() == null)
                    sb.append(' ');
                else
                    sb.append(label.getText());
            }
            sb.append('\n');
        }


        return sb.toString();

    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        if(this.cellSettingsRectangle.isMaximized())
            this.closeCellSettingsRectangle();
        else if(this.tapeSettingsRectangle.isMaximized())
            this.closeTapeSettingsRectangle();
        else {
            boolean pressFinished = !this.animating;
            this.stopTimeline();
            Integer line = this.getLine(mouseEvent.getY());
            Integer column = this.getColumn(mouseEvent.getX());

            if(!pressFinished)
                this.openCellSettingsRectangle(line, column);
            else
                this.openTapeSettingsRectangle(line, column);
        }

        return true;
    }

    @Override
    public boolean onMouseDragged(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode )
            return false;

        this.stopTimeline();
        return false;
    }

    @Override
    public boolean onMousePressed(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        if(!this.cellSettingsRectangle.isMaximized() && !this.tapeSettingsRectangle.isMaximized()) {
            Integer line = this.getLine(mouseEvent.getY());
            Integer column = this.getColumn(mouseEvent.getX());
            this.startTimeline(line, column);
        }
        return false;
    }
}

abstract class TranslateTapesArrow extends Polygon implements MouseListener {
    TapeBorderPanesHBox tapeBorderPanesHBox;
    TapeBorderPane tapeBorderPane;
    TranslateTapesArrow(TapeBorderPanesHBox tapeBorderPanesHBox,
                        TapeBorderPane tapeBorderPane) {
        this.tapeBorderPanesHBox = tapeBorderPanesHBox;
        this.tapeBorderPane = tapeBorderPane;
        this.setOnMouseClicked(TuringMachineDrawer.getInstance().mouseHandler);
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        TuringMachineDrawer.getInstance().tapesPane.centerOn(this.tapeBorderPane.tape);
        return true;
    }

    @Override
    public boolean onMouseDragged(MouseEvent mouseEvent) {
        return false;
    }

    @Override
    public boolean onMousePressed(MouseEvent mouseEvent) {
        return false;
    }

}

class RightArrow extends TranslateTapesArrow{

    RightArrow(TapeBorderPanesHBox tapeBorderPanesHBox, TapeBorderPane tapeBorderPane) {
        super(tapeBorderPanesHBox, tapeBorderPane);

        this.getPoints().addAll(
                0D, 0D,
                TuringMachineDrawer.TAPE_HOBX_ARROW_WIDTH,
                TuringMachineDrawer.TAPE_HOBX_ARROW_HEIGHT / 2,
                0D, TuringMachineDrawer.TAPE_HOBX_ARROW_HEIGHT);
    }
}

class LeftArrow extends TranslateTapesArrow{

    LeftArrow(TapeBorderPanesHBox tapeBorderPanesHBox, TapeBorderPane tapeBorderPane){
        super(tapeBorderPanesHBox, tapeBorderPane);

        this.getPoints().addAll(
                TuringMachineDrawer.TAPE_HOBX_ARROW_WIDTH, 0D,
                0D,
                TuringMachineDrawer.TAPE_HOBX_ARROW_HEIGHT / 2,
                TuringMachineDrawer.TAPE_HOBX_ARROW_WIDTH, TuringMachineDrawer.TAPE_HOBX_ARROW_HEIGHT);
    }
}