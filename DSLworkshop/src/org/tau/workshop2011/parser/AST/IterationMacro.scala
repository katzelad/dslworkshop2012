package org.tau.workshop2011.parser.AST

import org.tau.workshop2011.expressions.Type

/** Representation of iteration range and array definitions */
case class IterationProperties(indexID: String, start: Int, end: Int, vals: Map[String, List[DirectExpr]]) {

  override def toString = {
    var buf = ""
    for ((key, values) <- vals)
      buf += ", " + key + " = " + values.mkString("{ ", ", ", " }")

    "[ " + indexID + " = " + start + "..." + end + buf + " ]"
  }

}

case class IterationMacro(widget: Widget, direction: Container.Direction.Value, props: IterationProperties) 
	extends Widget("Iteration", None, None)
{
  widget.parent = this
  
  def accept(visitor: ASTVisitor): Any = widget accept visitor
}

/** An expander of iteration macros.
 *
 * This object does the necessary work to expand iteration macros which may be
 * present in programs written in the DSL. This is the only code that should
 * touch macro definitions since after it returns, no macro elements should
 * remain in the AST.
 *
 * This class is used directly by the [[org.tau.workshop2011.parsing.WidgetParser]]
 * class to make sure that the result from the parsing would already be free
 * from macro iterations.
 */
object IterationMacro {
  /** Expand an iteration macro which is described by the given arguments. Each
   *  sub-widget of iteration is returned as an independent copy that can be
   *  manipulated independently of the others
   */
  def expand(widget: Widget, direction: Container.Direction.Value, props: IterationProperties): Widget = {

    var widgetsReverse: List[Widget] = Nil
    for (i <- Range(props.start, props.end + 1)) {
      val widgetExpansion = widget accept new ASTDeepCloneVisitor {

        override def visit(node: ExpressionAttribute) = {
          if (node.getName == props.indexID)
            throw new Exception("Can't define a variable with the name " +
              node.getName + " under an iteration with an iteration variable of " +
              "the same getName");
          else
            super.visit(node)
        }

        override def visit(node: Variable) = {
          node match {
            case Variable(props.indexID, Type.tUnknown, _) | Variable(props.indexID, Type.tInt, _) => new Literal(i)
            case Variable(props.indexID, t, _) => throw new Exception("Can't replace the iteration variable " + props.indexID + " with an integer since a type conflict would occur! Expected type " + t)
            case _ => super.visit(node)
          }
        }

        override def visit(node: IterationVariable) = {
          /* If the iteration variable isn't the current one we are expanding,
           * then we shouldn't touch.
           */
          if (node.indexID != props.indexID)
            super.visit(node)

          /* Otherwise, we must check that the definition exists. */
          else props.vals.get(node.arrayID) match {

            /* Test if the "array" used for the expansion is defined */
            case None => throw new Exception("No macro expansion found for " + node)

            /* If we did find a matching array, we must assert it's long enough */
            case Some(values) => if (i >= values.length) {
              throw new Exception("Can't expand " + node + " for "
                + node.indexID + "=" + i + " since the index is too big");
            } else {
              /* Before expanding the macro definition, we want to prevent
               * an infinite recursion due to cyclic newDependencies. So let's make
               * sure that this expression does NOT contain a reference to any
               * other macro expansion with this variable. True, this doesn't
               * always mean recursion, but it's the easiest thing we can do
               */
              values(i) accept new ASTDeepVisitor {
                override def visit(n: IterationVariable) {
                  node match {
                    case IterationVariable(_, props.indexID, _) => throw new Exception("The macro expansion of " + node + " contains more macro expansions")
                    case _ => super.visit(n)
                  }
                }
              }

              (values(i) accept this).asInstanceOf[DirectExpr]
            }
          }
        }
      }

      /* In some cases, substitution won't work and we will still need to store
       * the index in the environment. This can happen when we have a non-atomic
       * widget inside our expansion. So let's make it available...
       */
      val envWidgetExpansion = new PropertyScope(widgetExpansion.asInstanceOf[Widget], List(new ExpressionAttribute(new Variable("i"), new Literal(i))))

      widgetsReverse = (envWidgetExpansion) :: widgetsReverse
    }

    /* If the iteration doesn't produce anything then we should put an empty
     * widget */
    if (widgetsReverse.length == 0)
      new AtomicWidget("", Nil, None, None)
    /* Otherwise if the iteration produces just one widget, we should return it
     * without needing to wrap it in some container
     */
    else if (widgetsReverse.length == 1)
      widgetsReverse.head
    /* Otherwise if we have multiple widgets, we should wrap them in a container
     * of the desired orientation
     */
    else
      new Container(direction, widgetsReverse.reverse, None, None)
  }
}
