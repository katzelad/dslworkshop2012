import org.eclipse.swt._
import org.eclipse.swt.layout._
import org.eclipse.swt.widgets._
import org.eclipse.swt.events._


object Main {

    def main(args:Array[String]) = {
    val display = new Display()

    
    val shell = new Shell(display)
    shell.setText("Elad")
    shell.setSize(200, 100)
    shell.setLayout(new GridLayout());

    val l1 = new Label(shell, SWT.PUSH);
    l1.setText("Hello");

    shell.open()
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep()
      }
    }
    display.dispose()
  }
}