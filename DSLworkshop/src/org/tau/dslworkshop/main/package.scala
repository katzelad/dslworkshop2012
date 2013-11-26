package org.tau.dslworkshop

package object main {
	
  type TEvaluatedVarMap = ScopingMap[String, Any]
  
  type TUnevaluatedVarMap = ScopingMap[String, Set[() => Unit]]
  
  type TEvalNodeReturn = (Int, Int, Boolean, Boolean, (Int, Int, Int, Int) => Unit)

  val SASH_WIDTH = 5
  
  val INITIAL_ATT_FLAG = () => {}
  
}