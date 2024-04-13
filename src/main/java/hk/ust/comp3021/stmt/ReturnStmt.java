package hk.ust.comp3021.stmt;

import hk.ust.comp3021.expr.ASTExpr;
import hk.ust.comp3021.misc.ASTElement;
import hk.ust.comp3021.utils.XMLNode;

import java.util.ArrayList;

public class ReturnStmt extends ASTStmt {
    // Return(expr? value)
    private ASTExpr value = null;
    public ReturnStmt(XMLNode node) {
        super(node);
        this.stmtType = StmtType.Return;
        if (!node.hasAttribute("value")) {
            value = ASTExpr.createASTExpr(node.getChildByIdx(0));
        }
    }

    @Override
    public ArrayList<ASTElement> getChildren() {
        ArrayList<ASTElement> children = new ArrayList<>();
        if (value != null) {
            children.add(value);
        }
        return children;
    }
}
