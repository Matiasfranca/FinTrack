package ui.javafx.events;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AppEventBus {

    public enum Type {
        DATA_CHANGED
    }

    private final static AppEventBus INSTANCE = new AppEventBus();

    private final List<Consumer<Type>> listeners = new ArrayList<>();

    public static AppEventBus getInstance() {
        return INSTANCE;
    }

    public void subscribe(Consumer<Type> listener) {
        listeners.add(listener);
    }

    public void publish(Type type) {
        for (Consumer<Type> listener : listeners) {
            listener.accept(type);
        }
    }
}
