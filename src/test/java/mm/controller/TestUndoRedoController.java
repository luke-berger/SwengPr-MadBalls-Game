package mm.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for UndoRedoController.
 * Tests all functionality without JavaFX dependencies.
 */
public class TestUndoRedoController {
    
    private UndoRedoController controller;
    
    @BeforeEach
    void setUp() {
        controller = new UndoRedoController();
    }
    
    /**
     * Mock command implementation for testing purposes.
     * Tracks execution and undo calls.
     */
    private static class MockCommand implements Command {
        private boolean executed = false;
        private boolean undone = false;
        private final String description;
        private int executeCount = 0;
        private int undoCount = 0;
        
        public MockCommand(String description) {
            this.description = description;
        }
        
        @Override
        public void execute() {
            executed = true;
            undone = false;
            executeCount++;
        }
        
        @Override
        public void undo() {
            executed = false;
            undone = true;
            undoCount++;
        }
        
        @Override
        public String getDescription() {
            return description;
        }
        
        public boolean isExecuted() {
            return executed;
        }
        
        public boolean isUndone() {
            return undone;
        }
        
        public int getExecuteCount() {
            return executeCount;
        }
        
        public int getUndoCount() {
            return undoCount;
        }
    }
    
    @Nested
    @DisplayName("Initial State Tests")
    class InitialStateTests {
        
        @Test
        @DisplayName("Controller should start with empty history")
        void testInitialState() {
            assertEquals(0, controller.getCommandCount());
            assertFalse(controller.canUndo());
            assertFalse(controller.canRedo());
        }
        
        @Test
        @DisplayName("Undo should return false when no commands exist")
        void testUndoOnEmptyHistory() {
            assertFalse(controller.undo());
        }
        
        @Test
        @DisplayName("Redo should return false when no commands exist")
        void testRedoOnEmptyHistory() {
            assertFalse(controller.redo());
        }
    }
    
    @Nested
    @DisplayName("Single Command Tests")
    class SingleCommandTests {
        
        @Test
        @DisplayName("Execute command should execute and add to history")
        void testExecuteCommand() {
            MockCommand command = new MockCommand("Test Command");
            
            controller.executeCommand(command);
            
            assertTrue(command.isExecuted());
            assertEquals(1, command.getExecuteCount());
            assertEquals(1, controller.getCommandCount());
            assertTrue(controller.canUndo());
            assertFalse(controller.canRedo());
        }
        
        @Test
        @DisplayName("Undo should work after executing a command")
        void testUndoAfterExecute() {
            MockCommand command = new MockCommand("Test Command");
            controller.executeCommand(command);
            
            boolean result = controller.undo();
            
            assertTrue(result);
            assertTrue(command.isUndone());
            assertEquals(1, command.getUndoCount());
            assertFalse(controller.canUndo());
            assertTrue(controller.canRedo());
        }
        
        @Test
        @DisplayName("Redo should work after undo")
        void testRedoAfterUndo() {
            MockCommand command = new MockCommand("Test Command");
            controller.executeCommand(command);
            controller.undo();
            
            boolean result = controller.redo();
            
            assertTrue(result);
            assertTrue(command.isExecuted());
            assertEquals(2, command.getExecuteCount()); // Once from executeCommand, once from redo
            assertTrue(controller.canUndo());
            assertFalse(controller.canRedo());
        }
    }
    
    @Nested
    @DisplayName("Multiple Commands Tests")
    class MultipleCommandsTests {
        
        @Test
        @DisplayName("Multiple commands should be executed in order")
        void testMultipleCommandExecution() {
            MockCommand command1 = new MockCommand("Command 1");
            MockCommand command2 = new MockCommand("Command 2");
            MockCommand command3 = new MockCommand("Command 3");
            
            controller.executeCommand(command1);
            controller.executeCommand(command2);
            controller.executeCommand(command3);
            
            assertTrue(command1.isExecuted());
            assertTrue(command2.isExecuted());
            assertTrue(command3.isExecuted());
            assertEquals(3, controller.getCommandCount());
            assertTrue(controller.canUndo());
            assertFalse(controller.canRedo());
        }
        
        @Test
        @DisplayName("Undo should work in reverse order")
        void testMultipleUndo() {
            MockCommand command1 = new MockCommand("Command 1");
            MockCommand command2 = new MockCommand("Command 2");
            MockCommand command3 = new MockCommand("Command 3");
            
            controller.executeCommand(command1);
            controller.executeCommand(command2);
            controller.executeCommand(command3);
            
            // Undo in reverse order
            assertTrue(controller.undo()); // Undo command3
            assertTrue(command3.isUndone());
            assertTrue(controller.canUndo());
            assertTrue(controller.canRedo());
            
            assertTrue(controller.undo()); // Undo command2
            assertTrue(command2.isUndone());
            assertTrue(controller.canUndo());
            assertTrue(controller.canRedo());
            
            assertTrue(controller.undo()); // Undo command1
            assertTrue(command1.isUndone());
            assertFalse(controller.canUndo());
            assertTrue(controller.canRedo());
        }
        
        @Test
        @DisplayName("Redo should work in forward order")
        void testMultipleRedo() {
            MockCommand command1 = new MockCommand("Command 1");
            MockCommand command2 = new MockCommand("Command 2");
            MockCommand command3 = new MockCommand("Command 3");
            
            controller.executeCommand(command1);
            controller.executeCommand(command2);
            controller.executeCommand(command3);
            
            // Undo all
            controller.undo();
            controller.undo();
            controller.undo();
            
            // Redo in forward order
            assertTrue(controller.redo()); // Redo command1
            assertTrue(command1.isExecuted());
            assertEquals(2, command1.getExecuteCount()); // Once from execute, once from redo
            assertTrue(controller.canUndo());
            assertTrue(controller.canRedo());
            
            assertTrue(controller.redo()); // Redo command2
            assertTrue(command2.isExecuted());
            assertEquals(2, command2.getExecuteCount());
            assertTrue(controller.canUndo());
            assertTrue(controller.canRedo());
            
            assertTrue(controller.redo()); // Redo command3
            assertTrue(command3.isExecuted());
            assertEquals(2, command3.getExecuteCount());
            assertTrue(controller.canUndo());
            assertFalse(controller.canRedo());
        }
    }
    
    @Nested
    @DisplayName("Redo History Clearing Tests")
    class RedoHistoryClearingTests {
        
        @Test
        @DisplayName("New command should clear redo history")
        void testRedoHistoryClearing() {
            MockCommand command1 = new MockCommand("Command 1");
            MockCommand command2 = new MockCommand("Command 2");
            MockCommand command3 = new MockCommand("Command 3");
            
            controller.executeCommand(command1);
            controller.executeCommand(command2);
            controller.undo(); // Now we can redo command2
            
            assertTrue(controller.canRedo());
            
            // Execute new command should clear redo history
            controller.executeCommand(command3);
            
            assertFalse(controller.canRedo());
            assertEquals(2, controller.getCommandCount()); // Only command1 and command3
            assertTrue(controller.canUndo());
        }
        
        @Test
        @DisplayName("Multiple undos followed by new command should clear all redo history")
        void testMultipleUndosWithNewCommand() {
            MockCommand command1 = new MockCommand("Command 1");
            MockCommand command2 = new MockCommand("Command 2");
            MockCommand command3 = new MockCommand("Command 3");
            MockCommand command4 = new MockCommand("Command 4");
            
            controller.executeCommand(command1);
            controller.executeCommand(command2);
            controller.executeCommand(command3);
            
            // Undo multiple commands
            controller.undo(); // Undo command3
            controller.undo(); // Undo command2
            
            assertTrue(controller.canRedo());
            
            // Execute new command should clear all redo history
            controller.executeCommand(command4);
            
            assertFalse(controller.canRedo());
            assertEquals(2, controller.getCommandCount()); // Only command1 and command4
        }
    }
    
    @Nested
    @DisplayName("History Limit Tests")
    class HistoryLimitTests {
        
        @Test
        @DisplayName("History should be limited to MAX_HISTORY commands")
        void testHistoryLimit() {
            // Execute more than MAX_HISTORY (50) commands
            for (int i = 0; i < 55; i++) {
                MockCommand command = new MockCommand("Command " + i);
                controller.executeCommand(command);
            }
            
            assertEquals(50, controller.getCommandCount()); // Should be limited to 50
            assertTrue(controller.canUndo());
            assertFalse(controller.canRedo());
        }
        
        @Test
        @DisplayName("Oldest commands should be removed when limit is exceeded")
        void testOldestCommandsRemoved() {
            MockCommand[] commands = new MockCommand[55];
            
            // Execute 55 commands
            for (int i = 0; i < 55; i++) {
                commands[i] = new MockCommand("Command " + i);
                controller.executeCommand(commands[i]);
            }
            
            assertEquals(50, controller.getCommandCount());
            
            // Undo all possible commands (should be 50)
            int undoCount = 0;
            while (controller.canUndo()) {
                controller.undo();
                undoCount++;
            }
            
            assertEquals(50, undoCount);
            
            // The first 5 commands should have been removed
            // So we should be able to redo 50 commands, starting from command 5
            int redoCount = 0;
            while (controller.canRedo()) {
                controller.redo();
                redoCount++;
            }
            
            assertEquals(50, redoCount);
        }
    }
    
    @Nested
    @DisplayName("Clear Functionality Tests")
    class ClearFunctionalityTests {
        
        @Test
        @DisplayName("Clear should reset controller to initial state")
        void testClear() {
            MockCommand command1 = new MockCommand("Command 1");
            MockCommand command2 = new MockCommand("Command 2");
            
            controller.executeCommand(command1);
            controller.executeCommand(command2);
            controller.undo();
            
            assertTrue(controller.canUndo());
            assertTrue(controller.canRedo());
            assertEquals(2, controller.getCommandCount());
            
            controller.clear();
            
            assertFalse(controller.canUndo());
            assertFalse(controller.canRedo());
            assertEquals(0, controller.getCommandCount());
        }
        
        @Test
        @DisplayName("Operations should work normally after clear")
        void testOperationsAfterClear() {
            // Setup some initial state
            MockCommand command1 = new MockCommand("Command 1");
            controller.executeCommand(command1);
            controller.clear();
            
            // Test normal operations after clear
            MockCommand command2 = new MockCommand("Command 2");
            controller.executeCommand(command2);
            
            assertTrue(command2.isExecuted());
            assertEquals(1, controller.getCommandCount());
            assertTrue(controller.canUndo());
            assertFalse(controller.canRedo());
            
            assertTrue(controller.undo());
            assertTrue(command2.isUndone());
            assertFalse(controller.canUndo());
            assertTrue(controller.canRedo());
        }
    }
    
    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {
        
        @Test
        @DisplayName("Multiple undos beyond available commands should return false")
        void testMultipleUndosBeyondLimit() {
            MockCommand command = new MockCommand("Command");
            controller.executeCommand(command);
            
            assertTrue(controller.undo()); // Should succeed
            assertFalse(controller.undo()); // Should fail
            assertFalse(controller.undo()); // Should fail
        }
        
        @Test
        @DisplayName("Multiple redos beyond available commands should return false")
        void testMultipleRedosBeyondLimit() {
            MockCommand command = new MockCommand("Command");
            controller.executeCommand(command);
            controller.undo();
            
            assertTrue(controller.redo()); // Should succeed
            assertFalse(controller.redo()); // Should fail
            assertFalse(controller.redo()); // Should fail
        }
        
        @Test
        @DisplayName("Undo and redo alternating should work correctly")
        void testAlternatingUndoRedo() {
            MockCommand command = new MockCommand("Command");
            controller.executeCommand(command);
            
            for (int i = 0; i < 5; i++) {
                assertTrue(controller.undo());
                assertTrue(command.isUndone());
                assertTrue(controller.redo());
                assertTrue(command.isExecuted());
            }
            
            assertEquals(1 + 5, command.getExecuteCount()); // 1 from executeCommand + 5 from redos
            assertEquals(5, command.getUndoCount()); // 5 from undos
        }
        
        @Test
        @DisplayName("Command count should remain consistent through operations")
        void testCommandCountConsistency() {
            MockCommand command1 = new MockCommand("Command 1");
            MockCommand command2 = new MockCommand("Command 2");
            MockCommand command3 = new MockCommand("Command 3");
            
            assertEquals(0, controller.getCommandCount());
            
            controller.executeCommand(command1);
            assertEquals(1, controller.getCommandCount());
            
            controller.executeCommand(command2);
            assertEquals(2, controller.getCommandCount());
            
            controller.undo();
            assertEquals(2, controller.getCommandCount()); // Undo doesn't change count
            
            controller.redo();
            assertEquals(2, controller.getCommandCount()); // Redo doesn't change count
            
            controller.undo();
            controller.executeCommand(command3); // This should clear redo history
            assertEquals(2, controller.getCommandCount()); // command1 and command3
        }
    }
    
    @Nested
    @DisplayName("Command State Verification Tests")
    class CommandStateVerificationTests {
        
        @Test
        @DisplayName("Commands should maintain correct execution state")
        void testCommandExecutionState() {
            MockCommand command1 = new MockCommand("Command 1");
            MockCommand command2 = new MockCommand("Command 2");
            
            // Initial state
            assertFalse(command1.isExecuted());
            assertFalse(command1.isUndone());
            assertFalse(command2.isExecuted());
            assertFalse(command2.isUndone());
            
            // Execute command1
            controller.executeCommand(command1);
            assertTrue(command1.isExecuted());
            assertFalse(command1.isUndone());
            
            // Execute command2
            controller.executeCommand(command2);
            assertTrue(command1.isExecuted());
            assertTrue(command2.isExecuted());
            assertFalse(command2.isUndone());
            
            // Undo command2
            controller.undo();
            assertTrue(command1.isExecuted());
            assertFalse(command2.isExecuted());
            assertTrue(command2.isUndone());
            
            // Undo command1
            controller.undo();
            assertFalse(command1.isExecuted());
            assertTrue(command1.isUndone());
            assertFalse(command2.isExecuted());
            assertTrue(command2.isUndone());
        }
        
        @Test
        @DisplayName("Command call counts should be accurate")
        void testCommandCallCounts() {
            MockCommand command = new MockCommand("Command");
            
            // Execute once
            controller.executeCommand(command);
            assertEquals(1, command.getExecuteCount());
            assertEquals(0, command.getUndoCount());
            
            // Undo and redo multiple times
            controller.undo();
            assertEquals(1, command.getExecuteCount());
            assertEquals(1, command.getUndoCount());
            
            controller.redo();
            assertEquals(2, command.getExecuteCount());
            assertEquals(1, command.getUndoCount());
            
            controller.undo();
            assertEquals(2, command.getExecuteCount());
            assertEquals(2, command.getUndoCount());
            
            controller.redo();
            assertEquals(3, command.getExecuteCount());
            assertEquals(2, command.getUndoCount());
        }
    }
}
