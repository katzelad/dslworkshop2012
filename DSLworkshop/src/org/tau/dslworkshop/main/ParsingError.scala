package org.tau.dslworkshop.main

class ParsingError(message: String, line: Int, column: Int) extends Exception(s"Syntax error in line $line, column $column:\n$message")