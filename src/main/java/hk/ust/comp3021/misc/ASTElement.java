package hk.ust.comp3021.misc;

import hk.ust.comp3021.utils.XMLNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public abstract class ASTElement {
    private int lineno;
    private int colOffset;
    private int endLineno;
    private int endColOffset;

    public ASTElement() {
        this.lineno = -1;
        this.colOffset = -1;
        this.endLineno = -1;
        this.endColOffset = -1;
    }

    public ASTElement(int lineno, int colOffset, int endLineno, int endColOffset) {
        this.lineno = lineno;
        this.colOffset = colOffset;
        this.endLineno = endLineno;
        this.endColOffset = endColOffset;
    }

    public ASTElement(XMLNode node) {
        if (node.hasAttribute("lineno")) {
            this.lineno = Integer.parseInt(node.getAttribute("lineno"));
        }
        if (node.hasAttribute("col_offset")) {
            this.colOffset = Integer.parseInt(node.getAttribute("col_offset"));
        }
        if (node.hasAttribute("end_lineno")) {
            this.endLineno = Integer.parseInt(node.getAttribute("end_lineno"));
        }
        if (node.hasAttribute("end_col_offset")) {
            this.endColOffset = Integer.parseInt(node.getAttribute("end_col_offset"));
        }
    }

    public int getLineNo() {
        return this.lineno;
    }

    public int getColOffset() {
        return this.colOffset;
    }

    public int getEndLineNo() {
        return this.endLineno;
    }

    public int getEndColOffset() {
        return this.endColOffset;
    }

    public abstract String getNodeType();
    /*
     * Return direct children of current node, which are fields whose type is `ASTElement`.
     * Noticed that field whose class type is `ASTEnumOp` should not be regarded as children.
     */
    public abstract ArrayList<ASTElement> getChildren();

    public ArrayList<ASTEnumOp> getOps() {
        return new ArrayList<>();
    }

    public List<ASTEnumOp> getAllOp() {
        ArrayList<ASTEnumOp> result = new ArrayList<>();
        result.addAll(this.getOps());
        this.getChildren().forEach(child -> result.addAll(child.getAllOp()));
        return result;
    }
    public List<ASTElement> getAll() {
        ArrayList<ASTElement> result = new ArrayList<>();
        result.add(this);
        this.getChildren().forEach(child -> result.addAll(child.getAll()));
        return result;
    }
    /**
     * TODO please design the function by yourself to pass complication and the provided test cases
     *
     * @param predicate representing a boolean-valued function that takes ASTElement as input parameter and returns a bool result
     * @return an ArrayList of ASTElement where predicate returns true
     * Hints: traverse the tree and put those satisfy predicates into array list
     */

    public List<ASTElement> filter(Predicate<ASTElement> predicate) {
        ArrayList<ASTElement> result = new ArrayList<>();
        if (predicate.test(this)) {
            result.add(this);
        }
        result.addAll(this.getChildren().stream().flatMap(child -> child.filter(predicate).stream()).collect(Collectors.toList()));
        return result;
    }

    /**
     * TODO please design the function by yourself to pass complication and the provided test cases
     *
     * @param action representing an operation that accepts ASTElement as input and performs some action
     * @return null
     * Hints: traverse the tree and perform the action on every node in the tree
     */
    public void forEach(Consumer<ASTElement> action) {
        action.accept(this);
        this.getChildren().forEach(child -> child.forEach(action));
    }

    /**
     * TODO `groupingBy` mimic {@link Collectors#groupingBy(Function, Collector)} )} but operates on AST tree structure instead of List
     * TODO please design the function by yourself to pass complication and the provided test cases
     *
     * @param classifier representing a function that classifies an ASTElement argument and produces
     * @param collector representing a collector used to accumulate the ASTElement object into results
     * @return a map whose key and value are all generic types
     * Hints: traverse the tree and group them if they belong to the same categories
     * Hints: please refer to the usage of {@link Collectors#groupingBy(Function, Collector)}} to learn more about this method
     */
     public <A, R, T> Map<A, R> groupingBy(Function<ASTElement, ? extends A> classifier, Collector<ASTElement, T, R> collector) {
        return getAll().stream().collect(Collectors.groupingBy(classifier, collector));
     }

}
