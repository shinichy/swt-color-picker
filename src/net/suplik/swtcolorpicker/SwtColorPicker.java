package net.suplik.swtcolorpicker;

import java.awt.AWTException;
import java.awt.Robot;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;

/**
 * Simple SWT color picker.
 * 
 * TODO: Fix cursor shape.
 * 
 * @author shinichi
 * 
 */
public class SwtColorPicker extends ApplicationWindow {
	/** timer interval for checking color */
	private static final int TIMER_INTERVAL = 100;

	/** RGB labels */
	private Label rLabel = null;
	private Label gLabel = null;
	private Label bLabel = null;

	/** canvas for showing pixel color at mouse location */
	private Canvas canvas = null;

	/** RGB color at current mouse location */
	private RGB rgb = new RGB(0, 0, 0);

	private Runnable runnable;

	public SwtColorPicker() {
		super(null);
		// always on top
		setShellStyle(SWT.ON_TOP | SWT.CLOSE);
	}

	public void run() {
		setBlockOnOpen(true);
		open();
		Display.getCurrent().timerExec(-1, runnable);
		Display.getCurrent().dispose();
	}

	protected Control createContents(Composite parent) {
		// set window title
		parent.getShell().setText("SWT Color Picker");

		runnable = new Runnable() {
			@Override
			public void run() {
				checkColor();
				Display.getCurrent().timerExec(TIMER_INTERVAL, this);
			}
		};
		Display.getCurrent().timerExec(TIMER_INTERVAL, runnable);

		// root composite
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));

		// Composite for RGB labels
		Composite rgbComposite = new Composite(composite, SWT.NONE);
		rgbComposite.setLayout(new GridLayout(1, false));

		rLabel = new Label(rgbComposite, SWT.LEFT);
		GridData gd_rLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false,
				1, 1);
		gd_rLabel.widthHint = 50;
		rLabel.setLayoutData(gd_rLabel);

		gLabel = new Label(rgbComposite, SWT.LEFT);
		GridData gd_gLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false,
				1, 1);
		gd_gLabel.widthHint = 50;
		gLabel.setLayoutData(gd_gLabel);

		bLabel = new Label(rgbComposite, SWT.LEFT);
		GridData gd_bLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false,
				1, 1);
		gd_bLabel.widthHint = 50;
		bLabel.setLayoutData(gd_bLabel);

		canvas = new Canvas(composite, SWT.NO_BACKGROUND);
		GridData gd_canvas = new GridData(SWT.LEFT, SWT.CENTER, false, false,
				1, 1);
		gd_canvas.widthHint = 50;
		gd_canvas.heightHint = 50;
		canvas.setLayoutData(gd_canvas);
		canvas.addPaintListener(getPaintListener());
		return parent;
	}

	private PaintListener getPaintListener() {
		return new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				// Double buffering
				Image image = new Image(Display.getCurrent(),
						canvas.getBounds());
				GC gcImage = new GC(image);
				Color color = new Color(Display.getCurrent(), rgb.red,
						rgb.green, rgb.blue);
				gcImage.setBackground(color);
				gcImage.fillRectangle(canvas.getClientArea());
				e.gc.drawImage(image, 0, 0);

				color.dispose();
				image.dispose();
				gcImage.dispose();
			}

		};
	}

	/**
	 * Check pixel color at current mouse location. Then update RGB labels and
	 * canvas.
	 */
	private void checkColor() {
		try {
			Robot robot = new Robot();
			Point pos = Display.getCurrent().getCursorLocation();
			java.awt.Color color = robot.getPixelColor(pos.x, pos.y);
			rgb = new RGB(color.getRed(), color.getGreen(), color.getBlue());
		} catch (AWTException ex) {
			ex.printStackTrace();
		}
		rLabel.setText("R: " + Integer.toString(rgb.red));
		gLabel.setText("G: " + Integer.toString(rgb.green));
		bLabel.setText("B: " + Integer.toString(rgb.blue));
		canvas.redraw();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new SwtColorPicker().run();
	}

}
