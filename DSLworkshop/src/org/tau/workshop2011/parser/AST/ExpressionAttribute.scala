package org.tau.workshop2011.parser.AST

case class ExpressionAttribute(override val leftSide:Variable, realValue: Expr)
   extends Attribute(leftSide, Some(realValue)) {

  override def accept(visitor: ASTVisitor): Any = visitor.visit(this)

  override def toString = {
    leftSide + "=" + "(" + value + ")"
  }

}