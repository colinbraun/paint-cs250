package paint.util;

import com.sun.istack.internal.NotNull;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import paint.Main;
import paint.constant.DrawMode;
import paint.shape.Drawable;
import paint.shape.Line;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * A utility class intended to make working with the canvas easier
 */
public class CanvasManager {
    /**
     * The canvas that this will do work on
     */
    private Canvas canvas;
    /**
     * The draw mode the canvas is in
     */
    private DrawMode drawMode;
    /**
     * The canvas' GraphicsContext, avoids constantly having to call canvas.getGraphicsContext2D()
     */
    private GraphicsContext context;
    /**
     * The item being currently drawn (I.E. a line)
     */
     private Drawable currentDrawing;
    /**
     * The currently selected color
     */
    private Color selectedColor;

    public CanvasManager(@NotNull Canvas canvas) {
        this.canvas = canvas;
        selectedColor = Color.BLACK;
        context = canvas.getGraphicsContext2D();
        drawMode = DrawMode.LINE;
        initEvents();
    }

    /**
     * Initialize how all the events for the canvas are handled
     */
    private void initEvents() {
        if(Main.DEBUG)
            canvas.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
                System.out.println("X: " + event.getX() + " Y: " + event.getY());
            });

        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, (event) -> {
            if(drawMode == null)
                return;
            switch(drawMode) {
                case LINE:
                    currentDrawing = new Line(event.getX(), event.getY());
                    break;
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            if(drawMode == null)
                return;
            switch(drawMode) {
                case LINE:
                    ((Line)currentDrawing).setEnd(event.getX(), event.getY()).draw(context);
                    break;
            }
        });
    }

    /**
     * Set the draw mode for the canvas
     * @param mode the draw mode to set the canvas to
     */
    public void setDrawMode(DrawMode mode) {
        this.drawMode = mode;
    }

    /**
     * Set the color to draw with
     * @param color the color to draw with
     */
    public void setSelectedColor(Color color) {
        this.selectedColor = color;
        context.setFill(color);
    }

    /**
     * Load an image onto the canvas from a file
     * @param imageFile The file to be loaded
     */
    public void loadImageFromFile(@NotNull File imageFile) {
        Image image = null;
        try {
            // TODO: Do this better. Not great to use the initial window size to determine the image's size. Try to make more dynamic.
            image = new Image(new FileInputStream(imageFile.getAbsolutePath()), Main.WIDTH * 0.8, Main.HEIGHT * 0.8, true, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        canvas.setHeight(image.getHeight());
        canvas.setWidth(image.getWidth());
        canvas.getGraphicsContext2D().drawImage(image, 0, 0, image.getWidth(), image.getHeight());
    }

    /**
     * Save the contents of the canvas to a file
     * @param file The file to be saved to
     */
    public void saveCanvasToFile(@NotNull File file) {
        WritableImage image = canvas.snapshot(new SnapshotParameters(), null);
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        try {
            ImageIO.write(bufferedImage, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
