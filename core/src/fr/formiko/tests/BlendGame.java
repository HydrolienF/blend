package fr.formiko.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * Simple example of draw only visible area of all map texture.
 */
public class BlendGame extends ApplicationAdapter {
	private SpriteBatch spriteBatch;
	private Texture donut;
	private List<Integer> times = new ArrayList<>();
	private Pixmap pxmVisible;
	private Set<VisibleArea> visibleAreas = new HashSet<VisibleArea>();
	private List<Point> donutsPoints = new ArrayList<Point>();

	// store initialized by lazy data.
	private Map<Integer, Texture> visibleCircleTextures;
	private Map<Integer, FrameBuffer> frameBuffers;
	private Rectangle rectangle1;
	private Rectangle rectangle2;

	@Override
	public void create() {
		visibleCircleTextures = new HashMap<Integer, Texture>();
		frameBuffers = new HashMap<Integer, FrameBuffer>();

		// donut = new Texture("donut.png");

		// Test with aphids as in Kokcinelo.
		Pixmap aphidPixmap1 = new Pixmap(new FileHandle("aphid.png"));
		Pixmap aphidPixmap2 = new Pixmap(80, 80, aphidPixmap1.getFormat());
		aphidPixmap2.drawPixmap(aphidPixmap1, 0, 0, aphidPixmap1.getWidth(), aphidPixmap1.getHeight(), 0, 0, aphidPixmap2.getWidth(),
				aphidPixmap2.getHeight());
		donut = new Texture(aphidPixmap2);

		// frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, visible.getWidth(), visible.getHeight(), false);

		spriteBatch = new SpriteBatch();

		visibleAreas.add(new VisibleArea(20, 20, 150));
		visibleAreas.add(new VisibleArea(200, 280, 100));
		visibleAreas.add(new VisibleArea(-100, 350, 100));


		donutsPoints.add(new Point(0, 0));
		donutsPoints.add(new Point(30, 0));
		donutsPoints.add(new Point(-60, 0));
		// lots of invisible donuts
		// for (int i = 0; i < 1000000; i++) {
		// donutsPoints.add(new Point(1000 + i, 1000 + i));
		// }
		// lots of visible donuts
		for (int i = 0; i < 1000; i++) {
			donutsPoints.add(new Point((int) (Math.random() * 1000), (int) (Math.random() * 1000)));
		}

		pxmVisible = new Pixmap(new FileHandle("visible2.png"));

		rectangle1 = new Rectangle(0, 0, 0, 0);
		rectangle2 = new Rectangle(0, 0, 0, 0);
	}
	/**
	 * Draw all creatures in the visible area.
	 */
	private void drawVisibleAreaCreature(VisibleArea visibleArea) {
		/* An example circle, remember to flush before changing the blending function */
		spriteBatch.setBlendFunction(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
		spriteBatch.begin();
		ScreenUtils.clear(Color.CLEAR);
		// TODO normal draw of all Creatures
		rectangle1.set(visibleArea.getX(), visibleArea.getY(), visibleArea.getWidth(), visibleArea.getHeight());
		for (Point p : donutsPoints) {
			// Draw only if in visible area to improve performance
			// It can be done with setVisible(boolean b) for Stage, but may cost more time than just draw all.
			// TODO to test with setVisible(boolean b) for Stage.
			rectangle2.set(p.x, p.y, donut.getWidth(), donut.getHeight());
			if (rectangle1.overlaps(rectangle2)) {
				spriteBatch.draw(donut, p.x - visibleArea.getX(), p.y - visibleArea.getY());
			}
		}
	}
	/**
	 * Draw a mask over the visible area.
	 * Mask will remove some previously drawn pixels depending of visible circle texture.
	 * Colored part of the mask will be removed (with same alpha).
	 */
	private void drawVisibleAreaMask(VisibleArea visibleArea) {
		/* We'll need blending enabled for the technique to work */
		Gdx.gl.glEnable(GL30.GL_BLEND);

		/*
		 * With this blending function, wherever we draw pixels next
		 * we will actually remove previously drawn pixels.
		 */
		spriteBatch.flush();
		// Gdx.gl.glBlendFuncSeparate(GL30.GL_ZERO, GL30.GL_ONE, GL30.GL_ZERO, GL30.GL_ONE_MINUS_SRC_ALPHA); // don't work
		spriteBatch.setBlendFunctionSeparate(GL30.GL_ZERO, GL30.GL_ONE, GL30.GL_ZERO, GL30.GL_ONE_MINUS_SRC_ALPHA);
		// 0, 0 because we are drawing in a frame buffer centered over the visible texture.
		spriteBatch.draw(getVisibleCircleTexture(visibleArea.getRadius()), 0, 0);
		spriteBatch.flush();
		spriteBatch.end();

		/* Restore defaults. */
		Gdx.gl.glDisable(GL30.GL_BLEND);
		// Next line is needed so that something is actually draw on user screen.
		spriteBatch.setBlendFunction(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
	}

	@Override
	public void render() {
		long time = System.currentTimeMillis();
		ScreenUtils.clear(Color.GREEN);

		// TODO for each Creature ally with Player one (and player one)
		for (VisibleArea visibleArea : visibleAreas) {
			// draw visible area into a frame buffer.
			getFrameBuffers(visibleArea.getRadius()).bind();
			drawVisibleAreaCreature(visibleArea);
			drawVisibleAreaMask(visibleArea);
			getFrameBuffers(visibleArea.getRadius()).end();

			// draw frame buffer into screen.
			Texture texture = getFrameBuffers(visibleArea.getRadius()).getColorBufferTexture();
			Sprite sprite = new Sprite(texture);
			sprite.flip(false, true);
			sprite.setPosition(visibleArea.getX(), visibleArea.getY());
			spriteBatch.begin();
			sprite.draw(spriteBatch);
			spriteBatch.end();
		}

		times.add((int) (System.currentTimeMillis() - time));
	}

	@Override
	public void dispose() {
		// spriteBatch.dispose();
		// donut.dispose();
		// visible.dispose();
		IntSummaryStatistics stats = times.stream().mapToInt(Integer::intValue).summaryStatistics();
		System.out.println(stats.getAverage() + " ms in average. (max: " + stats.getMax() + " ms)");
	}

	public Texture getVisibleCircleTexture(int radius) {
		if (!visibleCircleTextures.containsKey(radius)) {
			visibleCircleTextures.put(radius, createVisibleCircleTexture(radius));
		}
		return visibleCircleTextures.get(radius);
	}
	/** Draw the visible pixmap into a different radius pixmap. */
	public Texture createVisibleCircleTexture(int radius) {
		Pixmap pixmap = new Pixmap(radius * 2, radius * 2, pxmVisible.getFormat());
		pixmap.drawPixmap(pxmVisible, 0, 0, pxmVisible.getWidth(), pxmVisible.getHeight(), 0, 0, pixmap.getWidth(), pixmap.getHeight());
		return new Texture(pixmap);
	}

	public FrameBuffer getFrameBuffers(int radius) {
		if (!frameBuffers.containsKey(radius)) {
			frameBuffers.put(radius, new FrameBuffer(Format.RGBA8888, radius * 2, radius * 2, false));
		}
		return frameBuffers.get(radius);
	}

	private class VisibleArea {
		private int x;
		private int y;
		private int radius;

		public VisibleArea(int x, int y, int radius) {
			this.x = x;
			this.y = y;
			this.radius = radius;
		}

		public int getX() { return x; }
		public int getY() { return y; }
		public int getRadius() { return radius; }
		public int getWidth() { return radius * 2; }
		public int getHeight() { return radius * 2; }
	}
	private static class Point {
		public float x;
		public float y;

		public Point(float x, float y) {
			this.x = x;
			this.y = y;
		}
	}
}