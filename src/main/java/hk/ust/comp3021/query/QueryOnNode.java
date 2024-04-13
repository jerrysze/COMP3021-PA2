package hk.ust.comp3021.query;

import hk.ust.comp3021.misc.ASTElement;
import hk.ust.comp3021.misc.ASTEnumOp;
import hk.ust.comp3021.stmt.FunctionDefStmt;
import hk.ust.comp3021.utils.ASTModule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class QueryOnNode {

    private HashMap<String, ASTModule> id2ASTModules;

    public QueryOnNode(HashMap<String, ASTModule> id2ASTModules) {
        this.id2ASTModules = id2ASTModules;
    }

    /**
     * TODO `findFuncWithArgGtN` find all functions whose # arguments > given `paramN` in all modules {@link QueryOnNode#id2ASTModules}
     *
     * @param paramN the number of arguments user expects
     * @return null as PA1, simply print out all functions that satisfy the requirements with format ModuleID_FuncName_LineNo
     * Hints1: use {@link ASTElement#filter(Predicate)} method to implement the function
     */
    public Consumer<Integer> findFuncWithArgGtN = (paramN) -> {
        id2ASTModules.forEach((astID, astModule) -> {
            astModule.filter(x -> x instanceof FunctionDefStmt && ((FunctionDefStmt) x).getParamNum() >= paramN).forEach(x -> {
                System.out.println(astID + "_" + ((FunctionDefStmt) x).getName() + "_" + ((FunctionDefStmt) x).getLineNo());
            });
        });
    };


    /**
     * TODO `calculateOp2Nums` count the frequency of each operator in all modules {@link QueryOnNode#id2ASTModules}
     *
     * @param null
     * @return op2Num as PA1,the key is operator name, and value is the frequency
     * Hints1: use {@link ASTElement#forEach(Consumer)} method to implement the function
     */
    /*
     */
    public Supplier<HashMap<String, Integer>> calculateOp2Nums =
            () -> {
                HashMap<String, Integer> op2Nums = new HashMap<>();
                id2ASTModules.forEach((astID, astModule) -> {
                    astModule.getAllOp().forEach(x -> {
                        String nodeType = ((ASTEnumOp) x).getNodeType().replace("OP_", "");
                        op2Nums.put(nodeType, op2Nums.getOrDefault(nodeType, 0) + 1);

                    });
                });
                return op2Nums;
            };



    /**
     * TODO `calculateNode2Nums` count the frequency of each node in all modules {@link QueryOnNode#id2ASTModules}
     *
     * @param astID, a number to represent a specific AST or -1 for all
     * @return node2Nums as PA1,the key is node type, and value is the frequency
     * Hints1: use {@link ASTElement#groupingBy(Function, Collector)} method to implement the function
     * Hints2: if astID is invalid, return empty map
     */
    public Function<String, Map<String, Long>> calculateNode2Nums =
            astID ->
                    !id2ASTModules.containsKey(astID) ? new HashMap<String, Long>() :
                    id2ASTModules.get(astID).groupingBy(ASTElement::getNodeType, Collectors.counting());


    /**
     * TODO `processNodeFreq` sort all functions in all modules {@link QueryOnNode#id2ASTModules}
     *
     * with format ModuleID_FuncName_LineNo, and value is the # nodes
     * Hints1: use {@link ASTElement#forEach(Consumer)} method to implement the function
     * Hint2: note that `countChildren` method is removed, please do not use this method
     */
    public Supplier<List<Map.Entry<String, Integer>>> processNodeFreq =
            () ->
                    id2ASTModules.entrySet().stream()
                            .flatMap(x -> x.getValue().filter(y -> y instanceof FunctionDefStmt).stream().map(y -> {
                                String functionName = ((FunctionDefStmt) y).getMangle(x.getKey());
                                int numChildren = y.getAll().size();
                                return Map.entry(functionName, numChildren);
                        })).sorted((x, y) ->
                            y.getValue() - x.getValue()
                        ).collect(Collectors.toList());








}
