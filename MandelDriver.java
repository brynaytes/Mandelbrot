import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Instant;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.JButton;

import java.awt.event.*;

public class MandelDriver extends JFrame{
	public  int width = 600;
	public  int height = 400;
	public  int x=0,y=0;
	public  int r,g,b, rgb,incrementWhite, zoomval,maxIncrement;
	public  int imageOffsetX = width;
	public  int imageOffsetY = height;
	public  int zoom = 300;
	public  JLabel L;
	public  JFrame f;

	public  JLabel zoomLabel = new JLabel("Zoom:");
	public  JSlider zoomSlider = new JSlider(JSlider.HORIZONTAL,0,1000,100);
	public  JLabel iterationLabel = new JLabel("Iterations:");
	public  JTextField iterationField = new JTextField("50",4);

	public  JLabel screenWLabel = new JLabel("W:");
	public  JTextField screenW = new JTextField(width+"",4);	
	public  JLabel screenHLabel = new JLabel("H:");
	public  JTextField screenH = new JTextField(height+"",4);	
	public	JButton screenBsave=  new JButton("save");
	
	public BufferedImage currentImage;
	
	
	public static void main(String[] args) {
		new MandelDriver();
	}
	
	//Create frame and listen for clicks
	public MandelDriver() {
		f= this;
		JPanel controls = new JPanel();
		controls.add(zoomLabel);
		controls.add(zoomSlider);
		controls.add(iterationLabel);
		controls.add(iterationField);
		

		controls.add(screenWLabel);
		controls.add(screenW);
		controls.add(screenHLabel);
		controls.add(screenH);
		
		controls.add(screenBsave);
		screenBsave.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	File outputfile = new File(System.currentTimeMillis() + ".png");
	        	try {
					ImageIO.write(currentImage, "png", outputfile);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
	        }
	      });
	    
		setLayout(new BorderLayout());
		add(controls, BorderLayout.NORTH);
		controls.setPreferredSize(new Dimension(width,50));
		setSize(width,height+50);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		L = new JLabel(new ImageIcon(CreateImage()));
		add(L,BorderLayout.CENTER);
		//f = this;
		setVisible(true);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				try {
				if((f.getWidth() != Integer.parseInt(screenW.getText()) || f.getHeight() != Integer.parseInt(screenH.getText()) )&& Integer.parseInt(screenW.getText()) !=0 && Integer.parseInt(screenH.getText() )!=0){
					width = Integer.parseInt(screenW.getText());
					height = Integer.parseInt(screenH.getText());
					f.setSize(width,height+50);
					
				}
				//System.out.println("X: " + e.getX() +"   Y: "+ e.getY());
				if(SwingUtilities.isRightMouseButton(e)) {
				x = e.getX();
				y = e.getY();
				if(x > width/2 && y<height/2) {
					imageOffsetX = imageOffsetX - Math.abs(width/2 - e.getX());
					imageOffsetY = imageOffsetY +  Math.abs(height/2 - e.getY());
				}else if(x < width/2 && y<height/2) {
					imageOffsetX = imageOffsetX + Math.abs(width/2 - e.getX());
					imageOffsetY = imageOffsetY +  Math.abs(height/2 - e.getY());
				}else if(x > width/2 && y>height/2) {
					imageOffsetX = imageOffsetX - Math.abs(width/2 - e.getX());
					imageOffsetY = imageOffsetY -  Math.abs(height/2 - e.getY());
				}else if(x < width/2 && y>height/2) {
					imageOffsetX = imageOffsetX + Math.abs(width/2 - e.getX());
					imageOffsetY = imageOffsetY -   Math.abs(height/2 - e.getY());
				}
				
				}else if(SwingUtilities.isMiddleMouseButton(e))
				{
					System.out.println("middle click");
					zoom = zoom -zoomSlider.getValue();
					
				}else {
					zoomval =  zoomSlider.getValue();
					zoom = zoom + zoomval;
					if(zoomval < 100) zoomval = 100;
					imageOffsetX += 120* (zoomval/100);
					imageOffsetY -= 45* (zoomval/100) ;
				}
				
				
				L.setIcon( new ImageIcon(CreateImage()));
				}catch( NumberFormatException numEx) {
					JOptionPane.showMessageDialog(f,"The input fields can only contain numbers. Remove text or spaces.", "Error", JOptionPane.PLAIN_MESSAGE);
				}
			}
		});
	}
	
	
	
	//returns mandelbrot set image
	public  BufferedImage CreateImage() {
		BufferedImage img = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		maxIncrement = Integer.parseInt(iterationField.getText());
		incrementWhite = 5; //was 4
		
		if(incrementWhite < 1) {incrementWhite = 1;}
		
		
		//Thread to create the actual set
		 
		Thread t1 = new Thread( new splittingThread(0,0,width/2,height/2,img));
		t1.start();
		Thread t2 = new Thread( new splittingThread(width/2,0,width,height/2,img));
		t2.start();
		Thread t3 = new Thread( new splittingThread(0,height/2,width/2,height,img));
		t3.start();
		Thread t4 = new Thread( new splittingThread(width/2,height/2,width,height,img));
		t4.start();
		
		
		currentImage = img;
		return img;
	}
	
	public  int IsStable(double x, double y,int iterations) {
		double constantx =x;
		double constanty= y;
		double xz=0;
		double yz=0;
		for(int i =1; i < iterations; i++) {
			xz = x * x - y * y;
			yz = 2 * x *y;
			x = xz + constantx;
			y=yz+constanty;
			if(Math.abs(y) > 2 || Math.abs(x) > 2)return iterations - i;
		}	
		if(	Math.abs(y) > 1 || Math.abs(x) > 1 )
			return 1;
		return 0;
	}	
	
	public class splittingThread implements Runnable{
		public void run() {
			
		}
		public splittingThread(int x1, int y1, int x2, int y2, BufferedImage img) {
			int temp =0;
			for(int y =y1; y< y2;y++) {
				for(int x=x1;x < x2; x++) {
					temp = IsStable((double)(x-(imageOffsetX+100)) /zoom,(double)(y-imageOffsetY/2)/zoom,maxIncrement) ;
					temp *= incrementWhite;
			
					if(temp >  255) {
						temp = 255;
					}

					img.setRGB(x,y,new Color(temp,temp,temp).getRGB());
					
				}
			}
		}
	}	
}
