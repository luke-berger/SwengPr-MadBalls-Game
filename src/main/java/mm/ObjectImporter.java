package mm;


import java.util.ArrayList;
import java.util.List;

import mm.model.objects.GameObject;
import mm.model.objects.Level;
import mm.model.objects.LevelReader;


public class ObjectImporter {

    private Level level;

    public ObjectImporter() {
        LevelReader levelTemplate = new LevelReader("src\\main\\java\\mm\\model\\level\\", "standard_level.json");
        level = levelTemplate.readFile();

        if (level == null || level.getLevelObjects() == null) {
            System.err.println("No level objects found or failed to load level.");
        }
    }

    public List<GameObject> getGameObjects() {
        if (level != null && level.getLevelObjects() != null) {
            return level.getLevelObjects();
        }
        return new ArrayList<>();
    }

    public static void main(String[] args) {}

}
