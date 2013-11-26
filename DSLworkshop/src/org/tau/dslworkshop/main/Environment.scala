package org.tau.dslworkshop.main

import scala.reflect.ClassTag

import org.tau.workshop2011.parser.AST.Comparison
import org.tau.workshop2011.parser.AST.Condition
import org.tau.workshop2011.parser.AST.Conjuction
import org.tau.workshop2011.parser.AST.Disjunction
import org.tau.workshop2011.parser.AST.Expr
import org.tau.workshop2011.parser.AST.FunctionCall
import org.tau.workshop2011.parser.AST.IterationVariable
import org.tau.workshop2011.parser.AST.Literal
import org.tau.workshop2011.parser.AST.Negation
import org.tau.workshop2011.parser.AST.Product
import org.tau.workshop2011.parser.AST.Sum
import org.tau.workshop2011.parser.AST.Variable


class Environment(var evaluatedVarMap: TEvaluatedVarMap, var unevaluatedVarMap: TUnevaluatedVarMap) {

  // def expect[T: ClassTag](value: Any) = value match { case typed: T => typed case _ => throw new Error("Syntax Error") }

  //def evalExpr[T](exp: Option[Expr], expType: Type) = exp match {

  def eval[T: ClassTag](exp: Expr): T = (exp match {

    case Comparison(left, right) => eval[Int](left) == eval[Int](right)

    case Condition(conds, otherwise) =>
      conds
        .find({ case (pred, value) => !eval[Boolean](pred) })
        .map({ case (pred, value) => eval[T](value) })
        .getOrElse(eval[T](otherwise))

    case Conjuction(elems) => elems.map(eval[Boolean]).reduce(_ && _)

    case Disjunction(elems) => elems.map(eval[Boolean]).reduce(_ || _)

    case FunctionCall(func, args) => eval[(Seq[Any]) => T](func).apply(args) // TODO handle possible error

    case IterationVariable(_, _, _) => throw new Exception("Syntax Error")

    case Literal(value) => value

    case Negation(expr) => !eval[Boolean](expr)

    case Product(mul, div) => mul.map(eval[Int]).product / div.map(eval[Int]).product

    case Sum(add, sub) => add.map(eval[Int]).sum - sub.map(eval[Int]).sum

    case Variable(id, varType, _) => evaluatedVarMap(id)

  }) match {

    case typed: T => typed

    case other => throw new Exception("Syntax Error")
    
    // case other => throw new Exception("Syntax Error: Expected " + <type> + ", found " + Type.fromValue(other))

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

  def changeVarRTL[T](exp: Expr, value: T): (String, T) = exp match {
    case Variable(name, _, false) => (name, value)
    case Negation(expr) => changeVarRTL[T](exp, (!value.asInstanceOf[Boolean]).asInstanceOf[T])
    case Comparison(Variable(id, _, false), value) if eval(value).asInstanceOf[Boolean] && unevaluatedVarMap(id) == INITIAL_ATT_FLAG =>
      (id, eval(value))
    case Comparison(value, Variable(id, _, false)) if eval(value).asInstanceOf[Boolean] && unevaluatedVarMap(id) == INITIAL_ATT_FLAG =>
      (id, eval(value))
    case _ => null
  }
}
