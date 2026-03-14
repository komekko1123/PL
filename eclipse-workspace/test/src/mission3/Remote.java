package mission3;

public class Remote {
	    
		public Controller[] buttons = new Controller[5];
		
		public void setController(Controller controller) 
		{
		  int i = 0;
		  for(i = 0 ; i < 5 ; i++ ) {
		    if( buttons[i] == null) {
		      buttons[i] = controller;		
		      break;
		    } // if
		  } // for
		  
		  if( i == 5)
		    System.out.println("所有按鈕接有操縱裝置了");
		} // setController

		
		void buttonClicked(int buttonId) {
		  if( buttons[buttonId] != null) {
		    buttons[buttonId].execute();
		  } // if
		}
	    
	    /**
	     * 直接啟動所有的裝置(意即，每個裝置的按鈕都按一下)
	     */
	    void pressAllButtons() {
	        for ( int i = 0; i < buttons.length; i++ ) {
	            buttonClicked(i);
	        }
	    }
	
}
	