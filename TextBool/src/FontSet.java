import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FontSet {
    JDialog fontDialog;
    Container con;
    JLabel fontLabel=new JLabel("字体：");
    JLabel styleLabel=new JLabel("字形：");
    JLabel sizeLabel=new JLabel("大小：");
    JLabel sample=new JLabel("24320162202917-ShaneWang");
    JTextField fontText=new JTextField(9);
    JTextField styleText=new JTextField(8);
    int style[]={Font.PLAIN, Font.BOLD, Font.ITALIC, Font.BOLD+ Font.ITALIC};
    JTextField sizeText=new JTextField(5);
    JButton okButton=new JButton("确定");
    JButton cancel=new JButton("取消");
    JPanel panel1=new JPanel();



    public FontSet(MainTest jframe)
    {
        fontDialog=new JDialog(jframe,"字体设置",false);
        con=fontDialog.getContentPane();
        con.setLayout(new FlowLayout(FlowLayout.LEFT));

        fontLabel.setPreferredSize(new Dimension(100,20));
        styleLabel.setPreferredSize(new Dimension(100,20));
        sizeLabel.setPreferredSize(new Dimension(100,20));
        fontText.setPreferredSize(new Dimension(200,20));
        styleText.setPreferredSize(new Dimension(200,20));
        sizeText.setPreferredSize(new Dimension(200,20));
        JPanel samplePanel=new JPanel();

        cancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                fontDialog.dispose();
            }
        });

        Font currentFont=jframe.editArea.getFont();
        fontText.setText(currentFont.getFontName());
        fontText.selectAll();

        if(currentFont.getStyle()== java.awt.Font.PLAIN)
            styleText.setText("常规");
        else if(currentFont.getStyle()== java.awt.Font.BOLD)
            styleText.setText("粗体");
        else if(currentFont.getStyle()== java.awt.Font.ITALIC)
            styleText.setText("斜体");
        else if(currentFont.getStyle()==(java.awt.Font.BOLD+ java.awt.Font.ITALIC))
            styleText.setText("粗斜体");
        styleText.selectAll();
        String str=String.valueOf(currentFont.getSize());
        sizeText.setText(str);
        sizeText.selectAll();

        final JList fontList,styleList,sizeList;
        //获取系统所有字体
        GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
        final String fontName[]=ge.getAvailableFontFamilyNames();
        fontList=new JList(fontName);
        fontList.setFixedCellWidth(86);
        fontList.setFixedCellHeight(20);
        fontList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        final String fontStyle[]={"常规","粗体","斜体","粗斜体"};
        styleList=new JList(fontStyle);
        styleList.setFixedCellWidth(86);
        styleList.setFixedCellHeight(20);
        styleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        if(currentFont.getStyle()== java.awt.Font.PLAIN)
            styleList.setSelectedIndex(0);
        else if(currentFont.getStyle()== java.awt.Font.BOLD)
            styleList.setSelectedIndex(1);
        else if(currentFont.getStyle()== java.awt.Font.ITALIC)
            styleList.setSelectedIndex(2);
        else if(currentFont.getStyle()==(java.awt.Font.BOLD+ java.awt.Font.ITALIC))
            styleList.setSelectedIndex(3);
        final String fontSize[]={"8","9","10","11","12","14","16","18","20","22","24","26","28","36","48","72"};
        sizeList=new JList(fontSize);
        sizeList.setFixedCellWidth(43);
        sizeList.setFixedCellHeight(20);
        sizeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fontList.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent event)
            {
                fontText.setText(fontName[fontList.getSelectedIndex()]);
                fontText.selectAll();
                Font sampleFont1=new Font(fontText.getText(),style[styleList.getSelectedIndex()],Integer.parseInt(sizeText.getText()));
                sample.setFont(sampleFont1);
            }
        });
        styleList.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent event)
            {
                int s=style[styleList.getSelectedIndex()];
                styleText.setText(fontStyle[s]);
                styleText.selectAll();
                Font sampleFont2=new Font(fontText.getText(),style[styleList.getSelectedIndex()],Integer.parseInt(sizeText.getText()));
                sample.setFont(sampleFont2);
            }
        });
        sizeList.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent event)
            {
                sizeText.setText(fontSize[sizeList.getSelectedIndex()]);
                sizeText.selectAll();
                java.awt.Font sampleFont3=new java.awt.Font(fontText.getText(),style[styleList.getSelectedIndex()],Integer.parseInt(sizeText.getText()));
                sample.setFont(sampleFont3);
            }
        });
        okButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                java.awt.Font okFont=new java.awt.Font(fontText.getText(),style[styleList.getSelectedIndex()],Integer.parseInt(sizeText.getText()));
                jframe.editArea.setFont(okFont);
                fontDialog.dispose();
            }
        });

        samplePanel.setBorder(BorderFactory.createTitledBorder("示例"));
        samplePanel.add(sample);





        con.add(panel1);
        con.add(fontText);
        con.add(styleText);
        con.add(sizeText);
        con.add(new JScrollPane(fontList));
        con.add(new JScrollPane(styleList));
        con.add(new JScrollPane(sizeList));

        con.add(samplePanel);
        con.add(okButton);
        con.add(cancel);

        fontDialog.setSize(400,400);
        fontDialog.setLocation(200,200);
        fontDialog.setResizable(true);
        fontDialog.setVisible(true);
    }

}
