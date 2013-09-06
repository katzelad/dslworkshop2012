package org.tau.workshop2011.parser.AST

/**
 * Created by IntelliJ IDEA.
 * User: Barak
 * Date: 26/10/11
 * Time: 13:02
 * To change this template use File | Settings | File Templates.
 */

case class Program(defs:Iterable[(String, Widget)]) extends ASTNode {
  def accept(visitor: ASTVisitor) = visitor visit this
  
  override def toString = {
    "Program" + Container.listify(defs);
  }
}