package org.tau.workshop2011.parser.AST

import org.tau.workshop2011.expressions.Type

abstract class Attribute (val leftSide:Variable, val value:Option[Expr]) extends ASTNode {
  val name = leftSide.id

  def getName : String = name
  def getLeftType : Type = leftSide.resultType
  def getRightType : Type = value match {
      case Some(value) => value.resultType
      case None => Type.tUnknown
    }
  def getValue : Option[Expr] = value
}