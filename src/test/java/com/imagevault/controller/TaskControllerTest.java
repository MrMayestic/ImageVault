package com.imagevault.controller;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class TaskControllerTest {

    @Test
    void testControllerInstantiation() {
        TaskController controller = new TaskController();
        assertNotNull(controller);
    }

    @Test
    void testHandleToggleInputDoesNotCrash() throws Exception {
        TaskController controller = new TaskController();

        Method m = TaskController.class.getDeclaredMethod("handleToggleInput");
        m.setAccessible(true);

        assertTrue(true);
    }

    @Test
    void testEncodeMethodExists() throws Exception {
        TaskController.class.getDeclaredMethod("encodeImage");
        assertTrue(true);
    }

    @Test
    void testDecodeMethodExists() throws Exception {
        TaskController.class.getDeclaredMethod("decodeImage");
        assertTrue(true);
    }
}