//import evalExpr._
//
//class DSLProgram(name: String) {
//  var bindedFunctionsMap = Map[String, () => Unit]()
//  def set(varName: String, value: Any){}
//  
//  def bind (varToAdd: String, functionToAdd:  => Unit) {}
//  
//  def bind(varToAdd: String, functionToAdd: Any) { //TODO not good - functionToAdd should be able to have multiple parameters, and we don't know how many and of which type
//    functionToAdd match { //TODO fix this erasure problem, perhaps with menifest
//      case Function0[_] | Function1[_, _] => " "
//       
//        
//      //case f: (Any => Any) => " "
//    }
//    
//   // bindedFunctionsMap += varToAdd -> functionToAdd
//  }
//  
//  def when_changed(s: String, f: () => Unit){
//    
//  }
//  
//  def apply(name: String, parametersList: Map[String, Any]){
//    
//   // evalNode((Main.widgetsMap(name)), parametersList
//  }
//  
//}
//
//object DSLProgram {
//  def apply(name: String) {
//    evalCode
//  }
//}