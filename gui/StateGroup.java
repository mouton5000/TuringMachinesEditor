package gui;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.json.JSONObject;
import util.MouseHandler;
import util.Ressources;

class StateGroup extends Group implements MouseHandler {

    boolean animating;

    private Circle outerCircle;
    private Circle innerCircle;
    private ImageView acceptIcon;
    private Line initLine1;
    private Line initLine2;
    private Label label;


    private Timeline timeline;

    StateGroup(String name){
        this.timeline = new Timeline();
        this.timeline.setOnFinished(actionEvent -> animating = false);

        this.setOnMousePressed(TuringMachineDrawer.getInstance().graphPaneMouseHandler);
        this.setOnMouseClicked(TuringMachineDrawer.getInstance().graphPaneMouseHandler);
        this.setOnMouseDragged(TuringMachineDrawer.getInstance().graphPaneMouseHandler);

        outerCircle = new Circle(TuringMachineDrawer.STATE_RADIUS);
        outerCircle.setStroke(TuringMachineDrawer.STATE_OUTER_COLOR);
        outerCircle.setFill(TuringMachineDrawer.UNSELECTED_STATE_COLOR);

        innerCircle = new Circle(TuringMachineDrawer.STATE_RADIUS *
                TuringMachineDrawer.FINAL_STATE_RADIUS_RATIO);
        innerCircle.setStroke(TuringMachineDrawer.STATE_OUTER_COLOR);
        innerCircle.setFill(TuringMachineDrawer.UNSELECTED_STATE_COLOR);
        innerCircle.setVisible(false);
        
        acceptIcon = new ImageView(Ressources.getRessource("Accept-icon.png"));
        acceptIcon.setLayoutX(
                TuringMachineDrawer.STATE_RADIUS * Math.sqrt(2) / 2
                        - acceptIcon.getBoundsInLocal().getWidth() / 2);
        acceptIcon.setLayoutY(
                TuringMachineDrawer.STATE_RADIUS * Math.sqrt(2) / 2
                        - acceptIcon.getBoundsInLocal().getHeight() / 2);
        acceptIcon.setVisible(false);

        initLine1 = new Line();
        initLine2 = new Line();
        initLine1.setStartX(-TuringMachineDrawer.STATE_RADIUS);
        initLine2.setStartX(-TuringMachineDrawer.STATE_RADIUS);
        initLine1.setEndX(-(1 + Math.cos(TuringMachineDrawer.TRANSITION_ANGLE)) *
                TuringMachineDrawer.STATE_RADIUS);
        initLine2.setEndX(-(1 + Math.cos(TuringMachineDrawer.TRANSITION_ANGLE)) *
                TuringMachineDrawer.STATE_RADIUS);
        initLine1.setEndY(-Math.sin(TuringMachineDrawer.TRANSITION_ANGLE) *
                TuringMachineDrawer.STATE_RADIUS);
        initLine2.setEndY(Math.sin(TuringMachineDrawer.TRANSITION_ANGLE) *
                TuringMachineDrawer.STATE_RADIUS);
        initLine1.setVisible(false);
        initLine2.setVisible(false);

        label = new Label(name);
        label.setFont(Font.font(TuringMachineDrawer.STATE_NAME_FONT_NAME,
                TuringMachineDrawer.STATE_NAME_FONT_SIZE));
        label.setMinWidth(TuringMachineDrawer.STATE_RADIUS * 2);
        label.setMaxWidth(TuringMachineDrawer.STATE_RADIUS * 2);
        label.setMinHeight(TuringMachineDrawer.STATE_RADIUS * 2);
        label.setMaxHeight(TuringMachineDrawer.STATE_RADIUS * 2);
        label.setAlignment(Pos.CENTER);

        this.getChildren().addAll(outerCircle, innerCircle, acceptIcon, initLine1, initLine2, label);
        label.setLayoutX(- label.getMinWidth() / 2);
        label.setLayoutY(- label.getMinHeight() / 2);

        setUnselected();
    }

    void setSelected(){
        outerCircle.setFill(TuringMachineDrawer.SELECTED_STATE_COLOR);
        innerCircle.setFill(TuringMachineDrawer.SELECTED_STATE_COLOR);
    }

    void setUnselected(){
        outerCircle.setFill(TuringMachineDrawer.UNSELECTED_STATE_COLOR);
        innerCircle.setFill(TuringMachineDrawer.UNSELECTED_STATE_COLOR);
    }

    boolean isFinal(){
        return innerCircle.isVisible() && !acceptIcon.isVisible();
    }

    boolean isAccepting(){
        return acceptIcon.isVisible();
    }

    boolean isInitial(){
        return initLine1.isVisible();
    }

    void setFinal(boolean isFinal){
        innerCircle.setVisible(isFinal);
    }

    void setAccepting(boolean isAccepting){
        acceptIcon.setVisible(isAccepting);
    }

    void setInitial(boolean isInitial){
        initLine1.setVisible(isInitial);
        initLine2.setVisible(isInitial);
    }

    String getName(){
        return label.getText();
    }

    public void setName(String name) {
        this.label.setText(name);
    }


    void startTimeline(){
        this.animating = true;
        timeline.getKeyFrames().clear();
        KeyValue kOuterFill = new KeyValue(this.outerCircle.fillProperty(),
                TuringMachineDrawer.STATE_PRESS_COLOR,
                Interpolator.EASE_BOTH);
        KeyValue kInnerfill = new KeyValue(this.outerCircle.fillProperty(),
                TuringMachineDrawer.STATE_PRESS_COLOR,
                Interpolator.EASE_BOTH);
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(TuringMachineDrawer.STATE_PRESS_DURATION),
                        kOuterFill, kInnerfill)
        );

        this.outerCircle.setFill(TuringMachineDrawer.UNSELECTED_STATE_COLOR);
        this.innerCircle.setFill(TuringMachineDrawer.UNSELECTED_STATE_COLOR);
        timeline.play();
    }

    void stopTimeline(){
        timeline.stop();
        this.outerCircle.setFill(TuringMachineDrawer.UNSELECTED_STATE_COLOR);
        this.innerCircle.setFill(TuringMachineDrawer.UNSELECTED_STATE_COLOR);
        this.animating = false;
    }

    KeyValue getCurrentStateKeyValue() {
        return new KeyValue(this.outerCircle.fillProperty(),
                TuringMachineDrawer.STATE_CURRENT_COLOR,
                Interpolator.EASE_BOTH);
    }

    KeyValue getNoCurrentStateKeyValue() {
        return new KeyValue(this.outerCircle.fillProperty(),
                TuringMachineDrawer.UNSELECTED_STATE_COLOR,
                Interpolator.EASE_BOTH);
    }

    JSONObject getJSON() {
        return new JSONObject()
                .put("x", this.getLayoutX())
                .put("y", this.getLayoutY())
                .put("name", this.getName())
                .put("isFinal", this.isFinal())
                .put("isAccepting", this.isAccepting())
                .put("isInitial", this.isInitial());
    }

    @Override
    public boolean onMouseClicked(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode)
            return false;

        GraphPane graphPane = TuringMachineDrawer.getInstance().graphPane;

        if(graphPane.stateOptionRectangle.isMaximized()){
            graphPane.closeStateOptionRectangle();
            return true;
        }
        else if(graphPane.transitionOptionRectangle.isMaximized()){
            graphPane.closeTransitionOptionRectangle();
            return true;
        }

        if(TuringMachineDrawer.getInstance().manualMode)
            TuringMachineDrawer.getInstance().manualSelectCurrentState(this);
        else {
            boolean pressFinished = !this.animating;
            this.stopTimeline();

            if (pressFinished) {
                graphPane.unselect();
                graphPane.openStateOptionRectangle(this);
            } else {
                Node selected = graphPane.getSelected();
                if (selected == null)
                    graphPane.select(this);
                else if (selected instanceof StateGroup) {
                    graphPane.addTransition((StateGroup) selected, this);
                    graphPane.unselect();
                } else
                    graphPane.unselect();
            }
        }

        return true;
    }

    @Override
    public boolean onMouseDragged(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        this.stopTimeline();
        GraphPane graphPane = TuringMachineDrawer.getInstance().graphPane;
        graphPane.select(this);
        graphPane.moveStateGroup(this,this.getLayoutX() + mouseEvent.getX(),
                this.getLayoutY() + mouseEvent.getY());
        return true;
    }

    @Override
    public boolean onMousePressed(MouseEvent mouseEvent) {
        if(TuringMachineDrawer.getInstance().buildMode || TuringMachineDrawer.getInstance().manualMode)
            return false;

        if(!TuringMachineDrawer.getInstance().graphPane.stateOptionRectangle.isMaximized()
                && !TuringMachineDrawer.getInstance().graphPane.transitionOptionRectangle.isMaximized())
            this.startTimeline();

        return false;
    }
}