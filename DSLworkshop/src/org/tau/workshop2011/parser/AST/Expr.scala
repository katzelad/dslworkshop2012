package org.tau.workshop2011.parser.AST

import org.tau.workshop2011.expressions._

/**
 * A general expression - something that can be computed to produce a value
 */
trait Expr extends ASTNode {
  var resultType = Type.tUnknown

  /**
   * Try to automatically infer the type of this expression and/or it's
   * sub-expressions.
   */
  def autoResolveType { }


}
