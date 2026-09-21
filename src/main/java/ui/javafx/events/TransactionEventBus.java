package ui.javafx.events;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import model.Transaction;
import model.TransactionType;

public class TransactionEventBus {

    public enum Type {
        CREATED, UPDATED, DELETED
    }

    public record Event(Type type, Transaction transaction) {
    }

    private static final TransactionEventBus INSTANCE = new TransactionEventBus();

    private final Map<Type, Map<TransactionType, List<Consumer<Event>>>> listeners = new EnumMap<>(Type.class);

    private TransactionEventBus() {
        for (Type type : Type.values()) {
            listeners.put(type, new EnumMap<>(TransactionType.class));
            for (TransactionType transactionType : TransactionType.values()) {
                listeners.get(type).put(transactionType, new ArrayList<>());
            }
        }
    }

    public static TransactionEventBus getInstance() {
        return INSTANCE;
    }

    public void subscribe(Type type, TransactionType transactionType, Consumer<Event> listener) {
        listeners.get(type).get(transactionType).add(listener);
    }

    public void subscribe(Type eventType, Consumer<Event> listener) {
        for (TransactionType transactionType : TransactionType.values()) {
            this.subscribe(eventType, transactionType, listener);
        }
    }

    public void subscribe(Consumer<Event> listener) {
        for (Type type : Type.values()) {
            for (TransactionType transactionType : TransactionType.values()) {
                this.subscribe(type, transactionType, listener);
            }
        }

    }

    public void publish(Type type, Transaction transaction) {
        Event event = new Event(type, transaction);

        for (Consumer<Event> listener : listeners.get(type).get(transaction.getTransactionType())) {
            listener.accept(event);
        }
    }

}