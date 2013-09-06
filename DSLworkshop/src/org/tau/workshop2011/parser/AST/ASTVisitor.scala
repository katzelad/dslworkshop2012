package org.tau.workshop2011.parser.AST

/**
 * Created by IntelliJ IDEA.
 * User: Barak
 * Date: 15/10/11
 * Time: 15:08
 * To change this template use File | Settings | File Templates.
 */

trait ASTVisitor {
  def visit(node: AtomicWidget): Any;

  def visit(node: Comparision): Any;

  def visit(node: Condition): Any;

  def visit(node: Conjuction): Any;

  def visit(node: Container): Any;

  def visit(node: Disjunction): Any;

  def visit(node: ExpressionAttribute): Any;

  def visit(node: FunctionCall): Any;

  def visit(node: InitialAttribute): Any;

  def visit(node: IterationVariable): Any;

  def visit[T] (node: Literal[T]): Any;

  def visit(node: Negation): Any;

  def visit(node: Product): Any;

  def visit(node: Program): Any;

  def visit(node: PropertyScope): Any;

  def visit(node: Sum): Any;

  def visit(node: Variable): Any;
}