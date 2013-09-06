package org.tau.workshop2011.expressions

import java.awt.{Font => SwingFont}

sealed case class TextStyle(name: String) {
  TextStyle.values ::= this
}

object TextStyle {
  private var values: List[TextStyle] = Nil
  val bold = new TextStyle("bold")
  val italic = new TextStyle("italic")
  val regular = new TextStyle("regular")

  def fromSwing(ts: Int): TextStyle = {
    ts match {
      case SwingFont.BOLD => TextStyle.bold
      case SwingFont.ITALIC => TextStyle.italic
      case SwingFont.PLAIN => TextStyle.regular
    }
  }

  def toSwing(ts: TextStyle): Int = {
    ts match {
      case TextStyle.bold => SwingFont.BOLD
      case TextStyle.italic => SwingFont.ITALIC
      case TextStyle.regular => SwingFont.PLAIN
    }
  }

  val matchingRegex = values map {(value) => value.name} mkString("|")

  def parse(str: String): TextStyle = {
    assume(str matches matchingRegex)
    values.find(v => v.name == str) match {
      case Some(u) => u
      case None    => throw new AssertionError
    }
  }
}
