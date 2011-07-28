package cs211.labs.cgui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
/**
 * largely based on http://download.oracle.com/javase/tutorial/uiswing/layout/custom.html
 * Need to include logic to take into account animation in motion.
 * currently layout manager tramples on top of animation motion every time its called.
 * It appears that layoutContainer is called on mouse Enter events
 * @author rlum
 *
 */
public class PadLayout implements LayoutManager
{
	private int columns = 3;
	private int minWidth=0, minHeight = 0;
	private int prefWidth = 0, prefHeight =0;
	private boolean sizeUnknown = true;
	private int maxHt = 0, maxWt = 0;  // of all subcomponents.
	
	// consider adding a constructor that gets handed a method to stop 
	// animation timers.
	@Override
	public void addLayoutComponent(String arg0, Component arg1) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * this is the key method that determines where components go and their sizing.
	 * First half attempts to set container size based on keys taking their natural sizes.
	 * Second half compares the results of the fit and recalculates if the gap is bigger than fudge.
	 * It resorts to resizing components to fit within the container size that the layout manager allows
	 */
	@Override
	public void layoutContainer(Container parent) {
//		System.out.println("padlayout called");
		// need a way to freeze animation from here.
		if (sizeUnknown){
			setSizes(parent);
		}
		
		// make all buttons the same size
		int nComps = parent.getComponentCount();
		int row = 0;
    	for (int i = 0 ; i< nComps ; i++){
			Component c = parent.getComponent(i);
			if (c.isVisible()){
		       	if ((i%columns == 0 )&&(i/columns >= 1)){
		           	 row++;
		            }
		        c.setBounds(new Rectangle(
		            		 maxWt * (i%columns),
		            		 maxHt * (row%4),
		            		 maxWt,
		            		 maxHt));

		         c.setVisible(true);
		    }		
		}
        // all button sizes are now the same and locations set
//        System.out.println("layout - parent bounds = " + parent.getBounds());
//        System.out.println("button size = " + parent.getComponent(0).getBounds())	;
//        // it appears that we never get the size we want for our container on the first pass
        // a pure bottom up approach to sizing is overridden by layoutManager for some reason.
        // resizing buttons  if  delta is too great
        // height works out ok but width is alway half a key too narrow??
        // must be some explanation of missing logic I am not aware off
        int fudge = 5;
        
        if ((parent.getBounds().getWidth() - (parent.getComponent(0).getWidth() *columns) < fudge ) ||
        		(parent.getBounds().getWidth() - (parent.getComponent(0).getWidth() *columns) > fudge )||
        		(parent.getBounds().getHeight() - (parent.getComponent(0).getHeight() *row) < fudge ) ||
        		(parent.getBounds().getWidth() - (parent.getComponent(0).getHeight() *row) > fudge )
        ){
        	double newButWt = parent.getBounds().getWidth() / columns;
        	double newButHt = parent.getBounds().getHeight() / (row+1);
        	row = 0;
        	for (int i = 0 ; i < nComps ; i++){
        		Component c = parent.getComponent(i);
        		if(c.isVisible()){
        			if ((i%columns == 0 )&&(i/columns >= 1)){
        				row++;
        			}
        			c.setBounds(new Rectangle(
        					(int)(newButWt * (i%columns)),
        					(int)(newButHt * (row%4)),
        					(int) newButWt,
        					(int) newButHt));

        			c.setVisible(true);
        		}	
        	}
        	// note this logic completely ignores animation and rewrites original position.  
        	// this might be ok but would be better if we stopped animation, stored all positions
        	// recalculated new sizeing and positions and then restored them .   Otherwise
        	// any layouts done in mid animation will screw it up..
        }
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		Dimension dim = new Dimension (0,0);
		setSizes(parent);
		
		Insets insets = parent.getInsets();
		dim.width = minWidth + (insets.left + insets.right);
		dim.height = minHeight + insets.top + insets.bottom;
		sizeUnknown = false;
		
		return dim;
		
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		Dimension dim = new Dimension (0,0);
		setSizes(parent);
		
		Insets insets = parent.getInsets();
		dim.width = prefWidth + (insets.left + insets.right)*columns;
		dim.height = prefHeight + insets.top + insets.bottom;
		sizeUnknown = false;
		
		return dim;
	}

	@Override
	public void removeLayoutComponent(Component arg0) {

		
	}

	
	private void setSizes(Container  parent){
		int nComps = parent.getComponentCount();
		Dimension d = null;
		
		prefWidth = 0;
		prefHeight = 0;
		minWidth = 0;
		minHeight = 0;
		maxHt = 0;
		maxWt = 0;
		
		// get all buttons pref sizes and set them all to the same maximum in doLayout
		// here we just get the max and calculate 3column size for all buttons
		for (int i = 0 ; i< nComps ; i++){
			Component c = parent.getComponent(i);
			if (c.isVisible()){
				d = c.getPreferredSize();
				if (d.height>maxHt){
					maxHt = d.height;
				}
				if (d.width>maxWt){
					maxWt = d.width;
				}
			}		
		}
		prefWidth = maxWt * columns;
		minWidth = prefWidth;
		
		prefHeight = maxHt * ((nComps+columns)/columns);
		minHeight = prefHeight;	
	}
		
}
