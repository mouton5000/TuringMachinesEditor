package gui;

import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;

import java.util.ArrayList;

/**
 * Created by dimitri.watel on 07/06/18.
 */
class TapePane extends Pane {

    private TuringMachineDrawer drawer;
    private HBox tapeHBox;

    TapeLinesGroup tapeLinesGroup;

    private double offsetX;
    private double offsetY;

    TapePane(TuringMachineDrawer drawer, HBox tapeHBox){
        this.drawer = drawer;
        this.tapeHBox = tapeHBox;

        this.setOnMouseClicked(drawer.tapesMouseHandler);
        this.setOnMouseDragged(drawer.tapesMouseHandler);

        this.tapeLinesGroup = new TapeLinesGroup(this);
        this.getChildren().add(tapeLinesGroup);

        this.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            double width = this.getMaxWidth();
            double height = this.getMaxHeight();
            tapeLinesGroup.setLayoutX(width / 2);
            tapeLinesGroup.setLayoutY(height / 2);
            tapeLinesGroup.checkLinesAndColumns(width, height);
        });
    }

    void translate(double dx, double dy){

        offsetX += dx;
        offsetY += dy;

        tapeLinesGroup.setTranslateX(tapeLinesGroup.getTranslateX() + dx);
        tapeLinesGroup.setTranslateY(tapeLinesGroup.getTranslateY() + dy);

        double width = Math.max(Math.abs(2 * offsetX - this.getMaxWidth()), 2 * offsetX + this.getMaxWidth());
        double height = Math.max(Math.abs(2 * offsetX - this.getMaxHeight()), 2 * offsetY + this.getMaxHeight());

        tapeLinesGroup.checkLinesAndColumns(width, height);
    }
}

class TapeLinesGroup extends Group{

    private TapePane pane;

    private  ArrayList<Line> lines;
    private ArrayList<Line> columns;

    Integer bottom;
    Integer top;
    Integer left;
    Integer right;

    private double maxWidth;
    private double maxHeight;

    TapeLinesGroup(TapePane pane){
        this.pane = pane;
        this.maxWidth = 0;
        this.maxHeight = 0;

        lines = new ArrayList<>();
        columns = new ArrayList<>();

        setLeft(null);
        setRight(null);
        setBottom(0);
        setTop(0);

    }

    void setBottom(Integer bottom){
        this.bottom = bottom;
        int zeroIndex = lines.size() / 2;
        for (int i = 0; i <= zeroIndex - 1; i++)
            lines.get(i).setVisible(bottom == null || zeroIndex - 1 - i <= bottom);
    }

    void setTop(Integer top){
        this.top = top;
        int zeroIndex = lines.size() / 2;
        for (int i = zeroIndex; i < lines.size(); i++)
            lines.get(i).setVisible(top == null || i - zeroIndex <= top);
    }

    void setLeft(Integer left){
        this.left = left;
        int zeroIndex = columns.size() / 2;
        for (int i = 0; i <= zeroIndex - 1; i++)
            columns.get(i).setVisible(left == null || zeroIndex - 1 - i <= left);
    }

    void setRight(Integer right){
        this.right = right;
        int zeroIndex = columns.size() / 2;
        for (int i = zeroIndex; i < columns.size(); i++)
            columns.get(i).setVisible(right == null || i - zeroIndex <= right);
    }

    void checkLinesAndColumns(double width, double height) {
        System.out.println(width + " " + height+" // "+maxWidth+" "+maxHeight);
        ObservableList<Node> children = this.getChildren();

        int nbLines = 2 * (int)((height / (TuringMachineDrawer.TAPE_CELL_WIDTH * 2) - 0.5) + 2);
        int prevNbLines = lines.size();

        int nbColumns = 2 * (int)((width / (TuringMachineDrawer.TAPE_CELL_WIDTH * 2) - 0.5) + 2);
        int prevNbColumns = columns.size();

        double h, w;

        if(maxWidth < width) {
            for (Line line : lines) {
                line.setStartX(-TuringMachineDrawer.TAPE_CELL_WIDTH - width / 2);
                line.setEndX(TuringMachineDrawer.TAPE_CELL_WIDTH + width / 2);
            }
        }

        if(prevNbLines < nbLines) {
            for (int i = lines.size() / 2; i < nbLines / 2; i++) {
                h = TuringMachineDrawer.TAPE_CELL_WIDTH * (0.5 + i);
                Line linePos = new Line(-TuringMachineDrawer.TAPE_CELL_WIDTH - width / 2, h,
                        width / 2 + TuringMachineDrawer.TAPE_CELL_WIDTH, h);
                Line lineNeg = new Line(-TuringMachineDrawer.TAPE_CELL_WIDTH - width / 2, -h,
                        width / 2 + TuringMachineDrawer.TAPE_CELL_WIDTH, -h);

                lines.add(linePos);
                lines.add(0, lineNeg);
                children.add(lineNeg);
                children.add(linePos);
            }
        }

        h = TuringMachineDrawer.TAPE_CELL_WIDTH * 0.5;

        if(maxHeight < height) {
            for (Line column : columns) {
                column.setStartY(-h);
                column.setEndY(h);
            }
        }

        if(prevNbColumns < nbColumns) {
            for (int i = columns.size() / 2; i < nbColumns / 2; i++) {
                w = TuringMachineDrawer.TAPE_CELL_WIDTH * (0.5 + i);
                Line columnPos = new Line(w, -h, w, h);
                Line columnNeg = new Line(-w, -h, -w, h);

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

class Cell extends Rectangle{

    int i;
    int j;

}
