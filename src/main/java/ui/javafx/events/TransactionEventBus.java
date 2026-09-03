package ui.javafx.events;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import model.Transaction;

public class TransactionEventBus {
    
    public enum Type {
        CREATED, UPDATED, DELETED
    }
    
    public record Event(Type type, Transaction transaction) {}

    private static final TransactionEventBus INSTANCE = new TransactionEventBus();
    
    private final Map<Type, List<Consumer<Event>>> listeners = new EnumMap<>(Type.class);

    private TransactionEventBus() {
        for (Type type : Type.values()) {
            listeners.put(type, new ArrayList<>());
        }
    }

    public static TransactionEventBus getInstance() {
        return INSTANCE;
    }

    public void subscribe(Type type, Consumer<Event> listener) {
        listeners.get(type).add(listener);
    }

    public void publish(Type type, Transaction transaction) {
        Event event = new Event(type, transaction);
        
        for (Consumer<Event> listener : listeners.get(type)) {
            listener.accept(event);
        }
    }
}