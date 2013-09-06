package org.tau.workshop2011.parser.AST


import org.tau.workshop2011.expressions.Type

/**
 * An "Extended Product" of several mathematical expressions. For the expression
 *
 * " exprA1 * exprA2 * ... * exprAn / exprB1 / exprB2 / ... / exprBn "
 *
 * The object that will be created will contain two lists - "mul" and "div":
 *
 * mul = [exprA1, exprA2, ..., exprAn]
 * div = [exprB1, exprB2, ..., exprBn]
 *
 * Note that We only allow summing direct expressions (DirectExpr)
 */
case class Product(mul: List[DirectExpr], div: List[DirectExpr]) extends DirectExpr {

  for (expr <- mul union div)
    expr.parent = this


  override def accept(visitor: ASTVisitor): Any = visitor.visit(this)

  override def toString = {
    (mul map ((expr) => "(" + expr + ")") mkString ("(", "*", "")) +
    (div map ((expr) => "(" + expr + ")") mkString ("/", "/", ")"))
  }
}
