package mm.model;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.List;

/**
 * Service for handling JSON serialization and deserialization of simulation state.
 * Uses Jackson ObjectMapper for consistent JSON handling across the application.
 */
public class JsonStateService {
    private final ObjectMapper mapper;
    
    /**
     * Constructs a new JsonStateService with configured ObjectMapper.
     * The ObjectMapper is set up with indented output for better readability.
     */
    public JsonStateService() {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
    
    /**
     * Generates JSON representation of current simulation state.
     * @param droppedObjects List of game objects dropped in the simulation
     * @param inventoryObjects List of objects in the inventory
     * @return JSON string representation of the simulation state
     */
    public String generateStateJson(List<GameObject> droppedObjects, List<InventoryObject> inventoryObjects) {
        try {
            SimulationState state = new SimulationState();
            state.setDroppedObjects(droppedObjects);
            state.setInventoryObjects(inventoryObjects);
            return mapper.writeValueAsString(state);
        } catch (Exception e) {
            System.err.println("Failed to generate JSON: " + e.getMessage());
            return "{}";
        }
    }
    
    /**
     * Parses JSON and returns simulation state.
     * @param jsonString The JSON string to parse
     * @return SimulationState object parsed from JSON
     * @throws Exception if JSON cannot be parsed
     */
    public SimulationState parseStateJson(String jsonString) throws Exception {
        return mapper.readValue(jsonString, SimulationState.class);
    }
    
    /**
     * Validates if JSON string is valid simulation state format.
     * @param jsonString The JSON string to validate
     * @return true if JSON is valid, false otherwise
     */
    public boolean isValidJson(String jsonString) {
        try {
            parseStateJson(jsonString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Validates JSON and provides detailed error information.
     * @param jsonString The JSON string to validate
     * @return ValidationResult containing validation status and error details
     */
    public ValidationResult validateJson(String jsonString) {
        return validateJson(jsonString, 0, 0);
    }
    
    /**
     * Validates JSON and provides detailed error information including bounds checking.
     * @param jsonString The JSON string to validate
     * @param simSpaceWidth The width of the simulation space for bounds checking
     * @param simSpaceHeight The height of the simulation space for bounds checking
     * @return ValidationResult containing validation status and error details
     */
    public ValidationResult validateJson(String jsonString, double simSpaceWidth, double simSpaceHeight) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return new ValidationResult(false, "Please enter some JSON content to validate");
        }
        
        try {
            SimulationState state = parseStateJson(jsonString);
            return validateStateWithBounds(state, simSpaceWidth, simSpaceHeight);
        } catch (JsonParseException e) {
            return handleJsonParseException(e);
        } catch (JsonMappingException e) {
            return handleJsonMappingException(e);
        } catch (Exception e) {
            return new ValidationResult(false, "Unable to process JSON: " + e.getMessage());
        }
    }
    
    /**
     * Validates the parsed state with bounds checking if dimensions are provided.
     */
    private ValidationResult validateStateWithBounds(SimulationState state, double simSpaceWidth, double simSpaceHeight) {
        // Perform bounds checking if simulation space dimensions are provided
        if (simSpaceWidth > 0 && simSpaceHeight > 0) {
            ValidationResult boundsResult = validateObjectBounds(state, simSpaceWidth, simSpaceHeight);
            if (!boundsResult.isValid()) {
                return boundsResult;
            }
        }
        return new ValidationResult(true, "JSON is valid and all objects are positioned correctly");
    }
    
    /**
     * Handles JsonParseException and provides user-friendly error messages.
     */
    private ValidationResult handleJsonParseException(JsonParseException e) {
        String locationInfo = "";
        if (e.getLocation() != null) {
            locationInfo = String.format(" (line %d, column %d)", 
                e.getLocation().getLineNr(), e.getLocation().getColumnNr());
        }
        
        String friendlyMessage = createFriendlyParseErrorMessage(e.getOriginalMessage());
        return new ValidationResult(false, friendlyMessage + locationInfo);
    }
    
    /**
     * Creates user-friendly error messages for JSON parse exceptions.
     */
    private String createFriendlyParseErrorMessage(String originalMessage) {
        String lowercaseMessage = originalMessage.toLowerCase();
        
        if (lowercaseMessage.contains("unexpected character") || lowercaseMessage.contains("unexpected token")) {
            return "There's a syntax error in your JSON - check for missing commas, quotes, or brackets";
        } else if (lowercaseMessage.contains("expected") && lowercaseMessage.contains("'}'")) {
            return "Missing closing bracket '}' - make sure all brackets are properly closed";
        } else if (lowercaseMessage.contains("expected") && lowercaseMessage.contains("':'")) {
            return "Missing colon ':' after a property name";
        } else if (lowercaseMessage.contains("unexpected end")) {
            return "JSON is incomplete - it looks like it was cut off";
        } else {
            return "JSON syntax error: " + originalMessage;
        }
    }
    
    /**
     * Handles JsonMappingException and provides user-friendly error messages.
     */
    private ValidationResult handleJsonMappingException(JsonMappingException e) {
        String originalMessage = e.getOriginalMessage();
        String friendlyMessage;
        
        if (originalMessage.contains("Cannot deserialize")) {
            friendlyMessage = "The JSON structure doesn't match the expected format for game objects";
        } else if (originalMessage.contains("missing property") || originalMessage.contains("required property")) {
            friendlyMessage = "Required property is missing from the JSON";
        } else {
            friendlyMessage = "JSON format error: " + originalMessage;
        }
        
        return new ValidationResult(false, friendlyMessage);
    }
    
    /**
     * Validates that all objects in the simulation state are within bounds.
     * @param state The parsed simulation state
     * @param simSpaceWidth The width of the simulation space
     * @param simSpaceHeight The height of the simulation space
     * @return ValidationResult indicating if bounds are valid
     */
    private ValidationResult validateObjectBounds(SimulationState state, double simSpaceWidth, double simSpaceHeight) {
        if (state.getDroppedObjects() != null) {
            for (int i = 0; i < state.getDroppedObjects().size(); i++) {
                GameObject obj = state.getDroppedObjects().get(i);
                ValidationResult result = validateSingleObjectBounds(obj, simSpaceWidth, simSpaceHeight);
                if (!result.isValid()) {
                    return result;
                }
            }
        }
        return new ValidationResult(true, "All objects are positioned correctly");
    }
    
    /**
     * Validates that a single object is within bounds.
     */
    private ValidationResult validateSingleObjectBounds(GameObject obj, double simSpaceWidth, double simSpaceHeight) {
        if (obj.getPosition() == null) {
            return new ValidationResult(true, "Object position is valid");
        }
        
        ObjectDimensions dimensions = calculateObjectDimensions(obj);
        BoundingBoxParams params = new BoundingBoxParams(
            obj.getPosition().getX(), 
            obj.getPosition().getY(), 
            dimensions.width, 
            dimensions.height, 
            obj.getAngle(), 
            dimensions.isCircular
        );
        BoundingBox bounds = calculateRotatedBoundingBox(params);
        
        return checkBoundsViolation(obj, bounds, simSpaceWidth, simSpaceHeight);
    }
    
    /**
     * Calculates the dimensions of a game object.
     */
    private ObjectDimensions calculateObjectDimensions(GameObject obj) {
        float objectWidth = 0;
        float objectHeight = 0;
        boolean isCircular = false;
        
        if (obj.getSize() != null) {
            objectWidth = obj.getSize().getWidth();
            objectHeight = obj.getSize().getHeight();
            
            // For circular objects, use radius
            if (objectWidth == 0 && objectHeight == 0 && obj.getSize().getRadius() > 0) {
                float radius = obj.getSize().getRadius();
                objectWidth = objectHeight = radius * 2;
                isCircular = true;
            }
        }
        
        return new ObjectDimensions(objectWidth, objectHeight, isCircular);
    }
    
    /**
     * Checks if the object bounds violate the simulation space boundaries.
     */
    private ValidationResult checkBoundsViolation(GameObject obj, BoundingBox bounds, double simSpaceWidth, double simSpaceHeight) {
        // Check if any part of the object is outside simulation space bounds
        if (bounds.minX < 0 || bounds.maxX > simSpaceWidth || 
            bounds.minY < 0 || bounds.maxY > simSpaceHeight) {
            
            return createBoundsViolationMessage(obj, bounds, simSpaceWidth, simSpaceHeight);
        }
        
        return new ValidationResult(true, "Object is within bounds");
    }
    
    /**
     * Creates a detailed error message for bounds violations.
     */
    private ValidationResult createBoundsViolationMessage(GameObject obj, BoundingBox bounds, double simSpaceWidth, double simSpaceHeight) {
        // Create human-readable error message
        String objectName = obj.getName() != null ? obj.getName() : "object";
        String positionDesc = String.format("%.0f, %.0f", obj.getPosition().getX(), obj.getPosition().getY());
        
        StringBuilder problem = createBoundaryViolationDescription(bounds, simSpaceWidth, simSpaceHeight);
        String rotationInfo = createRotationInfo(obj.getAngle());
        
        return new ValidationResult(false, 
            String.format("The %s at position (%s)%s is placed %s and would not be visible in the game area. Please move it closer to the center.", 
                objectName, positionDesc, rotationInfo, problem.toString()));
    }
    
    /**
     * Creates a description of which boundaries are violated.
     */
    private StringBuilder createBoundaryViolationDescription(BoundingBox bounds, double simSpaceWidth, double simSpaceHeight) {
        StringBuilder problem = new StringBuilder();
        if (bounds.minX < 0) problem.append("too far left");
        if (bounds.maxX > simSpaceWidth) {
            if (problem.length() > 0) problem.append(" and ");
            problem.append("too far right");
        }
        if (bounds.minY < 0) {
            if (problem.length() > 0) problem.append(" and ");
            problem.append("too far up");
        }
        if (bounds.maxY > simSpaceHeight) {
            if (problem.length() > 0) problem.append(" and ");
            problem.append("too far down");
        }
        return problem;
    }
    
    /**
     * Creates rotation information for error messages.
     */
    private String createRotationInfo(float angle) {
        // Add rotation info if object is rotated
        if (Math.abs(angle) > 0.1) { // Only mention rotation if significant
            return String.format(" (rotated %.0f°)", angle);
        }
        return "";
    }
    
    /**
     * Calculates the axis-aligned bounding box for a rotated object.
     * @param params The parameters for bounding box calculation
     * @return BoundingBox containing the min/max coordinates
     */
    private BoundingBox calculateRotatedBoundingBox(BoundingBoxParams params) {
        // For circular objects, rotation doesn't change the bounding box
        if (params.isCircular) {
            float radius = params.width / 2;
            return new BoundingBox(params.centerX - radius, params.centerY - radius, 
                                 params.centerX + radius, params.centerY + radius);
        }
        
        // For rectangular objects, calculate rotated bounding box
        double angleRad = Math.toRadians(params.angleDegrees);
        double cos = Math.abs(Math.cos(angleRad));
        double sin = Math.abs(Math.sin(angleRad));
        
        // Calculate the new width and height of the bounding box after rotation
        double newWidth = params.width * cos + params.height * sin;
        double newHeight = params.width * sin + params.height * cos;
        
        // Calculate bounding box coordinates
        float minX = (float) (params.centerX - newWidth / 2);
        float maxX = (float) (params.centerX + newWidth / 2);
        float minY = (float) (params.centerY - newHeight / 2);
        float maxY = (float) (params.centerY + newHeight / 2);
        
        return new BoundingBox(minX, minY, maxX, maxY);
    }
    
    /**
     * Simple bounding box container class.
     */
    private static class BoundingBox {
        final float minX, minY, maxX, maxY;
        
        BoundingBox(float minX, float minY, float maxX, float maxY) {
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
        }
    }
    
    /**
     * Helper class to store object dimensions.
     */
    private static class ObjectDimensions {
        final float width, height;
        final boolean isCircular;
        
        ObjectDimensions(float width, float height, boolean isCircular) {
            this.width = width;
            this.height = height;
            this.isCircular = isCircular;
        }
    }
    
    /**
     * Parameter object for bounding box calculation to avoid excessive parameter lists.
     */
    private static class BoundingBoxParams {
        final float centerX, centerY, width, height, angleDegrees;
        final boolean isCircular;
        
        BoundingBoxParams(float centerX, float centerY, float width, float height, float angleDegrees, boolean isCircular) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.width = width;
            this.height = height;
            this.angleDegrees = angleDegrees;
            this.isCircular = isCircular;
        }
    }
    
    /**
     * Result of JSON validation containing status and error details.
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String message;
        
        /**
         * Constructs a new ValidationResult.
         * @param valid Whether the validation passed
         * @param message Descriptive message about the validation result
         */
        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
        
        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
    }
    
    /**
     * Container class for simulation state data.
     */
    public static class SimulationState {
        private List<GameObject> droppedObjects;
        private List<InventoryObject> inventoryObjects;
        
        // Getters and setters
        public List<GameObject> getDroppedObjects() { return droppedObjects; }
        public void setDroppedObjects(List<GameObject> droppedObjects) { this.droppedObjects = droppedObjects; }
        
        public List<InventoryObject> getInventoryObjects() { return inventoryObjects; }
        public void setInventoryObjects(List<InventoryObject> inventoryObjects) { this.inventoryObjects = inventoryObjects; }
    }
}