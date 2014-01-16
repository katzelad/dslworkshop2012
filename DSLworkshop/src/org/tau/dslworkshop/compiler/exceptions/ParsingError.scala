package org.tau.dslworkshop.compiler.exceptions

class ParsingError(message: String, line: Int, column: Int) extends Exception(s"Syntax error in line $line, column $column:\n$message")