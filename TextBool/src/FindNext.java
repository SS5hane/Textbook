
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FindNext {

    /**
     * 查找下一个
     * @param frame
     */
    public FindNext(MainTest frame)
    {

        int number=0;

        String allTexts=frame.editArea.getText().toUpperCase();
        if(frame.editArea.getSelectedText().equals("")){
        }
        else {
            String searchTexts = frame.editArea.getSelectedText().toUpperCase();
            number=allTexts.indexOf(searchTexts,frame.editArea.getCaretPosition());
            if(number==-1){
                frame.editArea.select(0,0);
            }else{
                frame.editArea.select(number,number+searchTexts.length());
            }
        }

    }

}
