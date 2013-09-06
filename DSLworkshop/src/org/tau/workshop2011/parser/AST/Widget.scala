package org.tau.workshop2011.parser.AST

import org.tau.workshop2011.expressions.Type

/**
 * None = Automatic size
 * Some(DirectExpr) = Custom size
 */
abstract class Widget(kind: String, width: Option[DirectExpr], height: Option[DirectExpr]) extends ASTNode {
  for (size <- width :: height :: Nil) size match {
    case Some(expr) => expr.parent = this
    case None =>
  }

  def getWidth = width
  def getHeight = height
}
