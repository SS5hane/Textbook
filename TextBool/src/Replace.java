import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Replace {
    JDialog jDialog;
    Container con;
    JPanel directionPanel=new JPanel();
    JLabel findLabel=new JLabel("查找:");
    JTextField findTextField=new JTextField(20);
    JButton findNextButton=new JButton("查找下一个");
    JLabel replaceLabel=new JLabel("替换为");
    JButton replaceButton=new JButton("替换");
    JButton replaceAllButton=new JButton("替换全部");
    JButton cancel=new JButton("取消");
    JCheckBox CaseBox=new JCheckBox("区分大小写");
    ButtonGroup bGroup=new ButtonGroup();
    JRadioButton upButton=new JRadioButton("向上(U)");
    JRadioButton downButton=new JRadioButton("向下(U)");
    JTextField replaceText=new JTextField(20);
    JPanel panel1=new JPanel();
    JPanel panel2=new JPanel();
    JPanel panel3=new JPanel();
    JPanel panel4=new JPanel();



    public Replace(MainTest frame)
    {   final JDialog replaceDialog=new JDialog(frame,"替换",false);//false时允许其他窗口同时处于激活状态(即无模式)
        Container con=replaceDialog.getContentPane();//返回此对话框的contentPane对象
        con.setLayout(new FlowLayout(FlowLayout.CENTER));
        cancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                replaceDialog.dispose();
            }
        });

        downButton.setSelected(true);
        bGroup.add(upButton);
        bGroup.add(downButton);



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


        replaceButton.addActionListener(new ActionListener()
        {   public void actionPerformed(ActionEvent e)
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
                if(replaceText.getText().length()==0 && frame.editArea.getSelectedText()!=null)
                    frame.editArea.replaceSelection("");
                if(replaceText.getText().length()>0 && frame.editArea.getSelectedText()!=null)
                    frame.editArea.replaceSelection(replaceText.getText());
            }
        });


        replaceAllButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                frame.editArea.setCaretPosition(0);   //将光标放到编辑区开头

                int number=0,replaceNumber=0;
                String allText,searchText;

                while(number!=-1){

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
                    number=useAllText.indexOf(usesearchText,frame.editArea.getCaretPosition());

                    if(number>-1){
                            frame.editArea.setCaretPosition(number);
                            frame.editArea.select(number,number+usesearchText.length());
                            frame.editArea.replaceSelection(replaceText.getText());
                            replaceNumber++;
                    }

            }
                if(replaceNumber==0){
                    JOptionPane.showMessageDialog(replaceDialog, "找不到您查找的内容!", "记事本",JOptionPane.INFORMATION_MESSAGE);
                }else{
                    JOptionPane.showMessageDialog(replaceDialog, "成功替换"+replaceNumber+"个内容", "记事本",JOptionPane.INFORMATION_MESSAGE);
                }
        }
        });

        directionPanel.setBorder(BorderFactory.createTitledBorder("方向"));
        directionPanel.add(upButton);
        directionPanel.add(downButton);
        panel4.setLayout(new GridLayout(2,1));
        panel1.add(findLabel);
        panel1.add(findTextField);
        panel1.add(findNextButton);
        panel4.add(replaceButton);
        panel4.add(replaceAllButton);
        panel2.add(replaceLabel);
        panel2.add(replaceText);
        panel2.add(panel4);
        panel3.add(CaseBox);
        panel3.add(directionPanel);
        panel3.add(cancel);
        con.add(panel1);
        con.add(panel2);
        con.add(panel3);



        replaceDialog.setSize(500,250);
        replaceDialog.setResizable(true);//不可调整大小
        replaceDialog.setLocation(230,280);
        replaceDialog.setVisible(true);
    }

}
