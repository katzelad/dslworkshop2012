package org.tau.workshop2011.parser.AST


case class AtomicWidget(kind: String, attributes: List[Attribute], width: Option[DirectExpr], height: Option[DirectExpr]) extends Widget(kind, width, height) {

  override def accept(visitor: ASTVisitor): Any = visitor.visit(this)

  override def toString = {
    "(" +
      kind +
      ":" +
      (width match { case None => "?"; case Some(w) => "(" + w + ")" }) +
      "x" +
      (height match { case None => "?"; case Some(w) => "(" + w + ")" }) +
    ")" +
      attributes.mkString("[", ", ", "]")
  }
}