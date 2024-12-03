package com.github.ChubarevYuri;

import java.util.HashMap;
import java.util.Map;

/**
 * Event with sync and async raise.
 * @param <T> format.
 */
public class Event<T> {

    /**
     * Executed when the event is called.
     * @param <T> format.
     */
    public interface Listener<T> {
        /**
         * Executed when the event is called.
         * @param e parameter.
         */
        void onRaise(T e);
    }

    private final Map<Integer, Event.Listener<T>> listeners = new HashMap<>();
    private final Map<Integer, Event.Listener<T>> asyncListeners = new HashMap<>();

    /**
     * Add {@code Listener} for raise in called Thread.
     * @param listener method.
     * @return ID.
     */
    public int add(Event.Listener<T> listener) {
        int key = System.identityHashCode(listener);
        synchronized (listeners) {
            if (!listeners.containsKey(key)) {
                listeners.put(key, listener);
            }
            return key;
        }
    }

    /**
     * Add {@code Listener} for async raise.
     * @param listener method.
     * @return ID.
     */
    public int addAsync(Event.Listener<T> listener) {
        int key = System.identityHashCode(listener);
        synchronized (listeners) {
            if (!asyncListeners.containsKey(key)) {
                asyncListeners.put(key, listener);
            }
            return key;
        }
    }

    /**
     * Remove {@code Listener}.
     * @param id ID (received when added method).
     */
    public void remove(int id) {
        synchronized (listeners) {
            listeners.remove(id);
            asyncListeners.remove(id);
        }
    }

    /**
     * Remove {@code Listener}.
     * @param listener {@code Listener}.
     */
    public void remove(Event.Listener<T> listener) {
        remove(System.identityHashCode(listener));
    }

    /**
     * Raise all {@code Listener}.
     * @param e parameter.
     */
    public void raise(T e) {
        synchronized (listeners) {
            for (Event.Listener<T> listener : asyncListeners.values()) {
                Thread th = new Thread(() -> {
                    try {
                        listener.onRaise(e);
                    } catch (Exception ignored) { }
                });
                th.setDaemon(true);
                th.start();
            }
            for (Event.Listener<T> listener : listeners.values()) {
                listener.onRaise(e);
            }
        }
    }
}
