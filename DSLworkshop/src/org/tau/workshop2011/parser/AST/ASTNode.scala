/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tau.workshop2011.parser.AST


abstract class ASTNode {
  var parent: ASTNode = null

  def accept(visitor: ASTVisitor): Any
}