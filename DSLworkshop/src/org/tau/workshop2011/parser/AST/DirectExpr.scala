package org.tau.workshop2011.parser.AST

/**
 * Any expression which is not conditional, and therefore can be computed in one
 * iteration
 */
trait DirectExpr extends Expr
