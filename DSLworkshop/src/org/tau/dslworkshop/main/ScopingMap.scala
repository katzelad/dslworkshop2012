package org.tau.dslworkshop.main

class ScopingMap[K, V](parent: ScopingMap[K, V] = null) extends scala.collection.mutable.HashMap[K, V] {
  override def apply(key: K) =
    if (parent == null)
      super.apply(key)
    else
      super.get(key) match {
        case Some(value) => value
        case None => parent(key)
      }
  override def update(key: K, value: V) =
    if (contains(key))
      super.update(key, value)
    else
      parent(key) = value
}