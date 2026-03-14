package mission3;

public class TestRemote {

    public static void main(String[] args) {      
        
        Remote r = new Remote() ;// 產生遙控器，可能還需要綁定設備
        r.setController(new TelevisionController( new Television() ) );
        r.setController(new LightController( new Light() ) );
        r.setController(new SoundboxController( new Soundbox() ) );
        
        //在此假設第零個按鈕是綁定電視
        System.out.println("---首先開電視-----\n");
        r.buttonClicked(0); // 按下第零個按鈕(開電視)
        r.buttonClicked(0); // 按下第零個按鈕(關電視)
        System.out.println("\n---換燈光-----\n");
        r.buttonClicked(1); // 按下第一個按鈕(燈光暗)
        r.buttonClicked(1); // 按下第一個按鈕(燈光亮)
        r.buttonClicked(1); // 按下第一個按鈕(關燈)
        System.out.println("\n---換音響-----\n");
        r.buttonClicked(2); // 按下第二個按鈕(音響音量小)
        r.buttonClicked(2); // 按下第二個按鈕(音響音量中)
        r.buttonClicked(2); // 按下第二個按鈕(音響音量大)
        r.buttonClicked(2); // 按下第二個按鈕(關音響)
        System.out.println("\n---接下來按下全部的按鈕-----");
        r.pressAllButtons(); //全部按一遍
        System.out.println("\n---按下第二次-----"); //分行好觀察
        r.pressAllButtons();
        System.out.println("\n---按下第三次-----"); 
        r.pressAllButtons();
        System.out.println("\n---按下第四次-----"); 
        r.pressAllButtons();
        System.out.println("\n---結束-----"); 
    }
}
