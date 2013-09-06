package org.tau.workshop2011.parser.AST

case class Comparision(left: DirectExpr, right: DirectExpr) extends DirectExpr {

  left.parent = this
  right.parent = this

  override def accept(visitor: ASTVisitor): Any = visitor.visit(this)

  override def toString = {
    left + "==" + right
  }
}
