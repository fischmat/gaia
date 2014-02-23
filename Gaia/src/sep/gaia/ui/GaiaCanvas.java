package sep.gaia.ui;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLCapabilitiesImmutable;
import javax.media.opengl.GLException;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;


public class GaiaCanvas extends GLCanvas {
	private static final long serialVersionUID = 4264757805580255971L;
	
	private static GaiaCanvas instance;
	
	private GaiaCanvas(GLCapabilitiesImmutable capsReqUser) throws GLException {
		super(capsReqUser);
	}

	public static GaiaCanvas getInstance() throws IllegalStateException {
		if(instance == null) {
			throw new IllegalStateException("The canvas must be initialized with GL-capabilities.");
		}
		return instance;
	}
	
	public static GaiaCanvas getInstance(GLCapabilities capabilities) {
		if(instance == null) {
			instance = new GaiaCanvas(capabilities);
		}
		return instance;
	}
}
