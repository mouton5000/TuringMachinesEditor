package util;

/**
 * Helper in order to simply access to the resource folder.
 */
public class Ressources {
    public static String getRessource(String filename){
        return Ressources.class.getClassLoader().getResource(filename).toExternalForm();
    }
}
