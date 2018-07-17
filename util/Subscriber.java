package util;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Simple published-subscriber method. The signals are sent using strings.
 *
 * A subscriber can listen to a signal using the method {@link #subscribe(String)}. It can, on the constrary, stop
 * listening using the method {@link #unsubscribe(String)}.
 *
 * The static method {@link #broadcast(String, Object...)} can then be used by any object to send a message to all
 * the subscribers listening to that signal. The parameters associated to the message can be of any class and there
 * is no restriction is the number of parameters.
 *
 * A subscriber should implement the method {@link #read(String, Object...)} in order to react to the signals it
 * listens to.
 */
public abstract class Subscriber {
    private static Map<String, List<Subscriber>> subscribersLists = new HashMap<>();

    public static void broadcast(String msg, Object... parameters){
        List<Subscriber> subscribers = subscribersLists.get(msg);
        if(subscribers == null)
            return;
        for(Subscriber subscriber : subscribers)
            subscriber.read(msg, parameters);
    }

    public void subscribe(String msg){
        subscribersLists.computeIfAbsent(msg, k -> new LinkedList<>()).add(this);
    }

    public void unsubscribe(String msg){
        List<Subscriber> subscribers = subscribersLists.get(msg);
        if(subscribers == null)
            return;
        subscribers.remove(this);
    }

    public abstract void read(String msg, Object... parameters);


}
