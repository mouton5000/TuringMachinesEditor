package util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by dimitri.watel on 18/06/18.
 */
public class BidirMap<K, V> {

    Map<K, V> direct;
    Map<V, K> reverse;

    public BidirMap() {
        direct = new HashMap<>();
        reverse = new HashMap<>();
    }

    public void put(K key, V value){

        V pvalue = direct.get(key);
        K pkey = reverse.get(key);

        direct.put(key, value);
        reverse.put(value, key);

        if(pvalue != null)
            reverse.remove(pvalue);
        if(pkey != null)
            direct.remove(pkey);
    }

    public int size(){
        return direct.size();
    }

    public void clear(){
        direct.clear();
        reverse.clear();
    }

    public Set<K> keySet(){
        return direct.keySet();
    }

    public Collection<V> values(){
        return direct.values();
    }

    public Set<Map.Entry<K, V>> entrySet(){
        return direct.entrySet();
    }

    public V getV(K key){
        return direct.get(key);
    }

    public K getK(V value){
        return reverse.get(value);
    }
}