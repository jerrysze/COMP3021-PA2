package hk.ust.comp3021.query;

import hk.ust.comp3021.expr.CallExpr;
import hk.ust.comp3021.expr.CompareExpr;
import hk.ust.comp3021.expr.NameExpr;
import hk.ust.comp3021.misc.ASTArguments;
import hk.ust.comp3021.misc.ASTEnumOp;
import hk.ust.comp3021.stmt.FunctionDefStmt;
import hk.ust.comp3021.stmt.IfStmt;
import hk.ust.comp3021.utils.ASTModule;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QueryOnMethod {
    /**
     * IMPORTANT: for all test cases for QueryOnMethod, we would not involve class
     */
    ASTModule module = null;

    public QueryOnMethod(ASTModule module) {
        this.module = module;
    }

    /**
     * TODO `findEqualCompareInFunc` find all comparison expression with operator \"==\" in current module {@link QueryOnMethod#module}
     *
     * @param funcName the name of the function to be queried
     * @return results List of strings where each represents a comparison expression, in format, lineNo:colOffset-endLineNo:endColOffset
     * Hints1: if func does not exist in current module, return empty list
     * Hints2: use {@link ASTElement#filter(Predicate)} method to implement the function
     */
    public Function<String, List<String>> findEqualCompareInFunc = funcName -> {
        var funcs = module.filter(x -> x instanceof FunctionDefStmt && ((FunctionDefStmt) x).getName().equals(funcName));
        List<String> ret = new ArrayList<>();
        funcs.stream().forEach(x -> {
                x.filter(t -> t instanceof CompareExpr && ((CompareExpr) t).getOps().size() == 1
                                && ((CompareExpr) t).getOps().get(0).getOp() == ASTEnumOp.ASTOperator.OP_Eq)
                .forEach(t -> ret.add(String.format("%d:%d-%d:%d",
                        t.getLineNo(), t.getColOffset(),
                        t.getEndLineNo(), t.getEndColOffset())));
        });
        return ret;
    };

    /**
     * TODO `findFuncWithBoolParam` find all functions that use boolean parameter as if condition in current module
     *{@link QueryOnMethod#module}
     * @param null
     * @return List of strings where each represents the name of function that satisfy the requirements
     * Hints1: the boolean parameter is annotated with type bool
     * Hints2: as long as the boolean parameter shown in the {@link IfStmt#getTest()} expression, we say it's used
     * Hints3: use {@link ASTElement#filter(Predicate)} method to implement the function
     */
    public Supplier<List<String>> findFuncWithBoolParam =
        () -> module.filter(x -> {
                if (!(x instanceof FunctionDefStmt)) return false;
                Stream<String> args = x.filter(y -> y instanceof ASTArguments.ASTArg &&
                                !((ASTArguments.ASTArg) y).getAnnotation().filter(type -> type instanceof NameExpr &&
                                        ((NameExpr) type).getId().equals("bool")).isEmpty()).
                        stream().map(arg -> ((ASTArguments.ASTArg) arg).getArg());
                return args.anyMatch(arg ->
                        !x.filter(t -> t instanceof IfStmt && !((IfStmt) t).getTest().filter(test -> test instanceof NameExpr &&
                                ((NameExpr) test).getId().equals(arg)).isEmpty()).isEmpty());
            }).stream().map(x -> ((FunctionDefStmt)x).getName()).collect(Collectors.toList());



    /**
     * TODO Given func name `funcName`, `findUnusedParamInFunc` find all unused parameter in current module {@link QueryOnMethod#module}
     *
     * @param funcName to be queried function name
     * @return results List of strings where each represents the name of an unused parameter
     * Hints1: if a variable is read, the ctx is `Load`, otherwise `Store` if written
     * Hints2: for the case where variable is written before read, we use line number and col offset to
     * check if the write operation is conducted before the first place where the parameter is read
     * Hints3: use {@link ASTElement#filter(Predicate)} method to implement the function
     * Hints4: if func does not exist in current module, return empty list
     */
    public Function<String, List<String>> findUnusedParamInFunc =
            (funcname) -> module.filter(x ->
                (x instanceof FunctionDefStmt) && ((FunctionDefStmt) x).getName().equals(funcname)
            ).stream().flatMap(x -> {
                FunctionDefStmt fs = (FunctionDefStmt) x;
                Stream<String> args = fs.filter(y -> y
                        instanceof ASTArguments.ASTArg).stream().map(arg -> ((ASTArguments.ASTArg)arg).getArg());
                return args.filter(y -> {
                    var f = fs.filter(ex -> ex instanceof NameExpr && ((NameExpr) ex).getId().equals(y));
                    return !(!f.isEmpty() && ((NameExpr)f.get(0)).getCtx().getOp() == ASTEnumOp.ASTOperator.Ctx_Load);
                });
            }).collect(Collectors.toList());





    /**
     * TODO Given func name `funcName`, `findDirectCalledOtherB` find all functions being direct called
     *{@link QueryOnMethod#module}
     * @param funcName the name of function B
     * @return results List of strings where each represents the name of a function that satisfy the requirement
     * Hints1: there is no class in the test cases for this code pattern, thus, no function names such as a.b()
     * Hints2: for a call expr foo(), we can directly use the called function name foo to location the implementation
     * Hints3: use {@link ASTElement#filter(Predicate)} method to implement the function
     * Hints4: if func does not exist in current module, return empty list
     */
    public Function<String, List<String>> findDirectCalledOtherB =
            (funcname) ->
                    module.filter(x -> x instanceof FunctionDefStmt && ((FunctionDefStmt)x).getName().equals(funcname))
                                    .stream().flatMap(x -> x.filter(z -> z instanceof CallExpr &&
                                                !module.filter(y -> y instanceof FunctionDefStmt && ((FunctionDefStmt)y)
                                                        .getName().equals(((CallExpr)z).getCalledFuncName())).isEmpty()
                                            )
                                            .stream().map(z -> ((CallExpr)z).getCalledFuncName())).collect(Collectors.toList());



    /**
     * TODO Given func name `funcNameA` and `funcNameB`, `answerIfACalledB` checks if A calls B directly or transitively in current module
     * {@link QueryOnMethod#module}
     * @param funcNameA the name of function A
     * @param funcNameB the name of function B
     * @return a boolean return value to answer yes or no
     * Hints1: there is no class in the test cases for this code pattern, thus, no function names such as a.b()
     * Hints2: for a call expr foo(), we can directly use the called function name foo to location the implementation
     * Hints3: use {@link ASTElement#filter(Predicate)} method to implement the function
     */

    public BiPredicate<String, String> answerIfACalledB =
            (A, B) -> {
                Set<String> visited = new HashSet<>();
                visited.add(A);
                Queue<String> queue = new LinkedList<>();
                queue.add(A);
                while (!queue.isEmpty()) {
                    String cur = queue.poll();
                    if (cur.equals(B)) return true;
                    List<String> next = findDirectCalledOtherB.apply(cur);
                    for (String s : next) {
                        if (!visited.contains(s)) {
                            visited.add(s);
                            queue.add(s);
                        }
                    }
                }
                return false;
            };


}
