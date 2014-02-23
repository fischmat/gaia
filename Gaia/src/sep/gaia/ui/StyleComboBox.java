package sep.gaia.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import sep.gaia.renderer.GaiaRenderer;
import sep.gaia.resources.ResourceMaster;
import sep.gaia.resources.tiles2d.Style;
import sep.gaia.resources.tiles2d.TileManager;
import sep.gaia.state.GLState;
import sep.gaia.state.State;
import sep.gaia.state.StateObserver;

/**
 * 
 * @author Johannes Bauer
 * 
 */
public class StyleComboBox extends JComboBox<String> implements StateObserver {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7116489781279936737L;

	private final GLState glState;

	private ComboBoxModel<String> styles3dModel;
	private ComboBoxModel<String> styles2dModel;

	private ComboBoxModel<String> activeModel;

	public StyleComboBox(final GLState glState) {
		super();
		this.glState = glState;

		// Init 2d styles.
		final TileManager tileManager = (TileManager) ResourceMaster
				.getInstance().getResourceManager(TileManager.MANAGER_LABEL);
		final List<Style> styles2d = tileManager.getAvailableStyles();
		String[] styles2dLabels = new String[styles2d.size()];
		for (int i = 0; i < styles2dLabels.length; i++) {
			styles2dLabels[i] = styles2d.get(i).getLabel();
		}
		styles2dModel = new DefaultComboBoxModel<>(styles2dLabels);

		// Init 3d styles.
		/* TODO */
		final List<String> styles3d = GaiaRenderer.getInstance().getMode3D()
				.getModel().getAvailableTextureNames();
		String[] styles3dLabels = new String[styles3d.size()];
		for (int i = 0; i < styles3dLabels.length; i++) {
			styles3dLabels[i] = styles3d.get(i);
		}
		styles3dModel = new DefaultComboBoxModel<>(styles3dLabels);

		// Set currently active Model according to the current mode.
		activeModel = (glState.is2DMode()) ? styles2dModel : styles3dModel;
		this.setModel(activeModel);

		// Add listener.
		this.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				StyleComboBox box = (StyleComboBox) e.getSource();
				String selectedItem = (String) box.getSelectedItem();

				if (glState.is2DMode()) {
					// Set style for 2d mode.
					for (Style current : styles2d) {
						if (current.getLabel().equals(selectedItem)) {
							tileManager.setCurrentStyle(current);
						}
					}
				} else {
					// Set style for 3d mode.
					for (String current : styles3d) {
						if (current.equals(selectedItem)) {
							GaiaRenderer.getInstance().getMode3D().getModel().setCurrentTexture(current);
						}
					}
				}
			}
		});
		;

		// Observe GLState.
		glState.register(this);
	}

	@Override
	public void onUpdate(State state) {
		// Set currently active Model according to the current mode.
		activeModel = (glState.is2DMode()) ? styles2dModel : styles3dModel;
		this.setModel(activeModel);
	}
}
