package com.imagevault.util;

import javafx.scene.control.Label;

public class errorHandler {
    public static void error(Label label, String msg) {
        label.setText(msg);
    }

    public static void clear(Label label) {
        label.setText("");
    }
}