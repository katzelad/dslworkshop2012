package org.tau.dslworkshop.compiler

import org.tau.dslworkshop.compiler.exceptions.VariableNotFound

/*
 * An implementation of a hash map which acts as a node in a tree of maps.
 * A key exists in the map if the current node, or any of the ancestor nodes, contains it.
 * A new key is always added to the current node.
 */
class ScopingMap[K, V](parent: ScopingMap[K, V] = null) extends scala.collection.mutable.HashMap[K, V] {
  override def apply(key: K) =
    if (parent == null)
      if (super.contains(key))
        super.apply(key)
      else
        throw new VariableNotFound(key)
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