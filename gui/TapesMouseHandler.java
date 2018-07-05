package gui;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import turingmachines.Tape;
import util.Pair;
import util.widget.VirtualKeyboard;

import java.util.Optional;

/**
 * Created by dimitri.watel on 04/06/18.
 */
public class TapesMouseHandler implements EventHandler<Event> {

    private Double dragX;
    private Double dragY;

    public TapesMouseHandler() {
        }

    @Override
    public void handle(Event event) {
        if(TuringMachineDrawer.getInstance().animating)
            return;

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
                || source instanceof CellOptionRectangleHeadOptionsGroup
                || source instanceof TapesHeadMenu
                || source instanceof SymbolsMenu) {
            dragX = x;
            dragY = y;
            mouseEvent.consume();
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof HeadMenuSelect){
            HeadMenuSelect headMenuSelect = (HeadMenuSelect) source;
            if(!headMenuSelect.tapeHeadMenu.headOptionRectangle.isMaximized()){
                headMenuSelect.startTimeline();
            }
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof TapePane){
            TapePane tapePane = (TapePane) source;
            if(!tapePane.cellOptionRectangle.isMaximized() && !tapePane.tapeOptionRectangle.isMaximized()) {
                Integer line = tapePane.getLine(y);
                Integer column = tapePane.getColumn(x);
                tapePane.startTimeline(line, column);
            }
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof SymbolLabel){
            SymbolLabel symbolLabel = (SymbolLabel) source;
            if(!symbolLabel.symbolsMenu.symbolOptionRectangle.isMaximized()){
                symbolLabel.startTimeline();
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
        if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof TapePane){
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
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof MinimizedOptionRectangle){
            MinimizedOptionRectangle minimizedOptionRectangle = (MinimizedOptionRectangle) source;
            if(minimizedOptionRectangle.optionRectangle instanceof CellOptionRectangle) {
                TapePane tapePane = (TapePane)minimizedOptionRectangle.optionRectangle.associatedNode();
                tapePane.closeCellOptionRectangle();
            }
            else if(minimizedOptionRectangle.optionRectangle instanceof TapeOptionRectangle) {
                TapePane tapePane = (TapePane)minimizedOptionRectangle.optionRectangle.associatedNode();
                tapePane.closeTapeOptionRectangle();
            }
            else if(minimizedOptionRectangle.optionRectangle instanceof HeadOptionRectangle) {
                HeadOptionRectangle optionRectangle = (HeadOptionRectangle) minimizedOptionRectangle.optionRectangle;
                optionRectangle.tapeHeadMenu.closeHeadOptionRectangle();
            }
            else if(minimizedOptionRectangle.optionRectangle instanceof SymbolOptionRectangle) {
                SymbolOptionRectangle optionRectangle = (SymbolOptionRectangle) minimizedOptionRectangle.optionRectangle;
                optionRectangle.symbolsMenu.closeSymbolOptionRectangle();
            }
            mouseEvent.consume();
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof CellOptionRectangleChooseSymbolOptionLabel){
            CellOptionRectangleChooseSymbolOptionLabel label = (CellOptionRectangleChooseSymbolOptionLabel) source;
            CellOptionRectangle optionRectangle = label.optionRectangle;
            int line = optionRectangle.currentLine;
            int column = optionRectangle.currentColumn;

            String symbol = label.getText();
            symbol = symbol.equals(TuringMachineDrawer.BLANK_SYMBOL)?null:symbol;
            optionRectangle.tapePane.tapeBorderPane.tape.writeInput(line,column, symbol);

            mouseEvent.consume();
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof CellOptionRectangleChooseHead){
            CellOptionRectangleChooseHead chooseHeadRectangle = (CellOptionRectangleChooseHead) source;
            CellOptionRectangle optionRectangle = chooseHeadRectangle.optionRectangle;
            Color color = (Color) chooseHeadRectangle.getStroke();

            int line = optionRectangle.currentLine;
            int column = optionRectangle.currentColumn;
            Pair<Tape, Integer> pair = TuringMachineDrawer.getInstance().getHead(color);
            TuringMachineDrawer.getInstance().moveHead(pair.first, line, column, pair.second);
            mouseEvent.consume();
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof AddTapeIcon){
            TuringMachineDrawer.getInstance().addTape();
            mouseEvent.consume();
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof TapeHeadMenu){
            TapeHeadMenu tapeHeadMenu = (TapeHeadMenu) source;
            if(tapeHeadMenu.headOptionRectangle.isMaximized())
                tapeHeadMenu.closeHeadOptionRectangle();
            mouseEvent.consume();
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof RemoveTapeIcon){
            RemoveTapeIcon removeTapeIcon = (RemoveTapeIcon) source;
            if(removeTapeIcon.tapeHeadMenu.headOptionRectangle.isMaximized())
                removeTapeIcon.tapeHeadMenu.closeHeadOptionRectangle();
            else
                TuringMachineDrawer.getInstance().removeTape(removeTapeIcon.tape);
            mouseEvent.consume();
        }
        else if(source instanceof HeadMenuSelect){

            HeadMenuSelect headMenuSelect = (HeadMenuSelect) source;

            if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode){
                TuringMachineDrawer.getInstance().centerOn(headMenuSelect.tapeHeadMenu.tape, headMenuSelect.getHead());
            }
            else {
                if (headMenuSelect.tapeHeadMenu.headOptionRectangle.isMaximized())
                    headMenuSelect.tapeHeadMenu.closeHeadOptionRectangle();
                else {
                    boolean pressFinished = !headMenuSelect.animating;
                    headMenuSelect.stopTimeline();

                    if (!pressFinished)
                        TuringMachineDrawer.getInstance().centerOn(headMenuSelect.tapeHeadMenu.tape, headMenuSelect.getHead());
                    else
                        headMenuSelect.tapeHeadMenu.openHeadOptionRectangle(headMenuSelect.getHead());
                }
                mouseEvent.consume();
            }
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof RemoveHeadIcon){
            RemoveHeadIcon removeHeadIcon = (RemoveHeadIcon) source;
            Tape tape = removeHeadIcon.optionRectangle.tape;
            int head = removeHeadIcon.optionRectangle.currentHead;
            TuringMachineDrawer.getInstance().removeHead(tape, head, true);
        }
        else if(source instanceof TranslateTapesArrow){
            TranslateTapesArrow translateTapesArrow = ((TranslateTapesArrow) source);
            TuringMachineDrawer.getInstance().tapesPane.centerOn(translateTapesArrow.tapeBorderPane.tape);

            mouseEvent.consume();
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof OptionRectangle)
            mouseEvent.consume();
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof TapeOptionIcon){
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
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof SymbolsMenu){
            if(((SymbolsMenu) source).symbolOptionRectangle.isMaximized())
                ((SymbolsMenu) source).closeSymbolOptionRectangle();
            mouseEvent.consume();
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof SymbolLabel){

            SymbolLabel symbolLabel = (SymbolLabel) source;

            if(symbolLabel.symbolsMenu.symbolOptionRectangle.isMaximized())
                symbolLabel.symbolsMenu.closeSymbolOptionRectangle();
            else {
                boolean pressFinished = !symbolLabel.animating;
                symbolLabel.stopTimeline();

                if (pressFinished)
                    symbolLabel.symbolsMenu.openSymbolOptionRectantle(symbolLabel.symbolsMenu.getIndex(symbolLabel));
            }
            mouseEvent.consume();
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof AddSymbolIcon){

            VirtualKeyboard virtualKeyboard = new VirtualKeyboard();
            virtualKeyboard.setX(mouseEvent.getScreenX() - virtualKeyboard.getWidth() / 2);
            virtualKeyboard.setY(mouseEvent.getScreenY());

            Optional<String> result = virtualKeyboard.showAndWait();
            if(result.isPresent()){
                TuringMachineDrawer.getInstance().addSymbol(result.get());
            }
            mouseEvent.consume();
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof EditSymbolIcon){

            VirtualKeyboard virtualKeyboard = new VirtualKeyboard();
            virtualKeyboard.setX(mouseEvent.getScreenX() - virtualKeyboard.getWidth() / 2);
            virtualKeyboard.setY(mouseEvent.getScreenY());

            Optional<String> result = virtualKeyboard.showAndWait();
            if(result.isPresent())
                TuringMachineDrawer.getInstance().editSymbol(((EditSymbolIcon) source).optionRectangle.currentSymbolIndex, result.get());

            mouseEvent.consume();
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof RemoveSymbolIcon){
            TuringMachineDrawer.getInstance().removeSymbol(((RemoveSymbolIcon) source).optionRectangle.currentSymbolIndex);
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
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof CellOptionRectangleHeadOptionsGroup){
            if(dragX == null)
                dragX = x;
            else {
                CellOptionRectangleHeadOptionsGroup group = (CellOptionRectangleHeadOptionsGroup) source;
                group.translate(x - dragX);
                dragX = x;
            }
            mouseEvent.consume();
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof CellOptionRectangleSymbolsOptionsGroup){
            if(dragX == null)
                dragX = x;
            else {
                CellOptionRectangleSymbolsOptionsGroup group = (CellOptionRectangleSymbolsOptionsGroup) source;
                group.translate(x - dragX);
                dragX = x;
            }
            mouseEvent.consume();
        }
        else if(source instanceof TapesHeadMenu){
            if(dragX == null)
                dragX = x;
            else {
                TapesHeadMenu group = (TapesHeadMenu) source;
                group.translate(x - dragX);
                dragX = x;
            }
            mouseEvent.consume();
        }
        else if(source instanceof SymbolsMenu){
            if(dragX == null)
                dragX = x;
            else {
                SymbolsMenu group = (SymbolsMenu) source;
                group.translate(x - dragX);
                dragX = x;
            }
            mouseEvent.consume();
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof HeadMenuSelect) {
            ((HeadMenuSelect) source).stopTimeline();
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof SymbolLabel) {
            ((SymbolLabel) source).stopTimeline();
        }
        else if(!TuringMachineDrawer.getInstance().buildMode && !TuringMachineDrawer.getInstance().manualMode && source instanceof TapePane) {
            ((TapePane) source).stopTimeline();
        }
    }
}