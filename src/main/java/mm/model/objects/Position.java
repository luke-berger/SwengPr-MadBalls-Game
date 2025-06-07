package mm.model.objects;

/**
 * POJO representing the position of an object in a level.
 * Stores the x and y coordinates as floats.
 */
public class Position {
    /** The x-coordinate of the position */
    private float x;
    /** The y-coordinate of the position */
    private float y;

    /**
     * Constructs a Position with specified x and y coordinates.
     * 
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public Position(float x, float y){
        this.x = x;
        this.y = y;
    }

    /**
     * Constructs a Position at the origin (0, 0). Default in our case
     */
    public Position(){
        this.x = 0;
        this.y = 0;
    }

    /**
     * Returns the x-coordinate.
     * @return the x-coordinate
     */
    public float getX() {return this.x;}

    /**
     * Sets the x-coordinate.
     * @param newX the new x-coordinate
     */
    public void setX(float newX) {this.x = newX;}

    /**
     * Returns the y-coordinate.
     * @return the y-coordinate
     */
    public float getY() {return this.y;}

    /**
     * Sets the y-coordinate.
     * @param newY the new y-coordinate
     */
    public void setY(float newY) {this.y = newY;}

}