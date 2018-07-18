package gui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Created by dimitri.watel on 06/06/18.
 */
class CoordinatesProperties {

    private DoubleProperty xProperty;
    private DoubleProperty yProperty;

    public CoordinatesProperties(){
        xProperty = new SimpleDoubleProperty();
        yProperty = new SimpleDoubleProperty();
    }

    DoubleProperty xProperty(){
        return xProperty;
    }

    DoubleProperty yProperty(){
        return yProperty;
    }

    double getX() {
        return xProperty.doubleValue();
    }

    void setX(double xProperty) {
        this.xProperty.setValue(xProperty);
    }

    double getY() {
        return yProperty.doubleValue();
    }

    void setY(double yProperty) { this.yProperty.setValue(yProperty); }
}
