package mission3;

public class TelevisionController implements Controller{
	
	private Television tv;
	int command;
	public void execute() {
	  if( command == 0) {
	    tv.on();
	    command = 1;
	  } // if
	  else if( command == 1) {
	    tv.off();
	    command = 0;
	  } // else if
	} // buttonclick()
	
	public TelevisionController(Television tv) {
	  this.tv = tv;	
	  this.command = 0;
	} // TelevisionController
	
}
