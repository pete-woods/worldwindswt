package gov.nasa.worldwind.swt;

import gov.nasa.worldwind.awt.AWTInputHandler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Listener;

public class SWTWWInputHandler extends AWTInputHandler {
	public SWTWWInputHandler(SWTGLCanvasAutoDrawable canvas) {
		Listener listener = new ListenerImplementation(canvas, this, this, this);
		canvas.addListener(SWT.MouseMove, listener);
		canvas.addListener(SWT.MouseDown, listener);
		canvas.addListener(SWT.MouseMove, listener);
		canvas.addListener(SWT.MouseUp, listener);
		canvas.addListener(SWT.MouseWheel, listener);
	}

}
