package org.tau.workshop2011.parser.AST

import org.tau.workshop2011.expressions.Type


case class Negation(expr: DirectExpr) extends DirectExpr {

  expr.parent = this

  override def accept(visitor: ASTVisitor): Any = visitor.visit(this)

  override def toString = "!" + "(" + expr + ")"
}
