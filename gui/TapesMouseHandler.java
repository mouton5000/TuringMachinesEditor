package gui;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import turingmachines.Tape;
import util.Pair;

/**
 * Created by dimitri.watel on 04/06/18.
 */
public class TapesMouseHandler implements EventHandler<Event> {

    private TuringMachineDrawer drawer;

    private Double dragX;
    private Double dragY;

    public TapesMouseHandler(TuringMachineDrawer drawer) {
        this.drawer = drawer;
    }

    @Override
    public void handle(Event event) {
        if(drawer.animating)
            return;

        System.out.println(event.getEventType()+" "+event.getClass()+" "+event.getSource().getClass());

        if(event.getEventType() == MouseEvent.MOUSE_CLICKED)
            this.handleClickedEvent((MouseEvent) event);
        else if(event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            this.handlePressEvent((MouseEvent) event);
        }
        else if(event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            this.handleDragEvent((MouseEvent) event);
        }
    }

    private void handlePressEvent(MouseEvent mouseEvent) {
        Object source = mouseEvent.getSource();

        double x = mouseEvent.getX();
        double y = mouseEvent.getY();

        if(source instanceof TapeBorderPane
                || source instanceof CellOptionRectangleSymbolsOptionsGroup
                || source instanceof CellOptionRectangleHeadOptionsGroup) {
            dragX = x;
            dragY = y;
            mouseEvent.consume();
        }
        else if(source instanceof HeadMenuSelect){
            ((HeadMenuSelect) source).startTimeline();
        }
        else if(source instanceof TapePane){
            TapePane tapePane = (TapePane) source;
            if(!tapePane.cellOptionRectangle.isMaximized() && !tapePane.tapeOptionRectangle.isMaximized()) {
                Integer line = tapePane.getLine(y);
                Integer column = tapePane.getColumn(x);
                tapePane.startTimeline(line, column);
            }
        }
    }

    private void handleClickedEvent(MouseEvent mouseEvent){
        if(!mouseEvent.isStillSincePress()) {
            return;
        }

        double x = mouseEvent.getX();
        double y = mouseEvent.getY();

        Object source = mouseEvent.getSource();
        if(source instanceof TapePane){
            TapePane tapePane = (TapePane) source;

            if(tapePane.cellOptionRectangle.isMaximized())
                tapePane.closeCellOptionRectangle();
            else if(tapePane.tapeOptionRectangle.isMaximized())
                tapePane.closeTapeOptionRectangle();
            else {
                boolean pressFinished = !tapePane.animating;
                tapePane.stopTimeline();
                Integer line = tapePane.getLine(y);
                Integer column = tapePane.getColumn(x);

                if(!pressFinished)
                    tapePane.openCellOptionRectangle(line, column);
                else
                    tapePane.openTapeOptionRectangle(line, column);
            }

            mouseEvent.consume();
        }
        else if(source instanceof MinimizedOptionRectangle){
            MinimizedOptionRectangle minimizedOptionRectangle = (MinimizedOptionRectangle) source;
            TapePane tapePane = (TapePane)minimizedOptionRectangle.optionRectangle.associatedNode();
            if(minimizedOptionRectangle.optionRectangle instanceof CellOptionRectangle)
                tapePane.closeCellOptionRectangle();
            else
                tapePane.closeTapeOptionRectangle();
            mouseEvent.consume();
        }
        else if(source instanceof ChooseSymbolOptionLabel){
            ChooseSymbolOptionLabel label = (ChooseSymbolOptionLabel) source;
            CellOptionRectangle optionRectangle = label.optionRectangle;
            int line = optionRectangle.currentLine;
            int column = optionRectangle.currentColumn;

            String symbol = label.getText();
            symbol = symbol.equals("\u2205")?null:symbol;
            optionRectangle.tapePane.tapeBorderPane.tape.writeInput(line,column, symbol);

            mouseEvent.consume();
        }
        else if(source instanceof ChooseHeadOptionRectangle){
            ChooseHeadOptionRectangle chooseHeadRectangle = (ChooseHeadOptionRectangle) source;
            CellOptionRectangle optionRectangle = chooseHeadRectangle.optionRectangle;
            Color color = (Color) chooseHeadRectangle.getStroke();

            int line = optionRectangle.currentLine;
            int column = optionRectangle.currentColumn;
            Pair<Tape, Integer> pair = drawer.getHead(color);
            drawer.moveHead(pair.first, line, column, pair.second);
            mouseEvent.consume();
        }
        else if(source instanceof AddHeadOptionIcon){
            AddHeadOptionIcon addHeadOptionIcon = (AddHeadOptionIcon) source;
            CellOptionRectangle optionRectangle = addHeadOptionIcon.optionRectangle;

            Tape tape = optionRectangle.currentTape;
            int line = optionRectangle.currentLine;
            int column = optionRectangle.currentColumn;
            drawer.addHead(tape, line, column);
            mouseEvent.consume();
        }
        else if(source instanceof HeadMenuSelect){
            HeadMenuSelect headMenuSelect = (HeadMenuSelect) source;
            boolean pressFinished = !headMenuSelect.animating;
            headMenuSelect.stopTimeline();
            if(!pressFinished)
                drawer.translateTo((Color) ((HeadMenuSelect) source).getStroke());
            else
                drawer.editHeadColor((Color) ((HeadMenuSelect) source).getStroke());
            mouseEvent.consume();
        }
        else if(source instanceof AddTapeIcon){
            this.drawer.addTape();
            mouseEvent.consume();
        }
        else if(source instanceof RemoveTapeIcon){
            this.drawer.removeTape(((RemoveTapeIcon) source).tape);
            mouseEvent.consume();
        }
        else if(source instanceof OptionRectangle)
            mouseEvent.consume();
        else if(source instanceof TapeOptionIcon){
            TapeOptionIcon tapeOptionIcon = (TapeOptionIcon) source;
            TapeBorderPane tapeBorderPane = tapeOptionIcon.optionRectangle.tapeBorderPane;
            int line = tapeOptionIcon.optionRectangle.currentLine;
            int column = tapeOptionIcon.optionRectangle.currentColumn;

            Integer coord = -1;
            Integer delta = 0;
            switch (tapeOptionIcon.tapeOptionIconDirection){

                case LEFT:
                    delta = -1;
                    coord = column;
                    break;
                case RIGHT:
                    delta = 1;
                    coord = column;
                    break;
                case BOTTOM:
                    delta = -1;
                    coord = line;
                    break;
                case TOP:
                    delta = 1;
                    coord = line;
                    break;
            }

            Integer value = -1;

            switch (tapeOptionIcon.tapeOptionIconAction){

                case INFINITE:
                    value = null;
                    break;

                case REMOVE:
                    value = coord;
                    break;

                case ADD:
                    value = coord + delta;
                    break;
            }

            switch (tapeOptionIcon.tapeOptionIconDirection) {
                case LEFT:
                    tapeBorderPane.tape.setLeftBound(value);
                    break;
                case RIGHT:
                    tapeBorderPane.tape.setRightBound(value);
                    break;
                case BOTTOM:
                    tapeBorderPane.tape.setBottomBound(value);
                    break;
                case TOP:
                    tapeBorderPane.tape.setTopBound(value);
                    break;
            }
        }
    }

    private void handleDragEvent(MouseEvent mouseEvent) {
        if(mouseEvent.isStillSincePress())
            return;

        Object source = mouseEvent.getSource();
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();

        if(source instanceof TapeBorderPane){
            if(dragX == null){
                dragX = x;
                dragY = y;
            }
            else {
                TapeBorderPane pane = (TapeBorderPane) source;
                pane.translate(x - dragX, y - dragY);
                dragX = x;
                dragY = y;
            }
            mouseEvent.consume();
        }
        else if(source instanceof CellOptionRectangleHeadOptionsGroup){
            if(dragX == null)
                dragX = x;
            else {
                CellOptionRectangleHeadOptionsGroup group = (CellOptionRectangleHeadOptionsGroup) source;
                group.translate(x - dragX);
                dragX = x;
            }
            mouseEvent.consume();
        }
        else if(source instanceof CellOptionRectangleSymbolsOptionsGroup){
            if(dragX == null)
                dragX = x;
            else {
                CellOptionRectangleSymbolsOptionsGroup group = (CellOptionRectangleSymbolsOptionsGroup) source;
                group.translate(x - dragX);
                dragX = x;
            }
            mouseEvent.consume();
        }
        else if(source instanceof HeadMenuSelect) {
            ((HeadMenuSelect) source).stopTimeline();
        }
        else if(source instanceof TapePane) {
            ((TapePane) source).stopTimeline();
        }
    }
}