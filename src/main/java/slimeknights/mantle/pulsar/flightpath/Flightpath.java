package slimeknights.mantle.pulsar.flightpath;

import com.google.common.collect.Lists;
import slimeknights.mantle.pulsar.flightpath.lib.BlackholeExceptionHandler;
import slimeknights.mantle.pulsar.pulse.PulseReceiver;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * Flightpath - an ordered event bus implementation.
 *
 * @author Arkan <arkan@drakon.io>
 */
@ParametersAreNonnullByDefault
@SuppressWarnings({"unused", "unchecked"})
public class Flightpath {
    
    // Note we *MUST* use a linked map, otherwise order is lost.
    private final List<PulseReceiver> subscribers = Lists.newArrayList();
    // Anything that manipulates state *MUST* acquire this lock. It prevents awkward issues when iterating.
    private final Object lock = new Object();
    
    private IExceptionHandler exceptionHandler = new BlackholeExceptionHandler();
    
    /**
     * Used to change exception handling behaviour.
     *
     * @param handler The handler to use.
     */
    public void setExceptionHandler(IExceptionHandler handler) {
        synchronized (this.lock) {
            this.exceptionHandler = handler;
        }
    }
    
    /**
     * Registers a given object onto the bus.
     *
     * @param obj Object to attach to the bus.
     */
    public void register(PulseReceiver obj) {
        synchronized (this.lock) {
            if (this.subscribers.contains(obj)) {
                return; // Nothing to do.
            }
            this.subscribers.add(obj);
        }
    }
    
    /**
     * Posts the given event on the bus.
     * <p>
     * By default this blackholes exceptions. If you need different behaviour, see setExceptionHandler.
     */
    public void init() {
        synchronized (this.lock) {
            for (PulseReceiver subscriber : subscribers) {
                try {
                    subscriber.onInitialize();
                } catch (Exception ex) {
                    this.exceptionHandler.handle(ex);
                }
            }
            this.exceptionHandler.flush();
        }
    }
}
