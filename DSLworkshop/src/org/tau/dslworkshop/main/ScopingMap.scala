package org.tau.dslworkshop.main

class ScopingMap[K, V](parent: ScopingMap[K, V] = null) extends scala.collection.mutable.HashMap[K, V] {
  override def apply(key: K) =
    if (parent == null)
      super.apply(key)
    else
      super.getOrElse(key, parent(key))
  override def update(key: K, value: V) =
    if (parent != null && parent.contains(key))
      parent(key) = value
    else
      super.update(key, value)
  // TODO maybe kick this out
  /*override def +[extendsV >: V](elem1: (K, extendsV), elem2: (K, extendsV), elems: (K, extendsV)*) = { 
      val ret = new OurMap[K, V](this)
      for ((key, value) <- (elems :+ elem1 :+ elem2).asInstanceOf[Seq[(K, V)]]) ret(key) = value
      ret.asInstanceOf[OurMap[K, extendsV]]
    }*/

  /*override def +[extendsV >: V](elem: (K, extendsV)) = {
    val ret = new ScopingMap[K, V](this)
    ret(elem._1) = elem._2.asInstanceOf[V]
    ret.asInstanceOf[ScopingMap[K, extendsV]]
  }*/
}