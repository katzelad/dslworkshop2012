package org.tau.dslworkshop.main

import org.tau.workshop2011.parser.AST._
import org.tau.workshop2011.expressions._

class Environment(var evaluatedVarMap: TEvaluatedVarMap, var unevaluatedVarMap: TUnevaluatedVarMap) {

  // def expect[T: ClassTag](value: Any) = value match { case typed: T => typed case _ => throw new Error("Syntax Error") }

  //def evalExpr[T](exp: Option[Expr], expType: Type) = exp match {

  def evalInt(exp: Expr): Int = eval(exp, Type.tInt) match {

    case typed: Int => typed

    case other => throw new TypeMismatch(Type.tInt, other, exp)

  }

  def evalBoolean(exp: Expr): Boolean = eval(exp, Type.tBoolean) match {

    case typed: Boolean => typed

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

  def eval(exp: Expr, expType: Type): Any = exp match {

    case Comparison(left, right) => eval(left) == eval(right)

    case Condition(conds, otherwise) =>
      conds
        .find({ case (pred, value) => !evalBoolean(pred) })
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

    case Variable(id, _, _) => evaluatedVarMap(id)

  }

  // TODO Delete
  /*
  //Var (adding to Varmap) //TODO - anything else here?
  def returnStringVal(att: Attribute, unevaluatedVarMap: scala.collection.mutable.Map[String, Any], evaluatedVarMap: Map[String, Any]) = {
    att.getValue
  }
  def addAttributeToVarMap(att: Attribute, unevaluatedVarMap: scala.collection.mutable.Map[String, Any], evaluatedVarMap: Map[String, Any]) = {
    unevaluatedVarMap(att.getName) = returnStringVal(att, unevaluatedVarMap, evaluatedVarMap)
    //TODO - anything else here?
  }
  def evalExprVar(att: Attribute, unevaluatedVarMap: scala.collection.mutable.Map[String, Any], evaluatedVarMap: Map[String, Any]) = {
    addAttributeToVarMap(att, unevaluatedVarMap, evaluatedVarMap)
    //TODO - anything else here?
  }
  //Int
  def evalExprInt(exp: Option[Expr], unevaluatedVarMap: scala.collection.mutable.Map[String, Any], evaluatedVarMap: Map[String, Any]): Option[Int] = exp match { //the "any" is useful because it allows to use one map for all types, and later i can extract the correct type from the exp using type.fromValue (value:Any) 
    case Some(value) => value match {
      //TODO test - basic scenario working
      case Product(mul: List[DirectExpr], div: List[DirectExpr]) => {
        var prodmul = 1;
        var proddiv = 1;
        mul foreach (expr => prodmul *= (evalExprInt(Some(expr), unevaluatedVarMap, evaluatedVarMap)).get)
        div foreach (expr => proddiv *= (evalExprInt(Some(expr), unevaluatedVarMap, evaluatedVarMap)).get)
        var prod = prodmul / proddiv
        Some(prod)
      }
      //TODO test - basic scenario working
      case Sum(add: List[DirectExpr], sub: List[DirectExpr]) => {
        var sumadd = 0;
        var sumsub = 0;
        add foreach (expr => sumadd += (evalExprInt(Some(expr), unevaluatedVarMap, evaluatedVarMap)).get)
        sub foreach (expr => sumsub += (evalExprInt(Some(expr), unevaluatedVarMap, evaluatedVarMap)).get)
        var sum = sumadd - sumsub
        Some(sum)
      }
      //TODO test -  basic scenario working, needs to test elaborate condition
      case Condition(conds: List[(DirectExpr, Expr)], otherwise: Expr) => {
        var returnval = 0;
        var conditionVal = false;
        var i = 0;
        //iterate over all the "condition=>val"
        while (!conditionVal && i < conds.length) {
          returnval = evalExprInt(Some(conds(i)._2), unevaluatedVarMap, evaluatedVarMap).get
          conditionVal = evalExprBoolean(Some(conds(i)._1), unevaluatedVarMap, evaluatedVarMap).get
          i += 1;
        }
        //otherwise
        if (!conditionVal) (returnval = evalExprInt(Some(otherwise), unevaluatedVarMap, evaluatedVarMap).get)
        Some(returnval)
      }
      //TODO test
      case Variable(id, varType, functionName) => {
        Type.fromValue(unevaluatedVarMap(id)) match {
          case tInt => Some(unevaluatedVarMap.get(id).asInstanceOf[Int])
          //else error
        }
      }
      //TODO test - working
      case Literal(value: Int) => Some(value) //TODO is this ok?
    }
    case None => None
  }
  //bool
  def evalExprBoolean(exp: Option[Expr], varmap: scala.collection.mutable.Map[String, Any], constmap: Map[String, Any]): Option[Boolean] = exp match {
    case Some(value) => value match {
      //TODO test -  basic scenario working
      //conjunction = "and"
      case Conjuction(elems: List[DirectExpr]) => {
        var conjval = true;
        elems foreach (expr => conjval = conjval & (evalExprBoolean(Some(expr), varmap, constmap)).get)
        Some(conjval)
      }
      //TODO test -  basic scenario working
      //conjunction = "or"
      case Disjunction(elems: List[DirectExpr]) => {
        var disjuncval = false;
        elems foreach (expr => disjuncval = disjuncval | (evalExprBoolean(Some(expr), varmap, constmap)).get)
        Some(disjuncval)
      }
      //TODO test - basic scenario ((true=true for example)) works with evalExprBool, but still needs to extend it
      case Comparison(left: DirectExpr, right: DirectExpr) => {
        //TODO FIX! change from evalExprBoolean to something general :(
        Some((evalExprBoolean(Some(left), varmap, constmap).get == evalExprBoolean(Some(right), varmap, constmap).get))
      }
      //TODO test - basic scenario works
      case Negation(expr: DirectExpr) => Some(!(evalExprBoolean(Some(expr), varmap, constmap).get))
      //TODO FIX! not working!!! :( fix and test again...
      case Variable(id, varType, functionName) => {
        Type.fromValue(varmap(id)) match {
          case tBoolean => Some(varmap(id).asInstanceOf[Some[Boolean]].get)
          //else error
        }
      }
      //TODO test - basic scenario works
      case Condition(conds: List[(DirectExpr, Expr)], otherwise: Expr) => {
        var returnval = false;
        var conditionVal = false;
        var i = 0;
        //iterate over all the conditions "condition=>val"
        while (!conditionVal && i < conds.length) {
          returnval = evalExprBoolean(Some(conds(i)._2), varmap, constmap).get
          conditionVal = evalExprBoolean(Some(conds(i)._1), varmap, constmap).get
          i += 1;
        }
        //otherwise
        if (!conditionVal) (returnval = evalExprBoolean(Some(otherwise), varmap, constmap).get)
        Some(returnval)
      }
      //TODO test - working
      case Literal(value: Boolean) => Some(value) //TODO is this ok?
    }
    case None => None
  }
*/

  def getVariables(exp: Expr): Set[String] = exp match {
    case Comparison(left, right) => getVariables(left) ++ getVariables(right)
    case Conjuction(elems) => elems.toSet.map(getVariables).flatten
    case Condition(conds, otherwise) => conds.map({ case (left, right) => getVariables(left) ++ getVariables(right) })
      .reduce(_ ++ _) ++ getVariables(otherwise)
    case Disjunction(elems) => elems.toSet.map(getVariables).flatten
    case FunctionCall(_, args) => args.toSet.map(getVariables).flatten
    // case IterationVariable(_, index, _) => Set(index)
    case Literal(_) => Set()
    case Negation(expr) => getVariables(expr)
    case Product(mul, div) => mul.toSet.map(getVariables).flatten ++ div.toSet.map(getVariables).flatten
    case Sum(add, sub) => add.toSet.map(getVariables).flatten ++ sub.toSet.map(getVariables).flatten
    case Variable(name, _, isFunction) => if (isFunction) Set() else Set(name)
  }

  // Returns the name of the changed variable or null if nothing was changed
  // Changes the variable, does not recurse
  def changeVarLTR(exp: Expr, value: Boolean): String = exp match {
    case Negation(inside) => changeVarLTR(inside, !value)
    case Comparison(left @ Variable(id, _, false), right) if unevaluatedVarMap(id)(INITIAL_ATT_FLAG) && value  =>
      changeVarLTR(left, evalBoolean(right))
    case Comparison(left, right @ Variable(id, _, false)) if unevaluatedVarMap(id)(INITIAL_ATT_FLAG) && value =>
      changeVarLTR(right, eval(left))
    case _ => changeVarLTR(exp, value.asInstanceOf[Any])
  }
  
  def changeVarLTR(exp: Expr, value: Any) = exp match {
    case Variable(name, _, false) =>
      evaluatedVarMap(name) = value
      name
    case _ => null
  }
  
}
