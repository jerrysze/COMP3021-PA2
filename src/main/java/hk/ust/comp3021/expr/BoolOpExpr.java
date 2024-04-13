package hk.ust.comp3021.expr;

import hk.ust.comp3021.misc.ASTElement;
import hk.ust.comp3021.misc.ASTEnumOp;
import hk.ust.comp3021.utils.XMLNode;

import java.util.ArrayList;
import java.util.Arrays;

public class BoolOpExpr extends ASTExpr {
    // BoolOp(boolop op, expr* values)
    private ASTEnumOp op;
    private ArrayList<ASTExpr> values = new ArrayList<>();

    public BoolOpExpr(XMLNode node) {
        super(node);
        this.exprType = ExprType.BoolOp;
        this.op = new ASTEnumOp(node.getChildByIdx(0));
        for (XMLNode valueNode : node.getChildByIdx(1).getChildren()) {
            this.values.add(ASTExpr.createASTExpr(valueNode));
        }
    }

    public ASTEnumOp getOp() {
        return op;
    }

    @Override
    public ArrayList<ASTEnumOp> getOps() {
        return new ArrayList<>(Arrays.asList(op));
    }

    @Override
    public ArrayList<ASTElement> getChildren() {
        ArrayList<ASTElement> children = new ArrayList<>();
        children.addAll(values);
        return children;
    }
}
