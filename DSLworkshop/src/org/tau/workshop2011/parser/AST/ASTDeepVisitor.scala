package org.tau.workshop2011.parser.AST

/**
 * Created by IntelliJ IDEA.
 * User: Barak
 * Date: 25/10/11
 * Time: 14:04
 * To change this template use File | Settings | File Templates.
 */

trait ASTDeepVisitor extends ASTVisitor {
  def visit(node: AtomicWidget) : Any = {
    node.attributes foreach ((attr) => attr accept this)
    node.width match { case None => None; case Some (w) => w accept this }
    node.height match { case None => None; case Some (w) => w accept this }
    null
  }

  def visit(node: Comparision) : Any = {
    node.left accept this
    node.right accept this
    null
  }

  def visit(node: Condition) : Any = {
      node.conds foreach {case (cond, expr) => cond accept this; expr accept  this}
      node.otherwise accept this
    null
  }

  def visit(node: Conjuction) : Any = {
    node.elems foreach ((elem) => elem accept this)
    null
  }

  def visit(node: Container) : Any = {
    node.children foreach ((child) => child accept this )
    node.width match { case None => None; case Some (w) => w accept this }
    node.height match { case None => None; case Some (w) => w accept this }
    null
  }

  def visit(node: Disjunction) : Any = {
    node.elems foreach ((elem) => elem accept this)
    null
  }

  def visit(node: ExpressionAttribute) : Any = {
    node.leftSide accept  this
    node.realValue accept this
    null
  }

  def visit(node: FunctionCall) : Any = {
    node.function accept  this
    for (arg <- node.args) arg accept this
    null
  }

  def visit (node: InitialAttribute) : Any = {
    node.leftSide accept  this
    node.value match {
      case Some(v) => v accept this
      case _ =>
    }
    null
  }

  def visit(node: IterationVariable) : Any = {
    null
  }

  def visit[T] (node: Literal[T]) : Any = {
    null
  }

  def visit(node: Negation) : Any = {
    node.expr accept this
    null
  }

  def visit(node: Product) : Any = {
    node.mul foreach ((expr) => expr accept this)
    node.div foreach ((expr) => expr accept this)
    null
  }

  def visit(node: Program) : Any = {
    node.defs foreach {case (name, wdgt) => wdgt accept this}
    return null
  }

  def visit(node: PropertyScope) : Any = {
    node.widget accept this
    node.attributes foreach ((attr) => attr accept this)
    null
  }

  def visit(node: Sum) : Any = {
    node.add map ((expr) => expr accept this)
    node.sub map ((expr) => expr accept this)
    null
  }

  def visit(node: Variable) : Any = {
    null
  }
}