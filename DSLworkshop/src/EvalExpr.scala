import org.eclipse.swt._
import scala.List
import org.eclipse.swt.layout._
import org.eclipse.swt.widgets._
import org.eclipse.swt.events._
import org.eclipse.swt.graphics.Image
import org.tau.workshop2011.parser._
import org.tau.workshop2011.parser.AST._
import org.tau.workshop2011.expressions._
import collection.mutable.ArrayBuffer

  
      //TODO added the old evalExp back - to allow everything to work, work on this and replace existing
  /*
  def evalExpr(exp: Expr, expType: Type) = (exp: @unchecked) match {
    case Literal(value) => value
  }
  */

object EvalExpr {  
  //TODO delete this later when everything is replaced with evalExpr(someType)  
  //def evalExpr[T](exp: Option[Expr], expType: Type) = exp match { //TODO follow al the changes Elad did on this regard
  def apply[T](exp: Option[Expr]) = exp match {
    //TODO not sure why but this was not recognized as "some" but rather as "int",but lets deal with this some other time
    case Some(value) => value match { case Literal(value: T) => Some(value) }
    case None => None
  }
  
  
  //TODO write evalExpr with pattern matching
 
  
  //Var (adding to Varmap) //TODO - anything else here?
  def returnStringVal(att: Attribute, unevaluatedVarMap: scala.collection.mutable.Map[String, Any], evaluatedVarMap: Map[String, Any]) = {
     att.getValue
  }  
  def addAttributeToVarMap(att: Attribute, unevaluatedVarMap: scala.collection.mutable.Map[String, Any], evaluatedVarMap: Map[String, Any]) = {
     unevaluatedVarMap(att.getName)=returnStringVal(att, unevaluatedVarMap, evaluatedVarMap)
     //TODO - anything else here?
  }
  def evalExprVar(att:Attribute, unevaluatedVarMap: scala.collection.mutable.Map[String, Any], evaluatedVarMap: Map[String, Any])={
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
        var returnval=0;
        var conditionVal=false;
        var i=0;
        //iterate over all the "condition=>val"
        while(!conditionVal && i<conds.length){
          returnval=evalExprInt(Some(conds(i)._2), unevaluatedVarMap, evaluatedVarMap).get
          conditionVal=evalExprBoolean(Some(conds(i)._1), unevaluatedVarMap, evaluatedVarMap).get
          i+=1;
          }
        //otherwise
        if (!conditionVal)(returnval = evalExprInt(Some(otherwise), unevaluatedVarMap, evaluatedVarMap).get)
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
      case Comparision(left: DirectExpr, right: DirectExpr) => {
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
        var returnval=false;
        var conditionVal=false;
        var i=0;
        //iterate over all the conditions "condition=>val"
        while(!conditionVal && i<conds.length){
          returnval=evalExprBoolean(Some(conds(i)._2), varmap, constmap).get
          conditionVal=evalExprBoolean(Some(conds(i)._1), varmap, constmap).get
          i+=1;
          }
        //otherwise
        if (!conditionVal)(returnval = evalExprBoolean(Some(otherwise), varmap, constmap).get)
        Some(returnval)
      }
      //TODO test - working
      case Literal(value: Boolean) => Some(value) //TODO is this ok?
    }

    case None => None
  }
  
  
  //halign
  //TODO  
//  def evalExprHAlign(exp: Option[Expr], varmap: Map[String, Any], constmap: Map[String, Any]): Option[Int] = exp match {
//    case Some(value) => value match {
//     {
//        
//      }
//    }
//    case None => None
//  }  
  

  //valign
//TODO
//  def evalExprVAlign(exp: Option[Expr], varmap: Map[String, Any], constmap: Map[String, Any]): Option[Int] = exp match {
//    case Some(value) => value match {
//      //case  Left(elem: DirectExpr)=>{
//        
//      }
//    }
//    case None => None
//  }  

  //fonttuple <font name, size, style>
    //TODO  
  /*   def evalExprFontTuple(exp: Option[Expr], varmap: Map[String, Any]): Option[Int] = exp match {
    case Some(value) => value match {
      
      }
    }
    case None => None
  }*/
  
  
  
}
