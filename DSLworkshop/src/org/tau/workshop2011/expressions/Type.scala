package org.tau.workshop2011.expressions

import java.lang.AssertionError

sealed class Type (val scalaName:String, val metaCode: String) {
  override def toString = scalaName
  def toCode: String = metaCode
}

sealed class ValueType (name:String) extends Type(name, "Type.t"+name)
sealed class FunctionType (name:String) extends Type("(Any *) => " + name, "Type.f"+name)

object Type extends Enumeration {
  val tInt       = new ValueType ("Int")
  val tBoolean   = new ValueType ("Boolean")
  val tString    = new ValueType ("String")
  val tColor     = new ValueType ("Color")
  val tFont      = new ValueType ("Font")
  val tHAlign    = new ValueType ("HAlign")
  val tVAlign    = new ValueType ("VAlign")
  val tTextStyle = new ValueType ("TextStyle")

  val fInt       = new FunctionType ("Int")
  val fBoolean   = new FunctionType ("Boolean")
  val fString    = new FunctionType ("String")
  val fColor     = new FunctionType ("Color")
  val fFont      = new FunctionType ("Font")
  val fHAlign    = new FunctionType ("HAlign")
  val fVAlign    = new FunctionType ("VAlign")
  val fTextStyle = new FunctionType ("TextStyle")

  val tUnknown   = new Type ("_", "Type.tUnknown")

  def fromValue (value:Any) : Type = {
    value match {
      case a:Int                 => tInt
      case a:Boolean             => tBoolean
      case a:String              => tString
      case a:Color               => tColor
      case a:Font                => tFont
      case a:HAlign              => tHAlign
      case a:TextStyle           => tTextStyle
      case a:VAlign              => tVAlign
      /* There should be no function constants */
      case _                     => tUnknown
    }
  }

  def result2function (resultType :Type) : Type = {
    resultType match {
      case `tInt`       => fInt
      case `tBoolean`   => fBoolean
      case `tString`    => fString
      case `tColor`     => fColor
      case `tFont`      => fFont
      case `tHAlign`    => fHAlign
      case `tTextStyle` => fTextStyle
      case `tVAlign`    => fVAlign
      case `tUnknown`   => tUnknown
      case _            => None; throw new AssertionError("No functions returning functions!")
    }
  }

  def function2result (funcType :Type) : Type = {
    funcType match {
      case `fInt`       => tInt
      case `fBoolean`   => tBoolean
      case `fString`    => tString
      case `fColor`     => tColor
      case `fFont`      => tFont
      case `fHAlign`    => tHAlign
      case `fTextStyle` => tTextStyle
      case `fVAlign`    => tVAlign
      case `tUnknown`   => tUnknown
      case _            => throw new AssertionError("Can't get the return type of a non function!")
    }
  }
}