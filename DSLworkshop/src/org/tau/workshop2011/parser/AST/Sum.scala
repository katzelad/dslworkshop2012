package org.tau.workshop2011.parser.AST

import org.tau.workshop2011.expressions.Type

/**
 * An "Extended Sum" of several mathematical expressions. For the expression
 *
 * " exprA1 + exprA2 + ... + exprAn - exprB1 - exprB2 - ... - exprBn "
 *
 * The object that will be created will contain two lists - "add" and "sub":
 *
 * add = [exprA1, exprA2, ..., exprAn]
 * sub = [exprB1, exprB2, ..., exprBn]
 *
 * Note that We only allow summing direct expressions (DirectExpr)
 */
case class Sum(add: List[DirectExpr], sub: List[DirectExpr]) extends DirectExpr {

  for (expr <- add union sub)
    expr.parent = this
  
  override def accept(visitor: ASTVisitor): Any = visitor.visit(this)

  override def toString = {
    (add map ((expr) => "(" + expr + ")") mkString ("(", "+", "")) +
    (sub map ((expr) => "(" + expr + ")") mkString ("-", "-", ")"))
  }
}
