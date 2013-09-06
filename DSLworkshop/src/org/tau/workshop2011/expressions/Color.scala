package org.tau.workshop2011.expressions

import java.awt.{ Color => SwingColor }

/**
 * An RGB color attribute. It will be specified in Hexa-Decimal format using
 * the string format "0xBBGGRR" (unlike the reversed order of the html format
 * which is "#RRGGBB")
 */
case class Color (hexa:String) {
  assume (hexa.matches (Color.matchingRegex), "Invalid Color Format " + hexa)
  val blue  = Integer.parseInt(hexa.substring(2,4), 16)
  val green = Integer.parseInt(hexa.substring(4,6), 16)
  val red   = Integer.parseInt(hexa.substring(6,8), 16)
  override def toString = "rgb("+red+", "+green+", "+blue+")"
  
  def toJava () = new java.awt.Color (red,green,blue)
}

object Color {
  val matchingRegex = """0x[0-9a-fA-F]{6}"""

  def toSwing (color:Color) : SwingColor = {
    new SwingColor (color.red, color.green, color.blue)
  }
  
  def dec2hex (value:Int, targetLength:Int) : String = {
    val result = Integer.toString (value, 16)
    return "0" * (targetLength - result.length) + result
  }
  
  def fromSwing (color:SwingColor) : Color = {
    new Color ("0x" + dec2hex (color.getRed, 2) + dec2hex (color.getGreen, 2)
               + dec2hex (color.getBlue, 2))
  }
}