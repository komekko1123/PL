package mission3;

public class LightController implements Controller {
	private Light lg;
	int command;
	public void execute() {
	  if( command == 0) {
		lg.dim();
	    command = 1;
	  } // if
	  else if( command == 1) {
		lg.bright();
	    command = 2;
	  } // else if
	  else if( command == 2) {
	    lg.off();
		command = 0;
	  } // else if
	} // buttonclick()
	
	public LightController(Light lg) {
	  this.lg = lg;	
	  this.command = 0;
	} // TelevisionController
	
}
