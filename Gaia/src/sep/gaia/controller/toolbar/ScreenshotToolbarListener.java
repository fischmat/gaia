package sep.gaia.controller.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import sep.gaia.renderer.GaiaRenderer;
import sep.gaia.renderer.layer.ScreenshotLayer;

/**
 * This <code>Listener</code> launches creating a screenshot by setting a
 * specific flag in the <code>GaiaRenderer</code> class.
 * 
 * Performance issues are avoided by not directly manipulating the flag or the
 * number of screenshots to be taken.
 * 
 * A screenshot can only be requested, but the <code>GaiaRenderer</code> decides
 * when he is taking a screenshot.
 * 
 * @author Johannes Bauer
 */
public class ScreenshotToolbarListener implements ActionListener {

	private ScreenshotLayer layer;
	
	public ScreenshotToolbarListener(ScreenshotLayer layer) {
		this.layer = layer;
	}
	
	 public void playSound() {
	        try {
	            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("res/camera.wav"));
	            Clip clip = AudioSystem.getClip( );
	            clip.open(audioInputStream);
	            clip.start( );
	        }
	        catch(Exception e)  {
	            e.printStackTrace( );
	        }
	    }
	
	@Override
	public void actionPerformed(ActionEvent e) {

		playSound();
		GaiaRenderer.getInstance().requestScreenshot();
		
		if(layer != null) {
			layer.setActive(true);
		}
		
		Runnable disableRoutine = new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(400);
				} catch (InterruptedException e) { }
				
				layer.setActive(false);
			}
		};
		
		new Thread(disableRoutine).start();
	}

}
