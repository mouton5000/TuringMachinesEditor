package gui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

class HelpMessages extends ImageView {
    private ListIterator<String> iter;

    HelpMessages(){
        super();

        File dir = new File(getClass().getClassLoader().getResource("helpCaptures").getPath());
        List<String> list = new LinkedList<>();
        for(File file : dir.listFiles()){
            list.add(getClass().getClassLoader().getResource(file.getName()).toExternalForm());
        }
        Collections.sort(list);
        iter = list.listIterator();

        this.setOnMouseClicked(mouseEvent -> {
            double x = mouseEvent.getX();
            double y = mouseEvent.getY();

            if(x < TuringMachineDrawer.WIDTH / 24 && y < TuringMachineDrawer.HEIGHT / 12){
                this.setVisible(false);
                return;
            }

            if(mouseEvent.getButton() == MouseButton.PRIMARY)
                next();
            else if(mouseEvent.getButton() == MouseButton.SECONDARY)
                previous();
        });

        next();

    }

    private void next(){
        if(iter.hasNext())
            this.setImage(new Image(iter.next()));
    }

    private void previous(){
            if(iter.hasPrevious())
                this.setImage(new Image(iter.previous()));
    }

}
