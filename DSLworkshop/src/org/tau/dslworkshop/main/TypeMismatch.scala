package org.tau.dslworkshop.main

import org.tau.workshop2011.expressions.Type

class TypeMismatch(expectedType: Type, found: Any) extends Exception(
    "Syntax Error: Expected " + expectedType + ", found " + Type.fromValue(found)
    )