package org.tau.dslworkshop.compiler.exceptions

class VariableNotFound(name: Any) extends Exception(s"""Error: Variable "$name" not found""")