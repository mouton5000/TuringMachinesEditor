package util;

public class Ressources {
    public static String getRessource(String filename){
        return Ressources.class.getClassLoader().getResource(filename).toExternalForm();
    }
}
