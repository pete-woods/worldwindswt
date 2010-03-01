package gov.nasa.worldwind.swt.stub;

import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.examples.util.StatusLayer;
import gov.nasa.worldwind.swt.WorldWindowSWTGLCanvas;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class WorldWindSWTGLStub {

	/**
	 * Starting point for the demonstration application.
	 * 
	 * @param args
	 *            ignored.
	 */
	public static void main(String[] args) {
		final Display display = new Display();
		Shell shell = new Shell(display);
		// shell.setSize(600, 300);
		shell.setLayout(new FillLayout());
		shell.setText("Test");

		GLData data = new GLData();
		data.doubleBuffer = true;
		WorldWindowSWTGLCanvas canvas = new WorldWindowSWTGLCanvas(shell, SWT.NO_BACKGROUND, data);
		Model m = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
		m.getLayers().add(new StatusLayer());
		canvas.setModel(m);

		// display.asyncExec(new Runnable() {
		// @Override
		// public void run() {
		// display.asyncExec(this);
		// }
		// });

		shell.setSize(640, 480);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		canvas.shutdown();
		shell.dispose();
		display.dispose();
	}

}
