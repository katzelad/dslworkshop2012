package org.tau.dslworkshop.compiler

import org.tau.workshop2011.parser.AST._
import org.tau.workshop2011.expressions._
import org.tau.dslworkshop.compiler.exceptions.TypeMismatch

/*
 * Represents an environment of a specific scope in the program
 * Contains the mappings of variables to values and observers of the scope
 */
class Environment(var varMap: TVarMap, var flowMap: TFlowMap) {
  
  /*
   * Methods for evaluating expressions.
   * Each receives an expression, performs type-checking and returns its value in the environment
   * or throws a 'TypeMismatch' exception.
   * Also, implicit conversions between integers and boolean-type variables are performed.
   * Note: These methods have to be type-specific due to scala's type erasure.
   */
  
  def evalInt(exp: Expr): Int = eval(exp, Type.tInt) match {

    case typed: Int => typed
    
    case typed: Boolean => if (typed) 1 else 0

    case other => throw new TypeMismatch(Type.tInt, other, exp)

  }

  def evalBoolean(exp: Expr): Boolean = eval(exp, Type.tBoolean) match {

    case typed: Boolean => typed
    
    case typed: Int if typed == 0 || typed == 1 => typed == 1

    case other => throw new TypeMismatch(Type.tBoolean, other, exp)

  }

  def evalString(exp: Expr): String = eval(exp, Type.tString) match {

    case typed: String => typed

    case other => throw new TypeMismatch(Type.tString, other, exp)

  }

  def evalColor(exp: Expr): Color = eval(exp, Type.tColor) match {

    case typed: Color => typed

    case other => throw new TypeMismatch(Type.tColor, other, exp)

  }

  def evalFont(exp: Expr): Font = eval(exp, Type.tFont) match {

    case typed: Font => typed

    case other => throw new TypeMismatch(Type.tFont, other, exp)

  }

  def evalHAlign(exp: Expr): HAlign = eval(exp, Type.tHAlign) match {

    case typed: HAlign => typed

    case other => throw new TypeMismatch(Type.tHAlign, other, exp)

  }

  def evalTextStyle(exp: Expr): TextStyle = eval(exp, Type.tTextStyle) match {

    case typed: TextStyle => typed

    case other => throw new TypeMismatch(Type.tTextStyle, other, exp)

  }

  def eval(exp: Expr): Any = eval(exp, Type.tUnknown)
  
  /*
   * Performs a generic evaluation of expressions.
   */
  def eval(exp: Expr, expType: Type): Any = exp match {

    case Comparison(left, right) => eval(left) == eval(right)

    case Condition(conds, otherwise) =>
      conds
        .find({ case (pred, value) => evalBoolean(pred) })
        .map({ case (pred, value) => eval(value, expType) })
        .getOrElse(eval(otherwise, expType))

    case Conjuction(elems) => elems.map(evalBoolean).reduce(_ && _)

    case Disjunction(elems) => elems.map(evalBoolean).reduce(_ || _)

    case FunctionCall(func, args) =>
      val ret = eval(func, Type.result2function(expType))
      try
        ret.asInstanceOf[(Any*) => Any].apply(args)
      catch { case e: ClassCastException => throw new TypeMismatch(Type.result2function(expType), ret, exp) }

    case IterationVariable(_, _, _) => throw new Exception("Syntax Error")

    case Literal(value) => value

    case Negation(expr) => !evalBoolean(expr)

    case Product(mul, div) => mul.map(evalInt).product / div.map(evalInt).product

    case Sum(add, sub) => add.map(evalInt).sum - sub.map(evalInt).sum

    case Variable(id, _, _) => varMap(id)

  }

  /*
   * Returns a set of all the variables which affect the value of the argument expression.
   */
  def getVariables(exp: Expr): Set[String] = exp match {
    case Comparison(left, right) => getVariables(left) ++ getVariables(right)
    case Conjuction(elems) => elems.toSet.map(getVariables).flatten
    case Condition(conds, otherwise) => conds.map({ case (left, right) => getVariables(left) ++ getVariables(right) })
      .reduce(_ ++ _) ++ getVariables(otherwise)
    case Disjunction(elems) => elems.toSet.map(getVariables).flatten
    case FunctionCall(_, args) => args.toSet.map(getVariables).flatten
    case Literal(_) => Set()
    case Negation(expr) => getVariables(expr)
    case Product(mul, div) => mul.toSet.map(getVariables).flatten ++ div.toSet.map(getVariables).flatten
    case Sum(add, sub) => add.toSet.map(getVariables).flatten ++ sub.toSet.map(getVariables).flatten
    case Variable(name, _, isFunction) => if (isFunction) Set() else Set(name)
  }

  /*
   * Receives an expression and a value.
   * Returns a name and a value of a variable such that the reassignment of the variable to the value
   * would change the value of the expression in the environment to the received value.
   * Returns '(null, null)' if no reassignment should occur, the change cannot occur or it is unsupported.
   */
  def changeVarLTR(exp: Expr, value: Boolean): (String, Any) = exp match {
    case Negation(inside) => changeVarLTR(inside, !value)
    case Comparison(left @ Variable(id, _, false), right) if flowMap(id)(INITIAL_ATT_FLAG) && value  =>
      changeVarLTR(left, eval(right))
    case Comparison(left, right @ Variable(id, _, false)) if flowMap(id)(INITIAL_ATT_FLAG) && value =>
      changeVarLTR(right, eval(left))
    case _ => changeVarLTR(exp, value.asInstanceOf[Any])
  }
  
  def changeVarLTR(exp: Expr, value: Any) = exp match {
    case Variable(name, _, false) =>
      val old = varMap(name)
      varMap(name) = value
      (name, old)
    case _ => (null, null)
  }
  
}
