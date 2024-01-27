package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {
    private MazeRunnerGame game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private List<GameObject> gameObjects;
    private Music gameMusic;
    private Character character;
    private int mazeWidth, mazeHeight;
    private TextureLoader textureLoader,enemyLoader,characterLoader;
    private final float CAMERA_PADDING = 0.1f;
    private float spawnX, spawnY;
    private int[][] mazeData;
    private float initialZoom;
    private float minCameraX, maxCameraX, minCameraY, maxCameraY;
    public GameScreen(MazeRunnerGame game, int[][] mazeData) {
        this.game = game;
        this.mazeData = mazeData;
        initialize();
        initialZoom = calculateInitialZoom();
        calculateCameraConstraints();
    }
    private void initialize() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        camera.zoom =0.5f;
        gameObjects = new ArrayList<>();
        textureLoader = new TextureLoader(Gdx.files.internal("basictiles.png"));
        enemyLoader = new TextureLoader(Gdx.files.internal("mobs.png"));
        characterLoader = new TextureLoader(Gdx.files.internal("character.png"));
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("InGame.mp3"));
        loadGameObjects(mazeData);
        calculateMazeDimensions();
    }
    private float calculateInitialZoom() {
        // Calculate the initial zoom level based on maze and window dimensions
        float horizontalZoom = (float) Gdx.graphics.getWidth() / mazeWidth;
        float verticalZoom = (float) Gdx.graphics.getHeight() / mazeHeight;

        // Choose the smaller zoom factor to ensure the entire maze fits
        return Math.min(horizontalZoom, verticalZoom);
    }

    private void calculateCameraConstraints() {
        // Calculate camera position constraints based on zoom level
        float cameraWidth = Gdx.graphics.getWidth() / camera.zoom;
        float cameraHeight = Gdx.graphics.getHeight() / camera.zoom;

        // Calculate the middle 80% of the screen
        float paddingX = cameraWidth * 0.1f;
        float paddingY = cameraHeight * 0.1f;

        // Set camera position constraints
        minCameraX = paddingX;
        maxCameraX = mazeWidth - cameraWidth + paddingX;
        minCameraY = paddingY;
        maxCameraY = mazeHeight - cameraHeight + paddingY;
    }
    private void loadGameObjects(int[][] mazeData) {
        TextureRegion wallRegion = textureLoader.getTextureRegion(0, 0, 16, 16);
        TextureRegion keyRegion = textureLoader.getTextureRegion(6*16, 3*16, 16, 6);
        TextureRegion entryRegion = textureLoader.getTextureRegion(2*16, 6*16, 16, 16);
        TextureRegion exitRegion = textureLoader.getTextureRegion(0, 6*16, 16, 16);
        TextureRegion trapRegion = textureLoader.getTextureRegion(2*16, 9*16, 16, 16);
        TextureRegion enemyRegion = enemyLoader.getTextureRegion(4*16, 5*16, 16, 16);
        for (int x = 0; x < mazeData.length; x++) {
            for (int y = 0; y < mazeData[x].length; y++) {
                switch (mazeData[x][y]) {
                    case 0:
                        gameObjects.add(new Wall(wallRegion, x, y));
                        break;
                    case 1:
                        gameObjects.add(new Entry(entryRegion, x, y));
                        setSpawnPoint(x+1,y);
                        initializeCharacter();
                        break;
                    case 2:
                        gameObjects.add(new Exit(exitRegion, x, y));
                        break;
                    case 3:
                        gameObjects.add(new Trap(trapRegion, x, y));
                        break;
                    case 4:
                        gameObjects.add(new Enemy(enemyRegion, x, y));
                        break;
                    case 5:
                        gameObjects.add(new Key(keyRegion, x, y));
                        break;
                    default:
                        break;
                }
            }
        }
    }
    private void setSpawnPoint(float x, float y) {
        spawnX = x;
        spawnY = y;
    }
    private void initializeCharacter() {
        TextureRegion characterRegion = characterLoader.getTextureRegion(0, 0, 16, 16); // Adjust coordinates and size as needed

        // If the character has multiple frames for animation, split the texture and create an animation
        // Otherwise, just use a single frame as below
        Animation<TextureRegion> characterAnimation = new Animation<>(0.1f, characterRegion);

        character = new Character(characterAnimation, spawnX * 16, spawnY * 16); // Position character at spawn point
    }
    @Override
    public void render(float delta) {
        // Clear the screen, update the game state, etc.
        ScreenUtils.clear(0, 0, 0, 1);
        handleInput(delta);
            if (character != null) {
                character.update(delta); // Update character state
                updateCameraPosition(delta);
            }
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (GameObject gameObject : gameObjects) {
            gameObject.draw(batch);
        }
        if (character != null) {
            character.draw(batch);
        }
        batch.end();
    }
    private void updateCameraPosition(float delta) {
        float lerp = 0.1f; // For smooth camera movement
        float cameraX = camera.position.x + (character.getX() - camera.position.x) * lerp;
        float cameraY = camera.position.y + (character.getY() - camera.position.y) * lerp;
        // Clamp camera position to keep character within the middle 80% of the screen
        float cameraMinX = mazeWidth * CAMERA_PADDING;
        float cameraMaxX = mazeWidth * (1 - CAMERA_PADDING);
        float cameraMinY = mazeHeight * CAMERA_PADDING;
        float cameraMaxY = mazeHeight * (1 - CAMERA_PADDING);

        camera.position.x = MathUtils.clamp(cameraX, cameraMinX, cameraMaxX);
        camera.position.y = MathUtils.clamp(cameraY, cameraMinY, cameraMaxY);
    }
    private void calculateMazeDimensions() {
        mazeWidth = mazeData.length * 16; // Assuming each cell is 16 pixels wide
        mazeHeight = mazeData[0].length * 16; // Assuming each cell is 16 pixels high
    }
    public void playGameMusic() {
        if (gameMusic != null) {
            gameMusic.setLooping(true);
            gameMusic.play();
        }
    }
    private void handleInput(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setPaused(true);
            game.goToMenu();
        }
    }
    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false,width,height);
        camera.update();
        calculateCameraConstraints();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void show() {
        if (!game.isPaused()) {
            playGameMusic();
        }
        if (character != null) {
            camera.position.set(character.getX(), character.getY(), 0);
            camera.zoom = 0.4f; // Adjust this value for desired zoom level
        }
        camera.update();
    }

    @Override
    public void hide() {
        if (gameMusic != null) {
            gameMusic.stop();
            gameMusic.dispose();
        }
    }

    @Override
    public void dispose() {
        textureLoader.dispose();
      batch.dispose();
    }
}
