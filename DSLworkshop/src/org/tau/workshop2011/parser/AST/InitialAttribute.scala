package org.tau.workshop2011.parser.AST

import org.tau.workshop2011.expressions.Type

case class InitialAttribute (override val leftSide:Variable, override val value:Option[Expr]) extends Attribute(leftSide, value) {

  override def accept(visitor: ASTVisitor): Any = visitor.visit(this)

  override def toString = {
    leftSide + "=" + "?" +  (value match {
      case Some(v) => "(" + v + ")"
      case None => ""
    })
  }

}