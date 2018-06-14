package gui;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by dimitri.watel on 04/06/18.
 */
public class TapesMouseHandler implements EventHandler<Event> {

    private TuringMachineDrawer drawer;

    private Double dragX;
    private Double dragY;

    private Timer timer;
    private TimerTask currentTask;

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


        if(source instanceof TapeBorderPane
                || source instanceof CellOptionRectangleSymbolsOptionsGroup
                || source instanceof CellOptionRectangleHeadOptionsGroup) {
            dragX = mouseEvent.getX();
            dragY = mouseEvent.getY();
            mouseEvent.consume();
        }
        else if(source instanceof HeadMenuSelect){
            if(currentTask == null) {
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        currentTask = null;
                    }
                };
                currentTask = timerTask;
                timer = new Timer();
                timer.schedule(timerTask, TuringMachineDrawer.TAPE_HEAD_MENU_EDIT_PRESS_DURATION);
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
            else {
                Integer line = tapePane.getLine(y);
                Integer column = tapePane.getColumn(x);
                if (line != null && column != null)
                    tapePane.openCellOptionRectangle(line, column);
            }

            mouseEvent.consume();
        }
        else if(source instanceof MinimizedOptionRectangle){
            TapePane tapePane = (TapePane)((MinimizedOptionRectangle) source).optionRectangle.associatedNode();
            tapePane.closeCellOptionRectangle();
            mouseEvent.consume();
        }
        else if(source instanceof ChooseSymbolOptionLabel){
            ChooseSymbolOptionLabel label = (ChooseSymbolOptionLabel) source;
            CellOptionRectangle optionRectangle = label.optionRectangle;
            int line = optionRectangle.currentLine;
            int column = optionRectangle.currentColumn;

            String symbol = label.getText();
            symbol = symbol.equals("\u2205")?null:symbol;
            optionRectangle.tapePane.drawSymbol(line, column, symbol);


            mouseEvent.consume();
        }
        else if(source instanceof ChooseHeadOptionRectangle){
            ChooseHeadOptionRectangle chooseHeadRectangle = (ChooseHeadOptionRectangle) source;
            CellOptionRectangle optionRectangle = chooseHeadRectangle.optionRectangle;
            Color color = (Color) chooseHeadRectangle.getStroke();

            int line = optionRectangle.currentLine;
            int column = optionRectangle.currentColumn;
            int head = drawer.getHead(color);
            drawer.moveHead(0, line, column, head);
            mouseEvent.consume();
        }
        else if(source instanceof AddHeadOptionIcon){
            AddHeadOptionIcon addHeadOptionIcon = (AddHeadOptionIcon) source;
            CellOptionRectangle optionRectangle = addHeadOptionIcon.optionRectangle;

            int line = optionRectangle.currentLine;
            int column = optionRectangle.currentColumn;
            drawer.addHead(0, line, column);
            mouseEvent.consume();
        }
        else if(source instanceof HeadMenuSelect){
            if(currentTask != null) {
                currentTask = null;
                timer.cancel();
                drawer.translateTo((Color) ((HeadMenuSelect) source).getStroke());
            }
            else
                drawer.editHeadColor((Color) ((HeadMenuSelect) source).getStroke());
            mouseEvent.consume();
        }
        else if(source instanceof OptionRectangle)
            mouseEvent.consume();
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
    }
}