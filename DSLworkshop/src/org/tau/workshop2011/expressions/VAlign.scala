package org.tau.workshop2011.expressions

import javax.swing.SwingConstants
import java.lang.AssertionError

sealed case class VAlign(name: String) {
  VAlign.values ::= this
}

object VAlign {
  private var values: List[VAlign] = Nil
  val top = new VAlign("top")
  val middle = new VAlign("middle")
  val bottom = new VAlign("bottom")

  def fromSwing(alignment: Int): VAlign = {
    alignment match {
      case SwingConstants.TOP => VAlign.top
      case SwingConstants.CENTER => VAlign.middle
      case SwingConstants.BOTTOM => VAlign.bottom
    }
  }

  def toSwing(alignment: VAlign): Int = {
    alignment match {
      case VAlign.top => SwingConstants.TOP
      case VAlign.middle => SwingConstants.CENTER
      case VAlign.bottom => SwingConstants.BOTTOM
    }
  }

  val matchingRegex = values map {(value) => value.name} mkString("|")

  def parse(str: String): VAlign = {
    require(str matches matchingRegex)
    values.find(v => v.name == str) match {
      case Some(u) => u
      case None    => throw new AssertionError
    }
  }
}
