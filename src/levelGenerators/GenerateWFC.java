package levelGenerators;

import java.nio.file.Path;
import java.nio.file.Paths;

import levelGenerators.ttWFC.BatchRunner;
import levelGenerators.ttWFC.LevelGenerator;

/**
 * Simple entry point for generating levels with the ttWFC implementation.
 * Edit the configuration below, then run this class from the repository root.
 */
public class GenerateWFC {
    private enum Mode {
        SINGLE_LEVEL,
        BATCH
    }

    private enum OutputMode {
        ONE_FOLDER,
        WINDOW_FOLDERS
    }

    // Choose which section below should run, Mode.SINGLE_LEVEL to generate singular level
    private static final Mode MODE = Mode.BATCH;
    //If mode = WINDOW_FOLDERS, output will be separated into folders based on window size (1x1, 2x2, etc.)
    //   mode = ONE_FOLDER | all output will be put into WFC_Output file
    private static final OutputMode OUTPUT_MODE = OutputMode.WINDOW_FOLDERS;
    private static final Path OUTPUT_ROOT = Paths.get("WFC_Output");

    // SINGLE_LEVEL settings: one generated level written to WFC_Output/.
    private static final String SINGLE_LEVEL = "lvl-13";
    private static final int SINGLE_M = 6;
    private static final int SINGLE_N = 3;

    // BATCH settings: each entry is one {M, N} window size.
    private static final String[] BATCH_LEVELS = {"all"};
    private static final int[][] BATCH_WINDOWS = {{1, 1}, {2, 2}};
    private static final int BATCH_REPEATS = 1; //AMOUNT OF LEVELS TO PRODUCE
    private static final int BATCH_ATTEMPTS_PER_REPEAT = 10000; //ATTEMPTS PER PRODUCTION (CONTRADCICTIONS)

    public static void main(String[] args) throws Exception {
        switch (MODE) {
            case SINGLE_LEVEL:
                generateSingleLevel();
                break;
            case BATCH:
                BatchRunner.run(
                    BATCH_LEVELS,
                    BATCH_WINDOWS,
                    BATCH_REPEATS,
                    BATCH_ATTEMPTS_PER_REPEAT,
                    OUTPUT_ROOT,
                    OUTPUT_MODE == OutputMode.WINDOW_FOLDERS
                );
                break;
            default:
                throw new IllegalStateException("Unsupported generation mode: " + MODE);
        }
    }

    private static void generateSingleLevel() throws Exception {
        LevelGenerator.runAndGetLevel(
            SINGLE_LEVEL,
            SINGLE_M,
            SINGLE_N,
            getOutputDirectory(SINGLE_M, SINGLE_N)
        );
        System.out.println("Generated " + SINGLE_LEVEL + " with a " + SINGLE_M + "x" + SINGLE_N
            + " window. The level is also available through the LevelGenerator API.");
    }

    private static Path getOutputDirectory(int M, int N) {
        if (OUTPUT_MODE == OutputMode.WINDOW_FOLDERS) {
            return OUTPUT_ROOT.resolve(M + "x" + N);
        }
        return OUTPUT_ROOT;
    }
}