package org.tau.workshop2011.parser.AST

case class Comparison(left: DirectExpr, right: DirectExpr) extends DirectExpr {

  left.parent = Comparison.this
  right.parent = Comparison.this

  override def accept(visitor: ASTVisitor): Any = visitor.visit(Comparison.this)

  override def toString = {
    left + "==" + right
  }
}
