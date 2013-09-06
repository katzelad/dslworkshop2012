package org.tau.workshop2011.parser

import scala.util.parsing.combinator.JavaTokenParsers;
import org.tau.workshop2011.parser.AST.{DirectExpr, Literal, Expr, FunctionCall}
import org.tau.workshop2011.expressions
import org.tau.workshop2011.expressions.Type

/**
 * The ExpressionParser trait extends the JavaTokenParsers trait by adding
 * the ability to parse basic mathematical expressions, including expressions
 * that include variables. Very few semantic rules are enforced de-facto by the
 * syntax limitations, which means that more decent type checking and symbol
 * resolving should happen later.
 *
 * This trait should NOT be used directly by anyone other than the
 * [[org.tau.workshop2011.parsing.WidgetParser]] class.
 *
 * @see org.tau.workshop2011.parsing.WidgetParser
 */
trait ExpressionParser extends JavaTokenParsers {

  /** A list of reserved words that may not serve as variable names */
  val reservedwords = List(
    "true", "false",
    "otherwise",
    "top", "middle", "bottom",
    "left", "center", "right",
    "bold", "italic", "regular"
  )
  /** A regex to match all the reserved words */
  val reservedwordsregex = reservedwords.mkString("|")

  /**
   * This function is used to filter a list of pairs of following elements in a
   * custom way. Given N pairs Ai~Bi (for i=1,...,N), this function receives an
   * element "pre", and it will return all the Bi's so that Ai equals pre.
   */
  def filterByPrefix[A,B] (prefix:A, elems:List[~[A,B]]) : List[B] = {
    var result:List[B] = Nil

    for (elem <- elems)
      if (elem._1.equals(prefix)) result = elem._2 :: result

    result
  }

  def safeIdent : Parser[String] = {
    // In order to avoid identifying reserved words as variables, use the
    // guard parser which does not consume input
    not(guard(reservedwordsregex.r)) ~> ident
//    ident
  }

  /* Identifier for variable and properties of variables */
  def SimpleVariable : Parser [AST.Variable] = {
    safeIdent ^^ {
      case id => new AST.Variable (id)
    }
  }

  /* Identifier for variable and properties of variables */
  def IterationVariable : Parser [AST.IterationVariable] = {
    safeIdent ~ "[" ~ safeIdent ~ "]" ^^ {
      case ar ~ "[" ~ in ~ "]" => new AST.IterationVariable (ar, in)
    }
  }

  def Variable(varType:Type = Type.tUnknown) = SimpleVariable ||| IterationVariable




  // ***************************************************************************
  // Function Call Expressions
  // ***************************************************************************

  def FunctionArguments: Parser[List[DirectExpr]] = {
    "(" ~> repsep (DirectExpr, ",") <~ ")"
  }

  def FunctionCall(varType:Type = Type.tUnknown): Parser[FunctionCall] = {
    (SimpleVariable ~ FunctionArguments) ^^ {
      case func ~ args => new FunctionCall(func, args)
    }
  }

  def UntypedDirectExpr(varType:Type = Type.tUnknown) = {
    FunctionCall(varType) ||| Variable(varType)
  }

  // ***************************************************************************
  // Numerical Expressions
  // ***************************************************************************
  // The parsing is defined by the inverse order of the operator precedence:
  //
  // * / => Prodcut
  // + - => Sum
  //
  // So we will have a Sum, composed out of products
  def NumExpr: Parser [DirectExpr] = Sum

  def Sum: Parser [DirectExpr] = {
    (Prod ~ rep("+" ~ Prod | "-" ~ Prod)) ^^ {
      case a ~ Nil => a
      case a ~ b => new AST.Sum(a::filterByPrefix("+", b),filterByPrefix("-", b))
    }
  }

  def Prod: Parser[DirectExpr] = {
    (NumElem ~ rep("*" ~ NumElem | "/" ~ NumElem)) ^^ {
      case a ~ Nil => a
      case a ~ b => new AST.Product (a::filterByPrefix("*", b), filterByPrefix("/", b))
    }
  }

  def NumConst: Parser[Literal[Int]] = {
    """(\+|\-)?\d+""".r ^^ { str => new Literal (Integer.parseInt(str)) }
  }

  def NumElem: Parser[DirectExpr] = {
    NumConst | ("("~>NumExpr<~")") | UntypedDirectExpr(Type.tInt)
  }

  // ***************************************************************************
  // Boolean Expressions
  // ***************************************************************************
  // The parsing is defined by the inverse order of the operator precedence:
  //
  // == => Comparision
  // !  => Negation
  // && => Conjuction
  // || => Disjunction
  //
  // So we will have a Disjunction, composed out of Conjuctions, composed out of
  // Negations, composed out of Comparisions (and other atomic Boolean values).
  def NonBoolComparable = NumExpr

  def BoolExpr: Parser[DirectExpr] = Disjunction

  def Disjunction: Parser[DirectExpr] = {
    (Conjuction ~ rep("||" ~ Conjuction)) ^^ {
      case a ~ Nil => a
      case a ~ b => new AST.Disjunction(a::filterByPrefix("||", b))
    }
  }

  def Conjuction: Parser[DirectExpr] = {
    (Negation ~ rep("&&" ~ Negation)) ^^ {
      case a ~ Nil => a
      case a ~ b => new AST.Conjuction(a::filterByPrefix("&&", b))
    }
  }

  def Negation : Parser[DirectExpr] = {
    (("!" ?) ~ BoolElem) ^^ {
      case None ~ elem => elem
      case Some(a) ~ elem => AST.Negation (elem)
    }
  }

  def BoolComparision: Parser[DirectExpr] = {
    (BoolElemNoComp ~ "=" ~ BoolElemNoComp) ^^ {
        case a ~ "=" ~ b => new AST.Comparision (a,b)
      }
  }

  def NonBoolComparision: Parser[DirectExpr] = {
    (NumExpr ~ "=" ~ NumExpr) ^^ {
      case a ~ "=" ~ b => new AST.Comparision (a,b)
    }
  }
  
  def BoolConst: Parser[Literal[Boolean]] = {
    ("true" | "false") ^^ { str => new Literal ("true" == str)}
  }

  def BoolElemNoComp: Parser[DirectExpr] = {
    BoolConst | ("("~>BoolExpr<~")") | UntypedDirectExpr(Type.tBoolean)
  }

  def BoolElem: Parser[DirectExpr] = {
    BoolElemNoComp ||| BoolComparision ||| NonBoolComparision
  }


  // ***************************************************************************
  // String Expressions
  // ***************************************************************************
  def StringConst: Parser[Literal[String]] = {
    /* JavaTokenParsers defined a regex for string literals, but it does not
     * allow for having the double quotes (") inside it. What we have here is a
     * cpy of that regex with the added option of quotes inside a string (using
     * a backslash for escaping).
     * 
     * JavaTokenParser.stringLiteral:
     * 
     * "\""+"""([^"\p{Cntrl}\\]|\\[\\/bfnrt]|\\u[a-fA-F0-9]{4})*"""+"\""
     *                                    |
     * We add a double quotes in here ----+ to get ---+
     *                                   v------------+ */                                  
    ("\""+"""([^"\p{Cntrl}\\]|\\[\\/bfnrt"]|\\u[a-fA-F0-9]{4})*"""+"\"").r ^^ {
      str => new Literal(str.substring(1,str.length()-1))
    }
  }

  def StringElem: Parser[DirectExpr] = {
    StringConst | "("~>StringExpr<~")" | UntypedDirectExpr(Type.tString)
  }

  def StringExpr: Parser[DirectExpr] = {
    StringElem
  }

  // ***************************************************************************
  // Color.scala Expressions
  // ***************************************************************************
  def ColorConst: Parser[Literal[expressions.Color]] = {
    expressions.Color.matchingRegex.r ^^ { str => new Literal(new expressions.Color(str)) }
  }

  def ColorElem: Parser[DirectExpr] = {
    ColorConst | "("~>ColorExpr<~")" | UntypedDirectExpr(Type.tColor)
  }

  def ColorExpr: Parser[DirectExpr] = {
    ColorElem
  }


  // ***************************************************************************
  // Font Expressions
  // ***************************************************************************
  
  def TextStyleConst : Parser [expressions.TextStyle] = {
    expressions.TextStyle.matchingRegex.r ^^ {str => expressions.TextStyle.parse (str) }
  }
  
  def FontConst: Parser[Literal[expressions.Font]] = {
    ("(" ~ StringConst ~ "," ~ NumConst ~ "," ~ TextStyleConst ~ ")") ^^ {
      case "(" ~ Literal(face) ~ "," ~ Literal(num) ~ "," ~ style ~ ")" => new Literal (new expressions.Font (face, num, style))
    }
  }

  def FontElem: Parser[DirectExpr] = {
    FontConst | "("~>FontExpr<~")" | UntypedDirectExpr(Type.tFont)
  }

  def FontExpr: Parser[DirectExpr] = {
    FontElem
  }


  // ***************************************************************************
  // HAlign Expressions
  // ***************************************************************************
  def HAlignConst: Parser[Literal[expressions.HAlign]] = {
    expressions.HAlign.matchingRegex.r ^^ { str => new Literal(expressions.HAlign.parse(str))}
  }

  def HAlignElem: Parser[DirectExpr] = {
    HAlignConst | "("~>HAlignExpr<~")" | UntypedDirectExpr(Type.tHAlign)
  }

  def HAlignExpr: Parser[DirectExpr] = {
    HAlignElem
  }

  // ***************************************************************************
  // VAlign Expressions
  // ***************************************************************************
  def VAlignConst: Parser[Literal[expressions.VAlign]] = {
    expressions.VAlign.matchingRegex.r ^^ { str => new Literal(expressions.VAlign.parse(str))}
  }

  def VAlignElem: Parser[DirectExpr] = {
    VAlignConst | "("~>VAlignExpr<~")" | UntypedDirectExpr(Type.tVAlign)
  }

  def VAlignExpr: Parser[DirectExpr] = {
    VAlignElem
  }

  // ***************************************************************************
  // Conditional Expressions
  // ***************************************************************************

  // TODO: If you want to allow nested conditions (yuck!)
  //       replace DirectExpr by Expr here
  def CondBody = DirectExpr

  def SimpleCond: Parser[(DirectExpr,DirectExpr)] = {
    (BoolExpr~"=>"~CondBody) ^^ { case bool~"=>"~expr => (bool,expr)}
  }

  def OtherwiseCond: Parser[DirectExpr] = {
    "otherwise"~>CondBody
  }

  def CondExpr: Parser[Expr] = {
    ("{"~>rep1(SimpleCond<~",")~OtherwiseCond<~"}") ^^ { 
      case conds~o => new AST.Condition(conds,o) 
    }
  }

  def UntypedDirectExpr: Parser[DirectExpr] = {
    Variable (Type.tUnknown)
  }

  def DirectExpr: Parser[DirectExpr] = {
    NumExpr ||| BoolExpr ||| ColorExpr ||| StringExpr ||| FontExpr ||| HAlignExpr ||| VAlignExpr
  }

  def Literal : Parser[Literal[_ <: Any]] = {
    /* Since ||| combines a Parser[T] with a Parser[U >: T] to create a
     * Parser[U], we will get into trouble when combining parsers like this:
     *
     * Parser[Literal[Int]] ||| Parser[Literal[Boolean]] ||| Parser[Literal[String]]
     *
     * The Boolean would be cast to AnyVal (since Int <: AnyVal) and so from the
     * first two we get a Parser[Literal[_ <: AnyVal]].
     *
     * Combining this with a Parser[Literal[String]] wouldn't work, since there
     * is no order at all between String <: AnyRef and AnyVal. So, we must help
     * the Scala compiler to cast the first parser into a
     * Parser[Literal[_ <:Any]] (since Any is the common base class of all)
     *
     * In order to be able to use the ||| function, we must make T so low that we would be able to
     * combine anything with it. That's why we are casting the first parser to
     * a Parser[Literal[_ <: Any]] - this will always force the second parser to
     * be "less accurate" (and less accurate than Any is Any)
     */
    NumConst.asInstanceOf[Parser[Literal[_ <: Any]]] ||| BoolConst ||| StringConst ||| ColorConst ||| FontConst ||| HAlignConst ||| VAlignConst
  }

  // ***************************************************************************
  // All available Expressions
  // ***************************************************************************

  def Expr : Parser [Expr] = DirectExpr | CondExpr

}
