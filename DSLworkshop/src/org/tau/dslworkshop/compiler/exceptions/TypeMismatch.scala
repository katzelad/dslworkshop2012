package org.tau.dslworkshop.compiler.exceptions

import org.tau.workshop2011.expressions.Type
import org.tau.workshop2011.parser.AST.Expr

class TypeMismatch(expectedType: Type, found: Any, expr: Expr) extends Exception(
    "Syntax Error: Expected " + expectedType + ", found " + Type.fromValue(found) + " in " + expr
    )