package util;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
