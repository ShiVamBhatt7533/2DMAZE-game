package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * The GameScreen class is responsible for rendering the gameplay screen.
 * It handles the game logic and rendering of the game elements.
 */
public class GameScreen implements Screen {

    private final MazeRunnerGame game;
    private final OrthographicCamera camera;
    private final BitmapFont font;
    private TiledMapRenderer tiledMapRenderer;
    private TiledMap currentMap;

    private float sinusInput = 0f;

    /**
     * Constructor for GameScreen. Sets up the camera and font.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public GameScreen(MazeRunnerGame game) {
        this.game = game;

        // Create and configure the camera for the game view
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        camera.zoom = 0.75f;

        // Get the font from the game's skin
        font = game.getSkin().getFont("font");
    }

    /**
     * Loads the Tiled map for the game.
     *
     * @param mapPath The path to the Tiled map file.
     */
    public void loadMap(String mapPath) {
        // Dispose of the previous map
        disposeCurrentMap();

        // Load the new map
        currentMap = new TmxMapLoader().load(mapPath);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(currentMap);
    }

    /**
     * Dispose of the current Tiled map if it exists.
     */
    private void disposeCurrentMap() {
        if (currentMap != null) {
            currentMap.dispose();
        }
    }

    // Screen interface methods with necessary functionality
    @Override
    public void render(float delta) {
        handleInput(); // Handle user input
        updateCamera(); // Update the camera

        ScreenUtils.clear(0, 0, 0, 1); // Clear the screen

        // Set up and begin drawing with the sprite batch
        game.getSpriteBatch().setProjectionMatrix(camera.combined);
        game.getSpriteBatch().begin();

        // Render the map
        renderMap();

        // Move text in a circular path to have an example of a moving object
        sinusInput += delta;
        float textX = (float) (camera.position.x + Math.sin(sinusInput) * 100);
        float textY = (float) (camera.position.y + Math.cos(sinusInput) * 100);

        // Render the text
        font.draw(game.getSpriteBatch(), "Press ESC to Pause the Game", textX, textY);

        // Draw the character next to the text :) / We can reuse sinusInput here
        renderCharacter(textX, textY);

        game.getSpriteBatch().end(); // Important to call this after drawing everything
    }

    /**
     * Handle user input.
     */
    private void handleInput() {
        // Check for escape key press to go back to the menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setPaused(true);
            game.goToMenu();
        }
    }

    /**
     * Update the camera.
     */
    private void updateCamera() {
        camera.update();
    }

    /**
     * Render the Tiled map.
     */
    private void renderMap() {
        if (tiledMapRenderer != null) {
            tiledMapRenderer.setView(camera);
            tiledMapRenderer.render();
        }
    }

    /**
     * Render the character.
     *
     * @param textX X-coordinate for rendering the character.
     * @param textY Y-coordinate for rendering the character.
     */
    private void renderCharacter(float textX, float textY) {
        game.getSpriteBatch().draw(
                game.getCharacterDownAnimation().getKeyFrame(sinusInput, true),
                textX - 96,
                textY - 64,
                64,
                128
        );
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        disposeCurrentMap();
    }

    // Additional methods and logic can be added as needed for the game screen
}
