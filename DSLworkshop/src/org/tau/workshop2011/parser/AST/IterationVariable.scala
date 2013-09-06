package org.tau.workshop2011.parser.AST

import org.tau.workshop2011.expressions.Type


case class IterationVariable(arrayID: String, indexID: String, varType: Type = Type.tUnknown) extends DirectExpr {
  override def accept(visitor: ASTVisitor): Any = visitor.visit (this)

  resultType = varType

  /* The toString method of case classes in Scala creates the string to be
   * returned upon the class creation. This is a source for confusions in cases
   * where the variable type is assigned later. So we shall override that method
   * ourselves in order to make it return the updated result
   */
  override def toString = {
    arrayID + "[" + indexID + "]:" + resultType
  }
}
