package org.tau.workshop2011.parser

import AST.{Literal, IterationMacro, Variable}

/**
 * <p>The WidgetParser object is a class that can convert a textual (String)
 * representation of the DSL program and convert it into an Abstract-Syntax-Tree
 * (AST) after expanding macro iteration widgets.</p>
 *
 * <p>This class inherits from the ExpressionParser class due to the typing
 * rules enforced by the Parser classes (we will have trouble combining parsers
 * of a parsing class A with a parsing class B if A and B are unrelated).</p>
 *
 * <p>The correct way to use this class is demonstrated in the following
 * example:</p>
 *
 * {{{
 * val code:String = ...
 * val program:AST.Program = WidgetParser iParse code
 * }}}
 *
 * <p><strong>This is step 1 of the entire DSL->Scala compilation process.</strong></p>
 *
 * @see ExpressionParser
 */
object LayoutParser extends ExpressionParser {

  import AST.Container.Direction._
  
  // Note that in order to wrap a widget in a "panel" for specifying size limits
  // or properties, the existing widget must again be wrapped in braces!

  // ***************************************************************************
  // Widget Size Expressions
  // ***************************************************************************

  def Dim: Parser[Option[AST.DirectExpr]] = {
    ((NumConst ^^ {
      case literal => Some(literal)
    })
      | (("(" ~> NumExpr <~ ")") ^^ {
      ne => Some(ne)
    })
      | ("?" ^^ {
      s => None
    })
      )
  }

  def Size: Parser[(Option[AST.DirectExpr], Option[AST.DirectExpr])] = {
    (":" ~ Dim ~ "x" ~ Dim) ^^ {
      case ":" ~ w ~ "x" ~ h => (w, h)
    }
  }

  case class OptionalDimension(width: Option[AST.DirectExpr], height: Option[AST.DirectExpr], explicit: Boolean)
  def SizeOpt: Parser[OptionalDimension] = {
    ((Size ?) ^^ {
      case None => OptionalDimension(None, None, false)
      case Some(size) => OptionalDimension(size._1, size._2, true)
    })
  }

  // ***************************************************************************
  // Widget attributes
  // ***************************************************************************
  def Attribute: Parser[AST.Attribute] = {
    ((safeIdent ~ "=" ~ Expr) ^^ {
      case id ~ "=" ~ expr => new AST.ExpressionAttribute(new AST.Variable(id), expr)
    }) |
      ((safeIdent ~ "=" ~ "?" ~ (("(" ~> Literal <~ ")") ?) ^^ {
        /* Although the two cases presented here look like obvious candidates
         * for merging into a single case, the type-checking phase of the
         * compiler will complain if you'll do so
         */
        case id ~ "=" ~ "?" ~ Some(value) => new AST.InitialAttribute(new AST.Variable(id), Some(value))
        case id ~ "=" ~ "?" ~ None => new AST.InitialAttribute(new AST.Variable(id), None)
      }))
  }

  def AttributeList: Parser[List[AST.Attribute]] = {
    ("[" ~> repsep(Attribute, ",") <~ "]")
  }

  def AttributeListOpt: Parser[List[AST.Attribute]] = {
    ((AttributeList ?) ^^ {
      case None => Nil;
      case Some(attrs) => attrs
    })
  }

  // ***************************************************************************
  // Widget types
  // ***************************************************************************
  def AtomicWidget: Parser[AST.AtomicWidget] = {
    "(" ~ (safeIdent | "") ~ SizeOpt ~ ")" ~ AttributeListOpt ^^ {
      case "(" ~ kind ~ size ~ ")" ~ attrs => new AST.AtomicWidget(kind, attrs, size.width, size.height)
    }
  }

  def SimpleVGroup: Parser[AST.Widget] = {
    rep1sep(SimpleHGroup, "---" ~ rep("-")) ^^ {
      case widget :: Nil => widget
      case widgets: List[AST.Widget] => AST.Container.Contain(Vertical, widgets, None, None)
    }
  }

  def SimpleHGroup: Parser[AST.Widget] = {
    rep1sep(ParenthesizedWidget, "|") ^^ {
      case widget :: Nil => widget
      case widgets: List[AST.Widget] => AST.Container.Contain(Horizontal, widgets, None, None)
    }
  }

  // ***************************************************************************
  // Widget Iteration
  // ***************************************************************************

  // safeIdentifier { expr1, expr2, expr3, ... }
  def ArrayOfExpressions: Parser[(String, List[AST.DirectExpr])] = {
    safeIdent ~ "=" ~ "{" ~ repsep(DirectExpr, ",") ~ "}" ^^ {
      case id ~ "=" ~ "{" ~ expressions ~ "}" => (id, expressions)
    }
  }

  def IterationProps: Parser[AST.IterationProperties] = {
    "[" ~ safeIdent ~ "=" ~ NumConst ~ "..." ~ NumConst ~ rep("," ~> ArrayOfExpressions) ~ "]" ^^ {
      case "[" ~ id ~ "=" ~ AST.Literal(s: Int) ~ "..." ~ AST.Literal(e: Int) ~ exprs ~ "]" => new AST.IterationProperties(id, s, e, Map(exprs: _*))
    }
  }

  def WidgetIteration: Parser[AST.Widget] = {
    ParenthesizedWidget ~ ("*|*" | "*---*") ~ IterationProps ^^ {
      case widget ~ sep ~ iterProps => {
        val dir = if (sep == "*---*")  Vertical else Horizontal
        new IterationMacro(widget, dir, iterProps)
      }
    }
  }

  /* A widget enclosed in parenthesis */
  def ParenthesizedWidget = AtomicWidget | WidgetWrap

  def NonParenthesizedWidget = SimpleHGroup ||| SimpleVGroup ||| WidgetIteration

  def WidgetWrap: Parser[AST.Widget] = {
    "(" ~ Widget ~ SizeOpt ~ ")" ~ AttributeListOpt ^^ {
      case "(" ~ widget ~ size ~ ")" ~ attrs => {
        var result: AST.Widget = widget

        result = AST.Container.Contain(Vertical, List(result), size.width, size.height)

        if (attrs != Nil)
          result = AST.PropertyScope.AddAttributes(result, attrs)

        result
      }
    }
  }

  def Widget = ParenthesizedWidget ||| NonParenthesizedWidget

  // ***************************************************************************
  // Layout Definitions - Program Expressions
  // ***************************************************************************
  def Layoutdef: Parser[(String, AST.Widget)] = {
    (safeIdent ~ "<-" ~ Widget) ^^ {
      case id ~ "<-" ~ w => (id, w)
    }
  }

  def Program: Parser[AST.Program] = {
    (Layoutdef *) ^^ {
      case defs => new AST.Program(defs)
    }
  }

  def iParse(input: String): AST.Program = {
    parseAll(Program, input) match {
      case Success(result, nextInput) => result
      case failure:NoSuccess => throw new Exception("Could not parse the input.\n" + failure)
    }
  }

  def main(args: Array[String]) {
    var s = new java.util.Scanner(System.in)
    var buffer = ""
    while (true) { {
        print("Enter Input: ")
        var line = s.nextLine();
        while (! line.equals("@quit") && ! line.equals("@finish") ) {
          buffer += line + " "
          line = s.nextLine();
        }
        if (line.equals("@quit")) return
        LayoutParser.parseAll(LayoutParser.Program, buffer) match {
          case Success(result, nextInput) => println("Parsed sucessfully!"); println(result)
          case NoSuccess(msg, nextInput) => println("Could not parse the input."); println(msg)
        }
      }
    }
  }
}