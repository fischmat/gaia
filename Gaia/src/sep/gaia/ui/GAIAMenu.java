package sep.gaia.ui;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import sep.gaia.controller.menubar.AboutMenubarListener;
import sep.gaia.controller.menubar.CreditsClickedListener;
import sep.gaia.controller.menubar.ExitMenubarListener;
import sep.gaia.controller.menubar.HelpMenubarListener;
import sep.gaia.controller.menubar.SettingsMenubarListener;
import sep.gaia.controller.menubar.WeatherMenubarListener;
import sep.gaia.controller.menubar.WikiMenubarListener;
import sep.gaia.controller.toolbar.ComponentHideListener;
import sep.gaia.renderer.GaiaRenderer;
import sep.gaia.renderer.Mode3D;

/**
 * The menubar
 * 
 * @author Michael Mitterer
 */
public class GAIAMenu extends JMenuBar {

	public GAIAMenu(JFrame mainWindow, MarkerPanel markerPanel, PoiBar poiBar) {
		JMenuItem item1, item2,item3,item4,item5,item6,item7,item8,item9;
		JMenu menu;
		menu = new JMenu("Programm");
		item1 = new JMenuItem("Einstellungen");
		item1.addActionListener(new SettingsMenubarListener(mainWindow));
		menu.add(item1);
		item2 = new JMenuItem("Beenden");
		item2.addActionListener(new ExitMenubarListener(mainWindow));
		menu.add(item2);
		this.add(menu);
		menu = new JMenu("Ansicht");
		item3 = new JMenuItem("Pin-Leiste anzeigen");
		item3.addActionListener(new ComponentHideListener(markerPanel));
		menu.add(item3);
		item4 = new JMenuItem("POI-anzeigen");
		item4.addActionListener(new ComponentHideListener(poiBar));
		menu.add(item4);
		item5 = new JMenuItem("Wetter anzeigen");
		item5.addActionListener(new WeatherMenubarListener());
		menu.add(item5);
		item6 = new JMenuItem("Wikipedia Markierungen anzeigen");
		item6.addActionListener(new WikiMenubarListener());
		menu.add(item6);
		this.add(menu);
		menu = new JMenu("Über");
		item7 = new JMenuItem("Hilfe");
		item7.addActionListener(new HelpMenubarListener());
		menu.add(item7);
		item8 = new JMenuItem("Über");
		item8.addActionListener(new AboutMenubarListener(mainWindow));
		menu.add(item8);
		
		Mode3D mode3d = GaiaRenderer.getInstance().getMode3D();
		
		item9 = new JMenuItem("Credits");
		item9.addActionListener(new CreditsClickedListener(mode3d));
		menu.add(item9);
		this.add(menu);
	}

}
