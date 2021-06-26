import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

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
	public static void main(String[] args) {
		new MandelDriver();
	}
	public  JLabel zoomLabel = new JLabel("Zoom:");
	public  JSlider zoomSlider = new JSlider(JSlider.HORIZONTAL,0,1000,100);
	public  JLabel iterationLabel = new JLabel("Iterations:");
	public  JTextField iterationField = new JTextField("50",4);

	public  JLabel screenWLabel = new JLabel("W:");
	public  JTextField screenW = new JTextField(width+"",4);	
	public  JLabel screenHLabel = new JLabel("H:");
	public  JTextField screenH = new JTextField(height+"",4);	
	
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
				//	imageOffsetX =  (imageOffsetX + x) /2;
				//	imageOffsetY = (imageOffsetY + y)/2;
				//zoom = zoom + 100;
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
		//System.out.println("making image");
		BufferedImage img = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		int temp=0;
		maxIncrement = Integer.parseInt(iterationField.getText());
		incrementWhite = 5; //was 4
	//	if(maxIncrement > 100)incrementWhite = 3;else if(maxIncrement > 200)incrementWhite = 1;
		
		if(incrementWhite < 1) {incrementWhite = 1;}
		//Loop to create the actual set
	//	threaded t = new threaded();
		for(int y =0; y< height;y++) {
			for(int x=0;x < width; x++) {
				temp = IsStable((double)(x-(imageOffsetX+100)) /zoom,(double)(y-imageOffsetY/2)/zoom,maxIncrement) ;
				temp *= incrementWhite;
		
				if(temp >  255) {
					temp = 255;
				}
					
		 	/*	if(temp < (Integer.parseInt(iterationField.getText()))){
				v = 255;
				}else {
					v=0;
				}
				
				rgb = Color.HSBtoRGB(255 * temp / Integer.parseInt(iterationField.getText()), 255, v);
				r = (rgb >> 16) & 0xFF;
				g = (rgb >> 8) & 0xFF;
				b = (rgb & 0xFF);
			*/
				img.setRGB(x,y,new Color(temp,temp,temp).getRGB());
				
			}
		}
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
			//if(Math.abs(y) > 2 || Math.abs(x) > 2 ) {return (int)((double)(iterations -i)*(255/(double)iterations));}
		}	
		if(	Math.abs(y) > 1 || Math.abs(x) > 1 )
			return 1;
		return 0;
	}	
}
