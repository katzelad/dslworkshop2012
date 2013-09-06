package org.tau.workshop2011.parser.AST

import org.tau.workshop2011.expressions.Type

/**
 * A Conjuction ('AND') of several boolean expressions
 */
case class Conjuction(elems: List[DirectExpr]) extends DirectExpr {

  for (elem <- elems)
    elem.parent = this

  override def accept(visitor: ASTVisitor): Any = visitor.visit(this)

  override def toString = {
    elems map ((expr) => "(" + expr + ")" + ":" + expr.resultType) mkString " && "
  }
}