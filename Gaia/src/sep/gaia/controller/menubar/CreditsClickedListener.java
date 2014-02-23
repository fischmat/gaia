package sep.gaia.controller.menubar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import sep.gaia.renderer.Mode3D;
import sep.gaia.resources.mode3d.Credits;
import sep.gaia.resources.mode3d.CreditsAnimator;

public class CreditsClickedListener implements ActionListener {

	
	private Mode3D mode;

	private CreditsAnimator animator;
	
	public CreditsClickedListener(Mode3D mode) {
		super();
		this.mode = mode;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(mode != null) {
			if(animator == null || (animator != null && animator.hasFinished())) {
				// Create the credits and an animator:
				Credits credits = new Credits();
				animator = new CreditsAnimator(credits);
				
				// Register them by the rendering instance and start the animation.
				mode.setCredits(credits);
				animator.start();
			}
		}
	}
	
}
