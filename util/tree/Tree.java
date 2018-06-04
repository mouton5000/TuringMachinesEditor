package util.tree;

import java.util.*;

public class Tree<T extends NodeExpander<T, U>, U> {

    private T root;

    public Tree(T root) {
        this.root = root;
    }

    public Iterator<T> bfs(){
        LinkedList<T> toExplore = new LinkedList<>();
        Map<T, T> fathers = new HashMap<>();

        toExplore.add(root);
        T u = null;
        while(!toExplore.isEmpty()){
            u = toExplore.pollFirst();

            if(u.endExploration())
                break;
            if(u.isLeaf())
                continue;


            List<T> children = u.explore();
            for(T child: children)
                fathers.put(child, u);

            toExplore.addAll(children);
        }

        LinkedList<T> toReturn = new LinkedList<>();
        while(u != null){
            toReturn.add(u);
            u = fathers.get(u);
        }
        return toReturn.iterator();

    }
}
