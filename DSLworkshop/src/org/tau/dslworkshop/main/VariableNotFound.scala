package org.tau.dslworkshop.main

class VariableNotFound(name: Any) extends Exception(s"""Error: Variable "$name" not found""")