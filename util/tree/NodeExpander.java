package util.tree;

import java.util.List;

public interface NodeExpander<T extends NodeExpander<T, U>, U> {
    List<T> explore();
    boolean isLeaf();
    boolean endExploration();
}
