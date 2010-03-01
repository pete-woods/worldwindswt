package gov.nasa.worldwind.swt;

import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.SceneController;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.cache.TextureCache;
import gov.nasa.worldwind.event.InputHandler;
import gov.nasa.worldwind.event.PositionListener;
import gov.nasa.worldwind.event.RenderingExceptionListener;
import gov.nasa.worldwind.event.RenderingListener;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.pick.PickedObjectList;
import gov.nasa.worldwind.util.PerformanceStatistic;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Set;
import java.util.Map.Entry;

@SuppressWarnings("serial")
public class WorldWindowEventSourceHack extends Component implements WorldWindow {
	private final WorldWindow slave;

	public WorldWindowEventSourceHack(WorldWindow slave) {
		this.slave = slave;

	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub

		return ((WorldWindowSWTGLCanvas) slave).getCanvas().getSize().x;
	}

	@Override
	public int getHeight() {
		return ((WorldWindowSWTGLCanvas) slave).getCanvas().getSize().y;
	}

	public void addPositionListener(PositionListener listener) {
		slave.addPositionListener(listener);
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		slave.addPropertyChangeListener(listener);
	}

	@Override
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		slave.addPropertyChangeListener(propertyName, listener);
	}

	public void addRenderingListener(RenderingListener listener) {
		slave.addRenderingListener(listener);
	}

	public void addSelectListener(SelectListener listener) {
		slave.addSelectListener(listener);
	}

	public AVList clearList() {
		return slave.clearList();
	}

	public AVList copy() {
		return slave.copy();
	}

	public void firePropertyChange(PropertyChangeEvent propertyChangeEvent) {
		slave.firePropertyChange(propertyChangeEvent);
	}

	@Override
	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		slave.firePropertyChange(propertyName, oldValue, newValue);
	}

	public Position getCurrentPosition() {
		return slave.getCurrentPosition();
	}

	public Set<Entry<String, Object>> getEntries() {
		return slave.getEntries();
	}

	public InputHandler getInputHandler() {
		return slave.getInputHandler();
	}

	public Model getModel() {
		return slave.getModel();
	}

	public PickedObjectList getObjectsAtCurrentPosition() {
		return slave.getObjectsAtCurrentPosition();
	}

	public Collection<PerformanceStatistic> getPerFrameStatistics() {
		return slave.getPerFrameStatistics();
	}

	public SceneController getSceneController() {
		return slave.getSceneController();
	}

	public String getStringValue(String key) {
		return slave.getStringValue(key);
	}

	public TextureCache getTextureCache() {
		return slave.getTextureCache();
	}

	public Object getValue(String key) {
		return slave.getValue(key);
	}

	public Collection<Object> getValues() {
		return slave.getValues();
	}

	public View getView() {
		return slave.getView();
	}

	public boolean hasKey(String key) {
		return slave.hasKey(key);
	}

	public void redraw() {
		slave.redraw();
	}

	public void redrawNow() {
		slave.redrawNow();
	}

	public Object removeKey(String key) {
		return slave.removeKey(key);
	}

	public void removePositionListener(PositionListener listener) {
		slave.removePositionListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		slave.removePropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		slave.removePropertyChangeListener(propertyName, listener);
	}

	public void removeRenderingListener(RenderingListener listener) {
		slave.removeRenderingListener(listener);
	}

	public void removeSelectListener(SelectListener listener) {
		slave.removeSelectListener(listener);
	}

	public void setInputHandler(InputHandler inputHandler) {
		slave.setInputHandler(inputHandler);
	}

	public void setModel(Model model) {
		slave.setModel(model);
	}

	public void setModelAndView(Model model, View view) {
		slave.setModelAndView(model, view);
	}

	public void setPerFrameStatisticsKeys(Set<String> keys) {
		slave.setPerFrameStatisticsKeys(keys);
	}

	public Object setValue(String key, Object value) {
		return slave.setValue(key, value);
	}

	public AVList setValues(AVList avList) {
		return slave.setValues(avList);
	}

	public void setView(View view) {
		slave.setView(view);
	}

	public void shutdown() {
		slave.shutdown();
	}

	@Override
	public void addRenderingExceptionListener(RenderingExceptionListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeRenderingExceptionListener(RenderingExceptionListener listener) {
		// TODO Auto-generated method stub

	}

}
