package hk.ust.comp3021.stmt;

import hk.ust.comp3021.expr.ASTExpr;
import hk.ust.comp3021.misc.ASTElement;
import hk.ust.comp3021.utils.XMLNode;

import java.util.ArrayList;


public class IfStmt extends ASTStmt {
    // If(expr test, stmt* body, stmt* orelse)
    private ASTExpr test;
    private ArrayList<ASTStmt> body = new ArrayList<ASTStmt>();
    private ArrayList<ASTStmt> orelse = new ArrayList<ASTStmt>();

    public IfStmt(XMLNode node) {
        super(node);
        this.stmtType = StmtType.If;
        this.test = ASTExpr.createASTExpr(node.getChildByIdx(0));

        for (XMLNode bodyNode: node.getChildByIdx(1).getChildren()) {
            body.add(ASTStmt.createASTStmt(bodyNode));
        }
        for (XMLNode orelseNode: node.getChildByIdx(2).getChildren()) {
            orelse.add(ASTStmt.createASTStmt(orelseNode));
        }
    }

    public ASTExpr getTest() {
        return test;
    }

    @Override
    public ArrayList<ASTElement> getChildren() {
        ArrayList<ASTElement> children = new ArrayList<>();
        children.add(test);
        children.addAll(body);
        children.addAll(orelse);
        return children;
    }
}
