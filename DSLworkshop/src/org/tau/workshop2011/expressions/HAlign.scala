package org.tau.workshop2011.expressions

import javax.swing.SwingConstants

sealed case class HAlign(name: String) {
  HAlign.values ::= this
}

object HAlign {
  private var values: List[HAlign] = Nil
  val left = new HAlign("left")
  val center = new HAlign("center")
  val right = new HAlign("right")

  def fromSwing(alignment: Int): HAlign = {
    alignment match {
      case SwingConstants.LEFT => HAlign.left
      case SwingConstants.CENTER => HAlign.center
      case SwingConstants.RIGHT => HAlign.right
    }
  }

  def toSwing(alignment: HAlign): Int = {
    alignment match {
      case HAlign.left => SwingConstants.LEFT
      case HAlign.center => SwingConstants.CENTER
      case HAlign.right => SwingConstants.RIGHT
    }
  }

  val matchingRegex = values map {(value) => value.name} mkString("|")

  def parse(str: String): HAlign = {
    require(str matches matchingRegex)
    values.find(v => v.name == str) match {
      case Some(u) => u
      case None    => throw new AssertionError
    }
  }
}
