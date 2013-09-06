package org.tau.workshop2011.parser.AST

import java.lang.AssertionError

case class Container(direction:Container.Direction.Value, children:List[Widget], width:Option[DirectExpr], height:Option[DirectExpr])
  extends Widget(direction.toString, width, height) {

  for (child <- children)
    child.parent = this

  override def accept(visitor: ASTVisitor): Any = visitor.visit(this)

  override def toString = {
    var op = direction.toString //if (isVertical) " --- " else " | ";
    var in = op + Container.listify(children);
      
    //  children map ((wdgt) => "(" + wdgt + ")") mkString ("(\n", 
    //    if (isVertical) " --- " else " | ", 
    //"\n)")
    (width, height) match {
      case (None, None) => in
      case _ => "(" + in + ":" +
        (width match { case None => "?"; case Some(w) => "(" + w + ")"}) +
        "x" +
        (height match { case None => "?"; case Some(w) => "(" + w + ")" }) + ")"
    }
  }
}

object Container {

  object Direction extends Enumeration
  {
    type Direction = Value
    val Horizontal, Vertical = Value
  }
  
  def Contain(direction:Direction.Value, children:List[Widget], width:Option[DirectExpr], height:Option[DirectExpr]) : Widget = {
    children match {
      case List(c : Container) if children(0).getWidth == width && children(0).getHeight == height => c
      case _ => new Container(direction, children, width, height)
    }
    /*
    if (children.length == 1 && children(0).getWidth == width && children(0).getHeight == height)
      children(0)
    else
      new Container(direction, children, width, height)
     */
  }
  
  def textIndent(text : String, indentPrefix : String = "   ") = {
    text split "\n" map ((line) => indentPrefix + line) mkString "\n";
  }
  
  def listify(elements : Iterable[_]) = {
    "[\n" + Container.textIndent(elements map (" - " + _) mkString "\n") + "\n]";    
  }
}