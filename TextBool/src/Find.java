import jdk.nashorn.internal.scripts.JO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Find {
    JDialog dialog;
    Container con;
    JLabel findContentLabel=new JLabel("查找内容");
    JTextField findTextField=new JTextField(10);
    JButton findNextButton=new JButton("查找下一个");
    JCheckBox CaseBox=new JCheckBox("区分大小写");
    ButtonGroup buttonGroup=new ButtonGroup();
    JRadioButton upButton=new JRadioButton("向上查找");
    JRadioButton downButton=new JRadioButton("向下查找");
    JButton cancel=new JButton("取消");
    JPanel panel1=new JPanel();
    JPanel panel2=new JPanel();
    JPanel panel3=new JPanel();
    JPanel directionPanel=new JPanel();

    public Find(MainTest frame)
    {   dialog=new JDialog(frame,"查找",false);
        con=dialog.getContentPane();
        con.setLayout(new FlowLayout(FlowLayout.LEFT));
        downButton.setSelected(true);
        buttonGroup.add(upButton);
        buttonGroup.add(downButton);


        //取消按钮事件处理
        cancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                dialog.dispose();
            }
        });
        //"查找下一个"按钮监听
        findNextButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {

                String allText,searchText;
                int number=0;
                allText=frame.editArea.getText();   //获取文本框中的字段
                searchText=findTextField.getText();    //获取要搜索的字段
                String CaseAllText=allText.toUpperCase();   //全部文本转换为大写
                String CaseSearchText=searchText.toUpperCase(); //搜索文本转换为大写

                String useAllText;      //查找时使用的文本
                String usesearchText;   //查找时使用的搜索文本

                if(CaseBox.isSelected()){   //是否选择大小写
                    useAllText=allText;
                    usesearchText=searchText;
                }else{
                    useAllText=CaseAllText;
                    usesearchText=CaseSearchText;
                }

                if(upButton.isSelected()){
                    if(frame.editArea.getSelectedText()==null) {
                        //如果文中没有选中的文本，就从光标位置开始查找上一个
                        number = useAllText.lastIndexOf(usesearchText, frame.editArea.getCaretPosition() - 1);
                    }else{
                        //如果有选中的文本，就从选中文本之前一个搜索文本的长度开始搜
                        number=useAllText.lastIndexOf(usesearchText,frame.editArea.getCaretPosition()-findTextField.getText().length()-1);
                    }
                    if(number==-1){
                        JOptionPane.showMessageDialog(null,"找不到"+searchText,"错误",JOptionPane.INFORMATION_MESSAGE);
                    }else{
                        frame.editArea.select(number,number+usesearchText.length());
                    }
                }else if(downButton.isSelected()){

                    number=useAllText.indexOf(usesearchText,frame.editArea.getCaretPosition());

                    if(number==-1){
                        JOptionPane.showMessageDialog(null,"找不到"+searchText,"错误",JOptionPane.INFORMATION_MESSAGE);
                    }else{
                        frame.editArea.select(number,number+usesearchText.length());
                    }
                }

        }
        });


        directionPanel.setBorder(BorderFactory.createTitledBorder("方向"));


        directionPanel.add(upButton);
        directionPanel.add(downButton);
        panel1.setLayout(new GridLayout(2,1));
        panel1.add(findNextButton);
        panel1.add(cancel);
        panel2.add(findContentLabel);
        panel2.add(findTextField);
        panel2.add(panel1);
        panel3.add(CaseBox);
        panel3.add(directionPanel);
        con.add(panel2);
        con.add(panel3);
        dialog.setSize(410,180);
        dialog.setResizable(false);//不可调整大小
        dialog.setLocation(230,280);
        dialog.setVisible(true);
    }//查找方法结束

}
