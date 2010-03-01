package gov.nasa.worldwind.swt;

import gov.nasa.worldwind.util.Logging;

import java.awt.event.ComponentListener;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyListener;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLEventListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class SWTGLCanvasAutoDrawable extends GLCanvas implements GLAutoDrawable {

	private GLContext context;

	private Point lastSize;

	private boolean doneInit;

	private boolean shuttingDown;

	public void shutdown() {
		shuttingDown = true;
		display(); // Invokes a repaint, where the rest of the shutdown
		// work is done.
	}

	protected void doShutdown() {
		// shuttingDown = false;
	}

	public SWTGLCanvasAutoDrawable(final Composite parent, final int style, final GLData data, GLContext context) {
		super(parent, style | SWT.NO_BACKGROUND, data);

		setCurrent();
		this.context = context;

		this.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				display();
			}
		});

		parent.addListener(SWT.RESIZE, new Listener() {
			@Override
			public void handleEvent(Event event) {
				notifyReshape();
			}

		});
	}

	public SWTGLCanvasAutoDrawable(final Composite parent, final int style, final GLData data) {
		super(parent, style | SWT.NO_BACKGROUND, data);

		setCurrent();
		context = GLDrawableFactory.getFactory().createExternalGLContext();

		this.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				display();
			}
		});

		parent.addListener(SWT.RESIZE, new Listener() {
			@Override
			public void handleEvent(Event event) {
				notifyReshape();
			}

		});
	}

	private void runGL(Runnable runnable) {
		setCurrent();
		context.makeCurrent();

		try {
			runnable.run();
		} finally {
			context.release();
		}
	}

	private List<GLEventListener> glEventListeners = new ArrayList<GLEventListener>();

	private void notifyInit() {
		if (!doneInit) {
			doneInit = true;
			runGL(new Runnable() {
				@Override
				public void run() {
					for (GLEventListener l : glEventListeners) {
						l.init(SWTGLCanvasAutoDrawable.this);
					}
				}
			});
		}
	}

	private void notifyReshape() {
		runGL(new Runnable() {
			@Override
			public void run() {
				context.getGL().glViewport(0, 0, getWidth(), getHeight());
				for (GLEventListener l : glEventListeners) {
					l.reshape(SWTGLCanvasAutoDrawable.this, 0, 0, getWidth(), getHeight());
				}
			}
		});
	}

	@Override
	public void addGLEventListener(GLEventListener arg0) {
		glEventListeners.add(arg0);
	}

	@Override
	public void removeGLEventListener(GLEventListener arg0) {
		glEventListeners.remove(arg0);
	}

	@Override
	public void display() {
		if (this.shuttingDown) {
			try {
				this.doShutdown();
			} catch (Exception e) {
				Logging.logger().log(Level.SEVERE,
						Logging.getMessage("SWTWorldWindowGLCanvas.ExceptionWhileShuttingDownWorldWindow"), e);
			}
			return;
		}

		notifyInit();
		Point newSize = getSize();
		if (!newSize.equals(lastSize)) {
			lastSize = newSize;
			notifyReshape();
		}

		runGL(new Runnable() {
			@Override
			public void run() {
				for (GLEventListener l : glEventListeners) {
					l.display(SWTGLCanvasAutoDrawable.this);
				}
				// GL gl = context.getGL();
				// float f = (float) Math.sin(System.currentTimeMillis()/100.0);
				// gl.glClearColor(.3f, .5f, f, 1.0f);
				// gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
				//				
				// swapBuffers();
			}
		});
	}

	@Override
	public boolean getAutoSwapBufferMode() {
		return false;
	}

	@Override
	public void setAutoSwapBufferMode(boolean arg0) {
	}

	@Override
	public GLContext getContext() {
		return context;
	}

	@Override
	public GL getGL() {
		return context.getGL();
	}

	@Override
	public void repaint() {
		Display d = Display.getCurrent();
		if (d != null) {
			d.asyncExec(new Runnable() {
				@Override
				public void run() {
					if (!isDisposed()) {
						redraw();
					}
				}
			});
		}
	}

	@Override
	public void setGL(GL arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public GLContext createContext(GLContext arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public GLCapabilities getChosenGLCapabilities() {
		return context.getGLDrawable().getChosenGLCapabilities();
	}

	@Override
	public void swapBuffers() {
		super.swapBuffers();
	}

	@Override
	public int getHeight() {
		return getSize().y;
	}

	@Override
	public int getWidth() {
		return getSize().x;
	}

	@Override
	public void setRealized(boolean arg0) {
	}

	@Override
	public void addComponentListener(ComponentListener arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addFocusListener(FocusListener arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addHierarchyBoundsListener(HierarchyBoundsListener arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addHierarchyListener(HierarchyListener arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addInputMethodListener(InputMethodListener arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addKeyListener(KeyListener arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addMouseListener(MouseListener arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addMouseMotionListener(MouseMotionListener arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addMouseWheelListener(MouseWheelListener arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeComponentListener(ComponentListener arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeFocusListener(FocusListener arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeHierarchyBoundsListener(HierarchyBoundsListener arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeHierarchyListener(HierarchyListener arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeInputMethodListener(InputMethodListener arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeKeyListener(KeyListener arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeMouseListener(MouseListener arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeMouseMotionListener(MouseMotionListener arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeMouseWheelListener(MouseWheelListener arg0) {
		throw new UnsupportedOperationException();
	}

	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}

	@Override
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(propertyName, listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(propertyName, listener);
	}

}
