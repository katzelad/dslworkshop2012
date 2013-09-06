package org.tau.workshop2011.parser.AST

import org.tau.workshop2011.expressions.Type


/**
 * Literal - A constant expression that always evaluates to the same value, and
 * depends on no external value
 */
case class Literal[T](value: T) extends DirectExpr {
  resultType = Type fromValue value

  override def accept(visitor: ASTVisitor): Any = visitor.visit(this)

  def eval(): T = value

  override def toString = {
    (value match {
      case v:String => '"' + v + '"'
      case _ => value toString
    }) + ":" + (Type fromValue value)
  }
}
