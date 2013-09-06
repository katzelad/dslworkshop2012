package org.tau.workshop2011.parser.AST

;

import org.tau.workshop2011.parser.AST._

/**
 * An object for doing formatter printing. This class has nothing to do with the
 * parsing, and it's here only to make the printing easier
 */
class Indenter {
  var space = ""
  val result = new StringBuffer()

  def doIndent() = {
    space = space + "  "
  }

  def unIndent() = {
    space = space.substring(2)
  }

  def iprintln(a: Any) = {
    result append (space + a + "\n")
  }

  def println(a: Any) = {
    result append (a + "\n")
  }

  override def toString = result.toString
}


////////////////////////////////////////////////////////////////////////////////
/// Heirachy of expressions by their type                                    ///
////////////////////////////////////////////////////////////////////////////////

/* Expr
 * |- Var
 * |- PropVar
 * |- Sum
 * |-
 *
 *
 *
 *
 *
 *
 *
 */
