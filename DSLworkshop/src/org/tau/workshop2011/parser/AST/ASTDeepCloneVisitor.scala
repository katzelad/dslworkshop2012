package org.tau.workshop2011.parser.AST

trait ASTDeepCloneVisitor extends ASTVisitor {

  protected case class ASTNodeWrapper[T <: ASTNode](node:T) {
    def cloneNode : T = {
      (node accept ASTDeepCloneVisitor.this).asInstanceOf[T]
    }
  }

  protected case class ASTOptionalNodeWrapper[T <: ASTNode](node: Option[T]) {
    def cloneNode : Option[T] = {
      node match {
        case Some(realNode) => Some((realNode accept ASTDeepCloneVisitor.this).asInstanceOf[T])
        case None => None
      }
    }
  }

  protected implicit def wrap1[T <: ASTNode](node:T) : ASTNodeWrapper[T] = {
    new ASTNodeWrapper(node)
  }

  protected implicit def wrap2[T <: ASTNode](node:Option[T]) : ASTOptionalNodeWrapper[T] = {
    new ASTOptionalNodeWrapper(node)
  }

  final def visit (node: Attribute) : Attribute = {
    (node accept this).asInstanceOf[Attribute]
  }
  
  override def visit(node: AtomicWidget) : Widget = {
    new AtomicWidget (node.kind,
      node.attributes map ((attr) => attr.cloneNode),
      node.width.cloneNode,
      node.height.cloneNode
    )
  }

  override def visit(node: Comparision) : DirectExpr = {
    new Comparision (node.left.cloneNode, node.right.cloneNode)
  }

  override def visit(node: Condition) : Expr = {
    new Condition (
      node.conds map ({case (cond, expr) => (cond.cloneNode,expr.cloneNode)}),
      node.otherwise.cloneNode
    )
  }

  override def visit(node: Conjuction) : DirectExpr = {
    new Conjuction (node.elems map ((elem) => elem.cloneNode))
  }

  override def visit(node: Container) : Widget = {
    new Container (node.direction,
      node.children map ((child) => child.cloneNode),
      node.width.cloneNode,
      node.height.cloneNode
    )
  }

  override def visit(node: Disjunction) : DirectExpr = {
    new Disjunction (node.elems map ((elem) => elem.cloneNode))
  }

  final def visit(node: DirectExpr) : DirectExpr = {
    node.accept (this).asInstanceOf[DirectExpr]
  }

  final def visit(node: Expr) : Expr = {
    node.accept (this).asInstanceOf[Expr]
  }

  override def visit(node: ExpressionAttribute) : ExpressionAttribute = {
    new ExpressionAttribute (node.leftSide.cloneNode, node.realValue.cloneNode)
  }

  def visit(node: FunctionCall) : DirectExpr = {
    new FunctionCall(node.function.cloneNode, node.args map {(arg) => arg.cloneNode})
  }

  override def visit (node: InitialAttribute) : InitialAttribute = {
    new InitialAttribute (node.leftSide.cloneNode, node.value.cloneNode)
  }

  override def visit(node: IterationVariable) : DirectExpr = {
    /* Case classes have default shallow copy functions. Since this class has no
     * mutable fields, a shallow copy is enough
     */
    node.copy()
  }

  override def visit[T] (node: Literal[T]) : Literal[T] = {
    /* Case classes have default shallow copy functions. Since this class has no
     * mutable fields, a shallow copy is enough
     */
    node.copy()
  }

  override def visit(node: Negation) : DirectExpr = {
    new Negation (node.expr.cloneNode)
  }

  override def visit(node: Product) : DirectExpr = {
    new Product (
      node.mul map ((expr) => expr.cloneNode),
      node.div map ((expr) => expr.cloneNode)
    )
  }

  override def visit(node: Program) : Program = {
    new Program(node.defs.map {case (name,widget) => (name,widget.cloneNode)})
  }

  override def visit(node: PropertyScope) : Widget = {
    new PropertyScope (
      node.widget.cloneNode,
      node.attributes map ((attr) => attr.cloneNode)
    )
  }

  override def visit(node: Sum) : DirectExpr = {
    new Sum (
      node.add map ((expr) => expr.cloneNode),
      node.sub map ((expr) => expr.cloneNode)
    )
  }

  override def visit(node: Variable) : DirectExpr = {
    /* Case classes have default shallow copy functions. Since this class has no
     * mutable fields, a shallow copy is enough
     */
    node.copy()
  }

  final def visit(node: Widget) : Widget = {
    node.cloneNode
  }
}

object ASTDeepCloneVisitor extends ASTDeepCloneVisitor {
  def visit(size: Option[DirectExpr]): Option[DirectExpr] = size.cloneNode
}