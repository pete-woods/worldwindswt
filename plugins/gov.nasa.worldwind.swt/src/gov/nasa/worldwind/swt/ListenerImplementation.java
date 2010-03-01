package gov.nasa.worldwind.swt;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class ListenerImplementation implements Listener {
	private static final JPanel DUMMY_PANEL = new JPanel();

	private static final int MOUSE_NONE = 0;

	private static final int MOUSE_DOWN = 0x1;

	private static final int MOUSE_UP = 0x10;

	private static final int MOUSE_CLICKED = 0x100;

	private static final int MOUSE_DOUBLE_CLICKED = 0x1000;

	private static final int MOUSE_MOVED = 0x10000;

	private static final int MOUSE_DRAGGED = 0x100000;

	private static final int CLICKED = 1;

	private Control control;

	private boolean mouseDragging;

	private MouseListener mouseListener;

	private MouseMotionListener mouseMotionListener;

	private MouseWheelListener mouseWheelListener;

	public ListenerImplementation(Control control, MouseListener mouseListener,
			MouseMotionListener mouseMotionListener, MouseWheelListener mouseWheelListener) {
		this.control = control;
		this.mouseListener = mouseListener;
		this.mouseMotionListener = mouseMotionListener;
		this.mouseWheelListener = mouseWheelListener;
	}

	public void handleEvent(Event event) {
		Point point = control.toDisplay(event.x, event.y);
		switch (event.type) {
		case SWT.MouseMove:
			if ((event.stateMask & SWT.BUTTON_MASK) != 0) {
				MouseEvent awtMouseEvent = toAWTMouseEvent(event, point.x, point.y, MOUSE_DRAGGED);
				mouseMotionListener.mouseDragged(awtMouseEvent);
				mouseDragging = true;
			} else {
				mouseMotionListener.mouseMoved(toAWTMouseEvent(event, point.x, point.y, MOUSE_MOVED));
			}
			break;
		case SWT.MouseDown:
			mouseListener.mousePressed(toAWTMouseEvent(event, point.x, point.y, MOUSE_DOWN));
			break;
		case SWT.MouseUp:
			if (mouseDragging) {
				mouseListener.mouseReleased(toAWTMouseEvent(event, point.x, point.y, MOUSE_UP));
			} else {
				mouseListener.mouseReleased(toAWTMouseEvent(event, point.x, point.y, MOUSE_UP));
				mouseListener.mouseClicked(toAWTMouseEvent(event, point.x, point.y, MOUSE_CLICKED));
			}
			mouseDragging = false;
			break;
		case SWT.MouseDoubleClick:
			mouseListener.mouseClicked(toAWTMouseEvent(event, point.x, point.y, MOUSE_DOUBLE_CLICKED));
			break;
		case SWT.MouseWheel:
			mouseWheelListener.mouseWheelMoved((MouseWheelEvent) toAWTMouseEvent(event, point.x, point.y, MOUSE_NONE));
			break;
		}
	}

	private MouseEvent toAWTMouseEvent(Event event, int x, int y, int type) {
		int modifiers = 0;

		if ((event.stateMask & SWT.ALT) != 0) {
			modifiers |= InputEvent.ALT_DOWN_MASK;
		}
		if ((event.stateMask & SWT.CONTROL) != 0) {
			modifiers |= InputEvent.CTRL_DOWN_MASK;
		}
		if ((event.stateMask & SWT.SHIFT) != 0) {
			modifiers |= InputEvent.SHIFT_DOWN_MASK;
		}
		if ((event.stateMask & SWT.BUTTON1) != 0) {
			modifiers |= InputEvent.BUTTON1_DOWN_MASK;
		}
		if ((event.stateMask & SWT.BUTTON2) != 0) {
			modifiers |= InputEvent.BUTTON2_DOWN_MASK;
		}
		if ((event.stateMask & SWT.BUTTON3) != 0) {
			modifiers |= InputEvent.BUTTON3_DOWN_MASK;
		}

		int button = MouseEvent.NOBUTTON;
		switch (event.button) {
		case 1:
			button = MouseEvent.BUTTON1;
			break;
		case 2:
			button = MouseEvent.BUTTON2;
			break;
		case 3:
			button = MouseEvent.BUTTON3;
			break;
		// Can't handle more than 3 buttons in Swing?
		}

		switch (event.type) {
		case SWT.MouseMove:
			if (button == MouseEvent.NOBUTTON) {

				return new MouseEvent(DUMMY_PANEL, MouseEvent.MOUSE_MOVED, event.time, modifiers, event.x, event.y, x,
						y, event.count, false, 0);
			} else {
				MouseEvent mouseEvent = new MouseEvent(DUMMY_PANEL, MouseEvent.MOUSE_DRAGGED, event.time, modifiers,
						event.x, event.y, x, y, event.count, false, 0);
				return mouseEvent;
			}
		case SWT.MouseDown:
			return new MouseEvent(DUMMY_PANEL, MouseEvent.MOUSE_PRESSED, event.time, modifiers, event.x, event.y, x, y,
					event.count, false, button);
		case SWT.MouseUp:
			if (type == CLICKED) {
				return new MouseEvent(DUMMY_PANEL, MouseEvent.MOUSE_CLICKED, event.time, modifiers, event.x, event.y,
						x, y, event.count, false, button);
			} else {
				return new MouseEvent(DUMMY_PANEL, MouseEvent.MOUSE_RELEASED, event.time, modifiers, event.x, event.y,
						x, y, event.count, false, button);
			}
		case SWT.MouseDoubleClick:
			return new MouseEvent(DUMMY_PANEL, MouseEvent.MOUSE_CLICKED, 0, modifiers, event.x, event.y, x, y,
					event.count, false, button);
		case SWT.MouseWheel:
			return new MouseWheelEvent(DUMMY_PANEL, MouseEvent.MOUSE_WHEEL, 0, modifiers, event.x, event.y,
					event.count, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, 1, event.count < 0 ? 1 : -1);
		default:
			throw new IllegalArgumentException();
		}
	}

}
