/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ru.viljinsky.Column;
import ru.viljinsky.DataModule;
import ru.viljinsky.Dataset;

/**
 *
 * @author вадик
 */

class Link{
    Point[] points = new Point[4];
    TableProp prop1;
    TableProp prop2;
    boolean selected = false;
    public Link(TableProp prop1,TableProp prop2){
        this.prop1=prop1;
        this.prop2= prop2;
    }
    
    public boolean hitTest(int x,int y){
        Rectangle r;
        for (Point p:points){
            r= new Rectangle(p.x-2,p.y-2,5,5);
            if (r.contains(x,y))
                return true;
        }
        return false;
    }
    public void draw(Graphics g){
        Point p1 = prop1.getCenter();
        Point p4 = prop2.getCenter();
        Point p2 = new Point();
        Point p3 = new Point();
        
        Rectangle r = new Rectangle();
        r.x = Math.min(p1.x, p4.x);
        r.y = Math.min(p1.y,p4.y);
        r.width = Math.abs(p1.x-p4.x);
        r.height = Math.abs(p1.y-p4.y);

        
        p2 = new Point(p1.x+(p4.x-p1.x)/2,p1.y);
        p3 = new Point(p1.x+(p4.x-p1.x)/2,p4.y);
        
        if (!prop1.getBounds().contains(p2)){

            p2 = new Point(p1.x,p1.y-(p1.y-p4.y)/2);
            p3 = new Point(p4.x,p1.y-(p1.y-p4.y)/2);
        }
        
        g.setColor(Color.black);
        g.drawRect(p1.x-2,p1.y-2,5, 5);
        g.drawRect(p2.x-2,p2.y-2,5, 5);
        g.drawRect(p3.x-2,p3.y-2,5, 5);
        g.drawRect(p4.x-2,p4.y-2,5, 5);
        
        if (selected)
            g.setColor(Color.red);
        else
            g.setColor(Color.black);
        
        g.drawPolyline(new int[]{p1.x,p2.x,p3.x,p4.x}, new int[]{p1.y,p2.y,p3.y,p4.y}, 4);
        
        r=prop1.getBounds();
        Point[] pp = new Point[]{p2,p3,new Point(r.x,r.y),new Point(r.x+r.width,r.y)};
        points[0]=p1;
        points[1]=p2;
        points[2]=p3;
        points[3]=p4;
    }
}

class TableProp {
    Dataset dataset;
    Rectangle bounds = new Rectangle(0,0,150,200);
    protected boolean selected = false;
    
    public TableProp(Dataset dataset){
        this.dataset = dataset;
    }

    public Point getLocation(){
        return new Point(bounds.x,bounds.y);
    }
    
    public void setLocation(int x,int y){
        bounds.x=x;
        bounds.y=y;
    }
    
    public void setLocation(Point p){
        bounds.x=p.x;
        bounds.y=p.y;
    }
    
    public int getWidth(){
        return bounds.width;
    }
    
    public int getHeight(){
        return bounds.height;
    }
    
    public Rectangle getBounds(){
        return bounds;
    }
    public boolean hitTest(int x,int y){
        Rectangle r = this.getBounds();
        selected = r.contains(x,y);
        return selected;
    }
    
    public void darw(Graphics g) {
        g.setColor(new Color(220,250,220));
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        int x=bounds.x+10,y=bounds.y+10;
        int h;
        h = g.getFontMetrics().getHeight();
        g.setColor(Color.black);
        g.drawString(dataset.getTableName(), x, y);
        y+=h;
//        for (String columnName:dataset.getColumns()){
        for (Column column:dataset.getColumns()){
            g.drawString(column.getColumnName(), x, y);
            y+=h;
        }
        g.setColor(Color.BLUE);
        g.drawRect(bounds.x,bounds.y, bounds.width,bounds.height);
    }

    Point getCenter() {
        int x,y;
        Rectangle r=getBounds();
        x=r.x+r.width/2;
        y=r.y+r.height/2;
        return new Point(x,y);
    }
}

class Desktop extends JPanel{
    public List<TableProp> list = new ArrayList<>();
    public List<Link> links = new ArrayList<>();
    private int startX,startY;
    public Desktop(){
        setBackground(Color.white);
        setLayout(null);
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                int x=e.getX(),y=e.getY();
                startX = x;startY=y;
                for (TableProp prop:list){
                    prop.selected=false;
                }
                
                for (Link link:links){
                        link.selected=link.hitTest(x, y);
//                    link.selected=false;
//                    if (link.hitTest(x, y)){
////                        onLinkClick(link);
//                    }
                }
                
                for (int i=list.size()-1;i>=0;i--){
                    TableProp prop = list.get(i);
                    if (prop.hitTest(x,y)){
                        System.out.println(prop.dataset.getTableName());
                        onTablePropClick(prop);
                        break;
                    };
                }
                repaint();
            }
           
        });
        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                int x=e.getX(),y=e.getY();
                Point p;
                for (TableProp prop:list){
                    if (prop.selected){
                        p = prop.getLocation();
                        p.x +=x-startX;
                        p.y +=y-startY;
                        prop.setLocation(p);
                    }
                }
                startX=x;startY=y;
                repaint();
            }
            
        });
    }
    

    public void onTablePropClick(TableProp prop){
        Dataset dataset = prop.dataset;
        System.out.println(dataset.getTableName());
        try{
//            list.set(list.size()-1, prop);
            list.remove(prop);
            list.add(prop);
            
            for (Dataset ds:dataset.getForeignDataset()){
                System.out.println("  "+ds.getTableName());
        }} catch (Exception e){
            e.printStackTrace();
        }
    }
    
    public void onLinkClick(Link link){
        System.out.println("LinkClick");
    }
    
    public void addProp(TableProp prop){
        list.add(prop);
    }
    
    
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        for (TableProp prop:list)
            prop.darw(g);
        for (Link link:links){
            link.draw(g);
        }
    }
    
    public TableProp findProp(Dataset dataset){
        for (TableProp prop:list){
            if (prop.dataset.getTableName().equals(dataset.getTableName())){
                return prop;
            }
        }
        return null;
    }

    public void makeLink() throws Exception{
        for (TableProp prop:list){
            Dataset ds = prop.dataset;
            try{
                for (Dataset ds1:ds.getForeignDataset()){
                    TableProp prop2 = findProp(ds1);
                    if (prop2!=null){
                        links.add(new Link(prop, prop2));
                    }
                            
                }
            } catch (Exception e ){
                throw new Exception("makeLink ERROR:\n"+e.getMessage());
            }
        }
    }
    
}

public class ERRModel extends JFrame {
    
    DataModule dataModule = DataModule.getInstance();
    Desktop desctop = new Desktop();
    public ERRModel(){
        super("Main2");
        Container content = getContentPane();
        content.setPreferredSize(new Dimension(800, 600));
        content.setLayout(new BorderLayout());
        content.add(desctop);
        pack();
    }
    
    public void open(){
        TableProp tableProp;
        int x=0,y=0;
        try{
            for (String datasetName:dataModule.getTableNames()){
                Dataset dataset = dataModule.getDataset(datasetName);
                dataset.test();
                tableProp= new TableProp(dataset);
                tableProp.setLocation(x, y);
                x+=tableProp.getWidth()+20;
                if (x>getWidth()){
                    x=0;
                    y+=tableProp.getHeight()+20;
                }
                desctop.addProp(tableProp);
                System.out.println(dataset.getTableName());
                for (Dataset d:dataset.getForeignDataset()){
                    System.out.println("  "+d.getTableName());
                }
                }
                
            desctop.makeLink();
            desctop.repaint();
        } catch (Exception e){
            JOptionPane.showMessageDialog(rootPane, "Ошибка при выполнении firstOperation"+e.getMessage());
        }
    }
    
    
    public static void showModel(){
        ERRModel frame = new ERRModel();
        frame.pack();
        frame.setVisible(true);
        frame.open();
    }
    
    public static void main(String[] args) throws Exception{
        DataModule.getInstance().open();// getInsatnce().open();
        
        ERRModel frame = new ERRModel();
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.open();
    }
    
}
