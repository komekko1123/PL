package mission3;

public class SoundboxController implements Controller {
	private Soundbox sb;
	int command;
	public void execute() {
	  if( command == 0) {
		sb.low();
	    command = 1;
	  } // if
	  else if( command == 1) {
		sb.medium();
	    command = 2;
	  } // else if
	  else if( command == 2) {
		sb.high();
	    command = 3;  
	  } // else if
	  else if( command == 3) {
		sb.off();
		command = 0;	   
	  } // else if
	} // buttonclick()
	
	public SoundboxController(Soundbox sb) {
	  this.sb = sb;	
	  this.command = 0;
	} // TelevisionController
	
}
