package mm.model.objects;

/**
 * POJO representing the size of an object for JavaFX.
 * Stores width, height, and radius as floats.
 */
public class Size {
    /** The width of the object */
    private float width;
    /** The height of the object */
    private float height;
    /** The radius of the object (for circular shapes) */
    private float radius;

    /**
     * Returns the width of the object.
     * @return the width
     */
    public float getWidth() {return this.width;}

    /**
     * Sets the width of the object.
     * @param w the new width
     */
    public void setWidth(float w) {this.width = w;}

    /**
     * Returns the height of the object.
     * @return the height
     */
    public float getHeight() {return this.height;}

    /**
     * Sets the height of the object.
     * @param h the new height
     */
    public void setHeight(float h) {this.height = h;}

    /**
     * Returns the radius of the object.
     * @return the radius
     */
    public float getRadius() {return this.radius;}

    /**
     * Sets the radius of the object.
     * @param r the new radius
     */
    public void setRadius(float r) {this.radius= r;}
}