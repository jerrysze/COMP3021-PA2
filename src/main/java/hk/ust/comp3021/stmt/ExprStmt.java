package hk.ust.comp3021.stmt;

import hk.ust.comp3021.expr.ASTExpr;
import hk.ust.comp3021.misc.ASTElement;
import hk.ust.comp3021.utils.XMLNode;

import java.util.ArrayList;

public class ExprStmt extends ASTStmt {
    // Expr(expr value)
    private ASTExpr value;
    public ExprStmt(XMLNode node) {
        super(node);
        this.stmtType = StmtType.Expr;
        value = ASTExpr.createASTExpr(node.getChildByIdx(0));
    }

    @Override
    public ArrayList<ASTElement> getChildren() {
        ArrayList<ASTElement> children = new ArrayList<>();
        children.add(value);
        return children;
    }
}
