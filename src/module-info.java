/**
 * Declares the Java module for the Tank Battle Arena JavaFX application.
 */
module tank.battle.arena {
    requires javafx.controls;
    requires javafx.media;

    exports app;
    exports entities;
    exports game;
    exports maps;
    exports obstacles;
    exports systems;
    exports ui;
}
