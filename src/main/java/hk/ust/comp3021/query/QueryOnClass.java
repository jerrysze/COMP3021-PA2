package hk.ust.comp3021.query;

import hk.ust.comp3021.expr.NameExpr;
import hk.ust.comp3021.stmt.ClassDefStmt;
import hk.ust.comp3021.stmt.FunctionDefStmt;
import hk.ust.comp3021.utils.ASTModule;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class QueryOnClass {

    ASTModule module = null;

    public QueryOnClass(ASTModule module) {
        this.module = module;
    }
    /**
     * TODO Given class name `className`, `findSuperClasses` finds all the super classes of
     * it in the current module {@link QueryOnClass#module}
     * @return results List of strings where each represents the name of a class that satisfy the requirement
     * returns the ClassDefStmt object.
     * Hint2: You can first find the direct super classes, and then RECURSIVELY finds the
     * super classes of the direct super classes.
     */
    private Function<String, ClassDefStmt> findClass =
            (A) -> {
                return module.filter(x -> x instanceof ClassDefStmt && ((ClassDefStmt)x).getName().equals(A))
                        .stream().map(x -> (ClassDefStmt)x).findFirst().orElse(null);
            };
    public Function<String, List<String>> findSuperClasses =
            (A) -> {
                Set<String> superClasses = new HashSet<>();
                Queue<String> queue = new LinkedList<>();
                queue.add(A);
                while (!queue.isEmpty()) {
                    String cur = queue.poll();
                    ClassDefStmt curClass = findClass.apply(cur);
                    if (curClass == null) {
                        continue;
                    }
                    curClass.getBases().forEach(x -> {
                        x.filter(t -> (t instanceof NameExpr)).stream().forEach(t -> {
                            superClasses.add(((NameExpr)t).getId());
                            queue.add(((NameExpr)t).getId());
                        });
                    });
                }
                return new ArrayList<>(superClasses);
            };

     /**
     * TODO Given class name `classA` and `classB` representing two classes A and B,
     *  `haveSuperClass` checks whether B is a super class of A in the current module.
     *  {@link QueryOnClass#module}
     * @param classA the name of class A.
     * @param classB the name of class B
     * @return returns true if B is A's super class, otherwise false.
     * Hint1: you can just reuse {@link QueryOnClass#findSuperClasses}
     */
    public BiFunction<String, String, Boolean> haveSuperClass =
                (A, B) -> findSuperClasses.apply(A).contains(B);

    private BiFunction<String, String, Boolean> classHaveMethod =
            (A, B) ->
                    !module.filter(x -> x instanceof ClassDefStmt && ((ClassDefStmt)x).getName().equals(A)
                            && !x.filter(z -> z instanceof FunctionDefStmt &&
                            ((FunctionDefStmt)z).getName().equals(B)).isEmpty()).isEmpty();
    private BiFunction<String, String, Boolean> classHaveMethodInSuperClass =
            (A, B) ->
                    findSuperClasses.apply(A).stream().filter(x -> classHaveMethod.apply(x, B)).findAny().isPresent();
    private Function<ClassDefStmt, List<String>> findMethodInClass =
            (A) -> A.filter(z -> z instanceof FunctionDefStmt).stream().map(z ->
                    ((FunctionDefStmt)z).getName()).collect(Collectors.toList());


    /**
     * {@link QueryOnClass#module}
     * Note: If there are multiple overriding functions with the same name, please include name
     * in the result list for MULTIPLE times. You can refer to the test case.
     * Hint1: you can implement a helper function that first finds the methods that a class
     *  directly contains.
     * Hint2: you can reuse the results of {@link QueryOnClass#findSuperClasses}
     */
    public Supplier<List<String>> findOverridingMethods =
            () -> module.filter(x -> x instanceof ClassDefStmt).stream().flatMap(cls ->
                findMethodInClass.apply((ClassDefStmt)cls).stream().filter(y ->
                    classHaveMethodInSuperClass.apply(((ClassDefStmt)cls).getName(), y))).collect(Collectors.toList());



    /**
     * TODO Returns all the methods that a class possesses in the current module
     * {@link QueryOnClass#module}
     * @param className the name of the class
     * @return results List of strings of names of the methods it possesses
     * Note: the same function name should appear in the list only once, due to overriding.
     * Hint1: you can implement a helper function that first finds the methods that a class
     *  directly contains.
     * Hint2: you can reuse the results of {@link QueryOnClass#findSuperClasses}
     */
    public Function<String, List<String>> findAllMethods =
            (classname) -> module.filter(x -> x instanceof ClassDefStmt && ((ClassDefStmt)x).getName().equals(classname))
                    .stream().flatMap(cls -> findMethodInClass.apply((ClassDefStmt)cls).stream()).collect(Collectors.toList());


     /**
     * TODO Returns all the classes that possesses a main function in the current module
     * {@link QueryOnClass#module}
     * @return results List of strings of names of the classes
     * Hint1: You can reuse the results of {@link QueryOnClass#findAllMethods}
     */
    public Supplier<List<String>> findClassesWithMain =
             () -> module.filter(x -> x instanceof ClassDefStmt &&
                     (classHaveMethod.apply(((ClassDefStmt)x).getName(), "main")  ||
                             classHaveMethodInSuperClass.apply(((ClassDefStmt)x).getName(), "main")))
                     .stream().map(x -> ((ClassDefStmt)x).getName()).collect(Collectors.toList());

}

