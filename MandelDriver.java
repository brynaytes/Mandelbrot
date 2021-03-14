package mandelbrot;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.awt.event.*;

public class MandelDriver extends JFrame{
	public static int width = 600;
	public static int height = 400;
	public static int x=0,y=0;
	public static int imageOffsetX = width;
	public static int imageOffsetY = height;
	public static int zoom = 300;
	public static JLabel L;
	public static JFrame f;
	public static void main(String[] args) {
		new MandelDriver();
	}
	public static JLabel zoomLabel = new JLabel("Zoom:");
	public static JSlider zoomSlider = new JSlider(JSlider.HORIZONTAL,0,1000,100);
	public static JLabel iterationLabel = new JLabel("Iterations:");
	public static JTextField iterationField = new JTextField("50",4);

	public static JLabel screenWLabel = new JLabel("W:");
	public static JTextField screenW = new JTextField(width+"",4);	
	public static JLabel screenHLabel = new JLabel("H:");
	public static JTextField screenH = new JTextField(height+"",4);	
	
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
					imageOffsetX = imageOffsetX - 100;
					imageOffsetY = imageOffsetY + 100;
				}else if(x < width/2 && y<height/2) {
					imageOffsetX = imageOffsetX + 100;
					imageOffsetY = imageOffsetY + 100;
				}else if(x > width/2 && y>height/2) {
					imageOffsetX = imageOffsetX - 100;
					imageOffsetY = imageOffsetY - 100;
				}else if(x < width/2 && y>height/2) {
					imageOffsetX = imageOffsetX + 100;
					imageOffsetY = imageOffsetY - 100;
				}
				//	imageOffsetX =  (imageOffsetX + x) /2;
				//	imageOffsetY = (imageOffsetY + y)/2;
				//zoom = zoom + 100;
				}else if(SwingUtilities.isMiddleMouseButton(e))
				{
					zoom = zoom -zoomSlider.getValue();
				}else {
					zoom = zoom + zoomSlider.getValue();
				}
				
				L.setIcon( new ImageIcon(CreateImage()));

			}
		});
	}
	
	
	
	//returns mandelbrot set image
	public static BufferedImage CreateImage() {
		//System.out.println("making image");
		BufferedImage img = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		int temp=0;
		
		//Loop to create the actual set
		for(int y =0; y< height;y++) {
			for(int x=0;x < width; x++) {
				//y is originally 200
				temp = IsStable((double)(x-(imageOffsetX)) /zoom,(double)(y-imageOffsetY/2)/zoom,Integer.parseInt(iterationField.getText())) ;
				//temp = IsInside.IsStable((double)x /zoom,((double)y-200)/zoom)  * 5;
				
				if(temp > 255) temp=255;
				if(temp<0) temp =0;
			//	System.out.println(temp);
				img.setRGB(x,y,new Color(temp,temp,temp).getRGB());
			}
		}
		return img;
	}
	
	public static int IsStable(double x, double y,int iterations) {
		double constantx =x;
		double constanty= y;
		double xz=0;
		double yz=0;
		for(int i =1; i < iterations; i++) {
			xz = x * x - y * y;
			yz = 2 * x *y;
			x = xz + constantx;
			y=yz+constanty;
			if(Math.abs(y) > 2 || Math.abs(x) > 2 ) {return (int)((double)(iterations -i)*(255/(double)iterations));}
		}	
		if(	Math.abs(y) > 1 || Math.abs(x) > 1 )
			return 1;
		return 0;
	}
}
