package org.tau.workshop2011.parser.AST

import java.lang.AssertionError

case class PropertyScope(widget:Widget, attributes: List[Attribute]) extends Widget("@", ASTDeepCloneVisitor visit widget.getWidth, ASTDeepCloneVisitor visit widget.getHeight) {

  widget.parent = this
  for (attr <- attributes)
    attr.parent = this

  override def accept(visitor: ASTVisitor): Any = visitor.visit(this)

  override def toString = {
    widget + attributes.mkString("[", ", ", "]")
  }
}

object PropertyScope {
  def AddAttributes(widget: Widget, attributes: List[Attribute]): Widget = {
    widget match {
      /* Important! Properties from the outter scope must be defined first! */
      case ps:PropertyScope => new PropertyScope(ps.widget, attributes ::: ps.attributes)
      case aw:AtomicWidget => new AtomicWidget(aw.kind, attributes ::: aw.attributes, aw.width, aw.height)
      case cn:Container => new PropertyScope(cn, attributes)
      case _ => throw new AssertionError("Bad widget types")
    }
  }
}