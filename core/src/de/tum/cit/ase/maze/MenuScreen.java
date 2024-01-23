package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * The MenuScreen class is responsible for displaying the main menu of the game.
 * It extends the LibGDX Screen class and sets up the UI components for the menu.
 */
public class MenuScreen implements Screen {

    private final Stage stage;
    private final MazeRunnerGame game;
    private TextButton loadMap;
    private TextButton exit;
    private TextButton returnMenu;
    private TextButton newGame;
    private TextButton resumeGame;
    private Label victory;
    private Label gameOver;
    private Label pausedLabel;
    private Label welcomeLabel;
    private boolean pauseMenuVisible;

    public void setLoadMap(TextButton loadMap) {
        this.loadMap = loadMap;
    }

    public void setExit(TextButton exit) {
        this.exit = exit;
    }

    public void setResumeGame(TextButton resumeGame) {
        this.resumeGame = resumeGame;
    }

    public void setPausedLabel(Label pausedLabel) {
        this.pausedLabel = pausedLabel;
    }

    public TextButton getReturnMenu() {
        return returnMenu;
    }

    public void setReturnMenu(TextButton returnMenu) {
        this.returnMenu = returnMenu;
    }

    public TextButton getNewGame() {
        return newGame;
    }

    public void setNewGame(TextButton newGame) {
        this.newGame = newGame;
    }

    public Label getVictory() {
        return victory;
    }

    public void setVictory(Label victory) {
        this.victory = victory;
    }

    public Label getGameOver() {
        return gameOver;
    }

    public void setGameOver(Label gameOver) {
        this.gameOver = gameOver;
    }

    public boolean isPauseMenuVisible() {
        return pauseMenuVisible;
    }

    public void setPauseMenuVisible(boolean pauseMenuVisible) {
        this.pauseMenuVisible = pauseMenuVisible;
    }

    public MenuScreen(MazeRunnerGame game) {
        this.game=game;
        // Set up the camera and viewport
        var camera = new OrthographicCamera();
        camera.zoom = 1.5f;
        Viewport viewport = new ScreenViewport(camera);

        // Create the stage for UI elements
        stage = new Stage(viewport, game.getSpriteBatch());

        // Create a table for layout
        Table tableM = new Table();
        tableM.setFillParent(true);
        stage.addActor(tableM);
        tableM.top();
        Table tableP = new Table();
        tableP.setFillParent(true);
        stage.addActor(tableP);
        tableP.top();


        // Add a welcome label
        welcomeLabel = new Label("Welcome to BlazeMaze", game.getSkin(), "title");
        tableM.add(welcomeLabel).padBottom(60).row();
        welcomeLabel.setVisible(!game.getPaused());
        // Add a button to start the game
        TextButton goToGameButton = new TextButton("Play", game.getSkin());
        tableM.add(goToGameButton).width(500).pad(10).row();
        goToGameButton.setVisible(!game.getPaused());
        // Create buttons for additional menu options
        loadMap = new TextButton("Levels", game.getSkin());
        tableM.add(loadMap).width(500).pad(10).row();
        loadMap.setVisible(!game.getPaused());
        exit = new TextButton("Exit", game.getSkin());
        tableM.add(exit).width(500).pad(10).row();
        exit.setVisible(!game.getPaused());

        goToGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToGame();
            }
        });
        // Add listeners to the buttons
        loadMap.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToMenu();
            }
        });
        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        // Create and add the paused label
        // Initialize the pause menu visibility
        pauseMenuVisible = game.getPaused();
        pausedLabel = new Label("Game Paused", game.getSkin(), "title");
        tableP.add(pausedLabel).padBottom(60).pad(10).row();
        pausedLabel.setVisible(game.getPaused());
        // Add a buttons to the paused menu
        resumeGame = new TextButton("Resume", game.getSkin());
        tableP.add(resumeGame).width(400).pad(10).row();
        resumeGame.setVisible(game.getPaused());
        newGame = new TextButton("New Game", game.getSkin());
        tableP.add(newGame).width(400).pad(10).row();
        newGame.setVisible(game.getPaused());
        loadMap= new TextButton("Load Map", game.getSkin());
        tableP.add(loadMap).width(400).pad(10).row();
        loadMap.setVisible(game.getPaused());
        returnMenu = new TextButton("Quit to Main Menu", game.getSkin());
        returnMenu.setVisible(game.getPaused());
        tableP.add(returnMenu).width(400).pad(10).row();
        resumeGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.resume();
            }
        });
        newGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToGame();
            }
        });
        returnMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setPaused(false);
                game.goToMenu();
            }
        });

        // Create labels for victory and game over messages
        victory = new Label("Congratulations! You Won!", game.getSkin(), "default");
        gameOver = new Label("Game Over. Better luck next time!", game.getSkin(), "default");

        // Set labels to be initially invisible
        victory.setVisible(false);
        gameOver.setVisible(false);

        // Add new game button and return to main menu button to the table

    }
    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f)); // Update the stage
        stage.draw(); // Draw the stage
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        resume();
    }

    // Other lifecycle methods (pause, resume, hide) are kept empty
    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        pause();
    }
}
