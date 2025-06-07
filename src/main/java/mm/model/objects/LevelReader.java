package mm.model.objects;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;

/**
 * Utility class for reading and deserializing Level objects from an input stream (e.g., JSON file).
 */
public class LevelReader {
    /** Jackson ObjectMapper for JSON deserialization */
    private ObjectMapper mapper;
    /** Input stream for the level resource */
    private InputStream inputStream;

    /**
     * Constructs a LevelReader for a given input stream.
     *
     * @param inputStream the input stream to read the level data from
     */
    public LevelReader(InputStream inputStream) {
        mapper = new ObjectMapper();
        this.inputStream = inputStream;
    }

    /**
     * Returns the ObjectMapper used for deserialization.
     *
     * @return the ObjectMapper instance
     */
    public ObjectMapper getMapper() { return this.mapper; }

    /**
     * Reads and deserializes a Level object from the input stream.
     *
     * @return the Level object if successful, or null if an error occurs
     */
    public Level readFile() {
        try {
            Level level = mapper.readValue(inputStream, Level.class);
            return level;
        } catch (Exception e) {
            System.err.println("Error: " + e + " occurred while trying to read resource. Returning null.");
            return null;
        }
    }
}
