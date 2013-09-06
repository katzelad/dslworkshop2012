package org.tau.workshop2011.parser.AST

import org.tau.workshop2011.expressions.Type

/**
 * A Conditional value block. Composed of a list of pairs of expressions (in
 * the form of if 'exprA' then 'exprB') and another expression ('otherwise')
 * for the case where all conditions are false.
 *
 * WARNING: Objects of this class must be immutable, meaning they must not be
 *          changed! Otherwise we will have a problem with the expressions!
 */
case class Condition (conds: List[(DirectExpr, Expr)], otherwise: Expr) extends Expr {

  for (expr <- otherwise :: (conds map { case (cond, expr) => expr }))
    expr.parent = this

  override def accept(visitor: ASTVisitor): Any = visitor.visit(this)

  override def toString = {
    "{" +
      (conds map {
        case (cond, body) => {
          "(" + cond + ")" + ":" + cond.resultType + " => " + "(" + body + ")" + ":" + body.resultType
        }
      }).mkString(", ") +
      ",  otherwise" + ": " + "(" + otherwise + ")" + ":" + otherwise.resultType +
      "}"
  }
}