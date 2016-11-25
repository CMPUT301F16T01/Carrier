package comcmput301f16t01.github.carrier;

/**
 * Basic listener interface for observer pattern objects.
 * Observables will implement the ability to use a listener and call .update() when they are changed.
 */
public interface Listener {
    void update();
}
