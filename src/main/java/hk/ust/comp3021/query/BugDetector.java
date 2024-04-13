package hk.ust.comp3021.query;

import hk.ust.comp3021.utils.ASTModule;

import java.util.List;
import java.util.function.Supplier;

public class BugDetector {
    ASTModule module = null;

    public BugDetector(ASTModule module) {
        this.module = module;
    }

    /**
     * TODO Returns all the functions that contains a bug of unclosed files in the current module
     * {@link QueryOnClass#module}
     * @return results List of strings of names of the functions
     */
    public Supplier<List<String>> detect;
}
