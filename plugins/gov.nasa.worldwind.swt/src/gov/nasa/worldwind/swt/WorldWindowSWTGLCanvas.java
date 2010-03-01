package gov.nasa.worldwind.swt;

import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.SceneController;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.WorldWindowGLAutoDrawable;
import gov.nasa.worldwind.WorldWindowGLDrawable;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.awt.WorldWindowGLJPanel;
import gov.nasa.worldwind.cache.BasicTextureCache;
import gov.nasa.worldwind.cache.TextureCache;
import gov.nasa.worldwind.event.InputHandler;
import gov.nasa.worldwind.event.PositionEvent;
import gov.nasa.worldwind.event.PositionListener;
import gov.nasa.worldwind.event.RenderingEvent;
import gov.nasa.worldwind.event.RenderingExceptionListener;
import gov.nasa.worldwind.event.RenderingListener;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.pick.PickedObjectList;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.PerformanceStatistic;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLContext;
import javax.swing.event.EventListenerList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class WorldWindowSWTGLCanvas implements WorldWindow {
	private final WorldWindowGLDrawable wwd;

	private SWTGLCanvasAutoDrawable canvas;

	public class SWTWWGLAutoDrawable extends WorldWindowGLAutoDrawable {
		private final EventListenerList overrideEventListeners = new EventListenerList();

		@Override
		public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int w, int h) {
			// avoid the cast to java.awt.Component in the parent class
		}

		@Override
		public void addRenderingListener(RenderingListener listener) {
			this.overrideEventListeners.add(RenderingListener.class, listener);
		}

		@Override
		public void removeRenderingListener(RenderingListener listener) {
			this.overrideEventListeners.remove(RenderingListener.class, listener);
		}

		@Override
		protected void callRenderingListeners(RenderingEvent event) {
			for (RenderingListener listener : this.overrideEventListeners.getListeners(RenderingListener.class)) {
				listener.stageChanged(event);
			}
		}

		@Override
		public void addPositionListener(PositionListener listener) {
			this.overrideEventListeners.add(PositionListener.class, listener);
		}

		@Override
		public void removePositionListener(PositionListener listener) {
			this.overrideEventListeners.remove(PositionListener.class, listener);
		}

		@Override
		protected void callPositionListeners(final PositionEvent event) {
			for (PositionListener listener : overrideEventListeners.getListeners(PositionListener.class)) {
				listener.moved(event);
			}
		}

		@Override
		public void addSelectListener(SelectListener listener) {
			this.overrideEventListeners.add(SelectListener.class, listener);
		}

		@Override
		public void removeSelectListener(SelectListener listener) {
			this.overrideEventListeners.remove(SelectListener.class, listener);
		}

		@Override
		protected void callSelectListeners(final SelectEvent event) {
			for (SelectListener listener : overrideEventListeners.getListeners(SelectListener.class)) {
				listener.selected(event);
			}
		}
	}

	// FIXME: this needs to allow WorldWind sharable components (as
	// with WorldWindGLCanvas)
	public WorldWindowSWTGLCanvas(final Composite parent, final int style, final GLData data) {

		this.canvas = new SWTGLCanvasAutoDrawable(parent, style | SWT.NO_BACKGROUND, data);

		try {
			this.wwd = new SWTWWGLAutoDrawable();
			this.wwd.initDrawable(canvas);
			this.wwd.initTextureCache(createTextureCache());
			this.createView();
			this.createDefaultInputHandler();
		} catch (Exception e) {
			String message = Logging.getMessage("Awt.WorldWindowGLSurface.UnabletoCreateWindow");
			Logging.logger().severe(message);
			throw new WWRuntimeException(message, e);
		}
	}

	public WorldWindowSWTGLCanvas(final Composite parent, final int style, final GLData data, WorldWindow worldWindow) {

		GLContext context = null;
		if (worldWindow instanceof WorldWindowSWTGLCanvas) {
			WorldWindowSWTGLCanvas worldWindowSWTGLCanvas = (WorldWindowSWTGLCanvas) worldWindow;
			SWTGLCanvasAutoDrawable swtglCanvasAutoDrawable = (SWTGLCanvasAutoDrawable) worldWindowSWTGLCanvas
					.getCanvas();
			context = swtglCanvasAutoDrawable.getContext();
		} else if (worldWindow instanceof WorldWindowGLCanvas) {
			context = ((WorldWindowGLCanvas) worldWindow).getContext();
		} else if (worldWindow instanceof WorldWindowGLJPanel) {
			context = ((WorldWindowGLJPanel) worldWindow).getContext();
		}
		this.canvas = new SWTGLCanvasAutoDrawable(parent, style | SWT.NO_BACKGROUND, data, context);

		try {
			this.wwd = new SWTWWGLAutoDrawable();
			this.wwd.initDrawable(canvas);
			this.wwd.initTextureCache(createTextureCache());
			this.createView();
			this.createDefaultInputHandler();
		} catch (Exception e) {
			String message = Logging.getMessage("Awt.WorldWindowGLSurface.UnabletoCreateWindow");
			Logging.logger().severe(message);
			throw new WWRuntimeException(message, e);
		}
	}

	public boolean isDisposed() {
		return canvas.isDisposed();
	}

	public void dispose() {
		shutdown();
	}

	// ////////////////////// COPIED FROM WorldWindowGLJPanel

	public void shutdown() {
		this.wwd.shutdown();
	}

	private void createView() {
		this.setView((View) WorldWind.createConfigurationComponent(AVKey.VIEW_CLASS_NAME));
	}

	private void createDefaultInputHandler() {
		// this.setInputHandler((InputHandler) WorldWind
		// .createConfigurationComponent(AVKey.INPUT_HANDLER_CLASS_NAME));
		setInputHandler(new SWTWWInputHandler(canvas));
	}

	@Override
	public InputHandler getInputHandler() {
		return this.wwd.getInputHandler();
	}

	@Override
	public void setInputHandler(InputHandler inputHandler) {
		if (this.wwd.getInputHandler() != null)
			this.wwd.getInputHandler().setEventSource(null); // remove this
		// window as a
		// source of
		// events

		this.wwd.setInputHandler(inputHandler);
		if (inputHandler != null)
			inputHandler.setEventSource(new WorldWindowEventSourceHack(this));
	}

	@Override
	public SceneController getSceneController() {
		return this.wwd.getSceneController();
	}

	@Override
	public TextureCache getTextureCache() {
		return this.wwd.getTextureCache();
	}

	@Override
	public void redrawNow() {
		this.wwd.redrawNow();
	}

	@Override
	public void setModel(Model model) {
		// null models are permissible
		this.wwd.setModel(model);
	}

	@Override
	public Model getModel() {
		return this.wwd.getModel();
	}

	@Override
	public void setView(View view) {
		// null views are permissible
		if (view != null)
			this.wwd.setView(view);
	}

	@Override
	public View getView() {
		return this.wwd.getView();
	}

	@Override
	public void setModelAndView(Model model, View view) { // null models/views
		// are permissible
		this.setModel(model);
		this.setView(view);
	}

	@Override
	public void addRenderingListener(RenderingListener listener) {
		this.wwd.addRenderingListener(listener);
	}

	@Override
	public void removeRenderingListener(RenderingListener listener) {
		this.wwd.removeRenderingListener(listener);
	}

	@Override
	public void addSelectListener(SelectListener listener) {
		this.wwd.getInputHandler().addSelectListener(listener);
		this.wwd.addSelectListener(listener);
	}

	@Override
	public void removeSelectListener(SelectListener listener) {
		this.wwd.getInputHandler().removeSelectListener(listener);
		this.wwd.removeSelectListener(listener);
	}

	@Override
	public void addPositionListener(PositionListener listener) {
		this.wwd.addPositionListener(listener);
	}

	@Override
	public void removePositionListener(PositionListener listener) {
		this.wwd.removePositionListener(listener);
	}

	@Override
	public Position getCurrentPosition() {
		return this.wwd.getCurrentPosition();
	}

	@Override
	public PickedObjectList getObjectsAtCurrentPosition() {
		return this.wwd.getSceneController() != null ? this.wwd.getSceneController().getPickedObjectList() : null;
	}

	@Override
	public Object getValue(String key) {
		return this.wwd.getValue(key);
	}

	@Override
	public Collection<Object> getValues() {
		return this.wwd.getValues();
	}

	@Override
	public Set<Map.Entry<String, Object>> getEntries() {
		return this.wwd.getEntries();
	}

	@Override
	public String getStringValue(String key) {
		return this.wwd.getStringValue(key);
	}

	@Override
	public boolean hasKey(String key) {
		return this.wwd.hasKey(key);
	}

	@Override
	public Object removeKey(String key) {
		return this.wwd.removeKey(key);
	}

	@Override
	public AVList copy() {
		return this.wwd.copy();
	}

	@Override
	public AVList clearList() {
		return this.wwd.clearList();
	}

	@Override
	public void setPerFrameStatisticsKeys(Set<String> keys) {
		this.wwd.setPerFrameStatisticsKeys(keys);
	}

	@Override
	public Collection<PerformanceStatistic> getPerFrameStatistics() {
		return this.wwd.getPerFrameStatistics();
	}

	private static final long FALLBACK_TEXTURE_CACHE_SIZE = 60000000;

	private static TextureCache createTextureCache() {
		long cacheSize = Configuration.getLongValue(AVKey.TEXTURE_CACHE_SIZE, FALLBACK_TEXTURE_CACHE_SIZE);
		return new BasicTextureCache((long) (0.8 * cacheSize), cacheSize);
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		wwd.addPropertyChangeListener(listener);
	}

	@Override
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		wwd.addPropertyChangeListener(propertyName, listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		wwd.removePropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		wwd.removePropertyChangeListener(propertyName, listener);
	}

	@Override
	public void redraw() {
		canvas.repaint();
	}

	@Override
	public void firePropertyChange(PropertyChangeEvent arg0) {
		wwd.firePropertyChange(arg0);
	}

	@Override
	public void firePropertyChange(String propertyName, Object index, Object arg2) {
		wwd.firePropertyChange(propertyName, index, arg2);
	}

	@Override
	public void addRenderingExceptionListener(RenderingExceptionListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeRenderingExceptionListener(RenderingExceptionListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public AVList setValues(AVList avList) {
		return this.wwd.setValues(avList);
	}

	@Override
	public Object setValue(String key, Object value) {
		return wwd.setValue(key, value);
	}

	public Canvas getCanvas() {

		return canvas;
	}

	public boolean setFocus() {
		return getCanvas().setFocus();
	}

}
