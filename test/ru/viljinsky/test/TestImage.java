package ru.viljinsky.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import ru.viljinsky.forms.CommandListener;
import ru.viljinsky.forms.CommandMngr;
import ru.viljinsky.sqlite.DataModule;
import ru.viljinsky.sqlite.Dataset;
import ru.viljinsky.sqlite.Grid;
import java.awt.image.*;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import org.ibex.nestedvm.util.Seekable;
import ru.viljinsky.sqlite.Values;

/**
 *
 * @author вадик
 */

class ImagePanel extends JPanel{
    BufferedImage image = null;
    public ImagePanel() {
        setLayout(null);
        setPreferredSize(new Dimension(200, 200));
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g); //To change body of generated methods, choose Tools | Templates.
        if (image!=null){
            g.drawImage(image,0, 0, null);
        }
    }
    
    
    
}

public class TestImage extends JPanel implements CommandListener{
    Grid grid = new Grid(){

        @Override
        public void gridSelectionChange() {
            if (getSelectedRow()>=0){
                int row = getSelectedRow();
                Values v = getValues();
                try {
                    Object photo = v.getObject("photo");
                    System.out.println(photo);
                    
                    if (photo!=null){
                        System.out.println(photo.getClass().getName());
                        if (photo instanceof String){
                            
                            byte[] b = ((String)photo).getBytes();
                            readImage(b);
                        }
//                        byte[] b = (byte[])photo;
//                        readImage(b);
                    } else {
                        imagePanel.image=null;
                        imagePanel.repaint();
                    }
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                        
            }
        }
    };
    CommandMngr commands = new CommandMngr();
    ImagePanel imagePanel = new ImagePanel();
    JFileChooser fc = new JFileChooser(new File("."));
    
    
    public TestImage() throws Exception{
        super(new BorderLayout());
        setPreferredSize(new Dimension(800, 600));
        JScrollPane scrollPane=new JScrollPane(grid);
        add(scrollPane);
        Dataset dataset = DataModule.getDataset("teacher");
        dataset.open();
        grid.setRealNames(Boolean.TRUE);
        grid.setDataset(dataset);
        commands.setCommands(new String[]{"LOAD_FOTO"});
        commands.addCommandListener(this);
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JButton(commands.getAction("LOAD_FOTO")));
        add(panel,BorderLayout.PAGE_START);
        add(imagePanel,BorderLayout.WEST);
    }
    
    public static void main(String[] args) throws Exception{
        DataModule.open();
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new TestImage());
        frame.pack();
        frame.setVisible(true);
    }

    public void readImage(byte[] b){
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        try{
            imagePanel.image  = ImageIO.read(bais);
            imagePanel.repaint();
            
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void saveImage(BufferedImage image) throws Exception{
        Values v = grid.getValues();
        if (v==null)
            throw new Exception("HAS_NOT_SELECTED");
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] b= baos.toByteArray();
        v.setValue("photo", b);
        grid.setValues(v);
        
        
    }
    
    @Override
    public void doCommand(String command) {
        int retVal = fc.showOpenDialog(this);
        if (retVal==JFileChooser.APPROVE_OPTION){
           
            try{
                BufferedImage image = ImageIO.read(fc.getSelectedFile());
                imagePanel.image=image;
                saveImage(image);
                imagePanel.repaint();
                JOptionPane.showMessageDialog(this, "OK");

//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                ImageIO.write(image, "PNG", baos);
//                byte[] b = baos.toByteArray();
//                for (byte h:b)
//                    System.out.println(h+" ");
                
                
            } catch (Exception e){
            }
        }
    }

    @Override
    public void updateAction(Action action) {
        
    }
    
}
