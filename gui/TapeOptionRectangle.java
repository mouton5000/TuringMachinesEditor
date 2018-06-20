package gui;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Line;
import turingmachines.Tape;

/**
 * Created by dimitri.watel on 14/06/18.
 */
class TapeOptionRectangle extends OptionRectangle{

    private final TapeOptionIcon add_column_left_icon;
    private final TapeOptionIcon add_column_right_icon;
    private final TapeOptionIcon add_line_bottom_icon;
    private final TapeOptionIcon add_line_top_icon;

    private final TapeOptionIcon remove_columns_left_icon;
    private final TapeOptionIcon remove_columns_right_icon;
    private final TapeOptionIcon remove_lines_bottom_icon;
    private final TapeOptionIcon remove_lines_top_icon;

    private final TapeOptionIcon inf_column_left_icon;
    private final TapeOptionIcon inf_column_right_icon;
    private final TapeOptionIcon inf_line_bottom_icon;
    private final TapeOptionIcon inf_line_top_icon;

    private final ImageView inf_column_left_icon_gray;
    private final ImageView inf_column_right_icon_gray;
    private final ImageView inf_line_bottom_icon_gray;
    private final ImageView inf_line_top_icon_gray;
    TapeBorderPane tapeBorderPane;

    Tape currentTape;
    int currentLine;
    int currentColumn;

    TapeOptionRectangle(TuringMachineDrawer drawer, TapeBorderPane tapeBorderPane) {
        super(drawer, drawer.tapesMouseHandler);
        this.tapeBorderPane = tapeBorderPane;

        add_column_left_icon = new TapeOptionIcon(this, "./images/add_column_left.png",
                TapeOptionIconAction.ADD, TapeOptionIconDirection.LEFT);
        add_column_right_icon = new TapeOptionIcon(this, "./images/add_column_right.png",
                TapeOptionIconAction.ADD, TapeOptionIconDirection.RIGHT);
        add_line_bottom_icon = new TapeOptionIcon(this, "./images/add_line_bottom.png",
                TapeOptionIconAction.ADD, TapeOptionIconDirection.BOTTOM);
        add_line_top_icon = new TapeOptionIcon(this, "./images/add_line_top.png",
                TapeOptionIconAction.ADD, TapeOptionIconDirection.TOP);

        remove_columns_left_icon = new TapeOptionIcon(this, "./images/remove_columns_left.png",
                TapeOptionIconAction.REMOVE, TapeOptionIconDirection.LEFT);
        remove_columns_right_icon = new TapeOptionIcon(this, "./images/remove_columns_right.png",
                TapeOptionIconAction.REMOVE, TapeOptionIconDirection.RIGHT);
        remove_lines_bottom_icon = new TapeOptionIcon(this, "./images/remove_lines_bottom.png",
                TapeOptionIconAction.REMOVE, TapeOptionIconDirection.BOTTOM);
        remove_lines_top_icon = new TapeOptionIcon(this, "./images/remove_lines_top.png",
                TapeOptionIconAction.REMOVE, TapeOptionIconDirection.TOP);

        inf_column_left_icon = new TapeOptionIcon(this, "./images/inf_column_left.png",
                TapeOptionIconAction.INFINITE, TapeOptionIconDirection.LEFT);
        inf_column_right_icon = new TapeOptionIcon(this, "./images/inf_column_right.png",
                TapeOptionIconAction.INFINITE, TapeOptionIconDirection.RIGHT);
        inf_line_bottom_icon = new TapeOptionIcon(this, "./images/inf_line_bottom.png",
                TapeOptionIconAction.INFINITE, TapeOptionIconDirection.BOTTOM);
        inf_line_top_icon = new TapeOptionIcon(this, "./images/inf_line_top.png",
                TapeOptionIconAction.INFINITE, TapeOptionIconDirection.TOP);

        inf_column_left_icon_gray = new ImageView("./images/inf_column_left_gray.png");
        inf_column_right_icon_gray = new ImageView("./images/inf_column_right_gray.png");
        inf_line_bottom_icon_gray = new ImageView("./images/inf_line_bottom_gray.png");
        inf_line_top_icon_gray = new ImageView("./images/inf_line_top_gray.png");

        add_column_left_icon.setFitWidth(TuringMachineDrawer.TAPE_OPTION_RECTANGLE_ICON_WIDTH);
        add_column_right_icon.setFitWidth(TuringMachineDrawer.TAPE_OPTION_RECTANGLE_ICON_WIDTH);
        add_line_bottom_icon.setFitWidth(TuringMachineDrawer.TAPE_OPTION_RECTANGLE_ICON_WIDTH);
        add_line_top_icon.setFitWidth(TuringMachineDrawer.TAPE_OPTION_RECTANGLE_ICON_WIDTH);

        remove_columns_left_icon.setFitWidth(TuringMachineDrawer.TAPE_OPTION_RECTANGLE_ICON_WIDTH);
        remove_columns_right_icon.setFitWidth(TuringMachineDrawer.TAPE_OPTION_RECTANGLE_ICON_WIDTH);
        remove_lines_bottom_icon.setFitWidth(TuringMachineDrawer.TAPE_OPTION_RECTANGLE_ICON_WIDTH);
        remove_lines_top_icon.setFitWidth(TuringMachineDrawer.TAPE_OPTION_RECTANGLE_ICON_WIDTH);

        inf_column_left_icon.setFitWidth(TuringMachineDrawer.TAPE_OPTION_RECTANGLE_ICON_WIDTH);
        inf_column_right_icon.setFitWidth(TuringMachineDrawer.TAPE_OPTION_RECTANGLE_ICON_WIDTH);
        inf_line_bottom_icon.setFitWidth(TuringMachineDrawer.TAPE_OPTION_RECTANGLE_ICON_WIDTH);
        inf_line_top_icon.setFitWidth(TuringMachineDrawer.TAPE_OPTION_RECTANGLE_ICON_WIDTH);

        double left2X = - TuringMachineDrawer.TAPE_OPTION_RECTANGLE_SPACING * 3 / 2
                - TuringMachineDrawer.TAPE_OPTION_RECTANGLE_ICON_WIDTH * 3 / 2
                - TuringMachineDrawer.TAPE_OPTION_RECTANGLE_ICON_WIDTH / 2;
        double leftX = - TuringMachineDrawer.TAPE_OPTION_RECTANGLE_SPACING / 2
                - TuringMachineDrawer.TAPE_OPTION_RECTANGLE_ICON_WIDTH / 2
                - TuringMachineDrawer.TAPE_OPTION_RECTANGLE_ICON_WIDTH / 2;
        double right2X = TuringMachineDrawer.TAPE_OPTION_RECTANGLE_SPACING * 3 / 2
                + TuringMachineDrawer.TAPE_OPTION_RECTANGLE_ICON_WIDTH * 3 / 2
                - TuringMachineDrawer.TAPE_OPTION_RECTANGLE_ICON_WIDTH / 2;
        double rightX = TuringMachineDrawer.TAPE_OPTION_RECTANGLE_SPACING / 2
                + TuringMachineDrawer.TAPE_OPTION_RECTANGLE_ICON_WIDTH / 2
                - TuringMachineDrawer.TAPE_OPTION_RECTANGLE_ICON_WIDTH / 2;

        double topY = TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT / 2
                - TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT * 3 / 4
                - TuringMachineDrawer.TAPE_OPTION_RECTANGLE_ICON_WIDTH / 2;
        double bottomY = TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT / 2
                - TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 4
                - TuringMachineDrawer.TAPE_OPTION_RECTANGLE_ICON_WIDTH / 2;

        add_column_left_icon.setLayoutX(left2X);
        add_column_left_icon.setLayoutY(bottomY);

        add_column_right_icon.setLayoutX(right2X);
        add_column_right_icon.setLayoutY(bottomY);

        add_line_bottom_icon.setLayoutX(leftX);
        add_line_bottom_icon.setLayoutY(bottomY);

        add_line_top_icon.setLayoutX(leftX);
        add_line_top_icon.setLayoutY(topY);

        remove_columns_left_icon.setLayoutX(left2X);
        remove_columns_left_icon.setLayoutY(bottomY);

        remove_columns_right_icon.setLayoutX(right2X);
        remove_columns_right_icon.setLayoutY(bottomY);

        remove_lines_bottom_icon.setLayoutX(leftX);
        remove_lines_bottom_icon.setLayoutY(bottomY);

        remove_lines_top_icon.setLayoutX(leftX);
        remove_lines_top_icon.setLayoutY(topY);

        inf_column_left_icon.setLayoutX(left2X);
        inf_column_left_icon.setLayoutY(topY);

        inf_column_right_icon.setLayoutX(right2X);
        inf_column_right_icon.setLayoutY(topY);

        inf_line_bottom_icon.setLayoutX(rightX);
        inf_line_bottom_icon.setLayoutY(bottomY);

        inf_line_top_icon.setLayoutX(rightX);
        inf_line_top_icon.setLayoutY(topY);

        inf_column_left_icon_gray.setLayoutX(left2X);
        inf_column_left_icon_gray.setLayoutY(topY);

        inf_column_right_icon_gray.setLayoutX(right2X);
        inf_column_right_icon_gray.setLayoutY(topY);

        inf_line_bottom_icon_gray.setLayoutX(rightX);
        inf_line_bottom_icon_gray.setLayoutY(bottomY);

        inf_line_top_icon_gray.setLayoutX(rightX);
        inf_line_top_icon_gray.setLayoutY(topY);

        Line leftLine = new Line(
                - TuringMachineDrawer.TAPE_OPTION_RECTANGLE_SPACING
                - TuringMachineDrawer.TAPE_OPTION_RECTANGLE_ICON_WIDTH,
                TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT / 2,
                - TuringMachineDrawer.TAPE_OPTION_RECTANGLE_SPACING
                        - TuringMachineDrawer.TAPE_OPTION_RECTANGLE_ICON_WIDTH,
                TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT / 2
                - TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT
        );

        Line rightLine = new Line(
                TuringMachineDrawer.TAPE_OPTION_RECTANGLE_SPACING
                + TuringMachineDrawer.TAPE_OPTION_RECTANGLE_ICON_WIDTH,
                TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT / 2,
                TuringMachineDrawer.TAPE_OPTION_RECTANGLE_SPACING
                        + TuringMachineDrawer.TAPE_OPTION_RECTANGLE_ICON_WIDTH,
                TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT / 2
                        - TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT
        );

        Line centerLine = new Line(
                - TuringMachineDrawer.TAPE_OPTION_RECTANGLE_SPACING
                        - TuringMachineDrawer.TAPE_OPTION_RECTANGLE_ICON_WIDTH,
                TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT / 2
                - TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2,
                TuringMachineDrawer.TAPE_OPTION_RECTANGLE_SPACING
                        + TuringMachineDrawer.TAPE_OPTION_RECTANGLE_ICON_WIDTH,
                TuringMachineDrawer.OPTION_RECTANGLE_MINIMIZED_HEIGHT / 2
                        - TuringMachineDrawer.OPTION_RECTANGLE_MAXIMIZED_HEIGHT / 2
        );

        this.getChildren().addAll(
                add_column_left_icon,
                add_column_right_icon,
                add_line_bottom_icon,
                add_line_top_icon,
                remove_columns_left_icon,
                remove_columns_right_icon,
                remove_lines_bottom_icon,
                remove_lines_top_icon,
                inf_column_left_icon,
                inf_column_right_icon,
                inf_line_bottom_icon,
                inf_line_top_icon,
                inf_column_left_icon_gray,
                inf_column_right_icon_gray,
                inf_line_bottom_icon_gray,
                inf_line_top_icon_gray,

                leftLine,
                rightLine,
                centerLine
        );

    }

    void setLineAndColumn(Tape tape, int line, int column){
        this.currentTape = tape;
        this.currentLine = line;
        this.currentColumn = column;

        reset();
    }

    void reset(){
        setLeft();
        setRight();
        setBottom();
        setTop();
    }

    private void setLeft(){
        Integer left = tapeBorderPane.left;
        boolean infinite = left == null;
        boolean border = !infinite && currentColumn == left;

        inf_column_left_icon.setVisible(!infinite);
        inf_column_left_icon_gray.setVisible(infinite);
        add_column_left_icon.setVisible(border);
        remove_columns_left_icon.setVisible(!border);
    }

    private void setRight(){
        Integer right = tapeBorderPane.right;
        boolean infinite = right == null;
        boolean border = !infinite && currentColumn == right;

        inf_column_right_icon.setVisible(!infinite);
        inf_column_right_icon_gray.setVisible(infinite);
        add_column_right_icon.setVisible(border);
        remove_columns_right_icon.setVisible(!border);
    }

    private void setBottom(){
        Integer bottom = tapeBorderPane.bottom;
        boolean infinite = bottom == null;
        boolean border = !infinite && currentLine == bottom;

        inf_line_bottom_icon.setVisible(!infinite);
        inf_line_bottom_icon_gray.setVisible(infinite);
        add_line_bottom_icon.setVisible(border);
        remove_lines_bottom_icon.setVisible(!border);
    }

    private void setTop(){
        Integer top = tapeBorderPane.top;
        boolean infinite = top == null;
        boolean border = !infinite && currentLine == top;

        inf_line_top_icon.setVisible(!infinite);
        inf_line_top_icon_gray.setVisible(infinite);
        add_line_top_icon.setVisible(border);
        remove_lines_top_icon.setVisible(!border);
    }

    @Override
    protected Node associatedNode() {
        return tapeBorderPane.tapePane;
    }
}

enum TapeOptionIconAction{
    INFINITE,
    REMOVE,
    ADD
}

enum TapeOptionIconDirection{
    LEFT,
    RIGHT,
    BOTTOM,
    TOP
}

class TapeOptionIcon extends ImageView{

    TapeOptionIconAction tapeOptionIconAction;
    TapeOptionIconDirection tapeOptionIconDirection;
    TapeOptionRectangle optionRectangle;

    public TapeOptionIcon(TapeOptionRectangle optionRectangle, String url,
                          TapeOptionIconAction tapeOptionIconAction, TapeOptionIconDirection tapeOptionIconDirection) {
        super(url);
        this.optionRectangle = optionRectangle;
        this.tapeOptionIconAction = tapeOptionIconAction;
        this.tapeOptionIconDirection = tapeOptionIconDirection;
        this.setOnMouseClicked(optionRectangle.tapeBorderPane.drawer.tapesMouseHandler);


    }

}