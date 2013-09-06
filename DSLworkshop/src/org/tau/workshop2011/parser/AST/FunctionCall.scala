package org.tau.workshop2011.parser.AST

import org.tau.workshop2011.expressions.{ValueType, FunctionType, Type}

/**
 * Created by IntelliJ IDEA.
 * User: Barak
 * Date: 02/12/11
 * Time: 19:20
 * To change this template use File | Settings | File Templates.
 */

case class FunctionCall(function: Variable, args: List[DirectExpr]) extends DirectExpr {

  function.parent = this
  function.functionName = true
  for (arg <- args)
    arg.parent = this

  def accept(visitor: ASTVisitor): Any = visitor visit this
}