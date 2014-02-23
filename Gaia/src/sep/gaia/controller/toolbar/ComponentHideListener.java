package sep.gaia.controller.toolbar;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ComponentHideListener implements ActionListener {

	private Component component;

	public ComponentHideListener(Component component) {
		this.component = component;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Switch: visible <-> not visible
		component.setVisible(!component.isVisible());
	}
}