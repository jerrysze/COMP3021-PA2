package hk.ust.comp3021.stmt;

import hk.ust.comp3021.misc.ASTElement;
import hk.ust.comp3021.utils.XMLNode;

import java.util.ArrayList;

public class BreakStmt extends ASTStmt {
    public BreakStmt(XMLNode node) {
        super(node);
        this.stmtType = StmtType.Break;
    }
    @Override
    public ArrayList<ASTElement> getChildren() {
        ArrayList<ASTElement> children = new ArrayList<>();
        return children;
    }
}
