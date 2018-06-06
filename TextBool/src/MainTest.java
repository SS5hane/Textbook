import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import javax.swing.undo.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.datatransfer.*;


public class MainTest extends JFrame implements DocumentListener
{
    JMenu fileMenu,editMenu,formatMenu,viewMenu
            ;
    JPopupMenu popupMenu;
    JMenuItem popupMenu_Undo,popupMenu_Cut,popupMenu_Copy,popupMenu_Paste,popupMenu_Delete,popupMenu_SelectAll;
    JMenuItem fileMenu_New,fileMenu_Open,fileMenu_Save,fileMenu_SaveAs,fileMenu_Exit;
    JMenuItem editMenu_Undo,editMenu_Cut,editMenu_Copy,editMenu_Paste,editMenu_Delete,editMenu_Find,editMenu_FindNext,editMenu_Replace,editMenu_GoTo,editMenu_SelectAll,editMenu_TimeDate;
    JCheckBoxMenuItem formatMenu_LineWrap;
    JMenuItem formatMenu_Font;
    JCheckBoxMenuItem viewMenu_Status;
    JPanel jPanel=new JPanel();
    JTextArea editArea;
    JLabel statusLabel;
    Toolkit toolkit=Toolkit.getDefaultToolkit();
    Clipboard clipBoard=toolkit.getSystemClipboard();
    protected UndoManager undo=new UndoManager();
    protected UndoableEditListener undoHandler=new UndoHandler();
    String oldValue;
    boolean isNewFile=true;
    File currentFile;
    int lineNum=0,columnNum=0;


    public MainTest()
    {
        super("无标题");
        //改变系统默认字体
        Font font = new Font("Dialog", Font.PLAIN, 12);
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, font);
            }
        }
        /**
         * 菜单条
         */
        JMenuBar menuBar=new JMenuBar();

        /**
         * 文件菜单栏
         */
        fileMenu=new JMenu("文件(F)");
        fileMenu.setMnemonic('F');//设置快捷键ALT+F
        fileMenu_New=new JMenuItem("新建(N)");
        fileMenu_New.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,InputEvent.CTRL_MASK));
        fileMenu_Open=new JMenuItem("打开(O)...");
        fileMenu_Open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,InputEvent.CTRL_MASK));
        fileMenu_Save=new JMenuItem("保存(S)");
        fileMenu_Save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_MASK));
        fileMenu_SaveAs=new JMenuItem("另存为(A)...");
        fileMenu_Exit=new JMenuItem("退出(X)");

        /**
         * 编辑菜单栏
         */
        editMenu=new JMenu("编辑(E)");
        editMenu.setMnemonic('E');//设置快捷键ALT+E


        editMenu.addMenuListener(new MenuListener()
        {
            public void menuCanceled(MenuEvent e)//取消菜单时调用
            {
                checkMenuStatus();
            }
            public void menuDeselected(MenuEvent e)//取消选择某个菜单时调用
            {
                checkMenuStatus();
            }
            public void menuSelected(MenuEvent e)//选择某个菜单时调用
            {
                checkMenuStatus();
            }
        });

        editMenu_Undo=new JMenuItem("撤销(U)");
        editMenu_Undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,InputEvent.CTRL_MASK));
        editMenu_Undo.setEnabled(false);

        editMenu_Cut=new JMenuItem("剪切(T)");
        editMenu_Cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,InputEvent.CTRL_MASK));

        editMenu_Copy=new JMenuItem("复制(C)");
        editMenu_Copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,InputEvent.CTRL_MASK));

        editMenu_Paste=new JMenuItem("粘贴(P)");
        editMenu_Paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,InputEvent.CTRL_MASK));

        editMenu_Delete=new JMenuItem("删除(D)");
        editMenu_Delete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0));

        editMenu_Find=new JMenuItem("查找(F)...");
        editMenu_Find.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,InputEvent.CTRL_MASK));

        editMenu_FindNext=new JMenuItem("查找下一个(N)");
        editMenu_FindNext.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3,0));

        editMenu_Replace = new JMenuItem("替换(R)...",'R');
        editMenu_Replace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_MASK));

        editMenu_GoTo = new JMenuItem("转到(G)...",'G');
        editMenu_GoTo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_MASK));

        editMenu_SelectAll = new JMenuItem("全选",'A');
        editMenu_SelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));

        editMenu_TimeDate = new JMenuItem("时间/日期(D)",'D');
        editMenu_TimeDate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5,0));

        /**
         * 格式菜单栏
         */
        formatMenu=new JMenu("格式(O)");
        formatMenu.setMnemonic('O');//设置快捷键ALT+O
        formatMenu_LineWrap=new JCheckBoxMenuItem("自动换行(W)");
        formatMenu_LineWrap.setMnemonic('W');//设置快捷键ALT+W
        formatMenu_LineWrap.setState(true);
        formatMenu_Font=new JMenuItem("字体(F)...");

        /**
         * 查看菜单
         */
        viewMenu=new JMenu("查看(V)");
        viewMenu.setMnemonic('V');//设置快捷键ALT+V

        viewMenu_Status=new JCheckBoxMenuItem("状态栏(S)");
        viewMenu_Status.setMnemonic('S');//设置快捷键ALT+S
        viewMenu_Status.setState(true);

        /**
         * 向主菜单添加菜单
         */
        menuBar.add(fileMenu);
        /**
         * 新建文件的监听事件
         */
        fileMenu.add(fileMenu_New);
        fileMenu_New.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editArea.requestFocus();
                String currentValue=editArea.getText();
                boolean isTextChange=(currentValue.equals(oldValue))?false:true;
                if(isTextChange)
                {   int saveChoose=JOptionPane.showConfirmDialog(MainTest.this,"您的文件尚未保存，是否保存？","提示",JOptionPane.YES_NO_CANCEL_OPTION);
                    if(saveChoose==JOptionPane.YES_OPTION)
                    {   String str=null;
                        JFileChooser fileChooser=new JFileChooser();
                        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                        fileChooser.setDialogTitle("另存为");
                        int result=fileChooser.showSaveDialog(MainTest.this);
                        if(result==JFileChooser.CANCEL_OPTION)
                        {   statusLabel.setText("您没有选择任何文件");
                            return;
                        }
                        File saveFileName=fileChooser.getSelectedFile();
                        if(saveFileName==null || saveFileName.getName().equals(""))
                        {   JOptionPane.showMessageDialog(MainTest.this,"不合法的文件名","错误",JOptionPane.ERROR_MESSAGE);
                        }
                        else
                        {
                            try
                            {
                                String strr=editArea.getText();
                                strr.replace("\n","\r\n");
                                FileWriter fw=new FileWriter(saveFileName);
                                BufferedWriter bfw=new BufferedWriter(fw);
                                bfw.write(strr,0,strr.length());
                                bfw.flush();//刷新该流的缓冲
                                bfw.close();
                                isNewFile=false;
                                currentFile=saveFileName;
                                oldValue=editArea.getText();
                                setTitle(saveFileName.getName()+" - 记事本");
                                statusLabel.setText("当前打开文件："+saveFileName.getAbsoluteFile());
                            }
                        catch (IOException ioException)
                        { }
                        }
                    }
                    else if(saveChoose==JOptionPane.NO_OPTION)
                    {   editArea.replaceRange("",0,editArea.getText().length());
                        statusLabel.setText(" 新建文件");
                        setTitle("无标题 - 记事本");
                        isNewFile=true;
                        undo.discardAllEdits(); //撤消所有的"撤消"操作
                        editMenu_Undo.setEnabled(false);
                        oldValue=editArea.getText();
                    }
                    else if(saveChoose==JOptionPane.CANCEL_OPTION)
                    {   return;
                    }
                }

                    editArea.replaceRange("", 0, editArea.getText().length());
                    statusLabel.setText(" 新建文件");
                    setTitle("无标题 - 记事本");
                    isNewFile = true;
                    undo.discardAllEdits();//撤消所有的"撤消"操作
                    editMenu_Undo.setEnabled(false);
                    oldValue = editArea.getText();

            }
        });

        /**
         * 打开新文件的监听事件
         */
        fileMenu.add(fileMenu_Open);
        fileMenu_Open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editArea.requestFocus();
                String currentValue=editArea.getText();
                boolean isTextChange= !currentValue.equals(oldValue);
                if(isTextChange)
                {   int saveChoose=JOptionPane.showConfirmDialog(MainTest.this,"您的文件尚未保存，是否保存？","提示",JOptionPane.YES_NO_CANCEL_OPTION);
                    if(saveChoose==JOptionPane.YES_OPTION)
                    {   String str=null;
                        JFileChooser fileChooser=new JFileChooser();
                        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                        //fileChooser.setApproveButtonText("确定");
                        fileChooser.setDialogTitle("另存为");
                        int result=fileChooser.showSaveDialog(MainTest.this);
                        if(result==JFileChooser.CANCEL_OPTION)
                        {   statusLabel.setText("您没有选择任何文件");
                            return;
                        }
                        File saveFileName=fileChooser.getSelectedFile();
                        if(saveFileName==null || saveFileName.getName().equals(""))
                        {   JOptionPane.showMessageDialog(MainTest.this,"不合法的文件名","不合法的文件名",JOptionPane.ERROR_MESSAGE);
                        }
                        else
                        {   try
                        {   FileWriter fw=new FileWriter(saveFileName);
                            BufferedWriter bfw=new BufferedWriter(fw);
                            bfw.write(editArea.getText(),0,editArea.getText().length());
                            bfw.flush();//刷新该流的缓冲
                            bfw.close();
                            isNewFile=false;
                            currentFile=saveFileName;
                            oldValue=editArea.getText();
                            setTitle(saveFileName.getName()+" - 记事本");
                            statusLabel.setText("当前打开文件："+saveFileName.getAbsoluteFile());
                        }
                        catch (IOException ioException)
                        {
                        }
                        }
                    }
                    else if(saveChoose==JOptionPane.NO_OPTION)
                    {   String str=null;
                        JFileChooser fileChooser=new JFileChooser();
                        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                        fileChooser.setDialogTitle("打开文件");
                        int result=fileChooser.showOpenDialog(MainTest.this);
                        if(result==JFileChooser.CANCEL_OPTION)
                        {   statusLabel.setText("您没有选择任何文件");
                            return;
                        }
                        File fileName=fileChooser.getSelectedFile();
                        if(fileName==null || fileName.getName().equals(""))
                        {   JOptionPane.showMessageDialog(MainTest.this,"不合法的文件名","不合法的文件名",JOptionPane.ERROR_MESSAGE);
                        }
                        else
                        {   try
                        {   FileReader fr=new FileReader(fileName);
                            BufferedReader bfr=new BufferedReader(fr);
                            editArea.setText("");
                            while((str=bfr.readLine())!=null)
                            {   editArea.append(str);
                            }
                            setTitle(fileName.getName()+" - 记事本");
                            statusLabel.setText(" 当前打开文件："+fileName.getAbsoluteFile());
                            fr.close();
                            isNewFile=false;
                            currentFile=fileName;
                            oldValue=editArea.getText();
                        }
                        catch (IOException ioException)
                        {
                        }
                        }
                    }
                    else
                    {   return;
                    }
                }
                else
                {   String str=null;
                    JFileChooser fileChooser=new JFileChooser();
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    //fileChooser.setApproveButtonText("确定");
                    fileChooser.setDialogTitle("打开文件");
                    int result=fileChooser.showOpenDialog(MainTest.this);
                    if(result==JFileChooser.CANCEL_OPTION)
                    {   statusLabel.setText(" 您没有选择任何文件 ");
                        return;
                    }
                    File fileName=fileChooser.getSelectedFile();
                    if(fileName==null || fileName.getName().equals(""))
                    {   JOptionPane.showMessageDialog(MainTest.this,"不合法的文件名","不合法的文件名",JOptionPane.ERROR_MESSAGE);
                    }
                    else
                    {   try
                    {   FileReader fr=new FileReader(fileName);
                        BufferedReader bfr=new BufferedReader(fr);
                        editArea.setText("");
                        while((str=bfr.readLine())!=null)
                        {   editArea.append(str);
                        }
                        setTitle(fileName.getName()+" - 记事本");
                        statusLabel.setText(" 当前打开文件："+fileName.getAbsoluteFile());
                        fr.close();
                        isNewFile=false;
                        currentFile=fileName;
                        oldValue=editArea.getText();
                    }
                    catch (IOException ioException)
                    {
                    }
                    }
                }
            }
        });
        /**
         * 保存的监听事件
         */
        fileMenu.add(fileMenu_Save);
        fileMenu_Save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editArea.requestFocus();
                if(isNewFile)
                {   String str=null;
                    JFileChooser fileChooser=new JFileChooser();
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    //fileChooser.setApproveButtonText("确定");
                    fileChooser.setDialogTitle("保存");
                    int result=fileChooser.showSaveDialog(MainTest.this);
                    if(result==JFileChooser.CANCEL_OPTION)
                    {   statusLabel.setText("您没有选择任何文件");
                        return;
                    }
                    File saveFileName=fileChooser.getSelectedFile();
                    if(saveFileName==null || saveFileName.getName().equals(""))
                    {   JOptionPane.showMessageDialog(MainTest.this,"不合法的文件名","不合法的文件名",JOptionPane.ERROR_MESSAGE);
                    }
                    else
                    {   try
                    {   FileWriter fw=new FileWriter(saveFileName);
                        BufferedWriter bfw=new BufferedWriter(fw);
                        bfw.write(editArea.getText(),0,editArea.getText().length());
                        bfw.flush();//刷新该流的缓冲
                        bfw.close();
                        isNewFile=false;
                        currentFile=saveFileName;
                        oldValue=editArea.getText();
                        setTitle(saveFileName.getName()+" - 记事本");
                        statusLabel.setText("当前打开文件："+saveFileName.getAbsoluteFile());
                    }
                    catch (IOException ioException)
                    {
                    }
                    }
                }
                else
                {   try
                {   FileWriter fw=new FileWriter(currentFile);
                    BufferedWriter bfw=new BufferedWriter(fw);
                    bfw.write(editArea.getText(),0,editArea.getText().length());
                    bfw.flush();
                    fw.close();
                }
                catch(IOException ioException)
                {
                }
                }
            }
        });

        fileMenu.add(fileMenu_SaveAs);
        fileMenu_SaveAs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editArea.requestFocus();
                String str=null;
                JFileChooser fileChooser=new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                //fileChooser.setApproveButtonText("确定");
                fileChooser.setDialogTitle("另存为");
                int result=fileChooser.showSaveDialog(MainTest.this);
                if(result==JFileChooser.CANCEL_OPTION)
                {   statusLabel.setText("　您没有选择任何文件");
                    return;
                }
                File saveFileName=fileChooser.getSelectedFile();
                if(saveFileName==null||saveFileName.getName().equals(""))
                {   JOptionPane.showMessageDialog(MainTest.this,"不合法的文件名","不合法的文件名",JOptionPane.ERROR_MESSAGE);
                }
                else
                {   try
                {   FileWriter fw=new FileWriter(saveFileName);
                    BufferedWriter bfw=new BufferedWriter(fw);
                    bfw.write(editArea.getText(),0,editArea.getText().length());
                    bfw.flush();
                    fw.close();
                    oldValue=editArea.getText();
                    setTitle(saveFileName.getName()+"  - 记事本");
                    statusLabel.setText("　当前打开文件:"+saveFileName.getAbsoluteFile());
                }
                catch(IOException ioException)
                {
                }
                }
            }
        });
        fileMenu.add(fileMenu_Exit);
        fileMenu_Exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int exitChoose=JOptionPane.showConfirmDialog(MainTest.this,"确定要退出吗?","退出提示",JOptionPane.OK_CANCEL_OPTION);
                if(exitChoose==JOptionPane.OK_OPTION)
                {   System.exit(0);
                }
                else
                {   return;
                }
            }
        });

        //向菜单条添加"编辑"菜单及菜单项
        menuBar.add(editMenu);
        /**
         * 撤销
         */
        editMenu.add(editMenu_Undo);
        editMenu_Undo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editArea.requestFocus();
                if(undo.canUndo())
                {
                    try
                    {
                    undo.undo();
                    }
                    catch (CannotUndoException ex)
                    {
                        ex.printStackTrace();
                    }
                    }
                if(!undo.canUndo())
                {
                    editMenu_Undo.setEnabled(false);
                }
            }
        });
        /**
         * 剪切
         */
        editMenu.add(editMenu_Cut);
        editMenu_Cut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editArea.requestFocus();
                String text=editArea.getSelectedText();
                StringSelection selection=new StringSelection(text);
                clipBoard.setContents(selection,null);
                editArea.replaceRange("",editArea.getSelectionStart(),editArea.getSelectionEnd());
                checkMenuStatus();//设置剪切，复制，粘帖，删除功能的可用性
            }
        });
        /**
         * 复制
         */
        editMenu.add(editMenu_Copy);
        editMenu_Copy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editArea.requestFocus();
                String text=editArea.getSelectedText();
                StringSelection selection=new StringSelection(text);
                clipBoard.setContents(selection,null);
                checkMenuStatus();
            }
        });
        /**
         * 粘贴
         */
        editMenu.add(editMenu_Paste);
        editMenu_Paste.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editArea.requestFocus();
                Transferable contents=clipBoard.getContents(this);
                if(contents==null)return;
                String text="";
                try
                {   text=(String)contents.getTransferData(DataFlavor.stringFlavor);
                }
                catch (Exception exception)
                {
                }
                editArea.replaceRange(text,editArea.getSelectionStart(),editArea.getSelectionEnd());
                checkMenuStatus();
            }
        });
        /**
         * 删除
         */
        editMenu.add(editMenu_Delete);
        editMenu_Delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editArea.requestFocus();
                editArea.replaceRange("",editArea.getSelectionStart(),editArea.getSelectionEnd());
                checkMenuStatus();
            }
        });
        /**
         * 查找
         */
        editMenu.add(editMenu_Find);
        editMenu_Find.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editArea.requestFocus();
                Find find=new Find(MainTest.this);
            }
        });
        /**
         * 查找下一个
         */
        editMenu.add(editMenu_FindNext);
        editMenu_FindNext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editArea.requestFocus();
                FindNext findNext=new FindNext(MainTest.this);
            }
        });
        /**
         * 替换
         */
        editMenu.add(editMenu_Replace);
        editMenu_Replace.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editArea.requestFocus();
                Replace replace=new Replace(MainTest.this);
            }
        });
        /**
         * 转到
         */
        editMenu.add(editMenu_GoTo);
        editMenu_GoTo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Goto g=new Goto(MainTest.this);
            }
        });
        /**
         * 全选
         */
        editMenu.add(editMenu_SelectAll);
        editMenu_SelectAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editArea.selectAll();
            }
        });
        /**
         * 时间选项
         */
        editMenu.add(editMenu_TimeDate);
        editMenu_TimeDate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editArea.requestFocus();
                Calendar rightNow=Calendar.getInstance();
                Date date=rightNow.getTime();
                editArea.insert(date.toString(),editArea.getCaretPosition());
            }
        });

        //向菜单条添加"格式"菜单及菜单项
        menuBar.add(formatMenu);
        /**
         * 自动换行
         */
        formatMenu.add(formatMenu_LineWrap);
        formatMenu_LineWrap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(formatMenu_LineWrap.getState())
                    editArea.setLineWrap(true);
                else
                    editArea.setLineWrap(false);
            }
        });

        /**
         * 字体设置
         */
        formatMenu.add(formatMenu_Font);
        formatMenu_Font.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editArea.requestFocus();
                FontSet fontSet =new FontSet(MainTest.this);
            }
        });


        /**
         * 状态菜单
         */
        menuBar.add(viewMenu);
        viewMenu.add(viewMenu_Status);
        viewMenu_Status.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(viewMenu_Status.getState())
                    jPanel.setVisible(true);
                else
                    jPanel.setVisible(false);
            }
        });


        //向窗口添加菜单条
        this.setJMenuBar(menuBar);

        //创建文本编辑区并添加滚动条
        editArea=new JTextArea(20,50);
        JScrollPane scroller=new JScrollPane(editArea);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(scroller,BorderLayout.CENTER);
        editArea.setWrapStyleWord(true);//设置单词在一行不足容纳时换行
        editArea.setLineWrap(true);//设置文本编辑区自动换行默认为true,即会"自动换行"
        //this.add(editArea,BorderLayout.CENTER);//向窗口添加文本编辑区
        oldValue=editArea.getText();//获取原文本编辑区的内容

        editArea.getDocument().addUndoableEditListener(undoHandler);
        editArea.getDocument().addDocumentListener(this);

        /**
         * 右键菜单
         */
        popupMenu=new JPopupMenu();
        popupMenu_Undo=new JMenuItem("撤销(U)");
        popupMenu_Cut=new JMenuItem("剪切(T)");
        popupMenu_Copy=new JMenuItem("复制(C)");
        popupMenu_Paste=new JMenuItem("粘帖(P)");
        popupMenu_Delete=new JMenuItem("删除(D)");
        popupMenu_SelectAll=new JMenuItem("全选(A)");

        popupMenu_Undo.setEnabled(false);

        /**
         * 右键菜单的撤销事件
         */
        popupMenu.add(popupMenu_Undo);
        popupMenu_Undo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editArea.requestFocus();
                if(undo.canUndo())
                {   try
                {   undo.undo();
                }
                catch (CannotUndoException ex)
                {
                    ex.printStackTrace();
                }
                }
                if(!undo.canUndo())
                {   editMenu_Undo.setEnabled(false);
                }
            }
        });

        /**
         * 右键菜单的剪切
         */
        popupMenu.add(popupMenu_Cut);
        popupMenu_Cut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editArea.requestFocus();
                String text=editArea.getSelectedText();
                StringSelection selection=new StringSelection(text);
                clipBoard.setContents(selection,null);
                editArea.replaceRange("",editArea.getSelectionStart(),editArea.getSelectionEnd());
                checkMenuStatus();//设置剪切，复制，粘帖，删除功能的可用性
            }
        });

        /**
         * 右键菜单的复制
         */
        popupMenu.add(popupMenu_Copy);
        popupMenu_Copy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editArea.requestFocus();
                String text=editArea.getSelectedText();
                StringSelection selection=new StringSelection(text);
                clipBoard.setContents(selection,null);
                checkMenuStatus();
            }
        });

        /**
         * 右键菜单的粘贴
         */
        popupMenu.add(popupMenu_Paste);
        popupMenu_Paste.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editArea.requestFocus();
                Transferable contents=clipBoard.getContents(this);
                if(contents==null)return;
                String text="";
                try
                {   text=(String)contents.getTransferData(DataFlavor.stringFlavor);
                }
                catch (Exception exception)
                {
                }
                editArea.replaceRange(text,editArea.getSelectionStart(),editArea.getSelectionEnd());
                checkMenuStatus();
            }
        });

        /**
         * 右键菜单的删除
         */
        popupMenu.add(popupMenu_Delete);
        popupMenu_Delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editArea.requestFocus();
                editArea.replaceRange("",editArea.getSelectionStart(),editArea.getSelectionEnd());
                checkMenuStatus(); //设置剪切、复制、粘贴、删除等功能的可用性
            }
        });
        /**
         * 全选
         */
        popupMenu.add(popupMenu_SelectAll);
        popupMenu_SelectAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editArea.selectAll();
            }
        });

        //文本编辑区注册右键菜单事件


        //文本编辑区注册右键菜单事件
        editArea.addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent e)
            {
                if(e.isPopupTrigger())//返回此鼠标事件是否为该平台的弹出菜单触发事件
                {
                    popupMenu.show(e.getComponent(),e.getX(),e.getY());//在组件调用者的坐标空间中的位置 X、Y 显示弹出菜单
                }
                    checkMenuStatus();//设置剪切，复制，粘帖，删除等功能的可用性
                    editArea.requestFocus();//编辑区获取焦点
        }
            public void mouseReleased(MouseEvent e)
            {
                if(e.isPopupTrigger())//返回此鼠标事件是否为该平台的弹出菜单触发事件
                {
                    popupMenu.show(e.getComponent(),e.getX(),e.getY());//在组件调用者的坐标空间中的位置 X、Y 显示弹出菜单
                }
                    checkMenuStatus();//设置剪切，复制，粘帖，删除等功能的可用性
                    editArea.requestFocus();//编辑区获取焦点
            }
        });//文本编辑区注册右键菜单事件结束

        //创建和添加状态栏

        statusLabel=new JLabel("状态栏");
        JLabel lineStatusLabel=new JLabel(" 第"+ lineNum + " 行, 第 " + columnNum+" 列  ");
        jPanel.add(statusLabel);
        jPanel.add(lineStatusLabel);
        editArea.addCaretListener(new CaretListener() {        //记录行数和列数
            public void caretUpdate(CaretEvent e) {
                JTextArea editArea = (JTextArea)e.getSource();
                try {
                    int caretpos = editArea.getCaretPosition();
                    System.out.print(caretpos);
                    lineNum = editArea.getLineOfOffset(caretpos);
                    System.out.println("|"+lineNum);
                    columnNum = caretpos - editArea.getLineStartOffset(lineNum);
                    lineStatusLabel.setText(" 第 " + (lineNum+1) + " 行, 第 " + (columnNum+1)+" 列  ");
                }
                catch(Exception ex) { }
            }});

        this.add(jPanel,BorderLayout.SOUTH);//向窗口添加状态栏标签

        //设置窗口在屏幕上的位置、大小和可见性
        this.setLocation(100,100);
        this.setSize(650,550);
        this.setVisible(true);
        //添加窗口监听器
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                exitWindowChoose();
            }
        });

        checkMenuStatus();
        editArea.requestFocus();
    }//构造函数Notepad结束

    /**
     * 设置编辑以及右键菜单是否可选
     */
    public void checkMenuStatus()
    {
        /**
         * 复制等键最重要的就是是否有选中的内容
         */
        String selectText=editArea.getSelectedText();

        if(selectText==null)
        {   editMenu_Cut.setEnabled(false);
            popupMenu_Cut.setEnabled(false);
            editMenu_Copy.setEnabled(false);
            popupMenu_Copy.setEnabled(false);
            editMenu_Delete.setEnabled(false);
            popupMenu_Delete.setEnabled(false);
//            editMenu_Find.setEnabled(false);
            editMenu_FindNext.setEnabled(false);
        }
        else
        {   editMenu_Cut.setEnabled(true);
            popupMenu_Cut.setEnabled(true);
            editMenu_Copy.setEnabled(true);
            popupMenu_Copy.setEnabled(true);
            editMenu_Delete.setEnabled(true);
            popupMenu_Delete.setEnabled(true);
            editMenu_FindNext.setEnabled(true);
        }

        /**
         * 粘贴键则是需要查看ClipBoard是否有值
         */
        Transferable contents=clipBoard.getContents(this);
        if(contents==null)
        {   editMenu_Paste.setEnabled(false);
            popupMenu_Paste.setEnabled(false);
        }
        else
        {   editMenu_Paste.setEnabled(true);
            popupMenu_Paste.setEnabled(true);
        }
    }

    /**
     * 关闭窗口时的判断
     */
    public void exitWindowChoose()
    {   editArea.requestFocus();
        String currentValue=editArea.getText();
        if(currentValue.equals(oldValue)==true)
        {   System.exit(0);
        }
        else
        {   int exitChoose=JOptionPane.showConfirmDialog(this,"您的文件尚未保存，是否保存？","退出提示",JOptionPane.YES_NO_CANCEL_OPTION);
            if(exitChoose==JOptionPane.YES_OPTION)
            {
                if(isNewFile)
                {
                    String str=null;
                    JFileChooser fileChooser=new JFileChooser();
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    fileChooser.setApproveButtonText("确定");
                    fileChooser.setDialogTitle("另存为");

                    int result=fileChooser.showSaveDialog(this);

                    if(result==JFileChooser.CANCEL_OPTION)
                    {   statusLabel.setText("　您没有保存文件");
                        return;
                    }

                    File saveFileName=fileChooser.getSelectedFile();

                    if(saveFileName==null||saveFileName.getName().equals(""))
                    {   JOptionPane.showMessageDialog(this,"不合法的文件名","不合法的文件名",JOptionPane.ERROR_MESSAGE);
                    }
                    else
                    {   try
                    {   FileWriter fw=new FileWriter(saveFileName);
                        BufferedWriter bfw=new BufferedWriter(fw);
                        bfw.write(editArea.getText(),0,editArea.getText().length());
                        bfw.flush();
                        fw.close();

                        isNewFile=false;
                        currentFile=saveFileName;
                        oldValue=editArea.getText();

                        this.setTitle(saveFileName.getName()+"  - 记事本");
                        statusLabel.setText("　当前打开文件:"+saveFileName.getAbsoluteFile());
                        //isSave=true;
                    }
                    catch(IOException ioException){
                    }
                    }
                }
                else
                {
                    try
                    {   FileWriter fw=new FileWriter(currentFile);
                        BufferedWriter bfw=new BufferedWriter(fw);
                        bfw.write(editArea.getText(),0,editArea.getText().length());
                        bfw.flush();
                        fw.close();
                        //isSave=true;
                    }
                    catch(IOException ioException){
                    }
                }
                System.exit(0);
                //if(isSave)System.exit(0);
                //else return;
            }
            else if(exitChoose==JOptionPane.NO_OPTION)
            {
            }

        }
    }

    /**
     * 重载DocumentListener接口中的方法
     * @param e
     */
    public void removeUpdate(DocumentEvent e)
    {
        editMenu_Undo.setEnabled(true);
    }
    public void insertUpdate(DocumentEvent e)
    {
        editMenu_Undo.setEnabled(true);
    }
    public void changedUpdate(DocumentEvent e)
    {
        editMenu_Undo.setEnabled(true);
    }

    /**
     * 重载撤销接口中的函数
     */
    class UndoHandler implements UndoableEditListener
    {
        public void undoableEditHappened(UndoableEditEvent uee)
        {
            undo.addEdit(uee.getEdit());
        }
    }


    /**
     * 主函数
     * @param args
     */
    public static void main(String args[])
    {   MainTest notepad=new MainTest();
        notepad.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//使用 System exit 方法退出应用程序
    }
}  