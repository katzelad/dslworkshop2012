/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tau.workshop2011.expressions

import java.awt.{ Font => SwingFont }

case class Font (face:String, size:Int, style:TextStyle) {
  def toSwing () : SwingFont = {
    Font toSwing this
  }
}

object Font {
  def fromSwing (font:SwingFont) : Font = {
    new Font (font.getName, font.getSize, TextStyle fromSwing font.getStyle)
  }
  
  def toSwing (font:Font) : SwingFont = {
    new SwingFont (font.face, font.size, TextStyle toSwing font.style)
  }
}